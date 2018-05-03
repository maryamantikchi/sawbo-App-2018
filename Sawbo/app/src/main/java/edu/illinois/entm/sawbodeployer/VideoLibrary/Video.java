package edu.illinois.entm.sawbodeployer.VideoLibrary;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahsa on 4/3/2017.
 */

public class Video implements Serializable, Comparable  {
    private String topic;

    private String url;
    private String country;
    private String language;
    private Boolean sDG9;
    private Boolean sDG5;
    private Boolean sDG6;
    private String image;
    private Boolean sDG7;
    private String lite_file;
    private Boolean sDG8;
    private String iD;
    private String id;
    private int post_date;
    private Boolean sDG1;
    private Boolean sDG2;
    private Boolean sDG3;
    private Boolean sDG4;
    private Boolean sDG12;
    private Boolean sDG13;
    private Boolean sDG10;
    private Boolean sDG11;
    private String category;
    private String description;
    private Boolean sDG16;
    private Boolean sDG17;
    private String gpFile;
    private Boolean sDG14;
    private Boolean sDG15;
    private String title;

    public void setGpFile(String gpFile) {
        this.gpFile = gpFile;
    }

    public void setLiteFile(String liteFile) {
        this.lite_file = liteFile;
    }

    public String getCountry() {
        return country;
    }

    public String getImage() {
        return image;
    }

    public String getLanguage() {
        return language;
    }

    public String getId() {
        return id;
    }

    public String getiD() {
        return iD;
    }

    public String getTopic() {
        return topic;
    }

    public String getLiteFile() {
        return lite_file;
    }

    public String getUrl() {
        return url;
    }

    public int getPostDate() {
        return post_date;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getGpFile() {
        return gpFile;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        int compareage=((Video)o).getPostDate();
        /* For Ascending order*/
        return this.post_date-compareage;
    }

    //    public List<all> all = new ArrayList<>();
//    public List<String>Language;
//    public List<String>Country;
//    public List<String>Topic;
//
//    public List<all> getAll() {
//        return all;
//    }
//
//    public void setAll(List<edu.illinois.entm.sawbodeployer.VideoLibrary.all> all) {
//        this.all = all;
//    }
//
//    public List<String> getLanguage() {
//        return Language;
//    }
//
//    public List<String> getCountry() {
//        return Country;
//    }
//
//    public List<String> getTopic() {
//        return Topic;
//    }
//
//
//    public void setLanguage(List<String> language) {
//        this.Language = language;
//    }
//
//    public void setTopic(List<String> topic) {
//        this.Topic = topic;
//    }
//
//    public void setCountry(List<String> country) {
//        this.Country = country;
//    }
}



