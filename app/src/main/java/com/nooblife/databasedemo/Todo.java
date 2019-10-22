package com.nooblife.databasedemo;

public class Todo {

    int id;
    String content;
//    String timestamp;

    public Todo(int id, String content) {
        this.id = id;
        this.content = content;
//        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

//    public String getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(String timestamp) {
//        this.timestamp = timestamp;
//    }
}
