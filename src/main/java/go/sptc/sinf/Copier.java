package go.sptc.sinf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import go.sptc.sinf.config.Config;
import go.sptc.sinf.services.IpedIndexService;
import go.sptc.sinf.services.Logger;
import me.tongfei.progressbar.ProgressBar;
import java.security.DigestInputStream;

public class Copier {
    private final Logger copyLogger;
    private final Logger hashLogger;
    private OutputStreamWriter hashWriter;

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
                if(Config.verbose){
                    e.printStackTrace();
                }
                hashLogger.write("Não foi possível fechar o arquivo de hashes");
            }
        }
    }

    private void calculateHash(File file, String originalPath) {
        if (Config.hashType.equals("NULL")) {
            return;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            MessageDigest digest = MessageDigest.getInstance(Config.hashType);
            DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);
            byte[] bytes = new byte[1024];
            // read all file content
            while (digestInputStream.read(bytes) > 0)
                ;

            // digest = digestInputStream.getMessageDigest();
            byte[] resultByteArry = digest.digest();
            hashWriter.write(String.format("%s - %s\n", file,  bytesToHexString(resultByteArry)));
            
        } catch (IOException | NoSuchAlgorithmException e) {
            if(Config.verbose){
                e.printStackTrace();
            }
            hashLogger.write(String.format("Não foi possível calcular o hash do arquivo \"%s\"", originalPath));
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int value = b & 0xFF;
            if (value < 16) {
                // if value less than 16, then it's hex String will be only
                // one character, so we need to append a character of '0'
                sb.append("0");
            }
            sb.append(Integer.toHexString(value).toUpperCase());
        }
        return sb.toString();
    }

}
