package cs451;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OutputWriter {
//https://www.baeldung.com/java-synchronized was used as reference for sync

    private final BufferedWriter writer;
    boolean open;

    public OutputWriter(String path) throws IOException {
        writer = new BufferedWriter(new FileWriter(path));
        open = true;
    }

    public void write(String s) throws IOException {
        synchronized (writer){
            if(!open){
                return;
            }

            writer.write(s);
            writer.flush();

        }
    }

    public void close() throws IOException {
        synchronized (writer){
            writer.flush();
            writer.close();
            open = false;
        }
    }
}
