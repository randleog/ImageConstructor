package com.example.imageconstructor;

public class ImageData {



    public ImageData() {

    }
    public ImageData(int id, String name, String[] content, String tags) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.tags = tags;

    }
    private int id;
    private String name;

    private String[] content;
    private String tags;


    public static ImageData createImageData(int id, String name, String[] content, String tags) {
        return new ImageData(id, name, content, tags);
    }

    public static ImageData createImageData() {
        return new ImageData(-1, "",new String[]{""}, "");
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public void setContent(String[] content) {
        this.content= content;
    }

    public String[] getContent() {
        return content;
    }



}