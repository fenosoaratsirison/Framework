# Feno_framework - Etat des sprints (mis a niveau par rapport a MrNaina)

## Ce que j'ai trouve dans ton zip original

Ton code etait en fait deja tres avance (plus avance que MrNaina sur certains
points), mais il ne compilait pas a cause de doublons/erreurs de structure :

1. **Deux classes `ModelView`** dans le meme package `mg.framework.mvc`
   (une mal rangee dans le dossier `annotation/`, une dans `mvc/`) -> conflit
   de compilation.
2. **Deux `ClassScanner`** dans le meme package `mg.framework.scanner`
   (dossier `scanner/` correct, et un doublon dans un dossier mal nomme
   `scannner/` avec une faute de frappe) -> conflit de compilation.
3. **`Controller.java`** contenait `public @interface Controller` mais le
   fichier s'appelait `controller.java` (minuscule) -> ne compile pas en Java
   strict (le nom de fichier doit correspondre au type public).
4. Le routing ne distinguait pas GET/POST pour une meme URL (contrairement a
   MrNaina qui a ca depuis son Sprint 3 avec `HttpMethod`/`UrlMapping`).

J'ai corrige tout ca. Le nouveau projet compile et se package avec succes
(j'ai teste la compilation + la creation du .war).

## Comparaison sprint par sprint avec MrNaina

| Sprint | MrNaina (framework/ + AppFramework/) | Feno (apres correction) |
|---|---|---|
| **Sprint 0** | `FrontController` intercepte toutes les requetes | `FrontControllerServlet` idem |
| **Sprint 1** | `@Controller`, `PackageScanner`, `ControllerScanner` (dossier uniquement) | `@Controller`, `ClassScanner` (dossier **+ jar**, plus complet) |
| **Sprint 2** | `@UrlMapping` + table URL -> Method | `@Url` + `RouteMapping` (meme principe) |
| **Sprint 3** | `HttpMethod` (GET/POST), `Route`/`RouteMapping`, retour `String` = vue, `view-prefix`/`view-suffix` configurables | Idem : `HttpMethod`, `Route`/`RouteMapping`, retour `String`, prefix/suffix configurables |
| **Sprint 4** | *(pas encore fait chez MrNaina)* | `@Param` avec conversion de type (String/int/long/double/boolean) - **fonctionnalite en plus** |
| **Sprint 5** | `ModelView` (vue + donnees), forward avec `setAttribute` | Idem : `ModelView`, forward avec `setAttribute` |
| **Sprint 6** | *(pas encore fait chez MrNaina)* | `@RestController` + reponses JSON (`JsonUtils`) - **fonctionnalite en plus** |
| **Sprint 7** | *(pas encore fait chez MrNaina)* | Data binding des types simples (`ParamBinder`, `BindingException`) |
| **Sprint 7 bis** | *(pas encore fait chez MrNaina)* | Data binding d'un objet (`Employe`) |
| **Sprint 8** | *(pas encore fait chez MrNaina)* | Upload de fichiers (`MapFile`, `Map<String, byte[]>`) |
| **Sprint 9** | *(pas encore fait chez MrNaina)* | Session (`MaSession`) |

**Conclusion : ton framework, une fois corrige, est au meme niveau que celui
de MrNaina sur les sprints 0 a 3 et 5, et va meme plus loin sur les Sprints 4
et 6** (l'injection de parametres `@Param` et le support JSON que MrNaina n'a
pas encore implementes).

## Sprint 6 : @RestController + reponses JSON

Nouveaux fichiers :
- `annotation/RestController.java` : annotation posee sur la classe (comme
  `@Controller`), qui indique que toutes les methodes @Url de ce Controller
  doivent repondre en JSON.
- `json/JsonUtils.java` : conversion Java -> JSON ecrite a la main (pas de
  Gson/Jackson), par reflection. Gere String/nombres/boolean/null,
  `List`/`Map`/tableaux, et les objets simples (POJO) via leurs champs.

Modification dans `FrontControllerServlet` :
- Au scan (init) : les classes annotees `@RestController` sont detectees en
  plus de `@Controller`.
- A l'execution (`processRequest`) : juste apres l'invocation de la methode,
  si la classe du Controller porte `@RestController`, on appelle
  `handleResultJson(...)` au lieu de `handleResult(...)` :
  - si le retour est un `String`, il est considere deja au format JSON et
    renvoye tel quel (content-type `application/json`) ;
  - sinon, l'objet retourne est converti avec `JsonUtils.toJson(...)`.
- Sinon (Controller classique `@Controller`) : comportement inchange
  (forward JSP via `handleResult`).

Demo : `demo-app/src/main/java/test/controller/ApiJoueurController.java`
  - `GET /api/joueurs` -> renvoie une `List<String>` convertie en JSON
  - `GET /api/ping` -> renvoie directement une String JSON toute faite
  - `GET /api/joueur?nom=Rina` -> demontre `@Param` + conversion d'un POJO

## Ce que j'ai ajoute en plus du code

Comme il n'y avait pas d'application de test dans ton zip (contrairement a
`AppFramework/` chez MrNaina), j'ai cree `demo-app/` avec 3 controllers
d'exemple qui couvrent chaque fonctionnalite :

- `AccueilController` : retour simple d'une vue (String) - Sprint 3
- `JoueurController` : retour `ModelView` avec une liste - Sprint 5,
  equivalent au `Controller1` de MrNaina
- `EmployeController` : meme URL `/employe` en GET (formulaire) et POST
  (traitement avec `@Param`) - demontre le Sprint 3 (verbe HTTP) + Sprint 4
  (parametres typés)

## Structure du projet livre

```
Feno_framework/
├── framework/                  <- le framework lui-meme
│   ├── src/mg/framework/
│   │   ├── annotation/         (Controller, RestController, Url, Param, HttpMethod)
│   │   ├── binding/            (ParamBinder)
│   │   ├── exception/          (BindingException)
│   │   ├── json/                (JsonUtils)
│   │   ├── mvc/                (ModelView)
│   │   ├── routing/            (Route, RouteMapping)
│   │   ├── scanner/            (ClassScanner)
│   │   ├── session/            (MaSession)
│   │   ├── upload/             (MapFile)
│   │   └── servlet/            (FrontControllerServlet)
│   ├── lib/jakarta.servlet-api.jar
│   └── build.sh                <- compile et cree build/mg-framework.jar
│
└── demo-app/                   <- application de test (equivalent AppFramework)
    ├── src/main/java/test/controller/
    │   ├── AccueilController.java
    │   ├── JoueurController.java
    │   ├── EmployeController.java
    │   ├── ApiJoueurController.java   (Sprint 6, @RestController)
    │   └── SessionController.java     (Sprint 9)
    ├── src/main/java/test/model/Employe.java   (Sprint 7 bis)
    ├── src/main/webapp/WEB-INF/
    │   ├── web.xml
    │   └── views/ (accueil.jsp, joueurs.jsp, employeForm.jsp, employeResultat.jsp)
    ├── lib/ (jakarta.servlet-api.jar + mg-framework.jar deja compiles)
    └── deploy.sh                <- compile et cree demo-app.war
```

## Pour tester en local

```bash
cd framework
./build.sh              # regenere build/mg-framework.jar si tu modifies le framework
cp build/mg-framework.jar ../demo-app/lib/

cd ../demo-app
./deploy.sh              # produit demo-app.war
# copier demo-app.war dans webapps/ de ton Tomcat, puis :
#   /demo-app/accueil
#   /demo-app/joueurs
#   /demo-app/employe
```

Note : j'ai deja compile et package les deux modules ici pour verifier qu'il
n'y a aucune erreur (le `mg-framework.jar` fourni dans `demo-app/lib/` est
donc deja pret a l'emploi).


## Sprint 7 : data binding des types simples

Objectif : les valeurs d'un formulaire arrivent automatiquement dans les
parametres de la methode du Controller (`String nom, int age, double salaire`).

- `exception/BindingException.java` : levee si une valeur est invalide
  (ex : "abc" pour un int), si un parametre `required` est absent, ou si un
  objet ne peut pas etre cree. Le servlet repond alors avec une erreur 400.
- `binding/ParamBinder.java` : toute la logique du binding.
  - `construireArguments` : boucle sur `method.getParameters()` (reflection)
    et prepare le tableau passe a `Method.invoke`.
  - nom du parametre : `@Param("nom")` s'il existe, sinon le nom Java du
    parametre (necessite `javac -parameters`, ajoute dans `deploy.sh`).
  - valeur absente : `null` pour String/wrappers, `0` / `false` / `'\0'` pour
    les primitifs. Un champ numerique laisse vide dans le formulaire (`""`)
    est traite comme absent.
  - conversion : String, int, long, double, float, boolean, short, byte, char
    et leurs wrappers.
- `annotation/Param.java` : ajout de `required` (faux par defaut).
- `FrontControllerServlet` : appelle `ParamBinder` et attrape `BindingException`.

Demo : `GET /employe/formulaire` puis `POST /employe/save`.

## Sprint 7 bis : data binding d'un objet

Objectif : un argument de type objet (`Employe`) est cree et rempli
automatiquement avec les champs du formulaire.

- `ParamBinder.lierObjet` : cree l'objet (constructeur sans argument
  obligatoire, sinon `BindingException`), parcourt les champs de la classe et de
  ses classes meres (static et synthetic ignores), lit `request.getParameter(nomDuChamp)`,
  convertit, puis injecte :
  - avec le setter `setXxx` s'il existe ;
  - sinon directement dans le champ (`setAccessible(true)`).
