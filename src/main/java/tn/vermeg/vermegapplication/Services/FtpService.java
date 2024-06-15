package tn.vermeg.vermegapplication.Services;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Service
public class FtpService {

    public void downloadFile(String username, String password, String host, int port, String remoteFilePath, String localDirectoryPath) throws Exception {
        FTPClient ftpClient = new FTPClient();

        // Extract file name from remote file path
        String fileName = new File(remoteFilePath).getName();
        String localFilePath = localDirectoryPath + File.separator + fileName;
        File localFile = new File(localFilePath);

        // Ensure the directory exists
        File directory = localFile.getParentFile();
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new Exception("Failed to create directory: " + directory.getAbsolutePath());
            }
        }

        // Ensure write permissions
        if (!directory.canWrite()) {
            throw new Exception("No write permissions for directory: " + directory.getAbsolutePath());
        }

        try {
            ftpClient.connect(host, port);
            if (!ftpClient.login(username, password)) {
                throw new Exception("Could not login to the FTP server with the provided credentials.");
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Debugging logs
            System.out.println("Connected to FTP server: " + host);
            System.out.println("Downloading remote file: " + remoteFilePath);
            System.out.println("Local file path: " + localFilePath);

            try (OutputStream outputStream = new FileOutputStream(localFile)) {
                boolean success = ftpClient.retrieveFile(remoteFilePath, outputStream);
                if (!success) {
                    throw new Exception("Could not download the file from FTP server. Check if the file exists and the user has permission to access it.");
                }
                System.out.println("File downloaded successfully!");
            }
        } catch (Exception e) {
            System.err.println("FTP error: " + e.getMessage());
            throw e;
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (Exception e) {
                    System.err.println("Error disconnecting from FTP server: " + e.getMessage());
                }
            }
        }
    }
}
