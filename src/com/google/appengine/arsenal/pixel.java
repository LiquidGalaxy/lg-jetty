package com.google.appengine.arsenal;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
/**
 * pixel servlet used by the HTML info balloons to get a transparent pixel in the response, and send the server the ID of the info balloon opened.<br>
 * used as a heart beat to detect when info balloons are closed on the earth interface, to synch with the screen overlays<br>
 * response will be a transparent pixel from a request forward to <i>/transparent.gif</i> if will render nice in a HTML info balloon
 * <br>
 * @author razvanculea
 * 
 * @param req HTTP request, <b>marker_id</b> parameter is mandatory (integer signed value), <b>lat</b>, <b>lng</b> parameters are also used (if missing assuming 0)
 */
@SuppressWarnings("serial")
@WebServlet("/pixel")
public class pixel extends HttpServlet {
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
    
    Integer marker_id = Integer.valueOf(req.getParameter("marker_id"));
    System.out.println(rHost+" Open balloon id="+marker_id + " beat=" + req.getParameter("beat")); 
    
    // Using the synchronous cache
    ServletContext application = getServletConfig().getServletContext();
    DataForEarth data = (DataForEarth) application.getAttribute(rHost);
    if (data == null) {//initialize data
      data = new DataForEarth(-1,Float.valueOf(0),Float.valueOf(0),"");
    }
    Float lat = Float.valueOf(0);
    Float lng = Float.valueOf(0);
    try {
      lat = Float.valueOf(req.getParameter("lat"));
      lng = Float.valueOf(req.getParameter("lng"));
    } catch (Exception e) {}
    data.marker_id = marker_id;
    data.lat = lat;
    data.lng = lng;
    data.timestamp = Long.valueOf(System.currentTimeMillis());
    // update the heartbeat for the opened balloon
    application.setAttribute(rHost,data);
    // send a transparent pixel gif
    getServletContext().getRequestDispatcher("/transparent.gif").forward(req, resp);
    
  }
}
