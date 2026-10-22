package mg.framework.routing;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import mg.framework.annotation.HttpMethod;

/**
 * Sprint 3 : table de routing du framework.
 * Pour chaque URL, on garde un EnumMap<HttpMethod, Route> afin qu'une meme
 * URL puisse avoir un comportement different en GET et en POST
 * (ex : /employe/ajouter en GET affiche le formulaire, en POST l'enregistre).
 */
public class RouteMapping {

    private final Map<String, EnumMap<HttpMethod, Route>> routes = new HashMap<>();

    public void addRoute(String url, HttpMethod httpMethod, Class<?> controller, Method method) {

        EnumMap<HttpMethod, Route> routesPourUrl =
                routes.computeIfAbsent(url, u -> new EnumMap<>(HttpMethod.class));

        if (routesPourUrl.containsKey(httpMethod)) {
            throw new IllegalStateException(
                    "La route " + httpMethod + " " + url + " existe deja (conflit entre "
                            + routesPourUrl.get(httpMethod).getController().getName()
                            + " et " + controller.getName() + ").");
        }

        routesPourUrl.put(httpMethod, new Route(controller, method));
    }

    public Route getRoute(String url, HttpMethod httpMethod) {
        EnumMap<HttpMethod, Route> routesPourUrl = routes.get(url);
        if (routesPourUrl == null) {
            return null;
        }
        return routesPourUrl.get(httpMethod);
    }

    public Map<String, EnumMap<HttpMethod, Route>> getRoutes() {
        return routes;
    }
}
