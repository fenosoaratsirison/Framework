<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Connexion (Sprint 10 / 10 bis)</title></head>
<body>
    <h1>Connexion</h1>

    <%-- "erreur" est un texte fixe ecrit dans le Controller, pas une saisie de l'utilisateur --%>
    <p style="color:red">${erreur}</p>

    <form action="${pageContext.request.contextPath}/login" method="post">
        Nom : <input type="text" name="nom" /><br/>
        Mot de passe : <input type="password" name="motDePasse" /><br/>
        <button type="submit">Se connecter</button>
    </form>
    <%-- Le role n'est PAS saisi ici : il est determine par le serveur (AuthController) --%>
    <p>Comptes de demo : rakoto / 1234 (role admin) &nbsp;&nbsp; rabe / abcd (role user)</p>
</body>
</html>
