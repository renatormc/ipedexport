package go.sptc.sinf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import go.sptc.sinf.config.Config;
import go.sptc.sinf.services.IpedIndexService;
import go.sptc.sinf.services.Logger;
import me.tongfei.progressbar.ProgressBar;


public class Copier {
    private final Logger copyLogger;
    private final Logger hashLogger;
    private FileWriter hashWriter;

    public Copier() {
        copyLogger = new Logger("copy.log");
        hashLogger = new Logger("hash.log");
    }


    public void run() {
        try {
            copyLogger.start();
            hashLogger.start();
            hashWriter = new FileWriter(Config.logsFolder + "/hash.txt");
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
                destFile = ipedService.exportFile(hashMap, new File(Config.destFolder, hashMap.get("category").toString()));
                if (destFile != null) {
                    calculateHash(destFile, hashMap.get("path").toString());
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
                hashWriter.close();
            } catch (IOException e) {
                hashLogger.write("Não foi possível fechar o arquivo de hashes");
            }
        }
    }


    private void calculateHash(File path, String originalPath) {
        if (Config.hashType.equals("NULL")) {
            return;
        }
        try {
            String hash = Arrays.toString(MessageDigest.getInstance("SHA-512").digest(Files.readAllBytes(path.toPath())));
            hashWriter.write(hash);
        } catch (IOException | NoSuchAlgorithmException e) {
            hashLogger.write(String.format("Não foi possível calcular o hash do arquivo \"%s\"", originalPath));
        }
    }

}
