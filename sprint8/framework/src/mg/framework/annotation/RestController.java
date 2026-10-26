package mg.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sprint 6 : marque un Controller comme etant un Controller "API" qui
 * repond en JSON au lieu de faire un forward vers une vue JSP.
 *
 * Fonctionne comme @Controller (requis en plus, ou a sa place selon le
 * choix du projet) mais indique au FrontControllerServlet de serialiser
 * directement le resultat de la methode en JSON :
 *  - si la methode retourne un String, il est considere comme deja du
 *    JSON et renvoye tel quel ;
 *  - sinon, l'objet retourne est converti en JSON via JsonUtils.toJson(...).
 *
 * Exemple :
 *   @RestController
 *   public class ApiJoueurController {
 *
 *       @Url("/api/joueurs")
 *       public List<String> joueurs() {
 *           return Arrays.asList("Rina", "Tojo");
 *       }
 *   }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RestController {
}
