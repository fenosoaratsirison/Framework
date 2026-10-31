package test.controller;

import mg.framework.annotation.Authenticated;
import mg.framework.annotation.Controller;
import mg.framework.annotation.HttpMethod;
import mg.framework.annotation.Url;
import mg.framework.mvc.ModelView;
import mg.framework.session.MaSession;

/**
 * Demo Sprint 10 : @Authenticated sur la CLASSE -> toutes les methodes de ce
 * Controller exigent un utilisateur connecte. Sinon, le framework redirige
 * vers "login-url" (web.xml) sans meme appeler la methode.
 */
@Controller
@Authenticated
public class ProfilController {

    @Url(value = "/profil", method = HttpMethod.GET)
    public ModelView profil(MaSession session) {
        ModelView mv = new ModelView("profil");
        mv.addObject("nom", session.getAttribute(AuthController.CLE_AUTH));
        return mv;
    }
}