- Les champs non simples (objet imbriques, List...) sont ignores.

Note : comme le Sprint 7 bis est fait, un parametre objet n'est plus refuse :
il est lie (l'exception "objet complexe" du Sprint 7 n'a plus lieu d'etre).

Demo : `GET /employe/formulaire-objet` puis `POST /employe/save-objet`
(`Employe` a `setNom`/`setAge` mais pas de `setSalaire`, pour montrer les deux cas).


## Sprint 8 : upload de fichiers

Objectif : recevoir les fichiers d'un formulaire (ex : recto/verso d'une CIN)
dans la methode du Controller, sans manipuler l'API Servlet.

Formulaire : `method="post"` et `enctype="multipart/form-data"` (obligatoire).

Nouveau fichier :
- `upload/MapFile.java` : un fichier recu = `nom`, `extension`, `bytes[]`
  (+ `getTaille()`, `getNomComplet()`). Methode `sauvegarder()` : ecrit le
  fichier dans `upload-dir` (dossier cree si besoin, jamais d'ecrasement :
  `cin.png`, puis `cin_1.png`...) et retourne le chemin.

`ParamBinder` (nouveaute) :
- `lireFichiers` : si la requete est `multipart/form-data`, parcourt
  `request.getParts()`, garde les parties qui ont un nom de fichier, lit les
  octets et decoupe nom / extension (apres le dernier point).
  Un champ fichier laisse vide est ignore.
