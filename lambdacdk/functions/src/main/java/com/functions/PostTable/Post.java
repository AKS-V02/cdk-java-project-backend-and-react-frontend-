package com.functions.PostTable;

public class Post {
    private String id;
    private String title;
    private String massage;
    private String username;
    private String imgUrl;
    private String imgKey;

    public Post(String title, String massage, String username, String imgUrl, String imgKey) {
        this.title = title;
        this.massage = massage;
        this.username = username;
        this.imgUrl = imgUrl;
        this.imgKey = imgKey;
    }

    
    public Post(String id, String title, String massage, String username, String imgUrl, String imgKey) {
        this.id = id;
        this.title = title;
        this.massage = massage;
        this.username = username;
        this.imgUrl = imgUrl;
        this.imgKey = imgKey;
    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getMassage() {
        return massage;
    }
    public void setMassage(String massage) {
        this.massage = massage;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getImgUrl() {
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public String getImgKey() {
        return imgKey;
    }
    public void setImgKey(String imgKey) {
        this.imgKey = imgKey;
    }
}
