package go.sptc.sinf.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ResourceManager {

    // Stores paths to files with the global jarFilePath as the key
    // private static Hashtable<String, String> fileCache = new Hashtable<String, String>();

    /**
     * Extract the specified resource from inside the jar to the local file system.
     * @param jarFilePath absolute path to the resource
     * @return full file system path if file successfully extracted, else null on error
     */
    public static void extract(String jarFilePath, String output){

        if(jarFilePath == null)
            return;

        // // See if we already have the file
        // if(fileCache.contains(jarFilePath))
        //     return fileCache.get(jarFilePath);

        // Alright, we don't have the file, let's extract it
        try {
            // Read the file we're looking for
            InputStream fileStream = ResourceManager.class.getResourceAsStream(jarFilePath);

            // Was the resource found?
            if(fileStream == null){
                return;
            }
                

            // Grab the file name
            // String[] chopped = jarFilePath.split("\\/");
            // String fileName = chopped[chopped.length-1];

            // Create our temp file (first param is just random bits)
            File outFile = new File(output);

         
            // Create an output stream to barf to the temp file
            OutputStream out = new FileOutputStream(outFile);

            // Write the file to the temp file
            byte[] buffer = new byte[1024];
            int len = fileStream.read(buffer);
            while (len != -1) {
                out.write(buffer, 0, len);
                len = fileStream.read(buffer);
            }

            // // Store this file in the cache list
            // fileCache.put(jarFilePath, tempFile.getAbsolutePath());

            // Close the streams
            fileStream.close();
            out.close();

           
        } catch (IOException e) {
            return;
        }
    }

    public static void copyInitFolder(){
        extract("/.ipedexport/config.ini", "./.ipedexport/config.ini");
        extract("/.ipedexport/query.txt", "./.ipedexport/query.txt");
    }
}
