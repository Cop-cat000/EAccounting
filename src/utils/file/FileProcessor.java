package utils.file;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;


public class FileProcessor {
    private static final String filePrefix = "/home/main/programs/";

    public File getFile(String text, String fileName) throws Exception {
        //Cleaning up
        File file = new File(filePrefix + fileName);
        try {
            file.delete();
            file.createNewFile();
        } catch(Exception e) {
            throw new Exception(e);
        }


        try {
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(filePrefix + fileName, true));
            out.write(text);
            out.close();
        } catch(Exception e) {
            throw new Exception(e);
        }
        return file;
    }
}
