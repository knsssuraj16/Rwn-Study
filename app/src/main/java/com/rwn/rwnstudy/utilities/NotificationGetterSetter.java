package com.rwn.rwnstudy.utilities;

public class NotificationGetterSetter {

    private  String from,node,parentNode,read,type,timeStamp;

    public NotificationGetterSetter() {
    }

    public NotificationGetterSetter(String from, String node, String parentNode, String read, String type,String timeStamp) {
        this.from = from;
        this.node = node;
        this.parentNode = parentNode;
        this.read = read;
        this.timeStamp=timeStamp;
        this.type = type;
    }


    public String getTimeStamp() {
        return timeStamp;
    }

    public NotificationGetterSetter setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public NotificationGetterSetter setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getNode() {
        return node;
    }

    public NotificationGetterSetter setNode(String node) {
        this.node = node;
        return this;
    }

    public String getParentNode() {
        return parentNode;
    }

    public NotificationGetterSetter setParentNode(String parentNode) {
        this.parentNode = parentNode;
        return this;
    }

    public String getRead() {
        return read;
    }

    public NotificationGetterSetter setRead(String read) {
        this.read = read;
        return this;
    }

    public String getType() {
        return type;
    }

    public NotificationGetterSetter setType(String type) {
        this.type = type;
        return this;
    }
}
