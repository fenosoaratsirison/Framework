package mg.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sprint 4 : permet de recuperer un parametre de la requete HTTP
 * (request.getParameter) directement comme argument d'une methode Controller.
 *
 * Sprint 7 : deux ameliorations
 *  - l'annotation devient optionnelle : sans @Param, le framework utilise le
 *    nom du parametre Java (il faut compiler avec l'option javac -parameters) ;
 *  - on peut rendre un parametre obligatoire avec required = true.
 *    S'il est absent, le framework leve une BindingException.
 *
 * Exemples :
 *   public String save(@Param("nom") String n)              // nom HTML = "nom"
 *   public String save(String nom, int age)                 // noms Java = noms HTML
 *   public String save(@Param(value = "nom", required = true) String nom)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
    String value();

    boolean required() default false;
}
