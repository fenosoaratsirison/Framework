package mg.framework.exception;

/**
 * Sprint 7 / 7 bis : exception levee quand le framework n'arrive pas a lier
 * les donnees de la requete HTTP aux parametres de la methode du Controller.
 *
 * Cas possibles :
 *  - un parametre obligatoire (@Param(required = true)) est absent ;
 *  - une valeur est invalide (ex : "abc" pour un int) ;
 *  - un objet ne peut pas etre instancie (pas de constructeur sans argument).
 *
 * C'est une RuntimeException : on n'est pas oblige de l'attraper partout.
 * Le FrontControllerServlet l'attrape et repond avec une erreur 400.
 */
public class BindingException extends RuntimeException {

    public BindingException(String message) {
        super(message);
    }

    public BindingException(String message, Throwable cause) {
        super(message, cause);
    }
}
