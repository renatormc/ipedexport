package go.sptc.sinf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import go.sptc.sinf.config.Config;
import go.sptc.sinf.services.IpedIndexService;
import go.sptc.sinf.services.Logger;
import me.tongfei.progressbar.ProgressBar;


public class Copier {
    private final Logger copyLogger;
    private final Logger hashLogger;
    private OutputStreamWriter hashWriter;
    private OutputStreamWriter exportedWriter;

    public Copier() {
        copyLogger = new Logger("copy.log");
        hashLogger = new Logger("hash.log");
    }

    public void run() {
        try {
            copyLogger.start();
            hashLogger.start();
            hashWriter = new OutputStreamWriter(new FileOutputStream(Config.logsFolder + "/hash.txt"),
                    Charset.forName("UTF-8").newEncoder());
            exportedWriter = new OutputStreamWriter(new FileOutputStream(Config.logsFolder + "/exported.csv"),
                    Charset.forName("UTF-8").newEncoder());
            writeExportedHeader();
            IpedIndexService ipedService = new IpedIndexService(Config.caseFolder, copyLogger);
            String queryString = Config.query;
            if (Config.limit > -1) {
                ipedService.setHitsPerPage(Config.limit);
                System.out.printf("Limite estabelecido de %d\n", Config.limit);
            } else {
                ipedService.setHitsPerPageAsTotal();
            }

            ArrayList<HashMap<String, Object>> data = ipedService.query(queryString, "*");
            File destFile;
            copyLogger.write("Iniciando os trabalhos");

            for (HashMap<String, Object> hashMap : ProgressBar.wrap(data, "TaskName")) {
                if (hashMap.get("category") == null) {
                    continue;
                }
                File categoryFolder = new File(Config.destFolder, hashMap.get("category").toString());

                destFile = ipedService.exportFile(hashMap, categoryFolder);
                if (destFile != null) {
                    // calculateHash(destFile, hashMap.get("path").toString());
                    registerExported(hashMap, destFile);
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            hashLogger.close();
            copyLogger.close();
            try {
                exportedWriter.close();
            } catch (IOException e1) {
                System.out.println("Não foi possível fechar o arquivo csv de items");
                e1.printStackTrace();
            }
            try {
                hashWriter.close();
            } catch (IOException e) {
                if(Config.verbose){
                    e.printStackTrace();
                }
                hashLogger.write("Não foi possível fechar o arquivo de hashes");
            }
        }
    }

   
    private void writeExportedHeader(){
        String row = String.join(",","Arquivo","SHA-256", "MD5", "Tamanho", "Categoria", "Caminho original");

        try {
            exportedWriter.write(row);
            exportedWriter.write("\n");
        } catch (IOException e) {
            System.out.println(e.toString());
            System.exit(1);
        }
        
    }

    private void registerExported(HashMap<String, Object> item, File destFile){
        String sha256 = "";
        String md5 = "";
        String size = "";
        String category = "";
        String originalPath = "";
        if (item.get("sha-256") != null){
            sha256 = item.get("sha-256").toString();
        }
        if (item.get("md5") != null){
            md5 = item.get("md5").toString();
        }
        if (item.get("size") != null){
            size = item.get("size").toString();
        }
        if (item.get("category") != null){
            category = item.get("category").toString();
        }
        if (item.get("path") != null){
            originalPath = item.get("path").toString();
        }
        try {
            String row = String.join(",", destFile.toString(),sha256, md5, size, category, originalPath);
            exportedWriter.write(row);
            exportedWriter.write("\n");
            // exportedWriter.write(item.toString());
            // exportedWriter.write("\n");
        } catch (IOException e) {
            copyLogger.write(String.format("Não foi possível registrar a copia do arquivo \"%s\"\n", destFile));
        }
    }

   

}
