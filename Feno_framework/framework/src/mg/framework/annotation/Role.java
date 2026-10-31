package mg.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sprint 10 bis : l'URL (ou toutes les URL d'un Controller) n'est accessible
 * qu'a un utilisateur connecte DONT LE ROLE est dans la liste donnee.
 *
 * Le role de l'utilisateur est lu dans la session, sous la cle definie dans
 * web.xml par le parametre "role-attribute" (ex : "role"). C'est un String.
 * C'est le Controller de connexion qui le met en session (avec MaSession.setAttribute),
 * a partir d'une source fiable cote serveur (base de donnees...), JAMAIS a partir
 * d'un champ de formulaire.
 *
 * Regles :
 *  - @Role implique @Authenticated : inutile de mettre les deux.
 *  - Pas connecte                    -> meme reponse qu'au Sprint 10 (redirection vers
 *                                       "login-url", ou 401 ; 401 JSON pour un @RestController).
 *  - Connecte mais mauvais role      -> erreur 403 (Forbidden). Pour un @RestController,
 *                                       la reponse est en JSON.
 *  - Annotation sur la classe ET sur la methode : la methode gagne (regle la plus precise).
 *    Une methode annotee @Authenticated dans une classe @Role("admin") est donc ouverte
 *    a tout utilisateur connecte.
 *
 * Exemples :
 *   @Controller
 *   @Role("admin")                          // toutes les methodes : admin seulement
 *   public class AdminController { ... }
 *
 *   @Role({"admin", "manager"})             // seulement cette methode : admin OU manager
 *   @Url("/rapports")
 *   public String rapports() { ... }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Role {

    /** Roles autorises (au moins un doit correspondre au role de l'utilisateur). */
    String[] value();
}
