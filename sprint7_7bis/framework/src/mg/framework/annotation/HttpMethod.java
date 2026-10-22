package mg.framework.annotation;

/**
 * Sprint 3 : verbes HTTP supportes par le framework. Permet a une meme
 * URL d'etre associee a deux methodes Controller differentes selon que
 * la requete est un GET ou un POST (ex : afficher un formulaire en GET,
 * le traiter en POST).
 */
public enum HttpMethod {
    GET,
    POST
}
