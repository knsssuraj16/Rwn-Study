package com.rwn.rwnstudy.utilities;public class DocumentGetterSetter {    private String Description;    private String DocumentLink;    private String Key;    private String Subject;    private String TopicName;    private String Unit;    private String UploadingDate;    private String UploadingTime;    private String uid;    private String userType;    private String DocumentType;    public DocumentGetterSetter() {    }    public DocumentGetterSetter(String Description, String DocumentLink, String Key, String Subject, String TopicName, String Unit, String UploadingDate, String UploadingTime, String uid, String userType, String DocumentType) {        this.Description = Description;        this. DocumentLink = DocumentLink;        this.Key = Key;        this. Subject = Subject;        this.TopicName = TopicName;        this.Unit = Unit;        this.UploadingDate = UploadingDate;        this.UploadingTime = UploadingTime;        this.uid = uid;        this.userType = userType;        this.DocumentType = DocumentType;    }    public String getDescription() {        return Description;    }    public void setDescription(String Description) {        this.Description = Description;    }    public String getDocumentLink() {        return DocumentLink;    }    public void setDocumentLink(String DocumentLink) {        this.DocumentLink = DocumentLink;    }    public String getKey() {        return Key;    }    public void setKey(String Key) {        this.Key = Key;    }    public String getSubject() {        return Subject;    }    public void setSubject(String Subject) {        this.Subject = Subject;    }    public String getTopicName() {        return TopicName;    }    public void setTopicName(String TopicName) {        this.TopicName = TopicName;    }    public String getUnit() {        return Unit;    }    public void setUnit(String Unit) {        this.Unit = Unit;    }    public String getUploadingDate() {        return UploadingDate;    }    public void setUploadingDate(String UploadingDate) {        this.UploadingDate = UploadingDate;    }    public String getUploadingTime() {        return UploadingTime;    }    public void setUploadingTime(String UploadingTime) {        this.UploadingTime = UploadingTime;    }    public String getUid() {        return uid;    }    public void setUid(String uid) {        this.uid = uid;    }    public String getUserType() {        return userType;    }    public void setUserType(String userType) {        this.userType = userType;    }    public String getDocumentType() {        return DocumentType;    }    public void setDocumentType(String DocumentType) {        this.DocumentType = DocumentType;    }    @Override    public String toString() {        return "DocumentGetterSetter{" +                "Description='" + Description + '\'' +                ", DocumentLink='" + DocumentLink + '\'' +                ", Key='" + Key + '\'' +                ", Subject='" + Subject + '\'' +                ", TopicName='" + TopicName + '\'' +                ", Unit='" + Unit + '\'' +                ", UploadingDate='" + UploadingDate + '\'' +                ", UploadingTime='" + UploadingTime + '\'' +                ", uid='" + uid + '\'' +                ", userType='" + userType + '\'' +                ", DocumentType='" + DocumentType + '\'' +                '}';    }}