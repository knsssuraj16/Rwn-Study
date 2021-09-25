package com.rwn.rwnstudy.utilities;

public class StatusGetterSetter {

    private  String caption,date,hour,key,link,min,uid,storagePath;

    public StatusGetterSetter() {
    }

    public StatusGetterSetter(String caption, String date, String hour, String key, String link, String min, String uid,  String storagePath) {
        this.caption = caption;
        this.date = date;
        this.hour = hour;
        this.key = key;
        this.link = link;
        this.min = min;
        this.uid = uid;
        this.storagePath= storagePath;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public StatusGetterSetter setStoragePath(String storagePath) {
        this.storagePath = storagePath;
        return this;
    }

    public String getCaption() {
        return caption;
    }

    public StatusGetterSetter setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public String getDate() {
        return date;
    }

    public StatusGetterSetter setDate(String date) {
        this.date = date;
        return this;
    }

    public String getHour() {
        return hour;
    }

    public StatusGetterSetter setHour(String hour) {
        this.hour = hour;
        return this;
    }

    public String getKey() {
        return key;
    }

    public StatusGetterSetter setKey(String key) {
        this.key = key;
        return this;
    }

    public String getLink() {
        return link;
    }

    public StatusGetterSetter setLink(String link) {
        this.link = link;
        return this;
    }

    public String getMin() {
        return min;
    }

    public StatusGetterSetter setMin(String min) {
        this.min = min;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public StatusGetterSetter setUid(String uid) {
        this.uid = uid;
        return this;
    }

    @Override
    public String toString() {
        return "StatusGetterSetter{" +
                "caption='" + caption + '\'' +
                ", date='" + date + '\'' +
                ", hour='" + hour + '\'' +
                ", key='" + key + '\'' +
                ", link='" + link + '\'' +
                ", min='" + min + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
