package go.sptc.sinf.services;

import go.sptc.sinf.config.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private final String logfile;
    private FileWriter writer;

    public Logger(String logfile) {
        File logDir = new File(Config.logsFolder);
        if(!logDir.exists()){
            logDir.mkdir();
        }
        this.logfile = Config.logsFolder + logfile;
    }

    public void start() {
        try {
            writer = new FileWriter(logfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String message){
        try {
            writer.write(getCurrentLocalDateTimeStamp() + ": " + message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentLocalDateTimeStamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
   
}
