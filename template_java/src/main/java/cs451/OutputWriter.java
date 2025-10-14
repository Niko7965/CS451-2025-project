package cs451;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OutputWriter {
//https://www.baeldung.com/java-synchronized was used as reference for sync

    private final BufferedWriter writer;

    public OutputWriter(String path) throws IOException {
        writer = new BufferedWriter(new FileWriter(path));
    }

    public void write(String s) throws IOException {
        synchronized (writer){
            writer.write(s);
        }
    }

    public void close() throws IOException {
        synchronized (writer){
            writer.close();
        }
    }
}
