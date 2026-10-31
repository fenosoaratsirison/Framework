package mg.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sprint 10 : l'URL (ou toutes les URL d'un Controller) n'est accessible
 * qu'a un utilisateur connecte.
 *
 * Un utilisateur est "connecte" quand la session contient la cle definie dans
 * web.xml par le parametre "auth-attribute" (ex : "utilisateurConnecte").
 * C'est le Controller de connexion qui met cette cle dans la session
 * (avec MaSession.setAttribute) et qui la supprime a la deconnexion.
 *
 * Si l'utilisateur n'est pas connecte :
 *  - Controller classique : redirection vers "login-url" (web.xml),
 *    ou erreur 401 si "login-url" n'est pas defini ;
 *  - @RestController : erreur 401 en JSON (une API ne redirige pas).
 *
 * Exemples :
 *   @Controller
 *   @Authenticated                      // protege toutes les methodes de la classe
 *   public class ProfilController { ... }
 *
 *   @Authenticated                      // protege seulement cette methode
 *   @Url("/api/secret")
 *   public String secret() { ... }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Authenticated {
}
