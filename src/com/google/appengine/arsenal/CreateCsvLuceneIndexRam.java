package com.google.appengine.arsenal;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class CreateCsvLuceneIndexRam  {
  Directory idx = null;
  public CreateCsvLuceneIndexRam(String path) {
    /** back up data used if URL is not available */
    /* data format sample 
     * 
     * 11468||arc3||||5|5|||True||Gare Nanterre - Université|Pôle multimodal Nanterre Université||Boulevard des provinces françaises|Seine Arche|Nanterre|92000||En cours||||2015||Arep|Urbanistes des espaces publics : TGT et associés|RATP, RFF, SNCF, EPADESA|STIF : autorité organisatrice  des transports.Financements : Etat, Région Ile-de-France, Conseil général des Hauts-de-Seine, RATP, EPADESA.|EPADESA|Interconnexion RER, SNCF et futur tramway T1.|La gare Nanterre-Université a amorcé en 2006 une véritable mutation. Associant tous les modes de déplacement, train, RER, bus, vélo et, à terme, tramway avec le prolongement de la ligne T1, le nouveau pôle multimodal Nanterre - Université offrira à l’horizon 2015 une gare entièrement neuve. L’architecture du bâtiment voyageur, conçu pour la RATP et la SNCF par Arep, a été pensée pour accueillir 75 000 utilisateurs par jour dans un esprit de confort et de modernité. Les liaisons entre les différents quartiers ont aussi été prises en compte : la gare sera reliée au reste de l’espace urbain par un long parvis. Il fera le trait d’union entre l’Université Paris-Ouest-Nanterre La Défense et les quartiers environnants.|||EPADESA - 2012||||||||
     * 11469||arc3||||5|5|||True||Secteur République Université|||Université|Seine Arche|Nanterre|92000||A l'étude||||||Atelier Ruelle|Bureau d’études Arcadis|||EPADESA|54 000 m²|Les objectifs de l’aménagement du secteur République-Université sont :- reconstituer un tissu urbain cohérent en créant un quartier mixte à dominante résidentielle : une offre de logements diversifiés (objectif 30 000 m² Shon), quelques bâtiments d'activités et de bureaux (8 000 m² environ) et des équipements de proximité,- requalifier l'avenue de la République, où passera à terme le tramway T1, et créer des espaces publics qualitatifs de desserte interne au secteur République - Université - Anatole France,- répondre aux éventuels besoins de développement de programmes universitaires, qualifier l’entrée de l’université sur l’avenue de la République et permettre au campus de s'ouvrir sur son environnement urbain,- accueillir l’université dans la ville dans le cadre d’un projet à définir, en créant une mixité des programmes universitaires et para-universitaires et en développant des équipements à destination de publics universitaires et autres (crèche). La programmation prévue se décompose ainsi : 25 000 m² logement, 23 000 m² equipement universitaire, 6 000 m² bureaux.|||EPADESA - 2012||||||||
     * 11470||urb2||||3|5|||True||Boulevard Pesaro|||Boulevard Pesaro||Nanterre|92000||En cours||||2011||TGT et associés et Y ingénierie||EPADESA||EPADESA||Le prolongement du boulevard Pesaro, actuellement en activité du Pont Picasso à Nanterre-Préfecture, se poursuit jusqu’au boulevard Pascal, en passant devant la cité administrative : Tribunal, Préfecture et Conseil général des Hauts-de-Seine, il en facilite leur accès. Le prolongement du boulevard Pesaro présentera les caractéristiques suivantes : un boulevard à deux fois une voie à vocation de desserte le long des immeubles existants une piste cyclable à double sens, une noue de récupération des eaux pluviales de six mètres de large composée d’arbres et de massifs plantés.|||EPADESA - 2012||||||||
     * 
     */
    String docLocal = path + "/bdd_markers.txt";
    URL urlCloud = null;
    try {
      urlCloud = new URL("http://data.parismetropole2020.com/blackoffice/api/api.php?exec=ficheInfo&export=markers");
    } catch (MalformedURLException e1) {
      System.out.println("Invalid cloud URL : " + e1.getMessage());
    }
    
    
    Date start = new Date();
    try {
      System.out.println("Indexing to directory memory ...");
    
      Directory dir = new RAMDirectory();//  FSDirectory.open(new File(indexPath));
      Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
      IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31, analyzer);
      
      
      // Create a new index in the directory, removing any
      // previously indexed documents:
      iwc.setOpenMode(OpenMode.CREATE);
      
      
      // Optional: for better indexing performance, if you
      // are indexing many documents, increase the RAM
      // buffer.  But if you do this, increase the max heap
      // size to the JVM (eg add -Xmx512m or -Xmx1g):
      //
      // iwc.setRAMBufferSizeMB(256.0);
      
      IndexWriter writer = new IndexWriter(dir, iwc);
      
      
      InputStream isContent = null;
      try {
        isContent = urlCloud.openStream();
      } catch (Exception e){
        // Cloud error, use local file
        System.out.println("Cloud error : " + e.getMessage());
        isContent = new FileInputStream(new File(docLocal));
      }
      indexDocs(writer, isContent);
      
      // NOTE: if you want to maximize search performance,
      // you can optionally call forceMerge here.  This can be
      // a terribly costly operation, so generally it's only
      // worth it when your index is relatively static (ie
      // you're done adding documents to it):
      //
      // writer.forceMerge(1);
      
      writer.close();
      idx = dir;
      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");
      
    } catch (IOException e) {
      System.out.println(" caught a " + e.getClass() +
          "\n with message: " + e.getMessage());
    }
    
  }
  
  /**
   * Indexes the given file using the given writer, or if a directory is given,
   * recurses over files and directories found under the given directory.
   * 
   * NOTE: This method indexes one document per input file.  This is slow.  For good
   * throughput, put multiple documents into your input file(s).  An example of this is
   * in the benchmark module, which can create "line doc" files, one document per line,
   * using the
   * <a href="../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
   * >WriteLineDocTask</a>.
   *  
   * @param writer Writer to the index where the given file/dir info will be stored
   * @param file The file to index, or the directory to recurse into to find files to index
   * @throws IOException
   */
   static void indexDocs(IndexWriter writer, InputStream is)
    throws IOException {
    // do not try to index files that cannot be read
    BufferedReader fis;
    try {
      fis = new BufferedReader(new InputStreamReader(is,"UTF8"));
      String l;
      while ((l = fis.readLine()) != null) {
        
        String[] s = l.split("\\|", -1);
        //12,13,25,26,27,28,29,30,32
        //19 latlng
        if (s.length >= 40)
        if (s[19].split(",").length == 2) {
          Document docT = new Document();
          Field pathFieldT = new Field("marker_id", s[0], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
          pathFieldT.setIndexOptions(IndexOptions.DOCS_ONLY);
          docT.add(pathFieldT);
          
          Field pathFieldT2 = new Field("title", s[12], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
          pathFieldT2.setIndexOptions(IndexOptions.DOCS_ONLY);
          docT.add(pathFieldT2);
          //zip 18. city17
          Field pathFieldT3 = new Field("zip", s[18], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
          pathFieldT3.setIndexOptions(IndexOptions.DOCS_ONLY);
          docT.add(pathFieldT3);
          Field pathFieldT6 = new Field("city", s[17], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
          pathFieldT6.setIndexOptions(IndexOptions.DOCS_ONLY);
          docT.add(pathFieldT6);
          
          //latlng
          Field pathFieldT4 = new Field("lat", (s[19].split(","))[0], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
          pathFieldT4.setIndexOptions(IndexOptions.DOCS_ONLY);
          docT.add(pathFieldT4);
          Field pathFieldT5 = new Field("lng", (s[19].split(","))[1], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
          pathFieldT5.setIndexOptions(IndexOptions.DOCS_ONLY);
          docT.add(pathFieldT5);
          
          //viewport 41
          
          String textT = s[12]+" "+s[13]+" "+s[17]+" "+s[18]+" "+s[25]+" "+s[26]+" "+s[27]+" "+s[28]+" "+s[29]+" "+s[30]+" "+s[32];
          textT = FrenchNormalizer.unFrench(textT);
          docT.add(new Field("contents", new BufferedReader(new InputStreamReader(new ByteArrayInputStream(textT.getBytes()), "UTF-8"))));
          
          writer.addDocument(docT);
          System.out.println("updating " + s[0]);
        } else System.out.println("ignoring no latlng " + s[0]);
        else System.out.println("ignoring no enough columns " + s[0]);
      }
    } catch (FileNotFoundException fnfe) {
      // at least on windows, some temporary files raise this exception with an "access denied" message
      // checking if the file can be read doesn't help
      return;
    }

  }
  
}
