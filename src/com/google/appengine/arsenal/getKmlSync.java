package com.google.appengine.arsenal;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * getKmlSync servlet sends a KML response with the right updates for each big Earth instance<br>
 * also used as a heart beat supervision for every big Earth instance (that is supposed to call getKmlSync once per second), 
 * if the grace period expires and rebootcount is positive a restart (earth/Xorg) is demanded.
 * @author razvanculea
 *
 */
@SuppressWarnings("serial")
@WebServlet("/getKmlSync")
public class getKmlSync extends HttpServlet {
  
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException,ServletException {
    resp.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    resp.setHeader("Pragma","no-cache"); //HTTP 1.0
    resp.setDateHeader ("Expires", 0); //prevents caching at the proxy server
    resp.setContentType("text/plain");
    
    int x = Integer.valueOf(req.getParameter("x"));
    int y = Integer.valueOf(req.getParameter("y"));
    

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
    
    // Using the synchronous cache
    ServletContext application = getServletConfig().getServletContext();
    /** podium data */
    DataForEarth data = (DataForEarth) application.getAttribute(masterHost);
    
    //markHeartBeat for screen (x,y) now
    HeartBeats heartBeats = (HeartBeats) application.getAttribute("HB");
    if (heartBeats == null) {
      //initialize
      heartBeats = new HeartBeats(HeartBeats.wait); // wait 60s
      //set
      application.setAttribute("HB", heartBeats);
    } else {
      //update hb
      heartBeats.hb[x][y] = System.currentTimeMillis() + HeartBeats.wait;
      heartBeats.rebootCountDown[x][y] = HeartBeats.maxreboots;
      //check for expired timers and do the reboots
      check(heartBeats);
      //set new hb
      application.setAttribute("HB", heartBeats);
    }
    
    if (data == null) {//initialize data
      data = new DataForEarth(-1,Float.valueOf(0),Float.valueOf(0),"");
      application.setAttribute(masterHost, data);
    } else {
      // check heart beat
      if (data.marker_id > 0 && (data.timestamp.longValue() + 6000 < System.currentTimeMillis() )) {
        data.marker_id = -1;
        application.setAttribute(masterHost, data);
      }
    }

    //set attributes 
    // for the right screen (ex. big : x0..5,y0..7)
    req.setAttribute("marker_id", data.marker_id);
    req.setAttribute("x", x);
    req.setAttribute("y", y);
    req.setAttribute("lat", data.lat);
    req.setAttribute("lng", data.lng);
    req.setAttribute("formatted_address"," ");//forced empty (String) syncCache.get("formatted_address"));
    
      
    //tablette
    if (y == 10) {
          //x is tablette number 0=main
      System.out.println(masterHost+" big screen x="+x+" y="+y+" show/hide virtual placemark no virtual balloon");
          if (data.marker_id >= 0) {
            // show
            getServletContext().getRequestDispatcher("/virtual_marker_show.jsp").forward(req, resp);
            
          } else {
            // hide
            getServletContext().getRequestDispatcher("/virtual_marker_hide.jsp").forward(req, resp);
            
          }
    } else {
      if (data.marker_id <= -2) {
        
        // big screen
        // hide virtual placemark
        // full virtual balloon
        System.out.println(masterHost+" big screen x="+x+" y="+y+" hide virtual placemark full virtual balloon");
        getServletContext().getRequestDispatcher("/virtual_marker_hide_screen_full.jsp").forward(req, resp);
      
      } else if (data.marker_id == -1) {
    
          // big screen
          // hide virtual placemark
          // hide virtual balloon
          System.out.println(masterHost+" big screen x="+x+" y="+y+" hide virtual placemark hide virtual balloon");
          getServletContext().getRequestDispatcher("/virtual_marker_hide_screen_hide.jsp").forward(req, resp);
        
      } else if (data.marker_id == 0) {
          //  show at lat, lng
          // hide virtual balloon
          
          System.out.println(masterHost+" big screen x="+x+" y="+y+" show virtual placemark hide virtual balloon");
          getServletContext().getRequestDispatcher("/virtual_marker_show_screen_hide.jsp").forward(req, resp);
      
      } else {
          // marker_id >= 1
          // show at lat, lng
          // show virtual balloon
          System.out.println(masterHost+" big screen x="+x+" y="+y+" show virtual placemark show virtual balloon");
          getServletContext().getRequestDispatcher("/virtual_marker_show_screen_show.jsp").forward(req, resp);
    
      }
    }
  }

  private synchronized void check(HeartBeats heartBeats) {
    // check for expired heart beats, send "reboots" and reset timers
    for (int x=0;x<6;x++)
      for (int y=0;y<8;y++) {
        if (((heartBeats.hb[x][y] - System.currentTimeMillis()) < 0) && (heartBeats.rebootCountDown[x][y] > 0)) {
          //wait has expired, no heart beat received => send a reboot
          System.out.println("We should boot x="+x+" y="+y);
          if (heartBeats.rebootCountDown[x][y] < (HeartBeats.maxreboots /2)) {
            //purge squid & relaunch Xorg on host
            try {
              System.out.println("purge squid on X="+x+" Y="+y+" hostname="+HeartBeats.hostnamesXY[x][y]);
              URL url = new URL("http://lg-head/cgi-bin/lg-control.py?action=purge&target=hostname&host="+HeartBeats.hostnamesXY[x][y]+"&squid=true");
              url.openStream();
              System.out.println("relaunch on X="+x+" Y="+y+" hostname="+HeartBeats.hostnamesXY[x][y]);
              url = new URL("http://lg-head/cgi-bin/lg-control.py?action=relaunch&target=hostname&host="+HeartBeats.hostnamesXY[x][y]);
              url.openStream();
            } catch (IOException e) {
              System.out.println("AHHHH SNAP purge squid and relaunch Xorg X="+x+" Y="+y+" hostname="+HeartBeats.hostnamesXY[x][y]+" ! "+e.getMessage());
            }
          } else {
            //only kill earth on grid
            try {
              System.out.println("kill earth on X="+x+" Y="+y+" hostname="+HeartBeats.hostnamesXY[x][y]);
              URL url = new URL("http://lg-head/cgi-bin/lg-control.py?action=kill&target=grid&x="+x+"&y="+y);
              url.openStream();
            } catch (IOException e) {
              System.out.println("AHHHH SNAP kill earth X="+x+" Y="+y+" hostname="+HeartBeats.hostnamesXY[x][y]+" ! "+e.getMessage());
            }
          }
          //update hb
          heartBeats.hb[x][y] = System.currentTimeMillis() + HeartBeats.wait;
          heartBeats.rebootCountDown[x][y]--; 
        } else {
          // 48+ requests/s in prod, do not uncomment if you are not debugging
          //System.out.println("OK for x="+x+" y="+y);
        }
      }
  }
}
