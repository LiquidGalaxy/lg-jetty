package com.google.appengine.arsenal;

import java.io.Serializable;
/**
 * XY coordinates to hostname translation used for big Earth screen heart beats
 * @author razvanculea
 *
 */
public class HeartBeats implements Serializable {
  private static final long serialVersionUID = 1L;
  /** grace period in ms */
  public static final long wait = 90000; // 90s
  /** max no of reboots without getting a heart beat */
  public static final int maxreboots = 6; 
  /** XY coordinates to hostname translation used for big Earth screen heart beats */
  public static final String[][] hostnamesXY = {
    {"lg3-y2","lg3-y2","lg3-y1","lg3-y1","lg3-y8","lg3-y8","lg3-y7","lg3-y7","lg3-y7","lg3-y7","lg-p1"},
    {"lg3-y2","lg3-y2","lg3-y1","lg3-y1","lg3-y8","lg3-y8","lg3-y7","lg3-y7","lg3-y7","lg3-y7","lg-p2"},
    {"lg1-y2","lg1-y2","lg1-y1","lg1-y1","lg1-y8","lg1-y8","lg1-y7","lg1-y7","lg1-y7","lg1-y7","lg-p3"},
    {"lg1-y2","lg1-y2","lg1-y1","lg1-y1","lg1-y8","lg1-y8","lg1-y7","lg1-y7","lg1-y7","lg1-y7","lg-p4"},
    {"lg7-y2","lg7-y2","lg7-y1","lg7-y1","lg7-y8","lg7-y8","lg7-y7","lg7-y7","lg7-y7","lg7-y7",""},
    {"lg7-y2","lg7-y2","lg7-y1","lg7-y1","lg7-y8","lg7-y8","lg7-y7","lg7-y7","lg7-y7","lg7-y7",""},
  };
  /**
   * Reinit heart beat table with new grace period, and reset to <i>maxreboots</i> the reboot count.
   * @param wait new grace period
   */
  public HeartBeats(long wait) {
    for (int x=0;x<6;x++)
      for (int y=0;y<11;y++) {
        hb[x][y] = System.currentTimeMillis() + wait;
        rebootCountDown[x][y] = maxreboots;
      }

  }
  /** last heart beat timestamp for each big Earth screen */
  public long[][] hb = new long[6][11];
  /** reboots left for each big Earth screen */
  public int[][] rebootCountDown = new int[6][11];
}
