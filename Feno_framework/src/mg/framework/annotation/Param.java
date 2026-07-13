package mg.framework.annotation;
 
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
/**
 * Sprint 4 : permet de recuperer un parametre de la requete HTTP
 * (request.getParameter) directement comme argument d'une methode Controller.
 *
 * Exemple :
 *   @Url("/employe/detail")
 *   public String detail(@Param("id") int id) { ... }
 *
 * Le FrontControllerServlet lit request.getParameter(value()) et convertit
 * automatiquement vers le type declare du parametre de la methode.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
    String value();
}