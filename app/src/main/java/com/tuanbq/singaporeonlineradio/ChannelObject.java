package com.tuanbq.singaporeonlineradio;

/**
 * Created by User on 5/22/2018.
 */

public class ChannelObject {
    private int mId;
    private int isFav;
    private String name;
    private String pic;
    private String link;
    private String location;
    private String bitrate;
    private String cat;

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public int isFav() {
        return isFav;
    }

    public void setFav(int fav) {
        isFav = fav;
    }

    public ChannelObject() {}

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }
}
