package mg.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sprint 2 : associe une URL a une methode d'un Controller.
 * Sprint 3 : permet en plus de preciser le(s) verbe(s) HTTP acceptes
 * (GET et/ou POST). Par defaut, la route repond aux deux, comme avant ;
 * on peut desormais restreindre pour separer un formulaire (GET) de son
 * traitement (POST) sur la meme URL.
 *
 * Exemple :
 *   @Url(value = "/employe/liste", method = HttpMethod.GET)
 *   public ModelView liste() { ... }
 *
 *   @Url(value = "/employe/ajouter", method = HttpMethod.POST)
 *   public String ajouter(@Param("nom") String nom) { ... }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Url {
    String value();

    HttpMethod[] method() default { HttpMethod.GET, HttpMethod.POST };
}
