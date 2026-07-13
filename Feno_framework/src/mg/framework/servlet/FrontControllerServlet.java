package mg.framework.servlet;
 
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
import mg.framework.annotation.Controller;
import mg.framework.annotation.Param;
import mg.framework.annotation.Url;
import mg.framework.mvc.ModelView;
import mg.framework.scanner.ClassScanner;
 
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
/**
 * Front Controller : point d'entree unique de toutes les requetes.
 *
 * Historique :
 * - Sprint 0 : intercepte toutes les requetes (GET/POST) et affiche l'URL appelee.
 * - Sprint 1 : au demarrage (init), scanne le package indique dans web.xml
 *              et memorise la liste des classes annotees avec @Controller.
 * - Sprint 2 : construit une table de routing (URL -> Method) a partir de
 *              l'annotation @Url posee sur les methodes des Controllers,
 *              et invoque la bonne methode selon l'URL demandee.
 * - Sprint 3 : la methode Controller peut retourner un String = nom de vue
 *              JSP. Le FrontController fait le forward vers WEB-INF/views/.
 * - Sprint 4 : les parametres de la methode Controller annotes @Param sont
 *              remplis automatiquement avec request.getParameter(...), avec
 *              conversion de type (String, int, long, double, boolean...).
 * - Sprint 5 : la methode Controller peut retourner un ModelView pour
 *              transmettre des donnees a la JSP (request.setAttribute),
 *              en plus de choisir la vue.
 *
 * NOTE : il ne faut PAS annoter cette classe avec @WebServlet en plus de la
 * declaration dans web.xml, sinon Tomcat leve une erreur de double declaration
 * du meme servlet. On garde uniquement la declaration via web.xml afin de
 * pouvoir lui passer le parametre "controller-package".
 */
public class FrontControllerServlet extends HttpServlet {
 
    /** Prefixe du dossier ou se trouvent les vues JSP, dans le webapp. */
    private static final String VUE_PREFIX = "/WEB-INF/views/";
 
    /** Liste des classes detectees comme etant des Controllers (annotees @Controller). */
    private final List<Class<?>> controllers = new ArrayList<>();
 
    /** Table de routing Sprint 2 : URL -> methode a invoquer. */
    private final Map<String, Method> routes = new HashMap<>();
 
    /** Table de routing Sprint 2 : URL -> classe proprietaire de la methode. */
    private final Map<String, Class<?>> routeOwners = new HashMap<>();
 
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
 
        // 1. Recuperer le package a scanner depuis web.xml (init-param du servlet)
        String controllerPackage = config.getInitParameter("controller-package");
 
        // Si rien n'est precise dans web.xml, on peut essayer un parametre global (context-param)
        if (controllerPackage == null || controllerPackage.isEmpty()) {
            controllerPackage = config.getServletContext().getInitParameter("controller-package");
        }
 
        if (controllerPackage == null || controllerPackage.isEmpty()) {
            System.err.println("[FrontControllerServlet] Aucun 'controller-package' defini dans web.xml. "
                    + "Aucun controller ne sera scanne.");
            return;
        }
 
        System.out.println("[FrontControllerServlet] Scan du package : " + controllerPackage);
 
        // 2. Scanner toutes les classes du package
        List<Class<?>> classesTrouvees = ClassScanner.scanPackage(controllerPackage);
 
