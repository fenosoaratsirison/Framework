package mg.framework.binding;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import mg.framework.annotation.Param;
import mg.framework.exception.BindingException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

/**
 * Sprint 7 et 7 bis : Data Binding.
 *
 * Role : preparer le tableau d'arguments avec lequel on va appeler la methode
 * du Controller (Method.invoke), a partir des donnees de la requete HTTP
 * (par exemple celles envoyees par un formulaire).
 *
 * Pour chaque parametre de la methode, trois cas :
 *  1. HttpServletRequest / HttpServletResponse -> on les donne directement ;
 *  2. type simple (String, int, double...)     -> Sprint 7 : on lit la valeur
 *     dans la requete avec request.getParameter(nom) puis on la convertit ;
 *  3. autre type (objet : Employe, Personne...) -> Sprint 7 bis : on cree
 *     l'objet et on remplit ses champs avec les parametres de la requete.
 */
public class ParamBinder {

    // ------------------------------------------------------------------
    // 1. Construction du tableau d'arguments
    // ------------------------------------------------------------------

    public static Object[] construireArguments(Method method, HttpServletRequest request,
                                               HttpServletResponse response) {

        Parameter[] parametres = method.getParameters();
        Object[] arguments = new Object[parametres.length];

        for (int i = 0; i < parametres.length; i++) {
            Parameter parametre = parametres[i];
            Class<?> type = parametre.getType();

            if (type.equals(HttpServletRequest.class)) {
                arguments[i] = request;

            } else if (type.equals(HttpServletResponse.class)) {
                arguments[i] = response;

            } else if (estTypeSimple(type)) {
                arguments[i] = lierTypeSimple(parametre, request);

            } else {
                arguments[i] = lierObjet(type, request);
            }
        }

        return arguments;
    }

    // ------------------------------------------------------------------
    // 2. Sprint 7 : types simples
    // ------------------------------------------------------------------

    private static Object lierTypeSimple(Parameter parametre, HttpServletRequest request) {

        // Le nom vient de @Param("...") s'il existe, sinon du nom Java du parametre
        // (necessite l'option de compilation -parameters).
        String nom = parametre.getName();
        boolean obligatoire = false;

        Param annotation = parametre.getAnnotation(Param.class);
        if (annotation != null) {
            nom = annotation.value();
            obligatoire = annotation.required();
        }

        String valeur = request.getParameter(nom);

        if (estAbsente(valeur, parametre.getType())) {
            if (obligatoire) {
                throw new BindingException("Le parametre obligatoire '" + nom + "' est absent.");
            }
            return valeurParDefaut(parametre.getType());
        }

        return convertir(valeur, parametre.getType(), nom);
    }

    /**
     * Une valeur est consideree comme absente si elle est null.
     * Pour les types autres que String, une chaine vide l'est aussi
     * (un champ numerique laisse vide dans un formulaire envoie "").
     */
    private static boolean estAbsente(String valeur, Class<?> type) {
        if (valeur == null) {
            return true;
        }
        if (valeur.isEmpty() && !type.equals(String.class)) {
            return true;
        }
        return false;
    }

    public static boolean estTypeSimple(Class<?> type) {
        return type.equals(String.class)
                || type.equals(int.class) || type.equals(Integer.class)
                || type.equals(long.class) || type.equals(Long.class)
                || type.equals(double.class) || type.equals(Double.class)
                || type.equals(float.class) || type.equals(Float.class)
                || type.equals(boolean.class) || type.equals(Boolean.class)
                || type.equals(short.class) || type.equals(Short.class)
                || type.equals(byte.class) || type.equals(Byte.class)
                || type.equals(char.class) || type.equals(Character.class);
    }

    /**
     * Valeur utilisee quand la requete ne contient rien pour ce parametre :
     * - primitif : 0, false ou '\0' ;
     * - String et wrappers (Integer, Double...) : null.
     */
    public static Object valeurParDefaut(Class<?> type) {
        if (type.equals(int.class)) {
            return 0;
        }
        if (type.equals(long.class)) {
            return 0L;
        }
        if (type.equals(double.class)) {
            return 0.0;
        }
        if (type.equals(float.class)) {
            return 0.0f;
        }
        if (type.equals(boolean.class)) {
            return false;
        }
        if (type.equals(short.class)) {
            return (short) 0;
        }
        if (type.equals(byte.class)) {
            return (byte) 0;
        }
        if (type.equals(char.class)) {
            return '\0';
        }
        return null;
    }

