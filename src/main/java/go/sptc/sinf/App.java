package go.sptc.sinf;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import go.sptc.sinf.config.Config;
import go.sptc.sinf.services.IpedIndexService;
import go.sptc.sinf.services.Logger;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class App {

    public static void main(String[] args) {
        BlockingQueue<HashTask> hashQueue = new LinkedBlockingQueue<>(10);

        ArgumentParser parser = ArgumentParsers.newFor("IpedExport").build().defaultHelp(true)
                .description("Exports files by category from iped processing results.");
        parser.addArgument("-d", "--dest").help("Destination folder");
        parser.addArgument("-c", "--case").help("Case folder.");
        parser.addArgument("-q", "--query").help("Path to the file that contains the query");
        parser.addArgument("-l", "--limit").type(Integer.class).setDefault(-1).help(
                "Limit. Max number of files to export. Case negative all files that pass the filter specified at the query file will be exported");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        Logger copyLogger = new Logger("copylog.log");
        Logger hashLogger = new Logger("hashlog.log");
        try {
            copyLogger.start();
            hashLogger.start();
            Thread copierThread = new Thread(new Copier(ns.getString("dest"), ns.getString("case"),
                    ns.getString("query"), ns.getInt("limit"), hashQueue, copyLogger));
            copierThread.start();

            Thread hashThread = new Thread(new Hasher(hashQueue, hashLogger));
            hashThread.start();

            try {
                copierThread.join();
                hashThread.join();
            } catch (InterruptedException e) {
                System.exit(1);
            }

        } finally {

            copyLogger.close();
            hashLogger.close();
        }

        // try {
        // Config.loadMetadataTypes();
        // IpedIndexService ipedService = new IpedIndexService(ns.getString("case"));
        // String queryString = readQueryFile(ns.getString("query"));
        // Integer limit = ns.getInt("limit");
        // if (limit > -1) {
        // ipedService.setHitsPerPage(limit);
        // System.out.printf("Limite estabelecido de %d\n", limit);
        // } else {
        // ipedService.setHitsPerPageAsTotal();
        // }

        // ArrayList<HashMap<String, Object>> data = ipedService.query(queryString,
        // "*");
        // File destFile;
        // for (HashMap<String, Object> hashMap : data) {
        // destFile = ipedService.exportFile(hashMap, new File(ns.getString("dest"),
        // hashMap.get("category").toString()));
        // if(destFile != null){
        // queue.
        // }

        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }

    // private static String readQueryFile(String filePath) {
    // StringBuilder contentBuilder = new StringBuilder();

    // try (Stream<String> stream = Files.lines(Paths.get(filePath),
    // StandardCharsets.UTF_8)) {
    // stream.forEach(s -> contentBuilder.append(s).append("\n"));
    // } catch (IOException e) {
    // e.printStackTrace();
    // }

    // return contentBuilder.toString();
    // }
}
