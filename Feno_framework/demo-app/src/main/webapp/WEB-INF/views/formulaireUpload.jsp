<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head><title>Upload (Sprint 8)</title></head>
<body>
    <h1>Sprint 8 : upload dans un objet Employe</h1>
    <!-- enctype="multipart/form-data" est OBLIGATOIRE pour envoyer des fichiers -->
    <form action="${pageContext.request.contextPath}/employe/upload" method="post"
          enctype="multipart/form-data">
        Nom : <input type="text" name="nom" /><br/>
        CIN recto : <input type="file" name="recto" /><br/>
        CIN verso : <input type="file" name="verso" /><br/>
        <button type="submit">Envoyer</button>
    </form>

    <h1>Sprint 8 : upload dans une Map&lt;String, byte[]&gt;</h1>
    <form action="${pageContext.request.contextPath}/employe/upload-map" method="post"
          enctype="multipart/form-data">
        Photo : <input type="file" name="photo" /><br/>
        Document : <input type="file" name="document" /><br/>
        <button type="submit">Envoyer</button>
    </form>
</body>
</html>
