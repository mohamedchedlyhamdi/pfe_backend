package tn.vermeg.vermegapplication.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.vermeg.vermegapplication.entities.Projet;
import tn.vermeg.vermegapplication.repository.ProjetRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class LogAnalyzerDB {

    @Autowired
    private ProjetRepository projetRepository;

    @PostMapping("/analyseLogDb")
    public ResponseEntity<List<String>> analyzeLogFile(@RequestBody LogAnalysisRequest request) {
        try {
            Optional<Projet> projectOptional = projetRepository.findById(request.getProjetId());

            if (projectOptional.isPresent()) {
                Projet project = projectOptional.get();
                Blob logFileBlob = project.getLogFile();
                byte[] logFileData = logFileBlob.getBytes(1, (int) logFileBlob.length());
                String logFileContent = new String(logFileData);

                String filterAttribute = request.getUploadRequest().getFilterAttribute();
                String searchQueryPattern = request.getUploadRequest().getSearchQuery().get(filterAttribute);

                if (searchQueryPattern == null || searchQueryPattern.isEmpty()) {
                    return ResponseEntity.badRequest().body(null);
                }

                Pattern pattern = Pattern.compile(Pattern.quote(filterAttribute) + "\\s*[ :=]\\s*" + searchQueryPattern, Pattern.CASE_INSENSITIVE);

                List<String> matchedSubstrings = new ArrayList<>();

                try (BufferedReader reader = new BufferedReader(new StringReader(logFileContent))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = pattern.matcher(line);
                        while (matcher.find()) {
                            String matchedSubstring = matcher.group();
                            matchedSubstrings.add(matchedSubstring);
                        }
                    }
                }

                List<String> results = new ArrayList<>();
                for (String substring : matchedSubstrings) {
                    String value = extractValue(substring, filterAttribute);
                    if (isEncrypted(value)) {
                        results.add(substring + " - Chiffrée");
                    } else {
                        results.add(substring + " - Non Chiffrée");
                    }
                }

                return ResponseEntity.ok().body(results);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException | SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String extractValue(String matchedSubstring, String filterAttribute) {
        int startIndex = matchedSubstring.indexOf(filterAttribute) + filterAttribute.length();
        return matchedSubstring.substring(startIndex).trim().replace("=", "").replace(":", "").trim();
    }

    private boolean isEncrypted(String value) {
        if (value.length() < 16) {
            return false;
        }
        String base64Pattern = "^[A-Za-z0-9+/=]+$";
        if (value.matches(base64Pattern)) {
            return true;
        }
        String hexPattern = "^[A-Fa-f0-9]+$";
        return value.matches(hexPattern);
    }
}
