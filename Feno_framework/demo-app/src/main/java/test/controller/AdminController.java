package test.controller;

import mg.framework.annotation.Authenticated;
import mg.framework.annotation.Controller;
import mg.framework.annotation.HttpMethod;
import mg.framework.annotation.Role;
import mg.framework.annotation.Url;
import mg.framework.mvc.ModelView;
import mg.framework.session.MaSession;

/**
 * Demo Sprint 10 bis : @Role("admin") sur la CLASSE -> toutes les methodes exigent
 * le role "admin" (pas besoin de @Authenticated en plus).
 *
 *  - non connecte         -> redirection vers /login (comme au Sprint 10)
 *  - connecte, role "user" -> erreur 403
 *  - connecte, role "admin" -> page affichee
 */
@Controller
@Role("admin")
public class AdminController {

    /** GET /admin : herite de @Role("admin") de la classe. */
    @Url(value = "/admin", method = HttpMethod.GET)
    public ModelView admin(MaSession session) {
        ModelView mv = new ModelView("admin");
        mv.addObject("nom", session.getAttribute(AuthController.CLE_AUTH));
        return mv;
    }

    /**
     * GET /admin/ouvert : la methode gagne sur la classe. Ici @Authenticated (sur la
     * methode) l'emporte sur @Role("admin") (sur la classe) : tout utilisateur
     * connecte peut entrer, meme avec le role "user".
     */
    @Authenticated
    @Url(value = "/admin/ouvert", method = HttpMethod.GET)
    public String ouvert() {
        return "adminOuvert";
    }
}
