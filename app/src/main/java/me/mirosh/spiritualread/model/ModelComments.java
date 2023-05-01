package me.mirosh.spiritualread.model;

public class ModelComments {
    String id, bookId, timestamp,comment,uid;

    public ModelComments(String id, String bookId, String timestamp, String comment, String uid) {
        this.id = id;
        this.bookId = bookId;
        this.timestamp = timestamp;
        this.comment = comment;
        this.uid = uid;
    }

    public ModelComments(){

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
