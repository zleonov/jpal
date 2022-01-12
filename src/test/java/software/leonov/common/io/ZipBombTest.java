package software.leonov.common.io;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipBombTest {
    
    public static void main(String[] args) throws IOException {
        
        Path zipfile = Paths.get("c:/software/test.zip");
        System.setProperty("software.leonov.common.io.Zip.MAX_ENTRIES", "2");
       // System.setProperty("software.leonov.common.io.Zip.MAX_SIZE", "1048576");
        Zip.unzip(zipfile, Paths.get("c:/software/test"));
        
    }

}
