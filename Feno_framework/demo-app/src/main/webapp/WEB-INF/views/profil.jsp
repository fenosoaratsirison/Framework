<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Profil (Sprint 10)</title></head>
<body>
    <h1>Page protegee</h1>
    <p>Bienvenue, ${nom} ! Votre role : <b>${role}</b></p>
    <ul>
        <li><a href="${pageContext.request.contextPath}/admin">Espace admin (@Role("admin") : 403 si vous etes "user")</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/ouvert">Page ouverte (@Authenticated sur la methode : gagne sur @Role de la classe)</a></li>
        <li><a href="${pageContext.request.contextPath}/api/admin">API admin/manager (JSON, 403 si "user")</a></li>
    </ul>
    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button type="submit">Se deconnecter</button>
    </form>
</body>
</html>
