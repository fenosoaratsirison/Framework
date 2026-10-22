package test.controller;

import mg.framework.annotation.Controller;
import mg.framework.annotation.HttpMethod;
import mg.framework.annotation.Param;
import mg.framework.annotation.Url;
import mg.framework.mvc.ModelView;
import test.model.Employe;

/**
 * Demo des Sprints 3, 4, 7 et 7 bis.
 *
 * Sprint 3 + 4 : meme URL "/employe", GET = formulaire, POST = traitement (@Param).
 * Sprint 7     : /employe/save       -> types simples (nom, age, salaire),
 *                                       sans @Param (nom Java = nom du champ HTML).
 * Sprint 7 bis : /employe/save-objet -> un objet Employe rempli automatiquement.
 */
@Controller
public class EmployeController {

    // ----- Sprint 3 + 4 -----

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

    // ----- Sprint 7 : types simples -----

    @Url(value = "/employe/formulaire", method = HttpMethod.GET)
    public String formulaireSimple() {
        return "formulaire";
    }

    // Pas de @Param : le framework utilise les noms nom, age, salaire
    // (le projet doit etre compile avec javac -parameters).
    @Url(value = "/employe/save", method = HttpMethod.POST)
    public ModelView save(String nom, int age, double salaire) {
        ModelView mv = new ModelView("employeSaveResultat");
        mv.addObject("titre", "Sprint 7 : binding de types simples");
        mv.addObject("nom", nom);
        mv.addObject("age", age);
        mv.addObject("salaire", salaire);
        return mv;
    }

    // ----- Sprint 7 bis : objet complexe -----

    @Url(value = "/employe/formulaire-objet", method = HttpMethod.GET)
    public String formulaireObjet() {
        return "formulaireObjet";
    }

    @Url(value = "/employe/save-objet", method = HttpMethod.POST)
    public ModelView saveObjet(Employe employe) {
        ModelView mv = new ModelView("employeSaveResultat");
        mv.addObject("titre", "Sprint 7 bis : binding d'un objet Employe");
        mv.addObject("nom", employe.getNom());
        mv.addObject("age", employe.getAge());
        mv.addObject("salaire", employe.getSalaire());
        return mv;
    }
}