    /**
     * Convertit un texte (valeur recue dans la requete) vers le type voulu.
     * Leve une BindingException si la valeur n'est pas valide.
     */
    public static Object convertir(String valeur, Class<?> type, String nom) {
        try {
            if (type.equals(String.class)) {
                return valeur;
            }
            if (type.equals(int.class) || type.equals(Integer.class)) {
                return Integer.parseInt(valeur.trim());
            }
            if (type.equals(long.class) || type.equals(Long.class)) {
                return Long.parseLong(valeur.trim());
            }
            if (type.equals(double.class) || type.equals(Double.class)) {
                return Double.parseDouble(valeur.trim());
            }
            if (type.equals(float.class) || type.equals(Float.class)) {
                return Float.parseFloat(valeur.trim());
            }
            if (type.equals(short.class) || type.equals(Short.class)) {
                return Short.parseShort(valeur.trim());
            }
            if (type.equals(byte.class) || type.equals(Byte.class)) {
                return Byte.parseByte(valeur.trim());
            }
            if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                // une case a cocher HTML envoie "on" quand elle est cochee
                return valeur.equalsIgnoreCase("true") || valeur.equalsIgnoreCase("on");
            }
            if (type.equals(char.class) || type.equals(Character.class)) {
                return valeur.charAt(0);
            }
        } catch (NumberFormatException e) {
            throw new BindingException("Valeur invalide pour '" + nom + "' : \"" + valeur
                    + "\" n'est pas de type " + type.getSimpleName() + ".", e);
        }

        throw new BindingException("Type non supporte pour '" + nom + "' : " + type.getName());
    }

    // ------------------------------------------------------------------
    // 3. Sprint 7 bis : objet complexe
    // ------------------------------------------------------------------

    /**
     * Cree un objet du type demande et remplit ses champs simples avec les
     * parametres de la requete qui portent le meme nom que les champs.
     */
    public static Object lierObjet(Class<?> type, HttpServletRequest request) {

        Object objet = creerObjet(type);

        // On parcourt la classe, puis sa classe mere, puis la mere de la mere...
        Class<?> classeCourante = type;
        while (classeCourante != null && classeCourante != Object.class) {

            for (Field champ : classeCourante.getDeclaredFields()) {

                // On ignore les champs static et synthetic (ajoutes par le compilateur)
                if (Modifier.isStatic(champ.getModifiers()) || champ.isSynthetic()) {
                    continue;
                }

                // Les champs non simples (objet imbriques, List...) sont ignores
                if (!estTypeSimple(champ.getType())) {
                    continue;
                }

                String valeur = request.getParameter(champ.getName());
                if (estAbsente(valeur, champ.getType())) {
                    continue; // le champ garde sa valeur par defaut
                }

                Object valeurConvertie = convertir(valeur, champ.getType(), champ.getName());
                injecter(objet, champ, valeurConvertie);
            }

            classeCourante = classeCourante.getSuperclass();
        }

        return objet;
    }

    private static Object creerObjet(Class<?> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new BindingException("La classe " + type.getName()
                    + " doit avoir un constructeur sans argument.", e);
        } catch (Exception e) {
            throw new BindingException("Impossible de creer un objet de type " + type.getName(), e);
        }
    }

    /**
     * Met la valeur dans le champ :
     *  - avec le setter (setNom, setAge...) s'il existe ;
     *  - sinon directement dans le champ, meme s'il est private.
     */
    private static void injecter(Object objet, Field champ, Object valeur) {
        Method setter = chercherSetter(objet.getClass(), champ);

        try {
            if (setter != null) {
                setter.invoke(objet, valeur);
            } else {
                champ.setAccessible(true);
                champ.set(objet, valeur);
            }
        } catch (Exception e) {
            throw new BindingException("Impossible de renseigner le champ '" + champ.getName()
                    + "' de " + objet.getClass().getName(), e);
        }
    }

    /** Cherche une methode publique setXxx(typeDuChamp). Retourne null si elle n'existe pas. */
    private static Method chercherSetter(Class<?> classe, Field champ) {
        String nom = champ.getName();
        String nomSetter = "set" + nom.substring(0, 1).toUpperCase() + nom.substring(1);

        try {
            return classe.getMethod(nomSetter, champ.getType());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
