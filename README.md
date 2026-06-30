# Feno_framework — corrections Sprint 0 / Sprint 1

## Bugs trouvés dans le code original

1. **Noms de fichiers/classes/packages incohérents** (bloquant — empêche la compilation) :
   - `controller.java` contenait `public @interface Controller` → en Java, le nom du fichier doit être identique au nom de la classe/interface publique (sensible à la casse sous Linux/Tomcat). Renommé en `Controller.java`.
   - `Classscanner.java` déclarait `package mg.framework.scanner` mais le dossier réel était `scannner` (3 "n"), et la classe s'appelait `ClassScanner` (avec un grand S) dans un fichier `Classscanner.java`. Renommé en `ClassScanner.java` dans le dossier `scanner`.

2. **Le Sprint 1 n'était pas implémenté** dans `FrontControllerServlet` :
   - Pas de méthode `init()`, donc aucun scan n'était fait au démarrage.
   - `ClassScanner` n'était jamais appelé.
   - L'annotation `@Controller` n'était jamais utilisée.
   - Le routage était fait "en dur" (if/else sur des URLs fixes) au lieu de se baser sur les controllers détectés.
   - → Ajout du champ `List<Class<?>> controllers`, d'un `init(ServletConfig)` qui lit le paramètre `controller-package` (déclaré dans `web.xml`), appelle `ClassScanner.scanPackage(...)`, filtre les classes avec `isAnnotationPresent(Controller.class)` et les stocke.

3. **Double déclaration du servlet** : la classe portait `@WebServlet(urlPatterns = {"/"})` ET était déclarée dans `web.xml`. Sous Tomcat, cela provoque une erreur au démarrage (`IllegalArgumentException: The servlet ... is already defined`). L'annotation `@WebServlet` a été supprimée : on garde uniquement `web.xml`, car c'est lui qui doit fournir le paramètre `controller-package`.

4. **`ClassScanner` original** : ne gérait que les classes présentes sous forme de `.class` dans un dossier, ne scannait pas les sous-packages, et plantait silencieusement si le `.jar` du framework était posé dans `WEB-INF/lib` (cas réel sous Tomcat). La nouvelle version gère à la fois le scan depuis un dossier (`WEB-INF/classes`) et depuis un `.jar` (`WEB-INF/lib`), et scanne récursivement les sous-packages.

5. **`url-pattern`** : gardé en `/*` comme demandé dans le Sprint 0 (toutes les requêtes passent par le FrontController).

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
