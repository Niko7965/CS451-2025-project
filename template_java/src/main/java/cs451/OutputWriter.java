package cs451;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OutputWriter {
//https://www.baeldung.com/java-synchronized was used as reference for sync

    int lineNumber;
    private final BufferedWriter writer;

    public OutputWriter(String path) throws IOException {
        writer = new BufferedWriter(new FileWriter(path));
        lineNumber = 0;
    }

    public void write(String s) throws IOException {
        synchronized (writer){
            writer.write(s);
            lineNumber += 1;
            if(lineNumber == 100){
                writer.flush();
                lineNumber = 0;
            }
        }
    }

    public void close() throws IOException {
        synchronized (writer){
            writer.close();
        }
    }
}
