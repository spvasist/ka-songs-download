package com.spvasist.util.kasongsdownload;


import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    static boolean saveAlbum(String albumUrl, String dirPath) {
        Album album = getAlbumObjectFromWebPage(albumUrl);
        if (album == null) return false;
        if (!downloadRmFiles(album, dirPath))
            return false;
        return true;
    }

    private static Album getAlbumObjectFromWebPage(String albumUrl) {
        try {
            Album album = new Album();
            album.albumUrl = albumUrl;
            Document doc = Jsoup.connect(albumUrl).get();
            Elements tailNodes = doc.getElementsByClass("right_content_y").first()
                    .getElementsByTag("table").get(4)
                    .getElementsByTag("tr");

            try {
                String relativeUrl = tailNodes.get(1).getElementsByTag("img").attr("src");
                if (!relativeUrl.isEmpty())
                    album.imageUrl = finalUrl(albumUrl, relativeUrl);
            } catch (Exception ex) {
            }
            album.title = tailNodes.get(0).getElementsByTag("strong").text();
            Elements songs = tailNodes.get(1)
                    .getElementsByTag("a");
            songs.remove(songs.size() - 1);
            album.songToUrlMap = new HashMap<>();
            for (Element songElement : songs) {
                URL url = getRmUrlFromRamUrl(finalUrl(albumUrl, songElement.attr("href")));
                String songTitle = songElement.text();
                album.songToUrlMap.put(songTitle, url);
            }
            return album;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean downloadRmFiles(Album album, String dirPath) {
        try {
            File file = Paths.get(dirPath, album.title).toFile();
            if (!file.mkdirs() && !file.exists()) {
                return false;
            }
            FileUtils.copyURLToFile(new URL(album.imageUrl), Paths.get(file.toString(), "image.jpg").toFile()
                    , 30000
                    , 10000);
            for (Map.Entry<String, URL> entry : album.songToUrlMap.entrySet()) {
                FileUtils.copyURLToFile(entry.getValue(), Paths.get(file.getPath(), entry.getKey() + ".rm").toFile());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static String finalUrl(String baseUrl, String relativeUrl) {
        URI url = URI.create(baseUrl);
        return url.resolve(relativeUrl).toString();
    }

    private static URL getRmUrlFromRamUrl(String ramUrl) {
        try {
            return new URL(IOUtils.toString(new URL(ramUrl), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
