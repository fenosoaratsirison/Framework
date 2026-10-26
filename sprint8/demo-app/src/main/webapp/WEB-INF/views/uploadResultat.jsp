<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<html>
<head><title>Resultat upload</title></head>
<body>
    <h1>${titre}</h1>
    <%
        List<String> lignes = (List<String>) request.getAttribute("lignes");
        if (lignes != null) {
            for (String ligne : lignes) {
    %>
    <p><%= ligne.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") %></p>
    <%
            }
        }
    %>
</body>
</html>
