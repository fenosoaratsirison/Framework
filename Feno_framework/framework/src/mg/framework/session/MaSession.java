package mg.framework.session;

import jakarta.servlet.http.HttpSession;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Sprint 9 : gestion de la session.
 *
 * Le developpeur n'utilise pas HttpSession directement : il ajoute simplement
 * un parametre MaSession a sa methode de Controller.
 *
 *   @Url(value = "/session/login", method = HttpMethod.POST)
 *   public String login(String nom, MaSession session) {
 *       session.setAttribute("utilisateur", nom);
 *       return "accueil";
 *   }
 *
 * Fonctionnement (dans le FrontControllerServlet) :
 *  1. avant l'appel de la methode : MaSession.from(request.getSession(true))
 *     fait une COPIE du contenu de la vraie HttpSession ;
 *  2. la methode lit/modifie la copie (getAttribute, setAttribute, removeAttribute) ;
 *  3. apres l'appel : flushTo(...) recopie la copie dans la vraie HttpSession.
 *
 * Pourquoi initialKeys ? Pour savoir quelles cles existaient au depart. Si une de
 * ces cles n'est plus dans la copie, c'est que la methode l'a supprimee : on la
 * supprime aussi de la vraie session.
 */
public class MaSession {

    private String id;
    private Map<String, Object> attributes = new HashMap<>();
    private Set<String> initialKeys = new HashSet<>();

    // Constructeur prive : on cree une MaSession seulement avec from(...)
    private MaSession() {
    }

    /** Cree une MaSession qui est une copie de la HttpSession donnee. */
    public static MaSession from(HttpSession httpSession) {
        MaSession maSession = new MaSession();
        maSession.id = httpSession.getId();

        Enumeration<String> noms = httpSession.getAttributeNames();
        while (noms.hasMoreElements()) {
            String nom = noms.nextElement();
            maSession.attributes.put(nom, httpSession.getAttribute(nom));
            maSession.initialKeys.add(nom);
        }

        return maSession;
    }

    /** Recopie le contenu de cette MaSession dans la vraie HttpSession. */
    public void flushTo(HttpSession httpSession) {

        // 1. Supprimer de la vraie session les cles qui existaient au depart
        //    mais qui ne sont plus dans la copie.
        for (String cle : initialKeys) {
            if (!attributes.containsKey(cle)) {
                httpSession.removeAttribute(cle);
            }
        }

        // 2. Ajouter / mettre a jour tous les attributs de la copie.
        for (Map.Entry<String, Object> entree : attributes.entrySet()) {
            httpSession.setAttribute(entree.getKey(), entree.getValue());
        }
    }

    // ----- Methodes pour le developpeur -----

    public String getId() {
        return id;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }
}
