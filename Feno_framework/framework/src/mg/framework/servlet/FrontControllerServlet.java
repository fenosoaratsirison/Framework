package mg.framework.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import mg.framework.annotation.Controller;
import mg.framework.annotation.HttpMethod;
import mg.framework.annotation.RestController;
import mg.framework.annotation.Url;
import mg.framework.binding.ParamBinder;
import mg.framework.exception.BindingException;
import mg.framework.json.JsonUtils;
import mg.framework.mvc.ModelView;
import mg.framework.routing.Route;
import mg.framework.routing.RouteMapping;
import mg.framework.scanner.ClassScanner;
import mg.framework.session.MaSession;
import mg.framework.upload.MapFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 * - Sprint 3 : la table de routing distingue desormais le verbe HTTP
 *              (GET/POST) pour une meme URL, et la methode Controller peut
 *              retourner un String = nom de vue JSP (forward automatique).
 * - Sprint 4 : les parametres de la methode Controller annotes @Param sont
 *              remplis automatiquement avec request.getParameter(...), avec
 *              conversion de type (String, int, long, double, boolean...).
 * - Sprint 5 : la methode Controller peut retourner un ModelView pour
 *              transmettre des donnees a la JSP (request.setAttribute),
 *              en plus de choisir la vue.
 * - Sprint 6 : une classe annotee @RestController repond en JSON au lieu
 *              de faire un forward JSP. Si la methode retourne un String,
 *              il est considere comme deja du JSON ; sinon l'objet retourne
 *              est converti en JSON par reflection (JsonUtils).
 * - Sprint 7 : data binding des types simples (String, int, double...) :
 *              nom via @Param ou nom Java du parametre, valeurs par defaut,
 *              BindingException (erreur 400) si valeur invalide/obligatoire absente.
 * - Sprint 7 bis : data binding d'un objet (ex : Employe) rempli a partir
 *              des champs du formulaire. Logique dans binding/ParamBinder.
 * - Sprint 8 : upload de fichiers (formulaire multipart/form-data) : les
 *              fichiers arrivent en MapFile (nom, extension, bytes) ou en
 *              Map<String, byte[]>. Le dossier "upload-dir" est lu dans web.xml.
 * - Sprint 9 : session. Un parametre MaSession recoit une copie de la HttpSession ;
 *              apres l'appel de la methode, la copie est recopiee dans la vraie session.
 *
 * NOTE : il ne faut PAS annoter cette classe avec @WebServlet en plus de la
 * declaration dans web.xml, sinon Tomcat leve une erreur de double declaration
 * du meme servlet. On garde uniquement la declaration via web.xml afin de
 * pouvoir lui passer les parametres "controller-package", "view-prefix" et
 * "view-suffix".
 */
public class FrontControllerServlet extends HttpServlet {

    /** Sprint 3/5 : prefixe/suffixe du dossier des vues, configurables via web.xml. Valeurs par defaut. */
    private String viewPrefix = "/WEB-INF/views/";
    private String viewSuffix = ".jsp";

    /** Sprint 3 : table de routing URL + verbe HTTP -> (Controller, Method). */
    private final RouteMapping routeMapping = new RouteMapping();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // 1. Recuperer la configuration depuis web.xml (init-param du servlet),
        //    avec repli sur un context-param global si absent.
        String controllerPackage = initParam(config, "controller-package");

        String prefix = initParam(config, "view-prefix");
        if (prefix != null && !prefix.isEmpty()) {
            viewPrefix = prefix;
        }

        String suffix = initParam(config, "view-suffix");
        if (suffix != null && !suffix.isEmpty()) {
            viewSuffix = suffix;
        }

        // Sprint 8 : dossier ou MapFile.sauvegarder() enregistre les fichiers recus
        String dossierUpload = initParam(config, "upload-dir");
        if (dossierUpload != null && !dossierUpload.isEmpty()) {
            MapFile.setDossierUpload(dossierUpload);
            System.out.println("[FrontControllerServlet] Dossier d'upload : " + dossierUpload);
        }

        if (controllerPackage == null || controllerPackage.isEmpty()) {
            System.err.println("[FrontControllerServlet] Aucun 'controller-package' defini dans web.xml. "
                    + "Aucun controller ne sera scanne.");
            return;
        }

        System.out.println("[FrontControllerServlet] Scan du package : " + controllerPackage);

        // 2. Scanner toutes les classes du package
        List<Class<?>> classesTrouvees = ClassScanner.scanPackage(controllerPackage);

        int nbControllers = 0;

        // 3. Ne garder que celles annotees avec @Controller ou @RestController (Sprint 6),
        //    puis enregistrer leurs routes
        for (Class<?> clazz : classesTrouvees) {
            boolean estControllerVue = clazz.isAnnotationPresent(Controller.class);
            boolean estControllerApi = clazz.isAnnotationPresent(RestController.class);

            if (estControllerVue || estControllerApi) {
                nbControllers++;
                System.out.println("[FrontControllerServlet] Controller detecte : " + clazz.getName()
                        + (estControllerApi ? " (REST - reponses JSON)" : ""));
                registerRoutes(clazz);
            }
        }

