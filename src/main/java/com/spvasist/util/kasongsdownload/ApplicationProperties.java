package com.spvasist.util.kasongsdownload;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ka.details")
public class ApplicationProperties {

    private String urlsFilePath;
    private String urlErrorsFileName;
    private String baseDownloadDirPath;

    public String getUrlsFilePath() {
        return urlsFilePath;
    }

    public void setUrlsFilePath(String urlsFilePath) {
        this.urlsFilePath = urlsFilePath;
    }

    public String getUrlErrorsFileName() {
        return urlErrorsFileName;
    }

    public void setUrlErrorsFileName(String urlErrorsFileName) {
        this.urlErrorsFileName = urlErrorsFileName;
    }

    public String getBaseDownloadDirPath() {
        return baseDownloadDirPath;
    }

    public void setBaseDownloadDirPath(String baseDownloadDirPath) {
        this.baseDownloadDirPath = baseDownloadDirPath;
    }
}
