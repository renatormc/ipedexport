package go.sptc.sinf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import com.mchange.io.FileUtils;

import org.apache.commons.io.FilenameUtils;

import go.sptc.sinf.config.Config;
import go.sptc.sinf.services.IpedIndexService;
import go.sptc.sinf.services.Logger;
import me.tongfei.progressbar.ProgressBar;

public class Copier {
    private final Logger copyLogger;
    private final Logger hashLogger;
    private OutputStreamWriter hashWriter;
    private OutputStreamWriter exportedWriter;
    private Path destFolderPath;

    public Copier() {
        copyLogger = new Logger("copy.log");
        hashLogger = new Logger("hash.log");
    }

    private File getAvailableFilename(String filename, File folder) {
        File destFile = new File(folder.toString(), filename);
        if (!destFile.exists()) {
            return destFile;
        }
        String ext = FilenameUtils.getExtension(filename);
        String baseName = FilenameUtils.getBaseName(filename);
        int count = 1;
        while (true) {
            destFile = new File(folder.toString(), String.format("%s_modificado_%d.%s", baseName, count, ext));
            if (!destFile.exists())
                break;
            count++;
        }
        return destFile;
    }

    public void run() {
        File categoryFolder;
        File destFile;
        try {
            copyLogger.start();
            hashLogger.start();
            hashWriter = new OutputStreamWriter(new FileOutputStream(Config.logsFolder + "/hash.txt"),
                    Charset.forName("UTF-8").newEncoder());
            exportedWriter = new OutputStreamWriter(new FileOutputStream(Config.logsFolder + "/exported.csv"),
                    Charset.forName("UTF-8").newEncoder());
            destFolderPath = Paths.get(Config.destFolder);
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

            copyLogger.write("Iniciando os trabalhos");

            for (HashMap<String, Object> hashMap : ProgressBar.wrap(data, "TaskName")) {
                if (hashMap.get("category") == null) {
                    continue;
                }
                categoryFolder = new File(Config.destFolder, hashMap.get("category").toString());

                if (!categoryFolder.exists()) {
                    categoryFolder.mkdirs();
                }
                destFile = getAvailableFilename(hashMap.get("name").toString(), categoryFolder);
                destFile = ipedService.exportFile(hashMap, destFile);
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
                if (Config.verbose) {
                    e.printStackTrace();
                }
                hashLogger.write("Não foi possível fechar o arquivo de hashes");
            }
        }
    }

    private void writeExportedHeader() {
        String row = String.join(",", "Arquivo", "SHA-256", "MD5", "Tamanho", "Categoria", "Caminho original",
                "Deletado", "Carved", "Criação", "Modificação");

        try {
            exportedWriter.write(row);
            exportedWriter.write("\n");
        } catch (IOException e) {
            System.out.println(e.toString());
            System.exit(1);
        }

    }

    private void registerExported(HashMap<String, Object> item, File destFile) {
        String sha256 = "";
        String md5 = "";
        String size = "";
        String category = "";
        String originalPath = "";
        String deleted = "";
        String carved = "";
        String created = "";
        String modified = "";
        if (item.get("sha-256") != null) {
            sha256 = item.get("sha-256").toString();
        }
        if (item.get("md5") != null) {
            md5 = item.get("md5").toString();
        }
        if (item.get("size") != null) {
            size = item.get("size").toString();
        }
        if (item.get("category") != null) {
            category = item.get("category").toString();
        }
        if (item.get("path") != null) {
            originalPath = item.get("path").toString();
        }
        if (item.get("deleted") != null) {
            deleted = item.get("deleted").toString();
        }
        if (item.get("carved") != null) {
            carved = item.get("carved").toString();
        }
        if (item.get("created") != null) {
            created = item.get("created").toString();
        }
        if (item.get("modified") != null) {
            modified = item.get("modified").toString();
        }
        try {
            String row = String.join(",", destFolderPath.relativize(destFile.toPath()).toString().replace("\\", "/"),
                    sha256, md5, size, category, originalPath, deleted, carved, created, modified);
            exportedWriter.write(row);
            exportedWriter.write("\n");
        } catch (IOException e) {
            copyLogger.write(String.format("Não foi possível registrar a copia do arquivo \"%s\"\n", destFile));
        }
    }

}