        System.out.println("[FrontControllerServlet] " + nbControllers
                + " controller(s) detecte(s), " + routeMapping.getRoutes().size() + " URL(s) enregistree(s).");
    }

    private String initParam(ServletConfig config, String name) {
        String value = config.getInitParameter(name);
        if (value == null || value.isEmpty()) {
            value = config.getServletContext().getInitParameter(name);
        }
        return value;
    }

    /**
     * Sprint 2/3 : parcourt les methodes d'un Controller et enregistre dans la
     * table de routing celles qui portent l'annotation @Url, pour chaque
     * verbe HTTP declare (GET et/ou POST).
     */
    private void registerRoutes(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Url.class)) {
                continue;
            }

            Url annotation = method.getAnnotation(Url.class);
            String url = annotation.value();

            for (HttpMethod httpMethod : annotation.method()) {
                try {
                    routeMapping.addRoute(url, httpMethod, clazz, method);
                    System.out.println("[FrontControllerServlet] Route enregistree : "
                            + httpMethod + " " + url + " -> " + clazz.getSimpleName() + "." + method.getName() + "()");
                } catch (IllegalStateException e) {
                    System.err.println("[FrontControllerServlet] " + e.getMessage());
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, HttpMethod.GET);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, HttpMethod.POST);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response, HttpMethod httpMethod)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        String requestPath = path.substring(contextPath.length());
        if (requestPath.isEmpty()) {
            requestPath = "/";
        }

        System.out.println("[FrontControllerServlet] " + httpMethod + " -> " + requestPath);

        // Sprint 3 : chercher la route associee a cette URL + ce verbe HTTP
        Route route = routeMapping.getRoute(requestPath, httpMethod);

        if (route == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h1>404</h1><p>Aucune route " + httpMethod
                    + " trouvee pour : " + requestPath + "</p>");
            return;
        }

        try {
            // Sprint 2 : instancier le controller (constructeur sans argument)
            Object instance = route.getController().getDeclaredConstructor().newInstance();

            // Sprint 4 / 7 / 7 bis : construire les arguments de la methode a partir de la
            // requete (types simples et objets) grace au ParamBinder
            Object[] args = ParamBinder.construireArguments(route.getMethod(), request, response);

            // Sprint 2 : invoquer la methode
            Object resultat = route.getMethod().invoke(instance, args);

            // Sprint 9 : si la methode a recu une MaSession, on la recopie dans la vraie
            // HttpSession (avant le forward, pour que la vue voie deja les changements)
            enregistrerSession(args, request);

            // Sprint 6 : si le Controller est annote @RestController, on repond en JSON
            // et on s'arrete la (pas de forward JSP).
            if (route.getController().isAnnotationPresent(RestController.class)) {
                handleResultJson(resultat, response);
            } else {
                // Sprint 3 / Sprint 5 : traiter le retour de la methode (forward JSP)
                handleResult(resultat, request, response);
            }

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            erreur500(response, "Impossible d'instancier ou d'invoquer le controller "
                    + route.getController().getName(), e);
        } catch (BindingException e) {
            // Sprint 7 : donnees du formulaire invalides ou manquantes -> erreur 400
            erreur400(response, e.getMessage());
        } catch (InvocationTargetException e) {
            erreur500(response, "Erreur dans le controller " + route.getController().getName()
                    + "." + route.getMethod().getName(), e.getCause() != null ? e.getCause() : e);
        }
    }

    /**
     * Sprint 3 / Sprint 5 : traite la valeur retournee par une methode Controller.
     * - String : nom de la vue JSP, forward direct, sans donnees.
     * - ModelView : injecte les donnees dans la requete (setAttribute) puis forward vers sa vue.
     * - autre type (ou null) : affichage brut, pour compatibilite avec le Sprint 0/1.
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
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<p>" + resultat + "</p>");
        }
    }

    /**
     * Sprint 6 : traite le retour d'une methode d'un Controller @RestController.
     * Ecrit directement une reponse "application/json", sans passer par une vue JSP.
     * - String : deja consideree comme du JSON, renvoyee telle quelle.
     * - autre type (ou null) : converti en JSON via JsonUtils.toJson(...).
     */
    private void handleResultJson(Object resultat, HttpServletResponse response) throws IOException {

        String json;

        if (resultat instanceof String) {
            // La methode a deja construit elle-meme la chaine JSON.
            json = (String) resultat;
        } else {
            // Conversion automatique (y compris pour un ModelView, une List, un POJO...).
            json = JsonUtils.toJson(resultat);
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }

    private void forward(String vue, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (vue == null || vue.isEmpty()) {
            erreur500(response, "La methode Controller n'a pas precise de vue (vue == null).", null);
            return;
        }
        request.getRequestDispatcher(viewPrefix + vue + viewSuffix).forward(request, response);
    }

    /**
     * Sprint 9 : cherche un argument de type MaSession parmi les arguments de la
     * methode et recopie son contenu dans la vraie HttpSession.
     */
    private void enregistrerSession(Object[] args, HttpServletRequest request) {
        for (Object argument : args) {
            if (argument instanceof MaSession) {
                HttpSession httpSession = request.getSession(false);
                if (httpSession != null) {
                    ((MaSession) argument).flushTo(httpSession);
                }
            }
        }
    }

    private void erreur400(HttpServletResponse response, String message) throws IOException {
        System.err.println("[FrontControllerServlet] Erreur 400 : " + message);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<h1>400</h1><p>" + message + "</p>");
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
