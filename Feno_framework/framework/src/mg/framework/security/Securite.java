package mg.framework.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import mg.framework.annotation.Authenticated;
import mg.framework.annotation.Role;

import java.lang.reflect.Method;

/**
 * Sprint 10 / 10 bis : verifications de securite faites par le FrontControllerServlet
 * avant d'appeler la methode d'un Controller.
 */
public class Securite {

    /**
     * Sprint 10 / 10 bis : la methode est protegee si elle (ou sa classe) porte
     * @Authenticated ou @Role. (@Role implique @Authenticated.)
     */
    public static boolean estProtege(Method methode, Class<?> controller) {
        return methode.isAnnotationPresent(Authenticated.class)
                || methode.isAnnotationPresent(Role.class)
                || controller.isAnnotationPresent(Authenticated.class)
                || controller.isAnnotationPresent(Role.class);
    }

    /**
     * Sprint 10 bis : roles exiges pour appeler cette methode, ou null si aucun role
     * particulier n'est exige (simple connexion, ou route publique).
     *
     * Regle "la methode gagne" : si la methode porte @Role ou @Authenticated, c'est
     * elle qui decide et la classe est ignoree. Sinon on regarde la classe.
     */
    public static String[] rolesAutorises(Method methode, Class<?> controller) {
        Role roleMethode = methode.getAnnotation(Role.class);
        if (roleMethode != null) {
            return roleMethode.value();
        }
        if (methode.isAnnotationPresent(Authenticated.class)) {
            return null; // la methode demande seulement une connexion : elle l'emporte sur la classe
        }
        Role roleClasse = controller.getAnnotation(Role.class);
        return roleClasse != null ? roleClasse.value() : null;
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

    /**
     * Sprint 10 bis : le role de l'utilisateur (String stocke en session sous la cle donnee)
     * est-il dans la liste autorisee ? Comparaison exacte (majuscules/minuscules comptent).
     * Sans role en session, ou sans liste, la reponse est non (par defaut on refuse).
     *
     * Plus tard (plusieurs roles par utilisateur) : stocker une List en session et
     * tester si elle contient au moins un des roles autorises.
     */
    public static boolean aUnRoleAutorise(HttpServletRequest request, String cleRole, String[] rolesAutorises) {
        if (rolesAutorises == null) {
            return false;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object role = session.getAttribute(cleRole);
        if (!(role instanceof String)) {
            return false;
        }
        for (String autorise : rolesAutorises) {
            if (autorise.equals(role)) {
                return true;
            }
        }
        return false;
    }
}
