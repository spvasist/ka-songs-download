package com.spvasist.util.kasongsdownload;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class KaSongsDownloadApplication {

    public static void main(String[] args) {
        SpringApplication.run(KaSongsDownloadApplication.class, args);
        List<String> urls;
        String baseDownloadPath = "d:\\kannada-audio\\";
        String urlListPath = "d:\\kannada-audio\\urls.txt";
        String errorFile = "error-urls.txt";
        try {
            urls = FileUtils.readLines(new File(urlListPath), "UTF-8");
            for (String url : urls) {
                if (!Album.saveAlbum(url, baseDownloadPath)) {
                    //Save the error urls
                    FileUtils.write(Paths.get(baseDownloadPath, errorFile).toFile(), url, "UTF-8", true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
