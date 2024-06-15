package tn.vermeg.vermegapplication.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tn.vermeg.vermegapplication.Services.DownloadFileRequest;
import tn.vermeg.vermegapplication.Services.FtpService;
import tn.vermeg.vermegapplication.Services.ScriptRunner;
import tn.vermeg.vermegapplication.entities.Projet;
import tn.vermeg.vermegapplication.repository.ProjetRepository;

import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/projets")
public class FtpServiceController {

    private final FtpService ftpService;
    private final ProjetRepository projetRepository;
    @Value("${elk.script.path}")
    private String elkScriptPath;

    @Value("${elk.url}")
    private String elkUrl;
    private final String localDirectoryPath;
    private final Logger logger = LoggerFactory.getLogger(FtpServiceController.class);

    @Autowired
    public FtpServiceController(FtpService ftpService,
                                ProjetRepository projetRepository,
                                @Value("${elk.script.path}") String elkScriptPath,
                                @Value("${elk.url}") String elkUrl,
                                @Value("${local.directory.path}") String localDirectoryPath) {
        this.ftpService = ftpService;
        this.projetRepository = projetRepository;
        this.elkScriptPath = elkScriptPath;
        this.elkUrl = elkUrl;
        this.localDirectoryPath = localDirectoryPath;
    }

    @PostMapping("/downloadLogFile")
    public ResponseEntity<String> downloadLogFile(@RequestBody DownloadFileRequest request) {
        try {


            String fileName = request.getRemoteFilePath();
            File localFile = new File(localDirectoryPath, fileName);
            if (localFile.exists()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File already exists in the local directory.");
            }

            // Download the file
            ftpService.downloadFile(request.getUsername(), request.getPassword(), request.getHost(), 21, fileName, localDirectoryPath);

            // Check if the project already exists in the database
            Projet existingProject = projetRepository.findByNom(fileName);
            if (existingProject != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A log file with the same name already exists in the database.");
            }

            // Convert the local file to a byte array
            byte[] logFileContent = Files.readAllBytes(localFile.toPath());

            // Save the new project using the constructor
            Projet project = new Projet(fileName, request.getUsername(), new Date(), new Date(), request.getDescription(), "En Cours", logFileContent);
            projetRepository.save(project);

            return ResponseEntity.status(HttpStatus.CREATED).body("File downloaded and project added successfully.");
        } catch (Exception e) {
            logger.error("Failed to download file and add project: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to download file and add project: " + e.getMessage());
        }
    }


    @PostMapping("/upload-log")
    public ResponseEntity<List<String>> processLogFile(@RequestBody LogUploadRequest uploadRequest) throws IOException {
        ScriptRunner.runScript(elkScriptPath);
        File logFile = new File(localDirectoryPath, uploadRequest.getFilename());
        List<String> matchedSubstrings = new ArrayList<>();

        // Get the regex pattern from the searchQuery map based on filterAttribute
        String filterAttribute = uploadRequest.getFilterAttribute();
        String searchQueryPattern = uploadRequest.getSearchQuery().get(filterAttribute);
        if (searchQueryPattern == null || searchQueryPattern.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonList("No search query provided for the given filter attribute"));
        }

        // Create a combined pattern to match the filter attribute followed by the search query pattern
        Pattern pattern = Pattern.compile(Pattern.quote(filterAttribute) + "\\s*[ :=]\\s*" + searchQueryPattern , Pattern.CASE_INSENSITIVE);

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    // Extract the matched group which includes the filter attribute and the value
                    String matchedSubstring = matcher.group();
                    matchedSubstrings.add(matchedSubstring);
                }
            }
        } catch (IOException e) {
            logger.error("Error processing log file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList("Error processing log file: " + e.getMessage()));
        }

        if (!matchedSubstrings.isEmpty()) {
            Path outputPath = Paths.get(logFile.getParent(), "processed-" + logFile.getName());
            try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                for (String matchedSubstring : matchedSubstrings) {
                    writer.write(matchedSubstring);
                    writer.newLine();
                }
            } catch (IOException e) {
                logger.error("Error writing matched substrings to file: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList("Error writing matched substrings to file: " + e.getMessage()));
            }

            uploadToELK(outputPath.toFile());
        }

        return ResponseEntity.ok(matchedSubstrings);
    }




    private void uploadToELK(File logFile) {


        try (BufferedReader reader = Files.newBufferedReader(logFile.toPath())) {
     ;
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

            String logContent = fileContent.toString();
            logger.info("Log content to be sent to Elasticsearch: {}", logContent);

            Map<String, String> jsonMap = Collections.singletonMap("log", logContent);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(jsonMap, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(elkUrl + "/logs/_doc/", requestEntity, Map.class);

            logger.info("Response from Elasticsearch: {}", response.getBody());

            String docId = (String) Objects.requireNonNull(response.getBody()).get("_id");
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(elkUrl + "/logs/_doc/" + docId);
            ResponseEntity<String> verificationResponse = restTemplate.getForEntity(builder.toUriString(), String.class);
            logger.info("Verification response from Elasticsearch: {}", verificationResponse.getBody());
        } catch (Exception e) {
            logger.error("Error uploading to Elasticsearch: {}", e.getMessage(), e);
        }
    }










    // Method to check if the specified field is encrypted and secure
    private boolean isFieldSecure(byte[] logFileData, String fieldName) {

        String logDataString = new String(logFileData);


        return logDataString.contains(fieldName + ": encrypted");
    }




























    public static class LogUploadRequest {
        private String filename;
        private String filterAttribute;
        private Map<String, String> searchQuery;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getFilterAttribute() {
            return filterAttribute;
        }

        public void setFilterAttribute(String filterAttribute) {
            this.filterAttribute = filterAttribute;
        }

        public Map<String, String> getSearchQuery() {
            return searchQuery;
        }

        public void setSearchQuery(Map<String, String> searchQuery) {
            this.searchQuery = searchQuery;
        }
    }


















}
