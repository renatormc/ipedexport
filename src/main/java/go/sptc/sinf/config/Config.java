package go.sptc.sinf.config;

import net.sourceforge.argparse4j.inf.Namespace;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.stream.Stream;

public class Config {

    private static Map<String, Class> typesMap = Collections
            .synchronizedMap(new TreeMap<String, Class>(new StringComparator()));

    public static String destFolder;
    public static String caseFolder;
    public static Integer limit;
    public static String query;
    public static String logsFolder;
    public static String hashType;

    private static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }

    public static void init(Namespace ns) throws IOException, ClassNotFoundException{
        destFolder = ns.getString("dest");
        caseFolder = ns.getString("case");
        limit = ns.getInt("limit");
        query = readQueryFile(ns.getString("query"));
        logsFolder = ns.getString("logsfolder");
        hashType = ns.getString("hash");
        File metadataTypesFile = new File(Config.class.getResource("/metadataTypes.txt").getFile());
        if (metadataTypesFile.exists()) {
            UTF8Properties props = new UTF8Properties();
            props.load(metadataTypesFile);
            for (String key : props.stringPropertyNames()) {
                typesMap.put(key, Class.forName(props.getProperty(key)));
            }
        }
    }


    private static String readQueryFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    public static Map<String, Class> getMetadataTypes() {
        return typesMap;
    }
}