package mg.framework.annotation;
 
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
/**
 * Sprint 2 : associe une URL a une methode d'un Controller.
 *
 * Exemple :
 *   @Url("/home")
 *   public String index() { ... }
 *
 * Le FrontControllerServlet lit cette annotation au demarrage pour construire
 * la table de routing (URL -> Method).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Url {
    String value();
}