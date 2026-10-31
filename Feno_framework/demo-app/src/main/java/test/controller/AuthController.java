package test.controller;

import jakarta.servlet.http.HttpServletRequest;

import mg.framework.annotation.Controller;
import mg.framework.annotation.HttpMethod;
import mg.framework.annotation.Url;
import mg.framework.mvc.ModelView;
import mg.framework.session.MaSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Demo Sprint 10 : connexion / deconnexion.
 *
 * Ce Controller est PUBLIC (pas de @Authenticated) : il faut bien pouvoir
 * se connecter avant d'etre connecte.
 *
 * Le framework considere qu'un utilisateur est connecte quand la session contient
 * la cle CLE_AUTH. Cette cle doit etre la meme que le parametre "auth-attribute"
 * du web.xml.
 *
 * ATTENTION : les utilisateurs et mots de passe sont ecrits en dur ici uniquement
 * pour la demo. Dans une vraie application, ils viennent de la base de donnees
 * et les mots de passe sont haches.
 */
@Controller
public class AuthController {

    public static final String CLE_AUTH = "utilisateurConnecte";

    private static final Map<String, String> UTILISATEURS = new HashMap<>();

    static {
        UTILISATEURS.put("rakoto", "1234");
        UTILISATEURS.put("rabe", "abcd");
    }

    @Url(value = "/login", method = HttpMethod.GET)
    public String formulaire() {
        return "login";
    }

    @Url(value = "/login", method = HttpMethod.POST)
    public Object connecter(String nom, String motDePasse, MaSession session, HttpServletRequest request) {

        String motDePasseAttendu = UTILISATEURS.get(nom);

        if (motDePasseAttendu == null || !motDePasseAttendu.equals(motDePasse)) {
            ModelView mv = new ModelView("login");
            mv.addObject("erreur", "Nom ou mot de passe incorrect.");
            return mv;
        }

        // Securite : on change l'identifiant de session apres la connexion
        // (evite qu'un identifiant de session connu d'avance serve a se faire passer pour l'utilisateur).
        request.changeSessionId();

        session.setAttribute(CLE_AUTH, nom);
        return "redirect:/profil";
    }

    @Url(value = "/logout", method = HttpMethod.POST)
    public String deconnecter(MaSession session) {
        session.removeAttribute(CLE_AUTH);
        return "redirect:/login";
    }
}