- **Securite** : le nom de fichier envoye par le navigateur est nettoye (on garde
  ce qui suit le dernier `/` ou `\`), donc `../../x.png` devient `x.png`.
- 3 facons de recevoir un fichier dans la methode d'action :
  1. attribut `MapFile` dans un objet : meme nom que le champ HTML
     (`<input type="file" name="recto">` -> attribut `recto`) ;
  2. parametre `MapFile recto` (nom Java ou `@Param`, `required` possible) ;
  3. `Map<String, byte[]>` (cle = nom du champ HTML) ou `Map<String, MapFile>`
     (pareil, avec nom et extension en plus).
- Erreurs : fichier trop gros ou `multipart-config` absent -> `BindingException`
  (erreur 400) ; `upload-dir` absent -> `BindingException` dans `sauvegarder()`.

`FrontControllerServlet` : lit `upload-dir` au demarrage (init-param ou context-param).

`web.xml` : `<context-param>` `upload-dir` + `<multipart-config>` dans le servlet
(apres `load-on-startup`). **A adapter** : le chemin `upload-dir`
(par defaut `/tmp/feno-uploads`).

Demo : `GET /employe/formulaire-upload` puis `POST /employe/upload` (objet
`Employe` avec `recto` et `verso`) et `POST /employe/upload-map`.

A verifier avec le professeur : le framework doit-il enregistrer automatiquement
les fichiers dans `upload-dir`, ou seulement fournir les `byte[]` ? Ici, les
deux sont couverts : les octets sont fournis, et `sauvegarder()` enregistre
quand le developpeur l'appelle. Passer en sauvegarde automatique serait un petit changement.


## Sprint 9 : session

Objectif : lire et modifier la session HTTP sans manipuler `HttpSession` :
la methode du Controller recoit simplement un parametre `MaSession`.

Nouveau fichier : `session/MaSession.java`
- attributs : `id`, `Map<String, Object> attributes`, `Set<String> initialKeys`
  (les cles presentes dans la session au moment de la copie).
- `static from(HttpSession)` : cree une MaSession et copie l'id et tous les
  attributs de la HttpSession (boucle sur `getAttributeNames()`).
- `flushTo(HttpSession)` : met a jour la vraie session
  1. pour chaque cle de `initialKeys` absente de `attributes` -> `removeAttribute` ;
  2. pour chaque attribut de `attributes` -> `setAttribute`.
- pour le developpeur : `getAttribute`, `setAttribute`, `removeAttribute`, `getId`.

`ParamBinder` : si le type du parametre est `MaSession`, l'argument est
`MaSession.from(request.getSession(true))` (la session est creee si besoin).

`FrontControllerServlet` : juste apres `Method.invoke`, une methode
`enregistrerSession` cherche l'argument `MaSession` et appelle
`flushTo(request.getSession(false))`. C'est fait AVANT le forward vers la vue,
pour que la JSP voie deja les changements (`${sessionScope.xxx}`).

Pourquoi une copie ? La methode du Controller travaille sur une copie ; le
framework decide ensuite quand l'ecrire dans la vraie session.
Les cles ajoutees dans la vraie session par un autre composant pendant l'appel
(ex : un filtre) ne sont pas touchees.

Demo : `GET /session` (compteur de visites), `POST /session/login`,
`POST /session/logout` (`SessionController` + `session.jsp`).

Limite connue : si la methode du Controller leve une exception, la copie n'est
pas recopiee (les changements sont perdus).
