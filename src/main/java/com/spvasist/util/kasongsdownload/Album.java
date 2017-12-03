package com.spvasist.util.kasongsdownload;


import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Data
public class Album {
    private String albumUrl;
    private String imageUrl;
    private String title;
    private Map<String, URL> songToUrlMap;

    private final ApplicationProperties appConfig;

    public Album(ApplicationProperties appConfig, String albumUrl) {
        this.appConfig = appConfig;
        this.albumUrl = albumUrl;
    }

    /**
     * @return Returns true if entire album save was successful.
     */
    boolean saveAlbum() {
        //Phase - 1. Parse the web page and get all the song urls.
        if (!getAlbumObjectFromWebPage(albumUrl))
            return false;
        //Phase - 2. Download songs and images.
        return downloadRmFiles();
    }

    private boolean getAlbumObjectFromWebPage(String albumUrl) {
        try {
            //Download the web page with album details.
            Document doc = Jsoup.connect(albumUrl).get();
            /*
            Entire section is specific to kannadaaudio.com.
            Can be moved to parser module.
            */
            {
                //Go to the element where the exact details are hidden.
                Elements tailNodes = doc.getElementsByClass("right_content_y").first()
                        .getElementsByTag("table").get(4)
                        .getElementsByTag("tr");
                //Obtain the url to image.
                try {
                    String relativeUrl = tailNodes.get(1).getElementsByTag("img").attr("src");
                    if (!StringUtils.isEmpty(relativeUrl))
                        this.imageUrl = finalUrl(albumUrl, relativeUrl);
                } catch (Exception ex) {
                    //Skip and move
                }
                //Scrap to get album title
                this.title = tailNodes.get(0).getElementsByTag("strong").text();
                Elements songs = tailNodes.get(1)
                        .getElementsByTag("a");
                //Remove the last link which is link to all songs.
                songs.remove(songs.size() - 1);
                /*
                Each link will point to a .ram file which contains the link to actual .rm files.
                Read the link from .ram and then obtain the actual .rm file link.
                */
                this.songToUrlMap = new HashMap<>();
                for (Element songElement : songs) {
                    URL url = getRmUrlFromRamUrl(finalUrl(albumUrl, songElement.attr("href")));
                    String songTitle = songElement.text();
                    this.songToUrlMap.put(songTitle, url);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean downloadRmFiles() {
        try {
            //Create directories if not present
            File file = Paths.get(appConfig.getBaseDownloadDirPath(), this.title).toFile();
            if (!file.mkdirs() && !file.exists()) {
                return false;
            }
            //Download image
            if (!StringUtils.isEmpty(this.imageUrl)) {
                FileUtils.copyURLToFile(new URL(this.imageUrl), Paths.get(file.toString(), "image.jpg").toFile()
                        , 30000
                        , 600000);
            }
            //Download files
            for (Map.Entry<String, URL> entry : this.songToUrlMap.entrySet()) {
                FileUtils.copyURLToFile(entry.getValue()
                        , Paths.get(file.getPath(), entry.getKey() + ".rm").toFile()
                        , 30000
                        , 600000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String finalUrl(String baseUrl, String relativeUrl) {
        URI url = URI.create(baseUrl);
        return url.resolve(relativeUrl).toString();
    }

    private URL getRmUrlFromRamUrl(String ramUrl) {
        try {
            return new URL(IOUtils.toString(new URL(ramUrl), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
