package in.androidhunt.musicDEmo.model;

/**
 * Created by Lincoln on 18/05/16.
 */
public class Album {
    private String name;
    private String about;
    private String thumbnail;
    private String id;
    private String status;

    public Album() {
    }

    public Album(String name, String about, String thumbnail, String id, String status) {
        this.name = name;
        this.about = about;
        this.thumbnail = thumbnail;
        this.id = id;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
