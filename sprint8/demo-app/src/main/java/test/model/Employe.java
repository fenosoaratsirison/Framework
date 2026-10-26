package test.model;

import mg.framework.upload.MapFile;

/**
 * Modele de demo pour le Sprint 7 bis (data binding d'un objet).
 *
 * - constructeur sans argument obligatoire (le framework en a besoin) ;
 * - setNom et setAge existent : le framework les utilise ;
 * - pas de setSalaire : le framework remplit alors le champ directement.
 *
 * Sprint 8 : recto et verso sont des MapFile. Ils sont remplis avec les fichiers
 * des champs <input type="file" name="recto"> et <input type="file" name="verso">.
 */
public class Employe {

    private String nom;
    private int age;
    private double salaire;
    private MapFile recto;
    private MapFile verso;

    public Employe() {
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNom() {
        return nom;
    }

    public int getAge() {
        return age;
    }

    public double getSalaire() {
        return salaire;
    }

    public MapFile getRecto() {
        return recto;
    }

    public void setRecto(MapFile recto) {
        this.recto = recto;
    }

    public MapFile getVerso() {
        return verso;
    }

    public void setVerso(MapFile verso) {
        this.verso = verso;
    }
}
