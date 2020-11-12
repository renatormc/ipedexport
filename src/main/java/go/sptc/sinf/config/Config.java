package go.sptc.sinf.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import go.sptc.sinf.services.ResourceManager;

public class Config {

    private static Map<String, Class> typesMap = Collections
            .synchronizedMap(new TreeMap<String, Class>(new StringComparator()));

    public static String destFolder;
    public static String caseFolder;
    public static Integer limit;
    public static String query;
    public static String logsFolder = "./.ipedexport";
    public static Boolean verbose;
    public static Boolean hasDB = true;

    private static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
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

    public static void load() {
        try {
            Ini ini = new Ini(new File("./.ipedexport/config.ini"));
            limit = ini.get("main", "limit", Integer.class);
            verbose = ini.get("main", "verbose", Boolean.class);
            destFolder = ini.get("main", "destDir", String.class);
            if(!(new File(destFolder)).exists()){
                System.out.printf("Pasta de destino \"%s\" não existe.\n", destFolder);
                System.exit(1);
            }
            System.out.printf("Pasta de destino: %s\n", destFolder);
            caseFolder = ".";
            query = readQueryFile("./.ipedexport/query.txt");
            System.out.printf("Query: %s\n", query);
            if(!isDirEmpty(Paths.get(destFolder))){
                System.out.printf("O diretório \"%s\" não está vazio.\n", destFolder);
                System.exit(1);
            }
            if(!(new File(caseFolder, "sleuth.db").exists())){
                hasDB = false;
            }
        } catch (InvalidFileFormatException e) {
            System.out.println("Formato do arquivo config.ini é inválido");
            System.exit(1);
        } catch (IOException e) {
            
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void initDir() {
        File directory = new File("./.ipedexport");

        try {
            if (directory.exists()) {
                FileUtils.deleteDirectory(directory);
            }
            directory.mkdir();
            ResourceManager.copyInitFolder();            

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static boolean isDirEmpty(final Path directory) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }
}