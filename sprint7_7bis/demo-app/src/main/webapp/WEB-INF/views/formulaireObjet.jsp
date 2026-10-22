<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Formulaire objet (Sprint 7 bis)</title></head>
<body>
    <h1>Sprint 7 bis : objet Employe</h1>
    <form action="${pageContext.request.contextPath}/employe/save-objet" method="post">
        Nom : <input type="text" name="nom" /><br/>
        Age : <input type="text" name="age" /><br/>
        Salaire : <input type="text" name="salaire" /><br/>
        <button type="submit">Enregistrer</button>
    </form>
</body>
</html>
