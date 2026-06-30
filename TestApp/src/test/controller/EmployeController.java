package test.controller;

import mg.framework.annotation.Controller;

/**
 * Classe de test : doit être détectée par le ClassScanner du framework
 * car elle est annotée @Controller.
 */
@Controller
public class EmployeController {

    public String list() {
        return "Liste des employés";
    }
}
