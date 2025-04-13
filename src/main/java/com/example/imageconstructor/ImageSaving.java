package com.example.imageconstructor;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ImageSaving {
    private static HashMap<String, Integer> code = new HashMap<>();
    private static String[] reverseCode = new String[260];

    private static int largestIndex = 0;


    private static boolean redTinge = true;


    //credit: https://alvinalexander.com/java/java-clipboard-image-copy-paste/
    //could also be cause of linux bug?
    public static Image getImageFromClipboard() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            //   try {

            //needs a workaround
            //    return SwingFXUtils.toFXImage((BufferedImage) transferable.getTransferData(DataFlavor.imageFlavor), null);
            //   } catch (UnsupportedFlavorException | IOException e) {
            //       // handle this as desired
            //       e.printStackTrace();
            //    }
        } else {
            //       System.err.println("getImageFromClipboard: That wasn't an image!");
        }
        return null;
    }
    public static Image loadImageThumb(String fileName) {


        Image image;
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(Util.getFolder() +fileName);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(inputstream, 234, 234, true, true);

        return image;
    }
    public static Image loadObjective(String fileName) {

        if (!(new File(fileName).exists())) {
            return new WritableImage(32,32);
        }
        Image image;
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(fileName);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(inputstream);

        return image;
    }

    public static Image loadImage(String fileName) {


        Image image;
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(Util.getFolder() +fileName);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(inputstream);

        return image;
    }

    public static Image loadImage(String fileName, int length, int width) {


        Image image;
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(Util.getFolder() +fileName);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(inputstream, length, width, true, false);

        return image;
    }

    public static int addExtra(String current, int index) {

        boolean shouldAdd = false;
        if (current.charAt(index) == '!') {
            shouldAdd = true;
            index++;
        }

        if (code.get(current.charAt(index) + "") == null) {
            return 0;
        }
        return code.get(current.charAt(index) + "") + (shouldAdd ? largestIndex : 0);

    }

    public static boolean isCryptic(String input) {
        return input.contains("png.");
    }

    public static Image getImageFromConverted(String name) {

        if (!isCryptic(name)) {
            return loadImage(name);
        }
        ArrayList<String> lines = loadConverted(name);

        String[] info = lines.get(0).split(" ");

        int length = Integer.parseInt(info[1]);
        int height = Integer.parseInt(info[2]);
        String type = info[0];


        lines.remove(0);
        WritableImage image = new WritableImage(length, height);
        switch (type) {
            case "half_color":
                return writeToHalfColorImage(image, lines, length, height);
            case "monochrome":
                return writeMonochromeImage(image, lines, length, height);
            default:
                return writeToNormalImage(image, lines, length, height);
        }
        //writeToNormalImage(image, lines, length, height);


    }


    public static Image writeMonochromeImage(WritableImage image, ArrayList<String> lines, int length, int height) {

        Color last = null;
        for (int i = 0; i < length; i++) {
            String row = lines.get(i);
            int index = 0;
            for (int j = 0; j < height; j++) {

                //  String current = row;


                if (row.charAt(index) == '\"') {
                    image.getPixelWriter().setColor(i, j, last);
                    index++;
                } else {


                    int r = addExtra(row, index);

                    if (r > largestIndex) {
                        index++;
                    }
                    index++;
                    Color currentColor;

                    currentColor = Color.rgb(r, r, r, 1);
                    //currentColor = Color.rgb(Math.min((int)(r/0.92),255), r, Math.min((int)(r/0.96),255), 1);
                    image.getPixelWriter().setColor(i, j, currentColor);


                    last = currentColor;
                }
            }
        }
        return image;
    }


    public static Image writeToNormalImage(WritableImage image, ArrayList<String> lines, int length, int height) {
        Color last = null;
        for (int i = 0; i < length; i++) {
            String row = lines.get(i);

            int index = 0;
            for (int j = 0; j < height; j++) {


                //  String current = row;


                if (row.charAt(index) == '\"') {
                    image.getPixelWriter().setColor(i, j, last);
                    index++;
                } else {


                    int r = addExtra(row, index);

                    if (r > largestIndex) {
                        index++;
                    }
                    index++;
                    Color currentColor;

                    if (row.charAt(index) == '#') { //monochrome detection
                        currentColor = Color.rgb(r, r, r, 1);
                        image.getPixelWriter().setColor(i, j, currentColor);
                        index++;
                    } else {
                        int g = addExtra(row, index);
                        index++;
                        if (g > largestIndex) {
                            index++;
                        }
                        int b = addExtra(row, index);
                        index++;
                        if (b > largestIndex) {
                            index++;
                        }

                        currentColor = Color.rgb(r, g, b, 1);

                        image.getPixelWriter().setColor(i, j, currentColor);
                    }

                    last = currentColor;
                }
            }
        }
        return image;
    }






    private static String getDoubleOrSingle(int value) {
        if (value > largestIndex) {
            return "!" + reverseCode[value - largestIndex];
        }
        return reverseCode[value] + "";
    }



    public static void convertMonochrome(Image image, String name) throws IOException {

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // dir.mkdir();
        File imageWidth = new File(Util.getFolder() +name.replace(".", "").replace("jpg", "png") + ".txt");
        FileWriter writer = new FileWriter(imageWidth);
        writer.write("monochrome " + (int) image.getWidth() + " " + (int) image.getHeight() + "\n");

        String last = "";
        PixelReader reader = image.getPixelReader();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int r = (int) ((reader.getColor(i, j).getBrightness() * 255));


                String pixel = getDoubleOrSingle(r);


                if (last.length() > 0 && last.equals(pixel)) {
                    writer.write("\"");
                } else {

                    writer.write(pixel);
                }
                last = pixel;


            }
            writer.write("\n");
        }
        writer.close();
    }

    public static ArrayList<String> loadConverted(String fileName) {


        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(Util.getFolder() +fileName);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner text = new Scanner(inputstream);
        ArrayList<String> lines = new ArrayList<>();


        while (text.hasNextLine()) {
            lines.add(text.nextLine());

        }

        text.close();


        return lines;
    }


    public static void convertHalfColor(Image image, String name, String tags) throws IOException {
        int factor = 2;

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        File dir = new File(name);

        File imageWidth = new File(name.replace(".", "").replace("jpg", "png") + ".txt");
        FileWriter writer = new FileWriter(imageWidth);
        writer.write("half_color " + (int) image.getWidth() + " " + (int) image.getHeight() +"\n");

        String last = "";
        String last2 = "";
        PixelReader reader = image.getPixelReader();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int m = (int) (((reader.getColor(i, j).getRed() + reader.getColor(i, j).getGreen() + reader.getColor(i, j).getBlue()) / 3.0) * 255);


                String pixel = getDoubleOrSingle(m);


                if (last.length() > 0 && last.equals(pixel)) {
                    writer.write("\"");
                } else {

                    writer.write(pixel);
                }
                last = pixel;


            }

            writer.write("\n");
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i % factor == 0 && j % factor == 0) {
                    String pixel2 = "";
                    //double r1 = (reader.getColor(i,j).getRed());
                    //  double g1 = (reader.getColor(i,j).getGreen());
                    //   double b1 = (reader.getColor(i,j).getBlue());


                    //  double x = 0.5/((r1+g1+b1)/3);

                    //     int r = Math.max(Math.min((int)(r1*x*255),255), 0);
                    //  int g = Math.max(Math.min((int)(g1*x*255),255), 0);
                    //  int b = Math.max(Math.min((int)(b1*x*255),255), 0);

                    int r = (int) ((reader.getColor(i, j).getRed()) * 255);
                    int g = (int) ((reader.getColor(i, j).getGreen()) * 255);
                    int b = (int) ((reader.getColor(i, j).getBlue()) * 255);


                    if (r == b && r == g) {
                        pixel2 = getDoubleOrSingle(r) + "#";
                    } else {
                        pixel2 = getDoubleOrSingle(r) + "" + getDoubleOrSingle(g) + "" + getDoubleOrSingle(b);
                    }

                    if (last2.length() > 0 && last2.equals(pixel2)) {

                        writer.write("\"");
                    } else {

                        writer.write(pixel2);

                    }
                    last2 = pixel2;
                }

            }
            if (i % factor == 0) {
                writer.write("\n");
            }
        }

        writer.close();
    }


    private static void assignHash(String text, Integer value) {
        code.put(text, value);

        reverseCode[value] = text;


    }

     public static void assignHashes() {
        int current = 0;
        for (int i = 36; i < 127; i++) {
            assignHash((char) i + "", current);
            current++;

        }
        for (int i = 174; i < 256; i++) {
            assignHash((char) i + "", current);
            current++;

        }

        // for (int i = 12353; i < 12437; i++) {
        //      assignHash((char)i+"", current);
        //      current++;

        //   }

        largestIndex = current - 1;
    }


    public static void convert(Image image, String name) throws IOException {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();


        File imageWidth = new File(Util.getFolder()  +name.replace(".", "").replace("jpg", "png") + ".txt");
        FileWriter writer = new FileWriter(imageWidth);
        writer.write("normal " + (int) image.getWidth() + " " + (int) image.getHeight() + "\n");

        String last = "";
        PixelReader reader = image.getPixelReader();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int r = (int) (reader.getColor(i, j).getRed() * 255);
                int g = (int) (reader.getColor(i, j).getGreen() * 255);
                int b = (int) (reader.getColor(i, j).getBlue() * 255);


                String pixel;
                //String pixel = String.format("%02x%02x%02x", r, g, b);
                if (r == b && r == g) {
                    pixel = getDoubleOrSingle(r) + "#";
                } else {
                    pixel = getDoubleOrSingle(r) + "" + getDoubleOrSingle(g) + "" + getDoubleOrSingle(b);
                }

                if (last.length() > 0 && last.equals(pixel)) {
                    writer.write("\"");
                } else {

                    writer.write(pixel);
                }
                last = pixel;


            }
            writer.write("\n");
        }
        writer.close();
    }
    public static Image writeToHalfColorImage(WritableImage image, ArrayList<String> lines, int length, int height) {
        Color last = null;
        int last2 = 0;
        int[][] brightness = new int[length][height];
        for (int i = 0; i < length; i++) {
            int index = 0;
            for (int j = 0; j < height; j++) {

                //System.out.println(lines.get(0).charAt(index) + " " + index + " " + i + " " + j);
                if (lines.get(0).charAt(index) == '\"') {
                    brightness[i][j] = last2;

                } else {
                    brightness[i][j] = addExtra(lines.get(0), index);
                    if (brightness[i][j] > largestIndex) {
                        index++;
                    }

                    last2 = brightness[i][j];
                }

                index++;
            }
            lines.remove(0);
        }


        return applyColorOverlay(image, lines, brightness);

    }

    public static WritableImage applyColorOverlay(WritableImage image1, ArrayList<String> lines, int[][] brightness) {

        Color last = null;
        int length = (int) image1.getWidth();
        int height = (int) image1.getHeight();
        WritableImage image = new WritableImage(length, height);
        Canvas canvas = new Canvas(length, height);
        for (int i = 0; i < length; i += 2) {
            String row = lines.get(i / 2);
            int index = 0;
            for (int j = 0; j < image.getHeight(); j += 2) {

                //  String current = row;

                Color currentColor = new Color(0, 0, 0, 0);
                if (row.charAt(index) == '\"') {
                    currentColor = last;
                    index++;
                } else {


                    int r = addExtra(row, index);

                    if (r > largestIndex) {
                        index++;
                    }
                    index++;

                    if (row.charAt(index) == '#') { //monochrome detection
                        currentColor = Color.rgb(r, r, r, 1);
                        index++;
                    } else {
                        int g = addExtra(row, index);
                        index++;
                        if (g > largestIndex) {
                            index++;
                        }
                        int b = addExtra(row, index);
                        index++;
                        if (b > largestIndex) {
                            index++;
                        }
                        currentColor = Color.rgb(r, g, b, 1);

                    }


                    last = currentColor;
                }

                //canvas.getGraphicsContext2D().setGlobalBlendMode(BlendMode.OVERLAY);
                for (int i2 = i; i2 < i + 2; i2++) {
                    for (int j2 = j; j2 < j + 2; j2++) {

                        double r1 = currentColor.getRed();
                        double g1 = currentColor.getGreen();
                        double b1 = currentColor.getBlue();


                        double x = (brightness[i2][j2] / 255.0) / ((r1 + g1 + b1) / 3);


                        int r = Math.max(Math.min((int) ((r1 * x * 255)), 255), 0);
                        int g = Math.max(Math.min((int) ((g1 * x * 255)), 255), 0);
                        int b = Math.max(Math.min((int) ((b1 * x * 255)), 255), 0);


                        // System.out.println(brightness[i2][j2] + " " + (int)(Color.rgb(r,g,b,1).getBrightness()*255));


                        image.getPixelWriter().setColor(i2, j2, Color.rgb(r, g, b, 1));
                        //image.getPixelWriter().setC
                    }
                }
            }
        }
        canvas.getGraphicsContext2D().drawImage(image1, 0, 0);
        //  canvas.getGraphicsContext2D().setGlobalBlendMode(BlendMode.OVERLAY);
        canvas.getGraphicsContext2D().drawImage(image, 0, 0);
        WritableImage image2 = new WritableImage(length, height);
        return canvas.getGraphicsContext2D().getCanvas().snapshot(null, image2);
    }

    private static int getDifferenceColorInt(double currentColor, int difference) {

        return Math.max(Math.min((int) (currentColor * 255) - difference, 255), 0);
    }
    public static void writeFileText(String name, ArrayList<String> text) throws IOException {

        File imageWidth = new File(Util.getFolder() +name);
        FileWriter writer = new FileWriter(imageWidth);
        for (String s : text) {
            writer.write(s + "\n");
        }

        writer.close();
    }


}
