package tn.vermeg.vermegapplication.Controllers;

public class LogAnalysisRequest {
    private Long projetId;

    public Long getProjetId() {
        return projetId;
    }

    public void setProjetId(Long projetId) {
        this.projetId = projetId;
    }

    public FtpServiceController.LogUploadRequest getUploadRequest() {
        return uploadRequest;
    }

    public void setUploadRequest(FtpServiceController.LogUploadRequest uploadRequest) {
        this.uploadRequest = uploadRequest;
    }

    private FtpServiceController.LogUploadRequest uploadRequest;

    // Getters and Setters
}
