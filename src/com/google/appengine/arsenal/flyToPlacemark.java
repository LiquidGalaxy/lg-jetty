package com.google.appengine.arsenal;

import java.io.IOException;
import java.net.URL;


import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


@SuppressWarnings("serial")
@WebServlet("/flyToPlacemark")
public class flyToPlacemark extends HttpServlet {
 
  
 //Length Of A Degree Of Longitude In Meters
 static final double oneDegreeLongitudeAt48inM = 74625.33;
 
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    
    resp.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    resp.setHeader("Pragma","no-cache"); //HTTP 1.0
    resp.setDateHeader ("Expires", 0); //prevents caching at the proxy server
    System.out.println("flyToPlacemark");
  
    Float lat = Float.valueOf("48.8655555");
    try {
      lat = Float.valueOf(req.getParameter("lat"));
    } catch (Exception e) {}
    Float lng = Float.valueOf("2.328888888");
    try {
      lng = Float.valueOf(req.getParameter("lng"));
    } catch (Exception e) {}
    String viewport = req.getParameter("viewport");
    double width = 3200; // default width of view
    if (viewport != null) {
      String[] viewsplit = viewport.split(",");
      if (viewsplit.length == 4) {
        // compute width
        try {
          double lng1 = Double.parseDouble(viewsplit[1]);
          double lng2 = Double.parseDouble(viewsplit[3]);
          width = (lng2-lng1)*oneDegreeLongitudeAt48inM; // width in meters, calculated from viewport
          if (width <0) width = width * (-1);
        } catch (Exception e) {
          System.out.println("Longitude conversion to width, error :"+e.getMessage());
        }
      }
    }
    Integer marker_id = Integer.valueOf(req.getParameter("id"));
    String formatted_address = req.getParameter("formatted_address");
    String rHostTemp = req.getRemoteHost();
    String rHost = "lg-p1";
    if (Mappings.mapName2IP.get(rHostTemp) == null ) { // did'n't got the name
      if (Mappings.mapIP2Name.get(rHostTemp) != null) // got a known IP
          rHost = Mappings.mapIP2Name.get(rHostTemp);
      else //unknown name and IP
          rHost = "lg-p1";
    } else // known name
      rHost = rHostTemp;
    
    System.out.println(rHost+" ("+rHostTemp+") flyToPlacemark lat="+lat.toString()+" lng="+lng.toString()+" placemark="+marker_id + " viewport"+ viewport);
    //multiple earths
    DataForEarth data = new DataForEarth(marker_id,lat,lng,formatted_address);
    long keep = 0;
    String sKeep = req.getParameter("keep");
    if (sKeep != null)
       if (!sKeep.equalsIgnoreCase(""))
          keep = Long.valueOf(sKeep);
    //add some time to keep the balloon open
    data.timestamp = data.timestamp + Long.valueOf(keep*1000);
    
    
    // Using the synchronous cache
    ServletContext application = getServletConfig().getServletContext();
    application.setAttribute(rHost,data);
    
    try {
      //range 
      double range = width / 2 * 3.5; // width = 2km range 3.5Km, angle 32¡
      URL url = new URL("http://lg-p1:81/change.php?query=flytoview=%3CLookAt%3E%3Clongitude%3E"+lng.toString()+"%3C/longitude%3E%3Clatitude%3E"+lat.toString()+"%3C/latitude%3E%3Caltitude%3E200%3C/altitude%3E%3Cheading%3E0%3C/heading%3E%3Ctilt%3E0%3C/tilt%3E%3Crange%3E"+range+"%3C/range%3E%3CaltitudeMode%3ErelativeToGround%3C/altitudeMode%3E%3Cgx:altitudeMode%3ErelativeToSeaFloor%3C/gx:altitudeMode%3E%3C/LookAt%3E&name=blabla");
      url.openStream();
    } catch (IOException e) {
      System.out.println("AHHHH SNAP ! "+e.getMessage());
    }
  }
}
