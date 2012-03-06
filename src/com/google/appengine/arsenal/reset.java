package com.google.appengine.arsenal;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
/**
 * reset servlet used by the html user interface to send a flyTo default location.<br>
 * response can be ignored
 * <br>
 * @author razvanculea
 *
 */
@SuppressWarnings("serial")
@WebServlet("/reset")
public class reset extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException,ServletException {
    // setting the response header
    resp.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    resp.setHeader("Pragma","no-cache"); //HTTP 1.0
    resp.setDateHeader ("Expires", 0); //prevents caching at the proxy server
    
    /** remote host asking for a reset */
    String rHostTemp = req.getRemoteHost();
    /** remote host name */
    String rHost = Mappings.getHost(rHostTemp);
    
    System.out.println(rHost+" reset"); 
    
    // Put default values for the calling host
    DataForEarth data = new DataForEarth(-1,Float.valueOf(0),Float.valueOf(0),"");
    ServletContext application = getServletConfig().getServletContext();
    application.setAttribute(rHost,data);
    
    try {
      //range 
      double range = 87000; // width = 2km range 3.5Km, angle 32¡
      URL url = new URL("http://"+rHost+":81/change.php?query=flytoview=%3CLookAt%3E%3Clongitude%3E2.33783%3C/longitude%3E%3Clatitude%3E48.8536%3C/latitude%3E%3Caltitude%3E200%3C/altitude%3E%3Cheading%3E0%3C/heading%3E%3Ctilt%3E0%3C/tilt%3E%3Crange%3E"+range+"%3C/range%3E%3CaltitudeMode%3ErelativeToGround%3C/altitudeMode%3E%3Cgx:altitudeMode%3ErelativeToSeaFloor%3C/gx:altitudeMode%3E%3C/LookAt%3E&name=reset");
      // call the earth command php server on the host
      url.openStream();
      resp.getWriter().println("reset done");
    } catch (IOException e) {
      System.out.println("AHHHH SNAP ! "+e.getMessage());
    }
    
  }
}
