package com.google.appengine.arsenal;

import java.io.Reader;

import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
@Deprecated
public class CustomFrenchAnalyzer extends Analyzer {
  Version v;
  public CustomFrenchAnalyzer(Version v) {
    this.v = v;
  }
  @Override
  public TokenStream tokenStream(String arg0, Reader reader) {

    /* initialisation du token */
    TokenStream result = new StandardTokenizer(v , reader);
    /* on retire apostrophe*/
    result = new StandardFilter(v, result);
    /* on retire article adjectif */
    //result = new StopFilter(Version.LUCENE_31, result, FrenchAnalyzer.getDefaultStopSet());
    // Convert to lowercase after stemming!
    result = new LowerCaseFilter(v , result);
    result = new ASCIIFoldingFilter(result);

    return result;
  }

}
