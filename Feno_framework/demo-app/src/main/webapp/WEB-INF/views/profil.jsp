<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Profil (Sprint 10)</title></head>
<body>
    <h1>Page protegee</h1>
    <p>Bienvenue, ${nom} !</p>
    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button type="submit">Se deconnecter</button>
    </form>
</body>
</html>
