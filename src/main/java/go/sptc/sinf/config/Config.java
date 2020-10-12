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
    public static Boolean verbose;

    private static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }

    public static void init(Namespace ns) throws IOException, ClassNotFoundException{
        destFolder = ns.getString("dest");
        System.out.printf("Diretório de destino: \"%s\"\n", destFolder);
        caseFolder = ns.getString("case");
        System.out.printf("Diretório do caso: \"%s\"\n", caseFolder);
        limit = ns.getInt("limit");
        System.out.printf("Limite: \"%d\"\n", limit);
        query = readQueryFile(ns.getString("query"));
        System.out.printf("Query: \"%s\"\n", query);
        logsFolder = ns.getString("logsfolder");
        System.out.printf("Pasta de logs: \"%s\"\n", logsFolder);
        hashType = ns.getString("hash");
        System.out.printf("Tipo do hash: \"%s\"\n", hashType);
        verbose = ns.getBoolean("verbose");
        System.out.printf("Verbose: \"%s\"\n", verbose);
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
            System.exit(1);
        }

        return contentBuilder.toString();
    }

    public static Map<String, Class> getMetadataTypes() {
        return typesMap;
    }
}