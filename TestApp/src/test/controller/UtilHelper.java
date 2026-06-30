package test.controller;

/**
 * Cette classe n'est volontairement PAS annotée @Controller.
 * Elle sert à vérifier que le ClassScanner ne la considère pas
 * comme un controller (test négatif).
 */
public class UtilHelper {

    public String hello() {
        return "Je ne suis pas un controller";
    }
}
