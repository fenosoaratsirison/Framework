# Feno_framework — corrections Sprint 0 / Sprint 1


## Structure livrée

```
Feno_framework/
└── src/mg/framework/
    ├── annotation/Controller.java     (annotation @Controller)
    ├── scanner/ClassScanner.java      (scan des classes d'un package)
    ├── servlet/FrontControllerServlet.java
    └── web.xml                        (fragment à copier dans l'app)

TestApp/                                <-- petite app de test
├── src/test/controller/
│   ├── EmployeController.java   (@Controller -> doit être détecté)
│   ├── HomeController.java      (@Controller -> doit être détecté)
│   └── UtilHelper.java          (PAS annoté -> ne doit PAS être détecté)
└── WEB-INF/web.xml
```

## Comment compiler et déployer (sans Maven, à la main)

1. Compiler le framework :
   ```
   javac -cp lib/jakarta.servlet-api.jar -d build/classes $(find Feno_framework/src -name "*.java")
   jar cf monframework.jar -C build/classes .
   ```
2. Copier `monframework.jar` dans `TestApp/WEB-INF/lib/`.
3. Compiler les controllers de test (avec `monframework.jar` dans le classpath) et placer les `.class` résultants dans `TestApp/WEB-INF/classes/test/controller/`.
4. Déployer le dossier `TestApp` (qui doit contenir `WEB-INF/web.xml`, `WEB-INF/classes`, `WEB-INF/lib`) sur Tomcat (`webapps/TestApp`).
5. Démarrer Tomcat et appeler `http://localhost:8080/TestApp/`.

## Résultat attendu en testant l'app

Dans la console Tomcat, au démarrage tu dois voir :
```
[FrontControllerServlet] Scan du package : test.controller
[FrontControllerServlet] Controller détecté : test.controller.EmployeController
[FrontControllerServlet] Controller détecté : test.controller.HomeController
[FrontControllerServlet] 2 controller(s) détecté(s) au total.
```
Et en appelant n'importe quelle URL de l'app, la page affiche la méthode, l'URL demandée, et la liste des 2 controllers détectés (`UtilHelper` ne doit PAS apparaître, ce qui prouve que le filtrage par annotation fonctionne).
