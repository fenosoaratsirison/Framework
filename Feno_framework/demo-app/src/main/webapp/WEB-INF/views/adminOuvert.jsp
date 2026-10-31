<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Page ouverte (Sprint 10 bis)</title></head>
<body>
    <h1>Page ouverte a tout utilisateur connecte</h1>
    <p>Dans AdminController, cette methode porte @Authenticated : elle l'emporte sur @Role("admin") de la classe.</p>
    <p><a href="${pageContext.request.contextPath}/profil">Retour au profil</a></p>
</body>
</html>
