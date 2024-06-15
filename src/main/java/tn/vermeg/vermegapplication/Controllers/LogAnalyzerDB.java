package tn.vermeg.vermegapplication.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.vermeg.vermegapplication.Controllers.FtpServiceController;
import tn.vermeg.vermegapplication.entities.Projet;
import tn.vermeg.vermegapplication.repository.ProjetRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class LogAnalyzerDB {

    @Autowired
    private ProjetRepository projetRepository;

    @PostMapping("/analyseDbLog")
    public ResponseEntity<List<String>> processLogFile(@RequestBody LogAnalysisRequest request) throws IOException, SQLException {
        // Retrieve the project from the database
        Optional<Projet> optionalProject = projetRepository.findById(request.getProjetId());

        Blob logFileBlob;
        if (optionalProject.isPresent()) {
            Projet project = optionalProject.get();
            // Get the log file from the project
            logFileBlob = project.getLogFile();

        } else {
            return ResponseEntity.notFound().build(); // Return 404 if project not found
        }

        // Convert Blob to byte array (assuming the log file is stored as a byte array in the database)
        byte[] logFileData = logFileBlob.getBytes(1, (int) logFileBlob.length());

        // Convert byte array to String (assuming the log file content is stored as a String)
        String logFileContent = new String(logFileData);

        // Get the regex pattern from the searchQuery map based on filterAttribute
        String filterAttribute = request.getUploadRequest().getFilterAttribute();
        String searchQueryPattern = request.getUploadRequest().getSearchQuery().get(filterAttribute);
        if (searchQueryPattern == null || searchQueryPattern.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonList("No search query provided for the given filter attribute"));
        }

        // Create a combined pattern to match the filter attribute followed by the search query pattern
        Pattern pattern = Pattern.compile(Pattern.quote(filterAttribute) + "\\s*[ :=]\\s*" + searchQueryPattern , Pattern.CASE_INSENSITIVE);

        List<String> matchedSubstrings = new ArrayList<>();

        // Process the log file and extract matched substrings
        BufferedReader reader = new BufferedReader(new StringReader(logFileContent));
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                // Extract the matched group which includes the filter attribute and the value
                String matchedSubstring = matcher.group();
                matchedSubstrings.add(matchedSubstring);
            }
        }

        return ResponseEntity.ok(matchedSubstrings);
    }
}
