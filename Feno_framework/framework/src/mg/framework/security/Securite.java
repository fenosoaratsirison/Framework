package mg.framework.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import mg.framework.annotation.Authenticated;

import java.lang.reflect.Method;

/**
 * Sprint 10 : verifications de securite faites par le FrontControllerServlet
 * avant d'appeler la methode d'un Controller.
 */
public class Securite {

    /**
     * La methode est protegee si elle porte @Authenticated,
     * ou si la classe du Controller porte @Authenticated.
     */
    public static boolean estProtege(Method methode, Class<?> controller) {
        return methode.isAnnotationPresent(Authenticated.class)
                || controller.isAnnotationPresent(Authenticated.class);
    }

    /**
     * L'utilisateur est connecte si la session existe et contient la cle donnee.
     * On utilise getSession(false) : on ne cree pas de session pour un simple visiteur.
     */
    public static boolean estAuthentifie(HttpServletRequest request, String cleAuthentification) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        return session.getAttribute(cleAuthentification) != null;
    }
}
