<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Formulaire (Sprint 7)</title></head>
<body>
    <h1>Sprint 7 : types simples</h1>
    <form action="${pageContext.request.contextPath}/employe/save" method="post">
        Nom : <input type="text" name="nom" /><br/>
        Age : <input type="text" name="age" /><br/>
        Salaire : <input type="text" name="salaire" /><br/>
        <button type="submit">Enregistrer</button>
    </form>
</body>
</html>
