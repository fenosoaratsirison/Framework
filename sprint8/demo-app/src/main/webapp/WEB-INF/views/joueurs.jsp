<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<html>
<head><title>Joueurs</title></head>
<body>
    <h1>Liste des joueurs</h1>
    <%
        List<String> joueurs = (List<String>) request.getAttribute("joueurs");
        if (joueurs != null) {
            for (String joueur : joueurs) {
    %>
    <p><%= joueur %></p>
    <%
            }
        }
    %>
</body>
</html>
