package mg.framework.mvc;

import java.util.HashMap;
import java.util.Map;

/**
 * Sprint 5 : objet retourne par une methode Controller quand elle a besoin
 * d'envoyer des donnees a la vue (JSP), en plus de choisir la vue elle-meme.
 *
 * Exemple dans un Controller :
 *
 *   @Url("/employe/detail")
 *   public ModelView detail(@Param("id") int id) {
 *       ModelView mv = new ModelView("employeDetail");
 *       mv.addObject("nom", "Rakoto");
 *       mv.addObject("id", id);
 *       return mv;
 *   }
 *
 * Le FrontControllerServlet fait ensuite, pour chaque entree :
 *   request.setAttribute(cle, valeur);
 * puis un forward vers la vue. Dans la JSP, on affiche avec l'EL : ${nom}
 */
public class ModelView {

    private String vue;
    private final Map<String, Object> data = new HashMap<>();

    public ModelView() {
    }

    public ModelView(String vue) {
        this.vue = vue;
    }

    public ModelView addObject(String cle, Object valeur) {
        data.put(cle, valeur);
        return this;
    }

    public String getVue() {
        return vue;
    }

    public void setVue(String vue) {
        this.vue = vue;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
