package mg.framework.routing;

import java.lang.reflect.Method;

/**
 * Sprint 3 : represente une route resolue, c'est-a-dire l'association entre
 * une URL/verbe HTTP et le couple (classe Controller, methode Java) a invoquer.
 */
public class Route {

    private final Class<?> controller;
    private final Method method;

    public Route(Class<?> controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public Class<?> getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }
}
