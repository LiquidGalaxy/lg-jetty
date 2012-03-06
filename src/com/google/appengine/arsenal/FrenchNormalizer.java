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
    r = r.replace('�', 'e');
    r = r.replace('�', 'e');
    r = r.replace('�', 'e');
    r = r.replace('�', 'e');
    r = r.replace('�', 'o');
    r = r.replace('�', 'o');
    r = r.replace('�', 'a');
    r = r.replace('�', 'c');
    r = r.replace('�', 'i');
    r = r.replace('�', 'u');
    r = r.replace('\"', ' ');
    return r;
  }
}
