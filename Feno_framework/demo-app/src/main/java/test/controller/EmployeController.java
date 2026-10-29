package test.controller;

import mg.framework.annotation.Controller;
import mg.framework.annotation.HttpMethod;
import mg.framework.annotation.Param;
import mg.framework.annotation.Url;
import mg.framework.mvc.ModelView;
import mg.framework.upload.MapFile;
import test.model.Employe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Demo des Sprints 3, 4, 7 et 7 bis.
 *
 * Sprint 3 + 4 : meme URL "/employe", GET = formulaire, POST = traitement (@Param).
 * Sprint 7     : /employe/save       -> types simples (nom, age, salaire),
 *                                       sans @Param (nom Java = nom du champ HTML).
 * Sprint 7 bis : /employe/save-objet -> un objet Employe rempli automatiquement.
 * Sprint 8     : /employe/upload     -> Employe avec 2 fichiers (recto, verso),
 *                 /employe/upload-map -> Map<String, byte[]> avec tous les fichiers.
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

    // ----- Sprint 8 : upload de fichiers -----

    @Url(value = "/employe/formulaire-upload", method = HttpMethod.GET)
    public String formulaireUpload() {
        return "formulaireUpload";
    }

    // Les attributs recto et verso (type MapFile) sont remplis par le framework.
    @Url(value = "/employe/upload", method = HttpMethod.POST)
    public ModelView upload(Employe employe) {
        List<String> lignes = new ArrayList<>();
        lignes.add("Nom : " + employe.getNom());
        lignes.add(decrireFichier("Recto", employe.getRecto()));
        lignes.add(decrireFichier("Verso", employe.getVerso()));

        ModelView mv = new ModelView("uploadResultat");
        mv.addObject("titre", "Sprint 8 : upload dans un objet Employe");
        mv.addObject("lignes", lignes);
        return mv;
    }

    // Ici, la methode recoit directement tous les fichiers : cle = nom du champ HTML.
    @Url(value = "/employe/upload-map", method = HttpMethod.POST)
    public ModelView uploadMap(Map<String, byte[]> fichiers) {
        List<String> lignes = new ArrayList<>();
        for (Map.Entry<String, byte[]> entree : fichiers.entrySet()) {
            lignes.add("Champ '" + entree.getKey() + "' : " + entree.getValue().length + " octets");
        }
        if (lignes.isEmpty()) {
            lignes.add("Aucun fichier recu.");
        }

        ModelView mv = new ModelView("uploadResultat");
        mv.addObject("titre", "Sprint 8 : upload dans une Map<String, byte[]>");
        mv.addObject("lignes", lignes);
        return mv;
    }

    private String decrireFichier(String titre, MapFile fichier) {
        if (fichier == null) {
            return titre + " : aucun fichier";
        }
        // sauvegarder() ecrit le fichier dans le dossier "upload-dir" de web.xml
        String chemin = fichier.sauvegarder();
        return titre + " : " + fichier.getNomComplet() + " (extension " + fichier.getExtension()
                + ", " + fichier.getTaille() + " octets) enregistre dans " + chemin;
    }
}
