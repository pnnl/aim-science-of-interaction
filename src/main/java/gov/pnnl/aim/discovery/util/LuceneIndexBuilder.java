package gov.pnnl.aim.discovery.util;

import gov.pnnl.aim.discovery.data.MatrixProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


/**
 * Class for building an AIM-SOI Lucene index. This is geared specifically
 * toward this project's requirements. It isn't a general-use class.
 * 
 * @author Grant Nakamura, April 2015
 */
public class LuceneIndexBuilder {

    // Not instantiable
    private LuceneIndexBuilder() {
    }

    /** Indexes a list of files. */
    public static void indexFiles(File indexDir, List<File> fileList) throws IOException {
        // Select an analyzer for tokenizing text.
        // Note: The same type of analyzer should be used for any later searching of the index.
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);

        Directory index = FSDirectory.open(indexDir);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);
        config.setOpenMode(OpenMode.CREATE);
        IndexWriter w = new IndexWriter(index, config);
        
        indexFiles(w, fileList);
        
        w.close();
        index.close();
    }
    
    /** Indexes a list of files. */
    private static void indexFiles(IndexWriter out, List<File> fileList) throws IOException {
        for (File f : fileList) {
            String directory = f.getParentFile().getName();
            String filename = f.getName();
            String id = String.format("%s/%s", directory, filename);
            
            String content = readFile(f);
            addDoc(out, id, f, content);
        }
    }
    
    /** Gets the content of a text file. */
    private static String readFile(File f) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(f))) {
            StringBuilder buffer = new StringBuilder();
            
            final String EOL = System.getProperty("line.separator");
            
            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line);
                buffer.append(EOL);
            }
            return buffer.toString();
        }
    }

    /** Adds a document to the index. */
    private static void addDoc(IndexWriter out, String id, File file, String content) throws IOException {
        Document doc = new Document();
        doc.add(new Field("id", id, Field.Store.YES, Field.Index.NO));
        doc.add(new Field("path", file.getAbsolutePath(), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("text__TEXT", content, Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, TermVector.WITH_POSITIONS));
        out.addDocument(doc);
    }

    /** Driver to pre-generate index for the data set in the Summer 2015 user study. */
    public static void main(String... args) {
        try {
            // Assume StreamDocs as current dir
            
            File indexDir = new File("index");
            
            List<File> fileList = new ArrayList<File>();
            
            String[] dirArray = { "base", "inc1", "inc2", "inc3", "inc4" };
            
            for (String dir : dirArray) {
                File contentDir = new File("content", dir);
                File[] files = contentDir.listFiles();
                fileList.addAll(Arrays.asList(files));
            }
            
            indexFiles(indexDir, fileList);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

























