<%@ page import="es.taw.primerparcial.entity.Cancion" %>
<%@ page import="java.util.List" %>
<%@ page import="es.taw.primerparcial.entity.Genero" %><%--
  Created by IntelliJ IDEA.
  User: Eduardo
  Date: 28/04/2025
  Time: 12:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
    <title>Añadir álbum | patapum</title>
</head>
<body>
    <h1>Nuevo álbum recopilatorio</h1>

    <h3>Géneros</h3>

    <form action="filter" method="POST">
    <%
        List<Genero> generos = (List<Genero>) request.getAttribute("generos");
         for (Genero genero : generos){


    %>
    <input type="radio" name="genero" value="<%=genero.getGeneroId()%>"><%=genero.getGeneroName()%></input>

    <%
        }
    %>
        <input type="submit" value="Filtrar">
    </form>

    <form:form method="POST"
               action="/saveAlbum" modelAttribute="albumRecopilatorio">
        Nombre del recopilatorio:<form:input type="text" path="albumRecopilatorioName"></form:input><br>
        Nombre del álbum:<form:input type="text" path="albumName"></form:input></br>
        Canciones: <form:select path="canciones" multiple="multiple" size="50" >
        <form:options items="${canciones}" itemValue="cancionId" itemLabel="cancionName" />
    </form:select>
        <button name="save" type="submit">Guardar</button>
    </form:form>


</body>
</html>
