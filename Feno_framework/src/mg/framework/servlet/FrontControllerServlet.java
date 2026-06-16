package mg.framework.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "FrontControllerServlet", urlPatterns = {"/"})
public class FrontControllerServlet extends HttpServlet {

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
        
        // Récupérer l'URL et le chemin
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        String requestPath = path.substring(contextPath.length());
        
        response.setContentType("text/html;charset=UTF-8");
        
        // Routage en fonction de l'URL
        if (requestPath.equals("/") || requestPath.isEmpty()) {
            response.getWriter().println("<h1>Accueil</h1>");
            
        } else if (requestPath.equals("/home")) {
            response.getWriter().println("<h1>Page Accueil</h1>");
            
        } else if (requestPath.equals("/about")) {
            response.getWriter().println("<h1>À Propos</h1>");
            
        } else if (requestPath.equals("/contact")) {
            response.getWriter().println("<h1>Contactez-nous</h1>");
            
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("<h1>Erreur 404</h1>");
        }
    }
}
