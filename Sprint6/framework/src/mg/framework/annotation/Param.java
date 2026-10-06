package mg.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sprint 4 : permet de recuperer un parametre de la requete HTTP
 * (request.getParameter) directement comme argument d'une methode Controller,
 * avec conversion automatique vers le type declare (String, int, long,
 * double, boolean...).
 *
 * Exemple :
 *   @Url("/employe/detail")
 *   public ModelView detail(@Param("id") int id) { ... }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
    String value();
}
