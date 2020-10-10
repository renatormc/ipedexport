package go.sptc.sinf.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Collections;
import java.util.TreeMap;

// import org.springframework.util.ResourceUtils;

import java.util.Comparator;

public class Config {

    private static Map<String, Class> typesMap = Collections
            .synchronizedMap(new TreeMap<String, Class>(new StringComparator()));

    private static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }

    public static void loadMetadataTypes() throws IOException, ClassNotFoundException {
        // File metadataTypesFile = ResourceUtils.getFile("classpath:metadataTypes.txt");
        // File metadataTypesFile = getClass().getClassLoader().getResourceAsFile("file.txt");
        // ClassLoader classLoader = getClass().getClassLoader();
 
        File metadataTypesFile = new File(Config.class.getResource("/metadataTypes.txt").getFile());
        if (metadataTypesFile.exists()) {
            UTF8Properties props = new UTF8Properties();
            props.load(metadataTypesFile);
            for (String key : props.stringPropertyNames()) {
                typesMap.put(key, Class.forName(props.getProperty(key)));
            }
        }
    }

    public static Map<String, Class> getMetadataTypes() {
        return typesMap;
    }
}