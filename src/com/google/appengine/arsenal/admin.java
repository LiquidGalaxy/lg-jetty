package com.google.appengine.arsenal;

import java.io.IOException;
import java.net.URL;
//import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * admin servlet used to create the HTML admin UI (<i>simple</i> or <i>full</i>, "gui" parameter)<br>
 * admin HTML UI, calls admin servlet to perform actions (reboot, restart Earth, Xorg, purge squid) 
 * @author razvanculea
 *
 */
@SuppressWarnings("serial")
@WebServlet("/admin")
public class admin extends HttpServlet {
  
  
  
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException,ServletException {
    resp.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    resp.setHeader("Pragma","no-cache"); //HTTP 1.0
    resp.setDateHeader ("Expires", 0); //prevents caching at the proxy server
    resp.setContentType("text/plain");
    
    int x = Integer.valueOf(req.getParameter("x")==null?"0":req.getParameter("x"));
    int y = Integer.valueOf(req.getParameter("y")==null?"0":req.getParameter("y"));
    String sGUI = (String) req.getParameter("gui");
    String rebootServername = (String) req.getParameter("servername");
    String sAction = (String) req.getParameter("action");
    if (sAction == null)
        sAction = "";

    //getting the right data
    String masterHost ="lg-p1"; // name of the host I should synch with
    if(y>=0 && y<=7) masterHost = "lg-p1"; //big
    if(y>=8 && y<=9 && x>=0 && x<=1) masterHost = "lg-p2";
    if(y>=8 && y<=9 && x>=2 && x<=3) masterHost = "lg-p3";
    if(y>=8 && y<=9 && x>=4 && x<=5) masterHost = "lg-p4";
    if(y==10 && x==0) masterHost = "lg-p1";
    if(y==10 && x==1) masterHost = "lg-p2";
    if(y==10 && x==2) masterHost = "lg-p3";
    if(y==10 && x==3) masterHost = "lg-p4";
    
    System.out.println("You are x="+x+",y="+y+" Your master host is:"+masterHost);
    
    if (rebootServername != null)
      if (rebootServername.startsWith("lg")) {
        if (sAction.startsWith("1")) {
          System.out.println("Restarting Earth if low memory "+rebootServername);
          //cull Earth on that host name
          try {
            URL url = new URL("http://lg-head/cgi-bin/lg-control.py?action=cull&target=hostname&host="+rebootServername);
            url.openStream();
          } catch (IOException e) {
            System.out.println("AHHHH SNAP low mem Earth ! "+e.getMessage());
          }
        } else if (sAction.startsWith("2")) {
          System.out.println("Purge squid "+rebootServername);
          //cull Earth on that host name
          try {
            URL url = new URL("http://lg-head/cgi-bin/lg-control.py?action=purge&target=hostname&host="+rebootServername);
            url.openStream();
          } catch (IOException e) {
            System.out.println("AHHHH SNAP purge Squid ! "+e.getMessage());
          }
        } else if (sAction.startsWith("3")) {
          System.out.println("Kill Earth "+rebootServername);
          //cull Earth on that host name
          try {
            URL url = new URL("http://lg-head/cgi-bin/lg-control.py?action=kill&target=hostname&host="+rebootServername);
            url.openStream();
          } catch (IOException e) {
            System.out.println("AHHHH SNAP kill Earth ! "+e.getMessage());
          }
        } else if (sAction.startsWith("4")) {
          System.out.println("Relaunch Xorg "+rebootServername);
          //cull Earth on that host name
          try {
            URL url = new URL("http://lg-head/cgi-bin/lg-control.py?action=relaunch&target=hostname&host="+rebootServername);
            url.openStream();
          } catch (IOException e) {
            System.out.println("AHHHH SNAP relaunch Xorg ! "+e.getMessage());
          }
        } else if (sAction.startsWith("5")) {
          System.out.println("REBOOT "+rebootServername);
          //cull Earth on that host name
          try {
            URL url = new URL("http://lg-head/cgi-bin/lg-control.py?action=reboot&target=hostname&host="+rebootServername);
            url.openStream();
          } catch (IOException e) {
            System.out.println("AHHHH SNAP REBOOT ! "+e.getMessage());
          }
        } else if (sAction.startsWith("6")) {
          System.out.println("Purge Squid and Relaunch Xorg "+rebootServername);
          //cull Earth on that host name
          try {
            //purge
            URL url = new URL("http://lg-head/cgi-bin/lg-control.py?action=purge&target=hostname&host="+rebootServername);
            url.openStream();
            //relaunch Xorg
            url = new URL("http://lg-head/cgi-bin/lg-control.py?action=relaunch&target=hostname&host="+rebootServername);
            url.openStream();
          } catch (IOException e) {
            System.out.println("AHHHH SNAP purge squid and relaunch Xorg ! "+e.getMessage());
          }
        }
      }
    
    
    if ("full".equalsIgnoreCase(sGUI))
      getServletContext().getRequestDispatcher("/admin.jsp").forward(req, resp);
    else
      getServletContext().getRequestDispatcher("/adminsimple.jsp").forward(req, resp);
  }
}
