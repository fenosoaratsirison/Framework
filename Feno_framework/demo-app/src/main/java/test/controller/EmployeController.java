package test.controller;

import mg.framework.annotation.Controller;
import mg.framework.annotation.HttpMethod;
import mg.framework.annotation.Param;
import mg.framework.annotation.Url;
import mg.framework.mvc.ModelView;

/**
 * Demo Sprint 3 + 4 : la meme URL "/employe" repond differemment selon le
 * verbe HTTP :
 *  - GET  -> affiche le formulaire (vue "employeForm")
 *  - POST -> recupere "nom" et "age" via @Param (avec conversion de type),
 *            et affiche le resultat (vue "employeResultat")
 *
 * Cette distinction GET/POST sur une meme URL, combinee a l'injection de
 * parametres typee, est une fonctionnalite que le framework de Feno propose
 * en plus de celui de MrNaina.
 */
@Controller
public class EmployeController {

    @Url(value = "/employe", method = HttpMethod.GET)
    public String formulaire() {
        return "employeForm";
    }

    @Url(value = "/employe", method = HttpMethod.POST)
    public ModelView enregistrer(@Param("nom") String nom, @Param("age") int age) {
        ModelView mv = new ModelView("employeResultat");
        mv.addObject("nom", nom);
        mv.addObject("age", age);
        return mv;
    }
}
