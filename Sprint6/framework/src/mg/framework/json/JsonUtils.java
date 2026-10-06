package mg.framework.json;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * Sprint 6 : convertisseur Java -> JSON ecrit "a la main" (sans
 * bibliotheque externe type Gson/Jackson), par reflection.
 *
 * Types geres :
 *  - null
 *  - String, caracteres (echappes correctement)
 *  - nombres (int, long, double, float, short, byte) et leurs wrappers
 *  - boolean / Boolean
 *  - Collection (List, Set...) -> tableau JSON
 *  - Map -> objet JSON
 *  - tableaux Java (Object[], int[]...) -> tableau JSON
 *  - objets "simples" (POJO) -> objet JSON, via les champs declares
 *    (getDeclaredFields), y compris les champs herites des classes meres
 *
 * Volontairement simple : pas de gestion des references circulaires, ni
 * des annotations de type @JsonIgnore. Suffisant pour le niveau du projet.
 */
public final class JsonUtils {

    private JsonUtils() {
    }

    public static String toJson(Object valeur) {
        StringBuilder sb = new StringBuilder();
        ecrireValeur(valeur, sb);
        return sb.toString();
    }

    private static void ecrireValeur(Object valeur, StringBuilder sb) {

        if (valeur == null) {
            sb.append("null");

        } else if (valeur instanceof String) {
            ecrireString((String) valeur, sb);

        } else if (valeur instanceof Character) {
            ecrireString(valeur.toString(), sb);

        } else if (valeur instanceof Number || valeur instanceof Boolean) {
            sb.append(valeur.toString());

        } else if (valeur instanceof Map) {
            ecrireMap((Map<?, ?>) valeur, sb);

        } else if (valeur instanceof Collection) {
            ecrireCollection((Collection<?>) valeur, sb);

        } else if (valeur.getClass().isArray()) {
            ecrireTableau(valeur, sb);

        } else if (estTypeSimpleOuEnum(valeur.getClass())) {
            // fallback : type non reconnu explicitement mais "simple" (ex: enum)
            ecrireString(valeur.toString(), sb);

        } else {
            ecrireObjet(valeur, sb);
        }
    }

    private static void ecrireString(String s, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }

    private static void ecrireCollection(Collection<?> collection, StringBuilder sb) {
        sb.append('[');
        boolean premier = true;
        for (Object element : collection) {
            if (!premier) {
                sb.append(',');
            }
            premier = false;
            ecrireValeur(element, sb);
        }
        sb.append(']');
    }

    private static void ecrireTableau(Object tableau, StringBuilder sb) {
        sb.append('[');
        int longueur = java.lang.reflect.Array.getLength(tableau);
        for (int i = 0; i < longueur; i++) {
            if (i > 0) {
                sb.append(',');
            }
            ecrireValeur(java.lang.reflect.Array.get(tableau, i), sb);
        }
        sb.append(']');
    }

    private static void ecrireMap(Map<?, ?> map, StringBuilder sb) {
        sb.append('{');
        boolean premier = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!premier) {
                sb.append(',');
            }
            premier = false;
            ecrireString(String.valueOf(entry.getKey()), sb);
            sb.append(':');
            ecrireValeur(entry.getValue(), sb);
        }
        sb.append('}');
    }

    private static void ecrireObjet(Object objet, StringBuilder sb) {
        sb.append('{');
        boolean premier = true;

        Class<?> classe = objet.getClass();
        while (classe != null && classe != Object.class) {

            for (Field champ : classe.getDeclaredFields()) {

                if (champ.isSynthetic()) {
                    continue;
                }

                champ.setAccessible(true);

                try {
                    Object valeurChamp = champ.get(objet);

                    if (!premier) {
                        sb.append(',');
                    }
                    premier = false;

                    ecrireString(champ.getName(), sb);
                    sb.append(':');
                    ecrireValeur(valeurChamp, sb);

                } catch (IllegalAccessException e) {
                    // champ illisible, on l'ignore simplement
                }
            }

            classe = classe.getSuperclass();
        }

        sb.append('}');
    }

    private static boolean estTypeSimpleOuEnum(Class<?> classe) {
        return classe.isEnum();
    }
}
