package utils.file;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import static logs.LogerBot.sendException;

public class FileProcessor {
    private static String filePrototype = "/home/main/programs/";

    public File getFile(String text, String fileName) {
        //Cleaning up
        File file = new File(filePrototype + fileName);
        try {
            file.delete();
            file.createNewFile();
        } catch(Exception e) {
            sendException(e);
            System.out.println(e.getMessage());
        }


        try {
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(fileName, true));
            out.write(text);
            out.close();
        } catch(Exception e) {
            sendException(e);
            System.out.println(e.getMessage());
        }
        return file;
    }
}
