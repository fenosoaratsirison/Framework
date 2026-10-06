package test.controller;

import java.util.Arrays;
import java.util.List;

import mg.framework.annotation.Controller;
import mg.framework.annotation.HttpMethod;
import mg.framework.annotation.Url;
import mg.framework.mvc.ModelView;

/**
 * Demo Sprint 5 : une methode Controller retourne un ModelView pour envoyer
 * des donnees a la vue JSP (equivalent du Controller1 de MrNaina).
 */
@Controller
public class JoueurController {

    @Url(value = "/joueurs", method = HttpMethod.GET)
    public ModelView liste() {
        List<String> joueurs = Arrays.asList("Fenosoa", "Jimmy", "Rija", "Mamy", "Mika");

        ModelView mv = new ModelView("joueurs");
        mv.addObject("joueurs", joueurs);
        return mv;
    }
}
