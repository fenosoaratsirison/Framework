package test.controller;

import mg.framework.annotation.Controller;
import mg.framework.annotation.HttpMethod;
import mg.framework.annotation.Url;
import mg.framework.session.MaSession;

/**
 * Demo Sprint 9 : gestion de la session avec MaSession.
 *
 * La methode recoit simplement un parametre MaSession : pas besoin de
 * HttpServletRequest ni de HttpSession. A la fin de la methode, le framework
 * recopie les changements dans la vraie session.
 *
 * La vue "session.jsp" lit la vraie session avec ${sessionScope.xxx} :
 * si le nom s'affiche, c'est que la recopie a bien fonctionne.
 */
@Controller
public class SessionController {

    // Affiche la page et compte les visites (la valeur est gardee dans la session)
    @Url(value = "/session", method = HttpMethod.GET)
    public String afficher(MaSession session) {
        int visites = 0;
        Object ancienneValeur = session.getAttribute("visites");
        if (ancienneValeur != null) {
            visites = (Integer) ancienneValeur;
        }
        session.setAttribute("visites", visites + 1);

        return "session";
    }

    // Connexion : on garde le nom de l'utilisateur dans la session
    @Url(value = "/session/login", method = HttpMethod.POST)
    public String login(String nom, MaSession session) {
        session.setAttribute("utilisateur", nom);
        return "session";
    }

    // Deconnexion : on supprime le nom de la session
    @Url(value = "/session/logout", method = HttpMethod.POST)
    public String logout(MaSession session) {
        session.removeAttribute("utilisateur");
        return "session";
    }
}
