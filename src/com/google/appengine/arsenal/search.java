package com.google.appengine.arsenal;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
/**
 * search servlet used by the html user interface to find information about the custom 3D models (architect, description, ...)
 * <br>
 * based on the Lucene samples org.apache.lucene.demo.SearchFiles
 * 
 * @author razvanculea
 * @see http://lucene.apache.org/java/4_0/demo.html for details
 */
@SuppressWarnings("serial")
@WebServlet("/search")
public class search extends HttpServlet {
  
  /**
   * Search servlet initializer creating the in memory Lucene index.
   * 
   * Index is stored as an application attribute named <i>luceneindex</i>
   * 
   * This init needs to be shorter than the webserver start time-out (2min on std jetty 8)
   */
  public void init(ServletConfig config) 
      throws ServletException {
    super.init(config);
    Directory idx = (new CreateCsvLuceneIndexRam(getServletConfig().getServletContext().getRealPath("/"))).idx;
    config.getServletContext().setAttribute("luceneindex", idx);
    System.out.println("init - luceneindex has been initialized");
  }
  /**
   * search GET handler, response is written in JS
   * <br>
   * The query string is read from the request parameter "q".<br>
   * Javascript callback function name is read from the request parameter "callback".<br>
   * <br>
   * The query response from the Lucene index is written to the "datas" javascript variable and sent the response. The callback function in called at the end.
   *  
   * @param req HTTP request
   * @param resp HTTP response
   */
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    // setting the response header
    resp.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    resp.setHeader("Pragma","no-cache"); //HTTP 1.0
    resp.setDateHeader ("Expires", 0); //prevents caching at the proxy server
    resp.setContentType("text/plain");
    
    
    /** Lucene searched field  */
    String field = "contents";

    /** query string */
    String queryString = req.getParameter("q");
    /** JS callback function name to be added at the end of the response */
    String callbackString = req.getParameter("callback");
    /** reindex param : lucene should reindex */
    String reindexString = req.getParameter("reindex");
    if (reindexString == null) reindexString = "";
    /** hits per result page */
    int hitsPerPage = 30;
    
    // Using the application Lucene index
    ServletContext application = getServletConfig().getServletContext();
    Directory idx = (Directory) application.getAttribute("luceneindex");
    if (idx == null || "true".equalsIgnoreCase(reindexString)) {
      // initialize the Lucene RAM index, we should never use this if the servlet init was successful (except for a forced reindex)
      idx = (new CreateCsvLuceneIndexRam(getServletConfig().getServletContext().getRealPath("/"))).idx;
      application.setAttribute("luceneindex", idx);
      System.out.println("reindex="+reindexString+". luceneindex has been initialized on first search");
    }
    
    IndexReader reader = IndexReader.open(idx);
    IndexSearcher searcher = new IndexSearcher(reader);
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
    QueryParser parser = new QueryParser(Version.LUCENE_31, field, analyzer);
  
    if (!(queryString == null || queryString.length() == -1)) {
      queryString = queryString.trim();
      if (queryString.length() != 0) {
        Query query = null;
        try {
          query = parser.parse(queryString);
        } catch (ParseException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        System.out.println("Searching for: " + query.toString(field));
        // do the search      
        doPagingSearch( resp,callbackString, searcher, query, hitsPerPage, false);
      } else {
        System.out.println("Query string is empty");
      }
    } else {
      System.out.println("Query string is null");
    }

    searcher.close();
    reader.close();
    
  }
  
  /**
   * This demonstrates a typical paging search scenario, where the search engine presents 
   * pages of size n to the user. The user can then go to the next page if interested in
   * the next hits.
   * 
   * When the query is executed for the first time, then only enough results are collected
   * to fill 5 result pages. If the user wants to page beyond this limit, then the query
   * is executed another time and all hits are collected.
   * 
   * @param resp HTTP response in JS. Sample response :<br><code> var datas = [ 
    {"id":9972,"title":"Empreinte","subtitle":"","zipcode":"","city":"","addr2":"","prime_contractor":"","architect":"","project_management":"","planner":"","photo_counter":"0","lat":"","lon":"","zoom":"5","zoom2":"5","viewport":"","bounds":""},
    {...}
    ];
    callbackFunction(); </code>
   * @param callbackString JS callback function name
   * @param raw "true" output is more human readable, "false" standard JS output
   */
  public  void doPagingSearch( HttpServletResponse resp,String callbackString, IndexSearcher searcher, Query query, 
                                     int hitsPerPage, boolean raw ) throws IOException {
 
    /*
    
    */
    // Collect enough docs to show 5 pages
    TopDocs results = searcher.search(query, 5 * hitsPerPage);
    ScoreDoc[] hits = results.scoreDocs;
    
    int numTotalHits = results.totalHits;
    System.out.println(numTotalHits + " total matching documents");

    int start = 0;
    int end = Math.min(numTotalHits, hitsPerPage);
        
    {

      resp.getWriter().println("var datas = [ ");
      end = Math.min(hits.length, start + hitsPerPage);
      String jsonLine = "";
      
      for (int i = start; i < end; i++) {
        if (raw) {                              // output raw format
          System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
          continue;
        }

        Document doc = searcher.doc(hits[i].doc);
        String path = doc.get("marker_id");
        if (path != null) {
          System.out.println((i+1) + ". " + path);
          String title = doc.get("marker_id");
          if (title != null) {
            System.out.println("   title: " + doc.get("title"));
            //json
            jsonLine = "{\"id\":\""+doc.get("marker_id") + "\""
                +",\"title\":\""+ ((doc.get("title")!=null)?doc.get("title"):"") + "\""
                +",\"subtitle\":\""+((doc.get("subtitle")!=null)?doc.get("subtitle"):"") + "\""
                +",\"zipcode\":\""+((doc.get("zip")!=null)?doc.get("zip"):"") + "\""
                +",\"city\":\""+((doc.get("city")!=null)?doc.get("city").length()==0?"":doc.get("city"):"") + "\""
                +",\"addr2\":\"\",\"prime_contractor\":\"\",\"architect\":\"\",\"project_management\":\"\",\"planner\":\"\",\"photo_counter\":\"0\",\"lat\":\""
                +doc.get("lat")+"\",\"lon\":\""+doc.get("lng")+"\",\"zoom\":\"5\",\"zoom2\":\"5\",\"viewport\":\""+doc.get("viewport")+"\",\"bounds\":\"\"}";
            if (i>start)
              resp.getWriter().println(",");
            resp.getWriter().print(jsonLine);
          }
        } else {
          System.out.println((i+1) + ". " + "No marker_id for this result");
        }
                  
      }
      //add the callback function call
      resp.getWriter().println("];"+callbackString+"();");
    }
  }
  
}
