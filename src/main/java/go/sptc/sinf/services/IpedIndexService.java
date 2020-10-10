package go.sptc.sinf.services;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.sleuthkit.datamodel.Content;
import org.sleuthkit.datamodel.SleuthkitCase;
// import org.sleuthkit.datamodel.TskCoreException;
import org.apache.lucene.search.IndexSearcher;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;

import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;

import java.text.DecimalFormat;

import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.AtomicReader;

import go.sptc.sinf.config.Config;

public class IpedIndexService {

    // private static IpedIndexService instance;
    private IndexSearcher searcher;
    private Analyzer analyzer;
    private int hitsPerPage = 10;
    private static Analyzer spaceAnalyzer = new WhitespaceAnalyzer(Version.LUCENE_4_9);
    private HashMap<String, NumericConfig> numericConfigMap;
    private AtomicReader atomicReader;
    private int totalDocuments;
    private String casePath;
    private SleuthkitCase sleuthCase;
    private Logger logger;

    public void setHitsPerPage(int value) {
        hitsPerPage = value;
    }

    public void setHitsPerPageAsTotal() {
        hitsPerPage = totalDocuments;
    }

    public IpedIndexService(String casePath, Logger logger) throws Exception {
        this.casePath = casePath;
        // analyzer = new StandardAnalyzer(Version.LUCENE_4_9);
        analyzer = AppAnalyzer.get();
        Path pathImage = Paths.get(casePath, "indexador\\index");
        Directory directory = FSDirectory.open(new File(pathImage.toString()));
        IndexReader reader = DirectoryReader.open(directory);
        totalDocuments = reader.maxDoc();
        atomicReader = SlowCompositeReaderWrapper.wrap(reader);
        searcher = new IndexSearcher(reader);

        sleuthCase = SleuthkitCase.openCase(casePath + "\\sleuth.db");
        this.logger = logger;
       
    }

    private HashMap<String, NumericConfig> getNumericConfigMap() {

        if (numericConfigMap != null)
            return numericConfigMap;

        numericConfigMap = new HashMap<String, NumericConfig>();

        DecimalFormat nf = new DecimalFormat();
        NumericConfig configLong = new NumericConfig(NumericUtils.PRECISION_STEP_DEFAULT, nf, NumericType.LONG);
        NumericConfig configInt = new NumericConfig(NumericUtils.PRECISION_STEP_DEFAULT, nf, NumericType.INT);
        NumericConfig configFloat = new NumericConfig(NumericUtils.PRECISION_STEP_DEFAULT, nf, NumericType.FLOAT);
        NumericConfig configDouble = new NumericConfig(NumericUtils.PRECISION_STEP_DEFAULT, nf, NumericType.DOUBLE);

        numericConfigMap.put("size", configLong);
        numericConfigMap.put("id", configInt);
        numericConfigMap.put("sleuthId", configInt);
        numericConfigMap.put("parentId", configInt);
        numericConfigMap.put("ftkId", configInt);

        try {
            for (String field : atomicReader.fields()) {
                Class<?> type = Config.getMetadataTypes().get(field);
                if (type == null)
                    continue;
                if (type.equals(Integer.class) || type.equals(Byte.class))
                    numericConfigMap.put(field, configInt);
                else if (type.equals(Long.class))
                    numericConfigMap.put(field, configLong);
                else if (type.equals(Float.class))
                    numericConfigMap.put(field, configFloat);
                else if (type.equals(Double.class))
                    numericConfigMap.put(field, configDouble);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return numericConfigMap;
    }

    public Query getQuery(String texto) throws ParseException, QueryNodeException {

        if (texto.trim().isEmpty())
            return new MatchAllDocsQuery();

        else {
            String[] fields = {"name", "content"};

            StandardQueryParser parser = new StandardQueryParser(analyzer);
            parser.setMultiFields(fields);
            parser.setAllowLeadingWildcard(true);
            parser.setFuzzyPrefixLength(2);
            parser.setFuzzyMinSim(0.7f);
            parser.setDateResolution(DateTools.Resolution.SECOND);
            parser.setMultiTermRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_QUERY_REWRITE);
            parser.setNumericConfigMap(getNumericConfigMap());

            // remove acentos, pois StandardQueryParser nÃ£o normaliza wildcardQueries

            if (analyzer != spaceAnalyzer) {
                char[] input = texto.toCharArray();
                char[] output = new char[input.length * 4];
                FastASCIIFoldingFilter.foldToASCII(input, 0, output, 0, input.length);
                texto = (new String(output)).trim();
            }

            try {
                return parser.parse(texto, null);
            } catch (org.apache.lucene.queryparser.flexible.core.QueryNodeException e) {
                throw new QueryNodeException(e);
            }
        }

    }

    public Document getFirst(String query) throws Exception {
        Query q = new QueryParser(Version.LUCENE_4_9, "content", analyzer).parse(query);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        if (hits.length == 0) {
            return null;
        }
        return searcher.doc(hits[0].doc);
    }

    public Document getDocById(String id) throws Exception {
        Query q = new QueryParser(Version.LUCENE_4_9, "id", analyzer).parse(id);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        if (hits.length == 0) {
            return null;
        }
        return searcher.doc(hits[0].doc);
    }

    public ArrayList<HashMap<String, Object>> query(String querystr, String fields) throws Exception {
        fields = fields.trim();
        ArrayList<String> fieldList = new ArrayList<String>();
        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        Query q = getQuery(querystr);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        if (hits.length == 0) {
            return result;
        }

        if (fields.equals("*")) {
            Document firstDoc = searcher.doc(hits[0].doc);
            for (IndexableField indexableField : firstDoc.getFields()) {
                fieldList.add(indexableField.name());
            }
        } else {
            for (String field : fields.split("\\s*,\\s*")) {
                fieldList.add(field);
            }
        }

        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            HashMap<String, Object> item = new HashMap<String, Object>();
            for (String field : fieldList) {
                item.put(field, d.get(field));
            }
            result.add(item);
        }
        return result;

    }

    public File exportFile(HashMap<String, Object> data, File destDir) {
        System.out.printf("Exportando arquivo \"%s\"\n", data.get("name"));
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
        String export = data.get("export").toString();
        String filename = data.get("name").toString();
        String path = data.get("path").toString();
        Long sleuthId = new Long(0);
        File destFile = new File(destDir.getAbsolutePath(), filename);
        if(data.get("sleuthId") != null){
            sleuthId = Long.valueOf(data.get("sleuthId").toString());
        }
        if ((export != null) && (!export.isEmpty())) {
            try {
                Files.copy(new File(casePath, export).toPath(), destFile.toPath());
                return destFile;
            } catch (IOException e) {
                
                System.out.printf("Não foi possível copiar o arquivo %s\n", export);
            }
        } else {
            try {
                Content content = sleuthCase.getContentById(sleuthId);

                int read = 0;
                int bufferSize = 1024;
                byte[] buf = new byte[bufferSize];
                FileOutputStream os = new FileOutputStream(destFile);

                long total = 0;
                long size = content.getSize();
                while (total < size) {
                    read = content.read(buf, total, bufferSize);
                    os.write(buf, 0, read);
                    total += read;
                }
                os.flush();
                os.close();
                return destFile;
            } catch (Exception e) {
                System.out.printf("Não foi possível copiar o arquivo \"{%s}\"\n", path);
                // App.LOGGER.error("Não foi possível copiar o arquivo \"{}\"", path);
                
            }
        }
        return null;
    }
}
