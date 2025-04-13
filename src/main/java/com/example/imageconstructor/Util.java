package com.example.imageconstructor;

public class Util {


    public static final String STYLE_FOLDER = "style";

    public static String directory = "";



    public static final int CANVAS_WIDTH = 5000;


    public static final int CANVAS_HEIGHT = 2760;


    public static final String CONNECTOR = "/";
    //System.getProperty("user.dir")+CONNECTOR+directory;
    public static String getAbsolouteFolder() {
        return filepath+CONNECTOR+directory;
    }

    public static String filepath = "";
    //selctedDir+CONNECTOR+directory;
    public static String getFolder() {
        return filepath+CONNECTOR+directory;
    }

    public static String appendMarkupSetting(String value, String input, String type) {
        if (value.contains(type)) {
            String[] sections = value.split(type);
            String thirdSection = sections.length > 2 ? sections[2] : "";

            return sections[0] + type + input + type + thirdSection;
        } else {
            return value + type + input +type;
        }
    }

}
