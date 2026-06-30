package test.controller;

import mg.framework.annotation.Controller;

/**
 * Deuxième classe de test, pour vérifier que le scanner détecte
 * bien plusieurs Controllers dans le même package.
 */
@Controller
public class HomeController {

    public String index() {
        return "Page d'accueil";
    }
}
