package edu.entm.illinois.edu.sawbo.VideoLibrary;

import java.util.List;

/**
 * Created by Mahsa on 4/3/2017.
 */

public class Video {
    public List<edu.entm.illinois.edu.sawbo.VideoLibrary.all> all;
    public List<String>Language;
    public List<String>Country;
    public List<String>Topic;

    public List<edu.entm.illinois.edu.sawbo.VideoLibrary.all> getAll() {
        return all;
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


}



