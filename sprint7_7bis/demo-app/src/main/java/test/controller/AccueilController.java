package test.controller;

import mg.framework.annotation.Controller;
import mg.framework.annotation.HttpMethod;
import mg.framework.annotation.Url;

/**
 * Demo Sprint 3 : une methode Controller peut simplement retourner le nom
 * d'une vue (String), sans donnees a transmettre.
 */
@Controller
public class AccueilController {

    @Url(value = "/accueil", method = HttpMethod.GET)
    public String accueil() {
        return "accueil";
    }
}
