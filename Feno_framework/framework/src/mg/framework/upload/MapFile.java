package mg.framework.upload;

import mg.framework.exception.BindingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Sprint 8 : represente un fichier recu depuis un formulaire (upload).
 *
 * Contenu :
 *  - nom       : nom du fichier sans l'extension (ex : "cin_recto")
 *  - extension : extension sans le point (ex : "png"), "" s'il n'y en a pas
 *  - bytes     : contenu du fichier
 *
 * Utilisation dans un modele :
 *   public class Employe {
 *       private MapFile recto;   // <input type="file" name="recto">
 *       private MapFile verso;   // <input type="file" name="verso">
 *   }
 *
 * Le framework cree l'objet MapFile tout seul quand le nom du champ du
 * formulaire est le meme que le nom de l'attribut.
 */
public class MapFile {

    /** Dossier d'upload, lu dans web.xml (parametre "upload-dir") au demarrage. */
    private static String dossierUpload;

    private String nom;
    private String extension;
    private byte[] bytes;

    public MapFile(String nom, String extension, byte[] bytes) {
        this.nom = nom;
        this.extension = extension;
        this.bytes = bytes;
    }

    public String getNom() {
        return nom;
    }

    public String getExtension() {
        return extension;
    }

    public byte[] getBytes() {
        return bytes;
    }

    /** Taille du fichier en octets. */
    public int getTaille() {
        return bytes.length;
    }

    /** Nom complet : "cin_recto.png" (ou juste "cin_recto" s'il n'y a pas d'extension). */
    public String getNomComplet() {
        if (extension.isEmpty()) {
            return nom;
        }
        return nom + "." + extension;
    }

    public static void setDossierUpload(String dossier) {
        dossierUpload = dossier;
    }

    public static String getDossierUpload() {
        return dossierUpload;
    }

    /**
     * Enregistre le fichier dans le dossier "upload-dir" et retourne le chemin complet.
     * - le dossier est cree s'il n'existe pas ;
     * - si un fichier du meme nom existe deja, on ajoute _1, _2... (rien n'est ecrase).
     */
    public String sauvegarder() {

        if (dossierUpload == null || dossierUpload.isEmpty()) {
            throw new BindingException("Le parametre 'upload-dir' n'est pas defini dans web.xml.");
        }

        try {
            Path dossier = Paths.get(dossierUpload);
            Files.createDirectories(dossier);

            String base = nom;
            if (base.isEmpty()) {
                base = "fichier";
            }

            String suffixe = "";
            if (!extension.isEmpty()) {
                suffixe = "." + extension;
            }

            Path chemin = dossier.resolve(base + suffixe);
            int numero = 1;
            while (Files.exists(chemin)) {
                chemin = dossier.resolve(base + "_" + numero + suffixe);
                numero++;
            }

            Files.write(chemin, bytes);
            return chemin.toString();

        } catch (IOException e) {
            throw new BindingException("Impossible d'enregistrer le fichier " + getNomComplet()
                    + " dans " + dossierUpload, e);
        }
    }
}
