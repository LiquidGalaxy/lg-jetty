package com.google.appengine.arsenal;
/**
 * simple French characters translator to non accented ones.
 * @author razvanculea
 *
 */
public final class FrenchNormalizer {
  /**
   * 
   * @param s a string with French characters
   * @return string converted to non accented characters
   */
  final static String unFrench(String s) {
    String r = s;
    r = r.replace('é', 'e');
    r = r.replace('è', 'e');
    r = r.replace('ê', 'e');
    r = r.replace('ë', 'e');
    r = r.replace('ô', 'o');
    r = r.replace('ö', 'o');
    r = r.replace('à', 'a');
    r = r.replace('ç', 'c');
    r = r.replace('ï', 'i');
    r = r.replace('ù', 'u');
    r = r.replace('\"', ' ');
    return r;
  }
}
