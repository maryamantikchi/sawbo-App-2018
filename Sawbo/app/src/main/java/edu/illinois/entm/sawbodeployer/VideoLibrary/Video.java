package edu.illinois.entm.sawbodeployer.VideoLibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahsa on 4/3/2017.
 */

public class Video {
    public List<all> all = new ArrayList<>();
    public List<String>Language;
    public List<String>Country;
    public List<String>Topic;

    public List<all> getAll() {
        return all;
    }

    public void setAll(List<edu.illinois.entm.sawbodeployer.VideoLibrary.all> all) {
        this.all = all;
    }

    public List<String> getLanguage() {
        return Language;
    }

    public List<String> getCountry() {
        return Country;
    }

    public List<String> getTopic() {
        return Topic;
    }

    public void setLanguage(List<String> language) {
        Language = language;
    }

    public void setTopic(List<String> topic) {
        Topic = topic;
    }

    public void setCountry(List<String> country) {
        Country = country;
    }
}



