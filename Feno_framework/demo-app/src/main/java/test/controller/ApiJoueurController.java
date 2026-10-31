package test.controller;

import java.util.Arrays;
import java.util.List;

import mg.framework.annotation.Authenticated;
import mg.framework.annotation.HttpMethod;
import mg.framework.annotation.Param;
import mg.framework.annotation.RestController;
import mg.framework.annotation.Role;
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

    /**
     * Demo Sprint 10 : @Authenticated sur une METHODE seulement. Les autres methodes
     * de ce Controller restent publiques. Sans connexion : erreur 401 en JSON
     * (c'est un @RestController, donc pas de redirection).
     * -> GET /api/secret
     */
    @Authenticated
    @Url(value = "/api/secret", method = HttpMethod.GET)
    public String secret() {
        return "{\"message\":\"Donnees reservees aux utilisateurs connectes\"}";
    }

    /**
     * Demo Sprint 10 bis : @Role sur une METHODE d'un @RestController. Plusieurs roles possibles.
     * Pas connecte -> 401 JSON ; connecte avec un autre role -> 403 JSON.
     * -> GET /api/admin
     */
    @Role({ "admin", "manager" })
    @Url(value = "/api/admin", method = HttpMethod.GET)
    public String admin() {
        return "{\"message\":\"Donnees reservees aux admins et managers\"}";
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
