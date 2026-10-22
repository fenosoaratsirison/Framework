package test.model;

/**
 * Modele de demo pour le Sprint 7 bis (data binding d'un objet).
 *
 * - constructeur sans argument obligatoire (le framework en a besoin) ;
 * - setNom et setAge existent : le framework les utilise ;
 * - pas de setSalaire : le framework remplit alors le champ directement.
 */
public class Employe {

    private String nom;
    private int age;
    private double salaire;

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
}
