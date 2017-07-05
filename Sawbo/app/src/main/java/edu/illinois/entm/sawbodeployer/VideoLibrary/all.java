package edu.illinois.entm.sawbodeployer.VideoLibrary;

import java.io.Serializable;

/**
 * Created by Mahsa on 4/28/2017.
 */

public class all implements Serializable{

    public Number post_date;
    public String url;
    public String country;
    public String topic;
    public String language;
    public Object f4v_file;
    public String image;
    public String lite_file;
    public String mov_file;
    public String description;
    public String ID;
    public String id;
    public String gp_file;
    public String title;
    public String mp4file;

    public String getLite_file() {
        return lite_file;
    }

    public Number getPost_date() {
        return post_date;
    }

    public Object getF4v_file() {
        return f4v_file;
    }

    public String getGp_file() {
        return gp_file;
    }

    public String getMov_file() {
        return mov_file;
    }

    public String getUrl() {
        return url;
    }


    public void setF4v_file(Object f4v_file) {
        this.f4v_file = f4v_file;
    }

    public void setGp_file(String gp_file) {
        this.gp_file = gp_file;
    }

    public void setLite_file(String lite_file) {
        this.lite_file = lite_file;
    }

    public void setMov_file(String mov_file) {
        this.mov_file = mov_file;
    }

    public void setPost_date(int post_date) {
        this.post_date = post_date;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
            return title;
        }

    public String getVideolight() {
            return lite_file;
        }

    public String getCountry() {
            return country;
        }

    public String getDescription() {
            return description;
        }

    public String getId() {
            return id;
        }

    public String getImage() {
            return image;
        }

    public String getLanguage() {
            return language;
        }

    public String getTopic() {
            return topic;
        }

    public String getMp4file() {
            return mp4file;
        }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setMp4file(String mp4file) {
        this.mp4file = mp4file;
    }

    public void setVideolight(String videolight) {
        lite_file = videolight;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}


