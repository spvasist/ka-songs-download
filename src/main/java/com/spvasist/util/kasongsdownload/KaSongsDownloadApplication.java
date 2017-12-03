package com.spvasist.util.kasongsdownload;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class KaSongsDownloadApplication {
    private static ApplicationProperties appConfig;

    @Autowired
    public KaSongsDownloadApplication(ApplicationProperties config) {
        appConfig = config;
    }


    public static void main(String[] args) {
        SpringApplication.run(KaSongsDownloadApplication.class, args);
        try {
            List<String> urls = FileUtils.readLines(new File(appConfig.getUrlsFilePath()), "UTF-8");
            for (String url : urls) {
                Album album = new Album(appConfig, url);
                if (!album.saveAlbum()) {
                    //Save the error urls
                    FileUtils.write(Paths.get(appConfig.getBaseDownloadDirPath(), appConfig.getUrlErrorsFileName()).toFile()
                            , url
                            , "UTF-8"
                            , true);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
