package edu.illionois.entm.sawbodeployer.sawbo.VideoLibrary;

/**
 * Created by Mahsa on 4/28/2017.
 */

public class all {

        public String id;
        public String Country;
        public String Language;
        public String Topic;
        public String Title;
        public String Video;
        public String Videolight;
        public String Description;
        public String Image;

        public String getTitle() {
            return Title;
        }

        public String getVideolight() {
            return Videolight;
        }

        public String getCountry() {
            return Country;
        }

        public String getDescription() {
            return Description;
        }

        public String getId() {
            return id;
        }

        public String getImage() {
            return Image;
        }

        public String getLanguage() {
            return Language;
        }

        public String getTopic() {
            return Topic;
        }

        public String getVideo() {
            return Video;
        }

    public void setCountry(String country) {
        Country = country;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setTopic(String topic) {
        Topic = topic;
    }

    public void setVideo(String video) {
        Video = video;
    }

    public void setVideolight(String videolight) {
        Videolight = videolight;
    }
}


