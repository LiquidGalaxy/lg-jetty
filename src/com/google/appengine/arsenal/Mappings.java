package com.google.appengine.arsenal;

import java.util.HashMap;
import java.util.Map;
/**
 * Hostname to IP, and IP to hostname mappings used to determine the calling host for reset, pixel servlets. 
 * @author razvanculea
 *
 */
public final class Mappings {
  public static final Map<String, String> mapIP2Name = new HashMap<String, String>();
  static {
      mapIP2Name.put("10.42.41.91", "lg-p1");
      mapIP2Name.put("10.42.41.92", "lg-p2");
      mapIP2Name.put("10.42.41.93", "lg-p3");
      mapIP2Name.put("10.42.41.94", "lg-p4");
  }

  public static final Map<String, String> mapName2IP = new HashMap<String, String>();
  static {
      {
        mapName2IP.put("lg-p1", "10.42.41.91");
        mapName2IP.put("lg-p2", "10.42.41.92");
        mapName2IP.put("lg-p3", "10.42.41.93");
        mapName2IP.put("lg-p4", "10.42.41.94");
      }
  };
  
  public static final String defaultControlPodium = "lg-p1";
  /** 
   * find the remote host name, even if we got a IP address
   * @param rHostTemp host from request (name or IP)
   */
  public static String getHost(String rHostTemp) {
    String rHost = Mappings.defaultControlPodium;
    // find the remote host name, even if we got a IP address
    if (Mappings.mapName2IP.get(rHostTemp) == null ) { // did'n't got the name
      if (Mappings.mapIP2Name.get(rHostTemp) != null) // got a known IP
          rHost = Mappings.mapIP2Name.get(rHostTemp);
      else //unknown name and IP
          rHost = Mappings.defaultControlPodium;
    } else // known name
      rHost = rHostTemp;
    return rHost;
  }
  
  /**
   * base URL for networklink updates
   */
  public static final String baseURLnwlink = "http://lg-head:8080/static";// "http://lg-head/kml";//
}
