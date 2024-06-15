package tn.vermeg.vermegapplication.Services;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LogProcessorService {

    public void processLogFile(String localDirectoryPath, String fileName) throws IOException {
        // Path to your Logstash executable
        String logstashPath = "C:\\logstash\\bin\\logstash.bat";
        // Path to the Logstash configuration file
        String configPath = "C:\\logstash\\config\\logstash.conf";

        // Construct the command to process the log file
        String command = logstashPath + " -f " + configPath + " --path.data " + localDirectoryPath + " --file " + fileName;

        // Start the Logstash process
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.inheritIO();
        Process process = processBuilder.start();

        // Wait for Logstash to finish processing
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
