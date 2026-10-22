package test.controller;

import java.util.Arrays;
import java.util.List;

import mg.framework.annotation.HttpMethod;
import mg.framework.annotation.Param;
import mg.framework.annotation.RestController;
import mg.framework.annotation.Url;

/**
 * Demo Sprint 6 : controller annote @RestController -> toutes ses methodes
 * repondent en JSON (pas de forward JSP).
 */
@RestController
public class ApiJoueurController {

    /**
     * Retourne directement un objet Java (ici une List<String>).
     * Le FrontControllerServlet le convertit en JSON via JsonUtils
     * car ce n'est pas un String.
     * -> GET /api/joueurs
     */
    @Url(value = "/api/joueurs", method = HttpMethod.GET)
    public List<String> joueurs() {
        return Arrays.asList("Fenosoa", "Jimmy", "Rija", "Mamy", "Mika");
    }

    /**
     * Retourne un String deja au format JSON : renvoye tel quel,
     * sans passer par JsonUtils.
     * -> GET /api/ping
     */
    @Url(value = "/api/ping", method = HttpMethod.GET)
    public String ping() {
        return "{\"status\":\"ok\"}";
    }

    /**
     * Demontre @Param + JSON : retourne un petit objet construit a
     * partir d'un parametre de requete.
     * -> GET /api/joueur?nom=Rina
     */
    @Url(value = "/api/joueur", method = HttpMethod.GET)
    public JoueurDto joueur(@Param("nom") String nom) {
        return new JoueurDto(nom, true);
    }

    /** Petit POJO pour montrer la conversion automatique d'un objet en JSON. */
    public static class JoueurDto {
        private String nom;
        private boolean actif;

        public JoueurDto(String nom, boolean actif) {
            this.nom = nom;
            this.actif = actif;
        }
    }
}
