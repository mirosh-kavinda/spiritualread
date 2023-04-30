package me.mirosh.spiritualread.model;

public class ModelRequested {


    String id, title,author,categoryId;
    long timestamp;

    //constructor empty requried for firebase
    public ModelRequested(){
    }

    public ModelRequested(String id, String title, String author, String categoryId, long timestamp) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.categoryId = categoryId;
        this.timestamp = timestamp;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
