package mg.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation utilisée pour marquer une classe comme étant un Controller
 * du framework. Le ClassScanner recherchera toutes les classes portant
 * cette annotation lors du démarrage de l'application.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller {
}
