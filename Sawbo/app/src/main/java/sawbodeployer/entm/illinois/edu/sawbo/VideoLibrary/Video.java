package sawbodeployer.entm.illinois.edu.sawbo.VideoLibrary;

import java.util.List;

/**
 * Created by Mahsa on 4/3/2017.
 */

public class Video {
    public List<all> all;
    public List<String>Languages;
    public List<String>Country;
    public List<String>Topic;

    public List<Video.all> getAll() {
        return all;
    }

    public List<String> getCountry() {
        return Country;
    }

    public List<String> getLanguages() {
        return Languages;
    }

    public List<String> getTopic() {
        return Topic;
    }

    public class all{
        public String Filename;

        public String Video;

        public String Language;

        public String Country;

        public String getFilename() {
            return Filename;
        }
        public String getVideo() {
            return Video;
        }

        public String getCountry() {
            return Country;
        }

        public String getLanguage() {
            return Language;
        }


    }
}