        // 3. Ne garder que celles annotees avec @Controller
        for (Class<?> clazz : classesTrouvees) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                controllers.add(clazz);
                System.out.println("[FrontControllerServlet] Controller detecte : " + clazz.getName());
                registerRoutes(clazz);
            }
        }
 
        System.out.println("[FrontControllerServlet] " + controllers.size()
                + " controller(s) detecte(s), " + routes.size() + " route(s) enregistree(s) au total.");
    }
 
    /**
     * Sprint 2 : parcourt les methodes d'un Controller et enregistre dans la
     * table de routing celles qui portent l'annotation @Url.
     */
    private void registerRoutes(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Url.class)) {
                continue;
            }
 
            String url = method.getAnnotation(Url.class).value();
 
            if (routes.containsKey(url)) {
                System.err.println("[FrontControllerServlet] Conflit de routing : l'URL '" + url
                        + "' est deja associee a " + routeOwners.get(url).getName()
                        + "." + routes.get(url).getName()
                        + " -> ignore pour " + clazz.getName() + "." + method.getName());
                continue;
            }
 
            routes.put(url, method);
            routeOwners.put(url, clazz);
            System.out.println("[FrontControllerServlet] Route enregistree : " + url
                    + " -> " + clazz.getSimpleName() + "." + method.getName() + "()");
        }
    }
 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, "GET");
    }
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, "POST");
    }
 
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String method)
            throws ServletException, IOException {
 
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        String requestPath = path.substring(contextPath.length());
 
        System.out.println("[FrontControllerServlet] " + method + " -> " + requestPath);
 
        // Sprint 2 : chercher la methode Controller associee a cette URL
        Method routeMethod = routes.get(requestPath);
        Class<?> routeOwner = routeOwners.get(requestPath);
 
        if (routeMethod == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h1>404</h1><p>Aucune route trouvee pour : " + requestPath + "</p>");
            return;
        }
 
        try {
            // Sprint 2 : instancier le controller (constructeur sans argument)
            Object instance = routeOwner.getDeclaredConstructor().newInstance();
 
            // Sprint 4 : construire les arguments de la methode a partir de la requete
            Object[] args = buildArgs(routeMethod, request, response);
 
            // Sprint 2 : invoquer la methode
            Object resultat = routeMethod.invoke(instance, args);
 
            // Sprint 3 / Sprint 5 : traiter le retour de la methode
            handleResult(resultat, request, response);
 
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            erreur500(response, "Impossible d'instancier ou d'invoquer le controller " + routeOwner.getName(), e);
        } catch (InvocationTargetException e) {
            erreur500(response, "Erreur dans le controller " + routeOwner.getName() + "." + routeMethod.getName(),
                    e.getCause() != null ? e.getCause() : e);
        }
    }
 
    /**
     * Sprint 4 : pour chaque parametre de la methode Controller, determine sa valeur :
     * - HttpServletRequest / HttpServletResponse : injecte directement
     * - parametre annote @Param("xxx") : recupere request.getParameter("xxx") et convertit
     * - sinon : null (parametre non supporte)
     */
    private Object[] buildArgs(Method method, HttpServletRequest request, HttpServletResponse response) {
        Parameter[] parametres = method.getParameters();
        Object[] args = new Object[parametres.length];
 
        for (int i = 0; i < parametres.length; i++) {
            Parameter p = parametres[i];
            Class<?> type = p.getType();
 
            if (type.equals(HttpServletRequest.class)) {
                args[i] = request;
            } else if (type.equals(HttpServletResponse.class)) {
                args[i] = response;
            } else if (p.isAnnotationPresent(Param.class)) {
                String nomParam = p.getAnnotation(Param.class).value();
                String valeurBrute = request.getParameter(nomParam);
                args[i] = convertir(valeurBrute, type);
            } else {
                System.err.println("[FrontControllerServlet] Parametre '" + p.getName()
                        + "' de " + method.getName() + " n'est pas annote @Param, valeur = null.");
                args[i] = null;
            }
        }
 
        return args;
    }
 
    /**
     * Sprint 4 : convertit une valeur String (venant de request.getParameter) vers
     * le type declare du parametre de la methode Controller.
     */
    private Object convertir(String valeur, Class<?> typeCible) {
        if (valeur == null) {
            return null;
        }
        try {
            if (typeCible.equals(String.class)) {
                return valeur;
            } else if (typeCible.equals(int.class) || typeCible.equals(Integer.class)) {
                return Integer.parseInt(valeur);
            } else if (typeCible.equals(long.class) || typeCible.equals(Long.class)) {
                return Long.parseLong(valeur);
            } else if (typeCible.equals(double.class) || typeCible.equals(Double.class)) {
                return Double.parseDouble(valeur);
            } else if (typeCible.equals(boolean.class) || typeCible.equals(Boolean.class)) {
                return Boolean.parseBoolean(valeur);
            }
        } catch (NumberFormatException e) {
            System.err.println("[FrontControllerServlet] Impossible de convertir '" + valeur
                    + "' vers " + typeCible.getSimpleName() + ", valeur = null.");
            return null;
        }
        // Type non supporte : on renvoie la chaine brute
        return valeur;
    }
 
    /**
     * Sprint 3 / Sprint 5 : traite la valeur retournee par une methode Controller.
     * - String : nom de la vue JSP, forward direct, sans donnees.
     * - ModelView : injecte les donnees dans la requete (setAttribute) puis forward vers sa vue.
     * - autre type (ou null) : affichage brut, pour compatibilite avec le Sprint 1.
     */
    private void handleResult(Object resultat, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
 
        if (resultat instanceof ModelView) {
            ModelView mv = (ModelView) resultat;
            for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
            forward(mv.getVue(), request, response);
 
        } else if (resultat instanceof String) {
            forward((String) resultat, request, response);
 
        } else {
            // Fallback (comportement Sprint 0/1) : on affiche la valeur telle quelle
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<p>" + resultat + "</p>");
        }
    }
 
    private void forward(String vue, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (vue == null || vue.isEmpty()) {
            erreur500(response, "La methode Controller n'a pas precise de vue (vue == null).", null);
            return;
        }
        request.getRequestDispatcher(VUE_PREFIX + vue).forward(request, response);
    }
 
    private void erreur500(HttpServletResponse response, String message, Throwable cause) throws IOException {
        System.err.println("[FrontControllerServlet] Erreur 500 : " + message);
        if (cause != null) {
            cause.printStackTrace();
        }
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<h1>500</h1><p>" + message + "</p>");
    }
}