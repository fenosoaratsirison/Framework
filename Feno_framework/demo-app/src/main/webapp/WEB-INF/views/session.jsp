<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Session (Sprint 9)</title></head>
<body>
    <h1>Sprint 9 : session</h1>

    <p>Identifiant de session : ${pageContext.session.id}</p>
    <p>Nombre de visites de cette page : ${sessionScope.visites}</p>

    <%
        // On lit la VRAIE session : si le nom est la, la recopie de MaSession a fonctionne.
        Object utilisateur = session.getAttribute("utilisateur");
        if (utilisateur == null) {
    %>
        <p>Vous n'etes pas connecte.</p>
        <form action="${pageContext.request.contextPath}/session/login" method="post">
            Nom : <input type="text" name="nom" />
            <button type="submit">Se connecter</button>
        </form>
    <%
        } else {
            // le nom vient de l'utilisateur : on echappe le HTML
            String nomSecurise = utilisateur.toString()
                    .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    %>
        <p>Connecte en tant que : <b><%= nomSecurise %></b></p>
        <form action="${pageContext.request.contextPath}/session/logout" method="post">
            <button type="submit">Se deconnecter</button>
        </form>
    <%
        }
    %>

    <p><a href="${pageContext.request.contextPath}/session">Recharger la page (le compteur augmente)</a></p>
</body>
</html>
