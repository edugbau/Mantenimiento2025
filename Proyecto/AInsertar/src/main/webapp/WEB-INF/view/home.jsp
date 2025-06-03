<%@ page import="es.taw.primerparcial.entity.Artista" %>
<%@ page import="java.util.List" %>
<%@ page import="es.taw.primerparcial.entity.Album" %>
<%@ page import="es.taw.primerparcial.entity.Cancion" %><%--
  Created by IntelliJ IDEA.
  User: Eduardo
  Date: 28/04/2025
  Time: 11:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Testicles</title>
  </head>
  <body>
  <h1>SALUDOS INTERNAUTA</h1>
  <h2>Lista de artistas de la BD</h2>
  <table style="width:100%" border="1" >
      <tr>
        <th>ARTISTA</th>
        <th>NÚM. ALBUMES</th>
        <th>NÚM. CANCIONES</th>
        <th>NÚM COLABORACIONES</th>
      </tr>
    <%
      List<Artista> artistas = (List<Artista>)request.getAttribute("artistas");
      for (Artista artista : artistas){
        int albumes = artista.getNumAlbumes();
        int colabs = artista.getNumColabs();
        int canciones = artista.getCancionesPropias();


    %>
    <tr>
      <td><%=artista.getArtistaName()%></td>
      <td><%=albumes%></td>
      <td><%=canciones%></td>
      <td><%=colabs%></td>
    </tr>
    <%
      }
    %>
  </table>

  </br>
  <a href="/addAlbum">Añadir álbum</a>
  
  </body>
</html>
