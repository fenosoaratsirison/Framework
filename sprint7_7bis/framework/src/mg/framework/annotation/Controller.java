package mg.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sprint 1 : marque une classe comme etant un Controller du framework.
 * Le ClassScanner recherche toutes les classes portant cette annotation
 * lors du demarrage de l'application (methode init() du FrontControllerServlet).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller {
}
