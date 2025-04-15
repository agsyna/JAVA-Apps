package com.example.photoapplication;

public class ImageItem {
    String name;
    String uri;
    String date;
    String size;

    public ImageItem(String name, String uri, String date, String size) {
        this.name = name;
        this.uri = uri;
        this.date = date;
        this.size = size;
    }

    public String getName() { return name; }
    public String getUri() { return uri; }
    public String getDate() { return date; }
    public String getSize() { return size; }
}
