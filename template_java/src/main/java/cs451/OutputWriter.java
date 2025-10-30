package cs451;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OutputWriter {
//https://www.baeldung.com/java-synchronized was used as reference for sync

    private final BufferedWriter writer;
    int lineNo;

    public OutputWriter(String path) throws IOException {
        writer = new BufferedWriter(new FileWriter(path));
        lineNo = 0;
    }

    public void write(String s) throws IOException {
        synchronized (writer){
            writer.write(s);
            lineNo++;

            if(lineNo == 30){
                lineNo = 0;
                writer.flush();
            }
        }
    }

    public void close() throws IOException {
        synchronized (writer){
            writer.close();
        }
    }
}
