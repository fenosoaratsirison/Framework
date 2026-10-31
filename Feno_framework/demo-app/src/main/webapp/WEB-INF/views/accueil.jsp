<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Accueil</title></head>
<body>
    <h1>Feno Framework - Demo</h1>
    <ul>
        <li><a href="joueurs">Liste des joueurs (ModelView)</a></li>
        <li><a href="employe">Formulaire employe (Param + GET/POST)</a></li>
        <li><a href="employe/formulaire">Sprint 7 : binding de types simples</a></li>
        <li><a href="employe/formulaire-objet">Sprint 7 bis : binding d'un objet</a></li>
        <li><a href="employe/formulaire-upload">Sprint 8 : upload de fichiers</a></li>
        <li><a href="session">Sprint 9 : session</a></li>
        <li><a href="profil">Sprint 10 : page protegee (connexion requise)</a></li>
        <li><a href="api/secret">Sprint 10 : API protegee (401 si non connecte)</a></li>
        <li><a href="admin">Sprint 10 bis : espace admin (@Role("admin") : 401/redirection, 403 ou OK)</a></li>
        <li><a href="api/admin">Sprint 10 bis : API admin/manager (JSON, 401 ou 403)</a></li>
        <li><a href="api/joueurs">Sprint 6 : JSON</a></li>
    </ul>
</body>
</html>
