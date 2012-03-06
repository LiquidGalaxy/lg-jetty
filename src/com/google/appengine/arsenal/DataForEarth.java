package com.google.appengine.arsenal;

import java.io.Serializable;
/**
 * Bean holding data for a placemark
 * @author razvanculea
 *
 */
public class DataForEarth implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public DataForEarth(Integer marker_id2, Float lat2, Float lng2,
      String formatted_address2) {
    marker_id = marker_id2;
    lat = lat2;
    lng = lng2;
    formatted_address = formatted_address2;
    timestamp = Long.valueOf( System.currentTimeMillis() );
  }
  /** placemark id */
  public Integer marker_id;
  /** latitude */
  public Float lat;
  /** longitude */
  public Float lng;
  /** placemark address*/
  public String formatted_address;
  /** placemark timestamp */
  public Long timestamp;
}
