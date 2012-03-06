package com.google.appengine.arsenal;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
/** 
 * closeBalloon servlet used by HTML user interface to close the current info balloon
 * @author razvanculea
 *
 */
@SuppressWarnings("serial")
@WebServlet("/closeBalloon")
public class closeBalloon extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    
    resp.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    resp.setHeader("Pragma","no-cache"); //HTTP 1.0
    resp.setDateHeader ("Expires", 0); //prevents caching at the proxy server
    
    String rHostTemp = req.getRemoteHost();
    String rHost = "lg-p1";
    if (Mappings.mapName2IP.get(rHostTemp) == null ) { // did'n't got the name
      if (Mappings.mapIP2Name.get(rHostTemp) != null) // got a known IP
          rHost = Mappings.mapIP2Name.get(rHostTemp);
      else //unknown name and IP
          rHost = "lg-p1";
    } else // known name
      rHost = rHostTemp;
    
    System.out.println(rHost +" Close balloon");
    // Using the synchronous cache
    ServletContext application = getServletConfig().getServletContext();
    DataForEarth data = (DataForEarth) application.getAttribute(rHost);
    data.marker_id = Integer.valueOf(-1);
    application.setAttribute(rHost,data);
    
  }
}
