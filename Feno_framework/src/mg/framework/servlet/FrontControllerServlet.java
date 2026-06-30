package mg.framework.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mg.framework.annotation.Controller;
import mg.framework.scanner.ClassScanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Front Controller : point d'entrée unique de toutes les requêtes.
 *
 * Sprint 0 : intercepte toutes les requêtes (GET/POST) et affiche l'URL appelée.
 * Sprint 1 : au démarrage (init), scanne le package indiqué dans web.xml
 *            et mémorise la liste des classes annotées avec @Controller.
 *
 * NOTE : il ne faut PAS annoter cette classe avec @WebServlet en plus de la
 * déclaration dans web.xml, sinon Tomcat lève une erreur de double déclaration
 * du même servlet. On garde uniquement la déclaration via web.xml afin de
 * pouvoir lui passer le paramètre "controller-package".
 */
public class FrontControllerServlet extends HttpServlet {

    /** Liste des classes détectées comme étant des Controllers (annotées @Controller) */
    private List<Class<?>> controllers = new ArrayList<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // 1. Récupérer le package à scanner depuis web.xml (init-param du servlet)
        String controllerPackage = config.getInitParameter("controller-package");

        // Si rien n'est précisé dans web.xml, on peut essayer un paramètre global (context-param)
        if (controllerPackage == null || controllerPackage.isEmpty()) {
            controllerPackage = config.getServletContext().getInitParameter("controller-package");
        }

        if (controllerPackage == null || controllerPackage.isEmpty()) {
            System.err.println("[FrontControllerServlet] Aucun 'controller-package' défini dans web.xml. "
                    + "Aucun controller ne sera scanné.");
            return;
        }

        System.out.println("[FrontControllerServlet] Scan du package : " + controllerPackage);

        // 2. Scanner toutes les classes du package
        List<Class<?>> classesTrouvees = ClassScanner.scanPackage(controllerPackage);

        // 3. Ne garder que celles annotées avec @Controller
        for (Class<?> clazz : classesTrouvees) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                controllers.add(clazz);
                System.out.println("[FrontControllerServlet] Controller détecté : " + clazz.getName());
            }
        }

        System.out.println("[FrontControllerServlet] " + controllers.size()
                + " controller(s) détecté(s) au total.");
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

        // Sprint 0 : on affiche l'URL appelée
        System.out.println("[FrontControllerServlet] " + method + " -> " + requestPath);

        response.setContentType("text/html;charset=UTF-8");

        // Affichage simple de vérification pour le Sprint 1 :
        // liste des controllers détectés au démarrage.
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Front Controller</h1>");
        sb.append("<p>Méthode : ").append(method).append("</p>");
        sb.append("<p>URL demandée : ").append(requestPath).append("</p>");
        sb.append("<h2>Controllers détectés (").append(controllers.size()).append(")</h2>");
        sb.append("<ul>");
        for (Class<?> c : controllers) {
            sb.append("<li>").append(c.getName()).append("</li>");
        }
        sb.append("</ul>");

        response.getWriter().println(sb.toString());
    }
}
