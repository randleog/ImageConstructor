package com.example.imageconstructor;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;


import javafx.scene.image.*;

import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

//import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


import static org.apache.commons.io.comparator.LastModifiedFileComparator.LASTMODIFIED_COMPARATOR;
import static org.apache.commons.io.comparator.LastModifiedFileComparator.LASTMODIFIED_REVERSE;



public class HelloApplication extends Application {


    //bug 1 from captain: the names of the images cant have spaces or special characters, so i need to add support for this
    //drag and drop image to file pane to save it


    //for the favorites list, i should code a new thing to animate it, detect where the grid patterns are and color detect to see where it went in the next image when using slideshow.
    //could be cool. and then maybe this could be the start of using this program to also make bespoke youtube style animations.

    private static final String STYLE_FOLDER = "style";

    private static HashMap<String, Integer> code = new HashMap<>();
    private static String[] reverseCode = new String[260];

    private static final int CANVAS_SIZE = 8000;
    private static int largestIndex = 0;


    private static boolean redTinge = true;

    private static final String CONNECTOR = "/";


    private static TextField length;
    private static TextField height;

    private static Image lastSaved = null;
    private static double scroll = 1;
    private static ScrollPane scrollPane;
    private static final int ARROW_MOVE_AMOUNT = 100;

    private static double x = 0;
    private static double y = 0;

    //private static Image

    private static double startx = 0;
    private static double starty = 0;


    private static String directory = "";

    private static double startMousex = 0;
    private static double startMousey = 0;
    private static boolean isDragging = false;
    private static Image undoImage = null;


    private static ArrayList<String> collage = new ArrayList<>();

    private static String toolType = "cursor";//cursor,paintbrush,select

    private static int lastMouseX;
    private static int lastMouseY;

    private static int SECURITY_LEVEL = 0; //can be ran with higher security level in args to only show images tagged with "safe" in the image browser

    private static Canvas canvas;



    private static int fileCount = 0;



    private static Color color;

    private static Stage primaryStage;
    private static int carrot = 0;
    private static String written = "";
    private static Text clipboard = new Text();
    private static ToggleButton toggleButton;


    private static String next = "";

    // private int furthestRight = 0;

    private static String previous = "";

    private static boolean space = false;


    private static Button nextButton = new Button("next>");

    private static Button previousButton = new Button("<prev");

    private static TextField input;

    private static Button bookmark;

    private static Text views;


    private static ArrayList<ImageView> stages;

    private static ArrayList<Image> imageStack;

    private static boolean thumbnailMode = false;

    private static TextField include;

    private static Text cuPage;




    private static ArrayList<String> showTitles = new ArrayList<>();

    private void establishLists() {
        characterlist.add("megumin");

        categories.add("manga");

        showTitles.add("Title");

    }

    private static final String CONFIG_FILE = "config.txt";

    private static String configSettings = "";

    private static String[] getConfigSettings () {
        return configSettings.split("\n");
    }

    private static String appendMarkupSetting(String value, String input, String type) {
        if (value.contains(type)) {
            String[] sections = value.split(type);
            String thirdSection = sections.length > 2 ? sections[2] : "";

            return sections[0] + type + input + type + thirdSection;
        } else {
            return value + type + input +type;
        }
    }
    private static void setConfigValues() {

        try {

            BufferedWriter writeConfig = new BufferedWriter(new FileWriter(CONFIG_FILE));

            writeConfig.write(configSettings);
            writeConfig.flush();
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }

    private static String getConfigValue(String input) {
        if (configSettings.contains(input)) {
            return configSettings.split(input)[1];
        } else {
            return -1+"";
        }


    }

    private static void loadConfigValues() {
        File config = new File(CONFIG_FILE);
        if (!config.exists()) {
            try {
                config.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


            BufferedReader reader = null;


            try {
                reader = new BufferedReader(new FileReader(config));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    configSettings = configSettings + line;
                }


            } catch (IOException ignored) {

            } finally {
                if (reader  != null) {
                    try {
                        reader .close();
                    } catch (Exception ignored) {

                    }
                }
            }


    }


    private static void userChooseDir(Stage stage) {


        DirectoryChooser choice = new DirectoryChooser();

        File option = choice.showDialog(stage);
        configSettings = appendMarkupSetting(configSettings,option.getAbsolutePath(),"<filepath>");
        filepath = option.getAbsolutePath();
        System.out.println(filepath);
        directory = "";
        setConfigValues();


    }

    @Override
    public void start(Stage stage) throws IOException {

        loadConfigValues();
        if (getConfigValue("<filepath>").equals("-1")) {
            userChooseDir(stage);
        }
        filepath = getConfigValue("<filepath>");


        lastSaved = new WritableImage(32,32);
        establishLists();

        primaryStage = stage;

        Pane root = new Pane();

        canvas = new Canvas(CANVAS_SIZE, 2160);


        readTagCount();
        refreshTagCounts();








        clipboard.setFill(Color.WHITE);

        color = Color.WHITE;


        canvasLayout = new HBox();
        canvasLayout.getChildren().add(canvas);


        canvasLayout.setOnKeyPressed(ke -> {
            if (ke.getCode() == KeyCode.SPACE) {
                space = true;

            }
        });
        canvasLayout.setOnKeyReleased(ke -> {
            if (ke.getCode() == KeyCode.SPACE) {
                space = false;

            }
        });


        assignHashes();


        //  canvas.setOnMouseDrag


        canvas.setOnMouseDragged(event -> {

            switch (toolType) {
                case "cursor":
                    if (!isDragging) {
                        startx = x;
                        starty = y;
                        startMousex = event.getX();
                        startMousey = event.getY();
                    }
                    isDragging = true;
                    x = (event.getX() / scroll + startx - startMousex / scroll);
                    y = (event.getY() / scroll + starty - startMousey / scroll);


                    update(canvas);
                    break;
                case "paint":

                    if (scroll < 1) {
                        return;
                    }
                    canvas.getGraphicsContext2D().setLineWidth(1);


                    if (!isDragging) {
                        lastMouseX = (int) ((((event.getX()))));

                        lastMouseY = (int) (((event.getY())));
                    }
                    isDragging = true;


                    canvas.getGraphicsContext2D().setLineWidth(Math.max(1, scroll));
                    canvas.getGraphicsContext2D().setStroke(color);
                    canvas.getGraphicsContext2D().strokeLine(((int) ((event.getX()))), ((int) ((event.getY()))), lastMouseX, lastMouseY);


                    lastMouseX = (int) ((((event.getX()))));

                    lastMouseY = (int) (((event.getY())));


                    break;
                case "select":

                    isDragging = true;


                    break;

                default:
                    System.out.println("invalid tool: " + toolType);
            }

        });


        canvas.getGraphicsContext2D().setImageSmoothing(false);

        canvas.setOnScroll(scrollEvent -> {
            long currentTimeScroll = System.currentTimeMillis();
            long timedelta = currentTimeScroll-lastScrollTime;
            lastScrollTime = currentTimeScroll;

            double factor;
            if (scrollEvent.getDeltaY() > 0) {
                factor = 1.1;
            } else {
                factor = 0.9;
            }
            scroll = scroll * factor;
            //  if (scroll < 1) {
            //      canvas.getGraphicsContext2D().setImageSmoothing(true);
            //  } else {
            //      canvas.getGraphicsContext2D().setImageSmoothing(false);
            //  }
          //  canvas.getGraphicsContext2D().setImageSmoothing(true);
            double distanceToCenterBeforeX = scrollEvent.getX();
            double distanceToCenterBeforeY = scrollEvent.getY();

            double distanceNowX = distanceToCenterBeforeX * factor;
            double distanceNowY = distanceToCenterBeforeY * factor;
            double xdiff = distanceNowX - distanceToCenterBeforeX;
            double ydiff = distanceNowY - distanceToCenterBeforeY;


            zoomcanvas(xdiff,ydiff,1);
        });
        canvas.setOnKeyPressed(keyEvent -> {
            canvas.requestFocus();
            if (keyEvent.getCode() == KeyCode.UP) {
                y += ARROW_MOVE_AMOUNT;
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                y -= ARROW_MOVE_AMOUNT;
            } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                x -= ARROW_MOVE_AMOUNT;
                collageIndex++;
            } else if (keyEvent.getCode() == KeyCode.LEFT) {
                x += ARROW_MOVE_AMOUNT;
                collageIndex--;
            }
            update(canvas);
        });

        canvas.setOnMouseClicked(event -> {
            canvasMouseClicked(event.getX(), event.getY(), event.getButton().name());


        });
        //  canvas.setOnMouseEntered(event -> {


        //   scrollPane.setFitToWidth(false);

        //   System.out.println("test");
        //    scrollPane.setMinWidth(165);
        //    scrollPane.setMaxWidth(165);
        //  });


        canvasLayout.setStyle("-fx-background-color: black;");


        root.getChildren().add(canvasLayout);

        //  canvas.getGraphicsContext2D().fillRect(0,0,100,100);


        Scene scene = new Scene(root, 1920, 1440);
        stage.setScene(scene);


        canvas.setOnDragOver(dragEvent -> {
            if (dragEvent.getDragboard().hasFiles() || dragEvent.getDragboard().hasImage()) {
           //     ClipboardContent content = new ClipboardContent();
               // content.putImage(dragEvent.getDragboard().getImage());



                dragEvent.acceptTransferModes(TransferMode.ANY);
            }
        });


        canvas.setOnDragDropped(dragEvent -> {

/*
            if (dragEvent.getDragboard().hasUrl()) {
                URL url = null;
                try {
                    url = new URL(dragEvent.getDragboard().getUrl());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }

                Image image = null;
                try {

                    image =getImageFromBuffered(ImageIO.read(url));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                pasteImage(0, 0, image);
                lastSaved = image;
                return;
            }

 */
            if (dragEvent.getDragboard().hasImage()) {
                Image image =dragEvent.getDragboard().getImage();
                if (dragEvent.getDragboard().getImage() == null) {
                    System.out.println("wtf? image is null");
                    System.out.println(dragEvent.getDragboard().getString());
                } else {

                    pasteImage(0, 0, image);
                    lastSaved = image;
                }

                return;
            }

            try {
                if (dragEvent.getDragboard().getFiles().size() > 1) {

                    File[] images2 = new File[dragEvent.getDragboard().getFiles().size()];

                    for (int i = 0; i < dragEvent.getDragboard().getFiles().size(); i++) {
                        images2[i] = dragEvent.getDragboard().getFiles().get(i);
                    }

                    Arrays.sort(images2, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
                    String batchname = UUID.randomUUID().toString();
                    for (int i = 0; i < images2.length; i++) {

                        System.out.println(images2[i].getAbsolutePath());
                        Image image = new Image(new FileInputStream(images2[i]));

                        pasteImage(0, 0, image);
                        saveCurrent(batchname + images2[i].getName()); //potential cause of issue is directory
                        //saveCurrent(images[i].getName(), "imported image");



                        lastSaved = image;
                    }
                } else {
                    Image image = new Image(new FileInputStream(dragEvent.getDragboard().getFiles().get(0)));
                    pasteImage(0, 0, image);
                    lastSaved = image;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }



        });


        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {

                stage.setFullScreen(!stage.isFullScreen());
            } else if (e.getCode() == KeyCode.R) {
                loadRandomImage();
            }
            if (toolType.equalsIgnoreCase("paint")) {

                if (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE) {
                    if (written.length() > 0) {
                        written = written.substring(0, written.length() - 1);
                    }
                } else if (e.getCode() == KeyCode.ENTER) {

                    written = written + "\n";
                } else if (e.getCode() == KeyCode.Z && e.isControlDown()) {
                    written = "";
                } else {
                    written = written + e.getText();

                }

                canvas.getGraphicsContext2D().setFont(Font.font("comic-sans", FontWeight.BOLD, 35));
                canvas.getGraphicsContext2D().setFill(color);
                canvas.getGraphicsContext2D().fillText(written.toUpperCase(Locale.ROOT), lastMouseX, lastMouseY);
                carrot++;
            }
        });

        root.setStyle("-fx-base:black");
        // setMainMenu();

        setUpScroller();
        setUpTagStage();

        readHeirarchy();
        stage.setTitle("Main Image Display");
        stage.getIcons().add(loadObjective(STYLE_FOLDER+CONNECTOR+"MainImage.png"));
        stage.show();
        stage.setMaximized(true);


    }

    private static void zoomcanvas(double xdiff, double ydiff, int iterations) {
        x -= ( xdiff / iterations) / scroll;
        y -= ( ydiff / iterations) / scroll;
        update(canvas);

    }

    private static String lastUsedTag = "";

    public static Stage dialog;
    public static Stage tagStage;

    public static FlowPane tagButtons;
    public static Button tagmodeButton;

    public static ArrayList<String> toggledTags = new ArrayList<>();

    private static VBox tagPage;
    private static HBox tagLayout;
    private static HBox navLayout;
    private static void setUpTagStage() throws MalformedURLException {
        tagStage = new Stage();
        tagButtons = new FlowPane();
        tagButtons.setMaxHeight(10000);


        tagLayout = new HBox();
        tagPage = new VBox();



        Button swapleftlayoutbutton2 = new Button("swap gui");
        swapleftlayoutbutton2.setOnAction(actionEvent -> {
            swapLeftPanel();
        });

        ScrollPane tagScrollPane = new ScrollPane();
        tagScrollPane.setMaxWidth(10000);


        tagScrollPane.setFocusTraversable(false);


        tagScrollPane.setContent(tagButtons);


        tagScrollPane.setMaxHeight(10000);
        tagScrollPane.setPannable(true);


        populateTagFieldButtons();


        tagStage.initOwner(primaryStage);
        //  VBox dialogVbox = new VBox(20);
        //      dialogVbox.getChildren().add(layout);
        URL url = Path.of(STYLE_FOLDER+CONNECTOR+"Style.css").toUri().toURL();
        if (url == null) {
            System.out.println("Resource not found. Aborting.");
            System.exit(-1);
        }
        String css = url.toExternalForm();

        TextField newTag = new TextField(searchNewTag);
        newTag.requestFocus();
        newTagButton = new Button("add new");

        tagmodeButton = new Button("mode: apply tag");
        Button moveTagButtons = new Button("Move to folder");
        Button addAll = new Button("add All Matching query");
        addAll.setOnAction(actionEvent -> {
            for (String s : acceptableFiles) {
                updateTagCountSafe(s,newTag.getText(),1);
            }
        });
        moveTagButtons.setOnAction(actionEvent -> {
            massStoreTag(newTag.getText(),newTag.getText());

        });

        notesField = new TextArea("");
        notesField.setMaxWidth(320);
        notesField.setWrapText(true);
        notesField.setPromptText("notes: ESC to reset, cursor at start & ctrl + Enter to save");
        notesField.setOnKeyPressed(keyEvent-> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (notesField.getCaretPosition() == 0) {
                    setMetaData("notes", notesField.getText(), input.getText());
                    notesField.setText("Saved!");
                }
            }
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                notesField.setText(getMetaData("notes",input.getText()));
                System.out.println("escaped to " + getMetaData("notes",input.getText()));
            }
        });


        newTag.setOnKeyTyped(keyEvent -> {
            searchNewTag = newTag.getText();
            populateTagFieldButtons();
        });

        newTagButton.setOnAction(actionEvent -> {
            for (String s : getSelected(false)) {
                updateTagCount(s, newTag.getText(), 1);
            }


            populateTagFieldButtons();
        });

        tagmodeButton.setOnAction(actionEvent -> {
            if (tagmodeButton.getText().contains("apply")) {
                tagmodeButton.setText("mode: toggle tag");
            } else  if (tagmodeButton.getText().contains("toggle")) {
                tagmodeButton.setText("mode: apply tag");
                toggledTags = new ArrayList<>();
                populateTagFieldButtons();
            }
        });
        HBox newButtonBox = new HBox();
        newButtonBox.getChildren().add(newTag);
        newButtonBox.getChildren().add(newTagButton);
        newButtonBox.getChildren().add(tagmodeButton);
        newButtonBox.getChildren().add(notesField);
        newButtonBox.getChildren().add(moveTagButtons);
        newButtonBox.getChildren().add(addAll);
        newButtonBox.getChildren().add(swapleftlayoutbutton2);
        tagPage.getChildren().addAll(newButtonBox,tagScrollPane);
        tagLayout.getChildren().add(tagPage);
        Scene dialogScene = new Scene(tagLayout, 1920, 1080);

        dialogScene.getStylesheets().add(css);


        //    dialog.setFullScreen(true);

        tagStage.setScene(dialogScene);
        tagStage.setTitle("tagging pannel");
        tagStage.getIcons().add(loadObjective(STYLE_FOLDER+CONNECTOR+"Tagging.png"));
        tagStage.show();

        tagStage.setMaximized(true);
    }

    private static Button lastUsedTagButton;

    public static Button newTagButton;


    public static ArrayList<String> characterlist = new ArrayList();
    public static ArrayList<String> categories = new ArrayList();

    private static int countvisiblepoint = 1;

    private static void populateTagFieldButtons() {


        int removeAmount = tagButtons.getChildren().size();
        for (int i = 0; i < removeAmount; i++) {
            tagButtons.getChildren().remove(0);
        }


        LinkedList<VBox> buttons = new LinkedList<>();





        for (String tag : tags) {


                int count = tagKeys.get(tag).size();

                if (!(tag.equals("safe") && SECURITY_LEVEL > 0) && tag.contains(searchNewTag) && (count >= countvisiblepoint
                        || !searchNewTag.isEmpty() || getTagCount(input.getText(), tag) > 0)) {


                    Button button = new Button(tag);

                    int size = tagKeys.get(tag).size();
                    Text count1 = new Text(size + "");
                    Text count2 = new Text(tagKeys.get(tag).size() + "");
                    if (!input.getText().isEmpty()) {
                        size = (getTagCount(input.getText(), tag));
                        count1.setText(size + "");
                        button.setOnAction(actionEvent -> {


                            if (tagmodeButton.getText().contains("toggle")) {
                                if (toggledTags.contains(tag)) {
                                    toggledTags.remove(tag);
                                } else {
                                    toggledTags.add(tag);
                                }
                                populateTagFieldButtons();
                            } else {
                                for (String s : getSelected(false)) {
                                    updateTagCount(s, tag, 1);
                                }

                            }
                            count1.setText(getTagCount(input.getText(), tag) + "");
                        });
                        button.setOnMouseClicked(mouseEvent -> {
                            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                                updateTagCount(input.getText(), tag, -1);

                            } else if (mouseEvent.getButton() == MouseButton.MIDDLE) {
                                for (String s : collage) {
                                    updateTagCountSafe(s, tag, 1);
                                    System.out.println("updating for " + s + " tag : " + s);

                                }
                                writeTagCount();

                            }
                            count1.setText(getTagCount(input.getText(), tag) + "");
                        });


                    }


                    int actualsize = Integer.parseInt(count1.getText());
                    if (actualsize > 0) {
                        count1.setFill(Color.LIGHTCYAN);
                    }
                    if (actualsize > 1) {
                        count1.setFill(Color.CYAN);
                    }



                    if (categories.contains(tag)) {
                        button.setFont(new Font(15));
                        //         button.setPadding(new Insets(1,1,1,1));
                        button.setTextFill(Color.LIGHTGREY);

                    }

                    //  if (!categories.contains(tag)) {
                    //  count2.setFill(Color.color(Math.min(tagKeys.get(tag).size() / 200.0, 1.0), 0, 0));
                    // }
                    // count1.setFill(Color.LIGHTCYAN);


                    VBox vBox = new VBox();
                    vBox.setAlignment(Pos.TOP_CENTER);


                    vBox.getChildren().addAll(button, count1, count2);
                    applyInsertSort(buttons, vBox, size);
                    if (toggledTags.contains(tag)) {
                        button.setFont(new Font(16));
                        button.setTextFill(Color.YELLOW);
                    }
                }


        }


        for (VBox vBox : buttons) {
            tagButtons.getChildren().add(vBox);
        }

    }

    public static String searchNewTag = "";



    public static boolean slideShowMode;

    private static int currentIndex = 0;

    private static void loadRightImage() {

        int filechoice = currentIndex+1;
        String name = acceptableFiles.get(filechoice);
        currPage = Math.min(filechoice / Integer.parseInt(pageSizeField.getText()), fileCount / Integer.parseInt(pageSizeField.getText()));
        updatePage();


        updateImageView(name);


        updateCounters();

        next = heirarchy2.getOrDefault(name, "");

        previous = heirarchy.getOrDefault(name, "");

        lastSaved = getImageFromConverted(name);
        length.setText((int) lastSaved.getWidth() + "");
        height.setText((int) lastSaved.getHeight() + "");
        //tagField.setText(getTagsString(name, false));
        resetPos();
        // update(canvas);
        //  draw(canvas, name);
        update(canvas);
        canvas.requestFocus();
        currentIndex = filechoice;
    }

    private static void loadLeftImage() {
        int filechoice = Math.max(currentIndex-1,0);

        String name = acceptableFiles.get(filechoice);
        currPage = Math.min(filechoice / Integer.parseInt(pageSizeField.getText()), fileCount / Integer.parseInt(pageSizeField.getText()));
        updatePage();

        updateImageView(name);
        updateCounters();

        next = heirarchy2.getOrDefault(name, "");

        previous = heirarchy.getOrDefault(name, "");

        lastSaved = getImageFromConverted(name);
        length.setText((int) lastSaved.getWidth() + "");
        height.setText((int) lastSaved.getHeight() + "");
        //tagField.setText(getTagsString(name, false));
        resetPos();
        // update(canvas);
        //  draw(canvas, name);
        update(canvas);
        canvas.requestFocus();
        currentIndex = filechoice;
    }

    private static void loadRandomImage() {
        System.out.println("random image loading");
        int filechoice = getRandomImage();
        String name = acceptableFiles.get(filechoice);
        currPage = Math.min(filechoice / Integer.parseInt(pageSizeField.getText()), fileCount / Integer.parseInt(pageSizeField.getText()));
        updatePage();

        updateImageView(name);
        updateCounters();

        next = heirarchy2.getOrDefault(name, "");

        previous = heirarchy.getOrDefault(name, "");

        lastSaved = getImageFromConverted(name);
        length.setText((int) lastSaved.getWidth() + "");
        height.setText((int) lastSaved.getHeight() + "");
        //tagField.setText(getTagsString(name, false));
        resetPos();
        // update(canvas);
        //  draw(canvas, name);
        update(canvas);
        canvas.requestFocus();
        currentIndex = filechoice;
    }



    private static ListView<String> sortByOptions = new ListView<String>();

    private static String slideshowModeType = "random";
    private static String getSlideshowMode() {


         return slideshowModeType;
    }

    private static Text lastVisitText;

    private static TextArea notesField;

    private static Button slideshowPaused;

    private static int timerStart = 10;

    private static long lastToggle = 0;

    private static void startSlideShow() {
        new Thread(
                new Runnable() {

                    int timer = timerStart;
                    long previousLastToggle = lastToggle;

                    public void run() {
                        while (slideShowMode&& previousLastToggle == lastToggle) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    switch(getSlideshowMode()) {
                                        case "random" -> loadRandomImage();
                                        case "left" -> loadLeftImage();
                                        case "right" -> loadRightImage();
                                    }

                                }
                            });
                            while (timer > 0 && previousLastToggle == lastToggle) {

                                    if (!slideShowMode) {
                                        timer = 0;

                                    }
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            Platform.runLater(() -> slideshowPaused.setText((slideShowMode ? "Pause " : "Unpause ") + timer));
                                        }
                                    });

                                    timer--;
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                            }
                            timer = timerStart;;


                        }
                    }
                }).start();
    }

    private static void setUpScroller() throws MalformedURLException {

        dialog = new Stage();
        stages = new ArrayList<>();
        imageStack = new ArrayList<>();

        length = new TextField("");
        length.setPromptText("length");
        length.setFocusTraversable(false);
        height = new TextField("");
        height.setPromptText("height");
        height.setFocusTraversable(false);

        toggleButton = new ToggleButton();
        toggleButton.setSelected(true);
        toggleButton.setFocusTraversable(false);


        ObservableList<String> list = FXCollections.observableArrayList();





        HBox search = new HBox();

        sortByOptions= new ListView<String>();
        ObservableList<String> sortlist = FXCollections.observableArrayList();
        sortlist.add("modified");
        sortlist.add("visited");
        sortByOptions.setItems(sortlist);


        sortByOptions.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        sortByOptions.getSelectionModel().select(0);
        search.getChildren().add(sortByOptions);
        sortByOptions.setMaxHeight(50);


        list.add("random");
        list.add("left");
        list.add("right");



        Button changeDir = new Button("folder");
        changeDir.setOnAction(actionEvent -> {
            userChooseDir(primaryStage);
            depopulateImageButtons(imageButtons);
            populateImageButtons(input,canvas,imageButtons,length,height);
        });

        navButtons = new VBox();
        navLayout = new HBox();
        navLayout.getChildren().add(navButtons);


        include = new TextField("*");
        include.setFocusTraversable(false);
        include.setPromptText("tags to include");

        Button collageMode = new Button("collage mode");
        Button slideShowModeButton = new Button("slideshow");
        slideshowPaused = new Button("");
        slideshowPaused.setOnAction(actionEvent -> {
            slideShowMode = !slideShowMode;
            slideshowPaused.setText((slideShowMode ? "Pause " : "Unpause "));
            lastToggle= System.currentTimeMillis();
            if (slideShowMode) {
                startSlideShow();
            }
        });


        slideShowModeButton.setOnAction(event -> {

            ButtonType random = new ButtonType("random", ButtonBar.ButtonData.OK_DONE);
            ButtonType left = new ButtonType("left", ButtonBar.ButtonData.OK_DONE);
            ButtonType right = new ButtonType("right", ButtonBar.ButtonData.OK_DONE);

            Alert chooseSlideshow = new Alert(Alert.AlertType.CONFIRMATION, "slideshow options" , ButtonType.CANCEL,random,left,right);
            //chooseSlideshow.setHeaderText("");
            HBox timerB = new HBox();
            Text timerS = new Text("Countdown: ");
            TextField timer = new TextField(timerStart+"");
            timer.setMinWidth(50);
            timerB.setSpacing(10);
            timerB.getChildren().addAll(timerS,timer);






            VBox slideshowOptions = new VBox();
            slideshowOptions.setSpacing(10);

            slideshowOptions.getChildren().addAll(timerB);


            chooseSlideshow.getDialogPane().getChildren().add(slideshowOptions);


            chooseSlideshow.showAndWait();
            timerStart = Integer.parseInt(timer.getText());
            lastToggle= System.currentTimeMillis();
            switch(chooseSlideshow.getResult().getText()) {
                case "random" -> {
                   slideshowModeType = "random";
                    slideShowMode = true;
                    startSlideShow();

                }
                case "left" -> {
                    slideshowModeType = "left";
                    slideShowMode = true;
                    startSlideShow();

                }
                case "right" -> {
                    slideshowModeType = "right";
                    slideShowMode = true;
                    startSlideShow();

                }
            }


        });
        collageMode.setFocusTraversable(false);


        Button cursorButton = new Button("cursor");
        cursorButton.setFocusTraversable(false);
        Button paintButton = new Button("paint");
        paintButton.setFocusTraversable(false);
        Button selectButton = new Button("select");
        selectButton.setFocusTraversable(false);

        Button thumbnailModeButton = new Button("fastMode");
        Button refresh = new Button("refresh feed");
        Button delete = new Button("delete");
        Button swapGUi = new Button("swap panel");
        delete.setFocusTraversable(false);
        refresh.setFocusTraversable(false);
        Button load = new Button("Load Image");

        Button joinLast = new Button("join Last");
        Button export = new Button("export");
        Button saveCurrent = new Button("save current");
        Button saveCurrentMonochrome = new Button("save current Monochrome");


        swapGUi.setOnAction(actionEvent -> {
            swapLeftPanel();
        });


        HBox tools = new HBox();
        tools.getChildren().addAll(cursorButton, paintButton, selectButton, toggleButton, thumbnailModeButton);

        thumbnailModeButton.setOnAction(actionEvent -> {
            thumbnailMode = !thumbnailMode;
            if (thumbnailMode) {
                thumbnailModeButton.setStyle("-fx-background-color: white");
            } else {
                thumbnailModeButton.setStyle("");
            }
        });


        cursorButton.setOnAction(actionEvent -> {
            toolType = "cursor";
        });
        paintButton.setOnAction(actionEvent -> {
            toolType = "paint";
            if (color == Color.BLACK) {
                color = Color.WHITE;
            } else {
                color = Color.BLACK;
            }

        });
        selectButton.setOnAction(actionEvent -> {
            toolType = "select";
        });


        input = new TextField("");
        input.setPromptText("enter file name");


        imageButtons = new FlowPane();
        imageButtons.setMaxHeight(10000);



        joinLast.setOnAction(actionEvent -> {
            //    images[0].get//.getName().replace("png.txt", ".png");
            //System.out.println("join: " + images[0].getName());
            String name = ((Button) ((VBox) imageButtons.getChildren().get(0)).getChildren().get(0)).getTooltip().getText().split("\n")[0].replace(".png", "png.txt");
            String name2 = ((Button) ((VBox) imageButtons.getChildren().get(1)).getChildren().get(0)).getTooltip().getText().split("\n")[0].replace(".png", "png.txt");


            System.out.println("joining " + name + " with " + name2);
            ArrayList<String> file1 = loadConverted(name);
            ArrayList<String> file2 = loadConverted(name2);
            String firstLine1 = file1.get(0);
            String firstLine2 = file2.get(0);
            file1.remove(0);

            String width = "" + (Integer.parseInt(firstLine1.split(" ")[1]) + Integer.parseInt(firstLine2.split(" ")[1]));
            String[] firstLine2Segments = firstLine2.split(" ");
            String resultFirstLine2 = firstLine2Segments[0] + " " + width;

            for (int i = 2; i < firstLine2Segments.length; i++) {

                resultFirstLine2 = resultFirstLine2 + " " + firstLine2Segments[i];
            }

            file2.set(0, resultFirstLine2);
            file2.addAll(file1);
            try {
                writeFileText(name2, file2);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            delete(name);


            // depopulateImageButtons(imageButtons);
            // populateImageButtons();

        });
        //   imageButtons.setHgap(1);
        //   imageButtons.setVgap(1);
        //   imageButtons.setPrefWrapLength(200);


        saveCurrentMonochrome.setOnAction(event -> {
            length.setText("" + Math.min(Integer.parseInt(length.getText()), 8000));
            prepareSave(canvas, length, height);

            updateTagCount(getCurrentFileName(), "monochrome", 1);

            if (SECURITY_LEVEL > 0) {
                updateTagCount(getCurrentFileName(), "safe", SECURITY_LEVEL);
            }
            try {
                convertMonochrome(lastSaved, getCurrentFileName());
                convertMonochrome(getThumbnail(), getCurrentFileName().replace(".png", "Thumbnail.png"));
                //  convertJSonMonochrome(lastSaved, input.getText());

            } catch (IOException e) {
                e.printStackTrace();
            }


            depopulateImageButtons(imageButtons);
            populateImageButtons(input, canvas, imageButtons, length, height);
        });

        export.setOnAction(event -> {
            ButtonType png = new ButtonType("png", ButtonBar.ButtonData.APPLY);
            ButtonType cryptic = new ButtonType("cryptic", ButtonBar.ButtonData.APPLY);

            Alert notCryptic = new Alert(Alert.AlertType.CONFIRMATION, "select export option" , png, cryptic,ButtonType.CANCEL);
            notCryptic.showAndWait();

            switch(notCryptic.getResult().getText()) {
                case "png" -> {
                    saveAs("png");
                }
                case "cryptic" -> {
                    saveAs("cryptic");
                }
                default -> {

                }
            }

          //  saveAs("cryptic");


        });

        load.setOnAction(event -> {
            WritableImage writableImage = new WritableImage(CANVAS_SIZE, 1400);
            lastSaved = canvas.snapshot(null, writableImage);
            draw(canvas, getCurrentFileName());
            length.setText((int) lastSaved.getWidth() + "");
            height.setText((int) lastSaved.getHeight() + "");


        });




        scrollPane = new ScrollPane();
        scrollPane.setMaxWidth(navLayout.getMaxWidth());


        scrollPane.setFocusTraversable(false);

        populateImageButtons(input, canvas, imageButtons, length, height);
        scrollPane.setContent(imageButtons);


        // scrollPane.setPrefViewportHeight(1000);
        scrollPane.setMaxHeight(10000);
        scrollPane.setPannable(true);
        //scrollPane.setPrefViewport;

        saveCurrent.setOnAction(event -> {
            length.setText("" + Math.min(Integer.parseInt(length.getText()), 8000));
            prepareSave(canvas, length, height);

            updateTagCount(getCurrentFileName(), "normal", 1);
            for (String s : toggledTags) {
                updateTagCountSafe(getCurrentFileName(), s, 1);
            }
            if (SECURITY_LEVEL > 0) {
                updateTagCount(getCurrentFileName(), "safe", SECURITY_LEVEL);
            }
            try {
                convert(lastSaved, getCurrentFileName());
                convert(getThumbnail(), getThumbnailName(getCurrentFileName()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            depopulateImageButtons(imageButtons);
            populateImageButtons(input, canvas, imageButtons, length, height);
        });


        refresh.setOnAction(actionEvent -> {

            depopulateImageButtons(imageButtons);
            populateImageButtons(input, canvas, imageButtons, length, height);
        });

        delete.setOnAction(actionEvent -> {
            deleteSelection();




        });



        collageMode.setOnAction(actionEvent -> {

            depopulateImageButtons(imageButtons);
            populateImageButtons(input, canvas, imageButtons, length, height);

            x = 0;
            y = 0;
            scroll = 1;

            constructCollage(canvas);
            lastSaved = canvas.snapshot(null, null);
            update(canvas);
        });

        navButtons.getChildren().add(clipboard);
        HBox oldstuff = new HBox();
        oldstuff.getChildren().add(changeDir);
        oldstuff.getChildren().add(export);
        if (useExperimentalFeatures>1) {

            oldstuff.getChildren().add(load);

        }

        oldstuff.getChildren().add(delete);
        oldstuff.getChildren().add(swapGUi);



        navButtons.getChildren().add(oldstuff);


        Button randomNameButton = new Button("Randomise");
        randomNameButton.setOnAction(event ->

        {

            updateImageView(getCurrentFileName());
            updateCounters();
        });


        nextButton = new Button("next>");
        nextButton.setFocusTraversable(false);
        paintButton.setFocusTraversable(false);
        previousButton = new Button("<prev");
        selectButton.setFocusTraversable(false);
        previousButton.setFocusTraversable(false);

        nextButton.setOnAction(e -> {

            if (space) {
                next = Clipboard.getSystemClipboard().getString();
                nextButton.setTooltip(new Tooltip(next));
                canvas.requestFocus();
            } else {

                loadRightImage();
            }

        });

        previousButton.setOnAction(e -> {
            if (space) {
                previous = Clipboard.getSystemClipboard().getString();
                previousButton.setTooltip(new Tooltip(previous));
            } else {
                loadLeftImage();
            }
        });

        HBox pager = new HBox();
        lastVisitText = new Text("69-69-69");




        HBox randomm = new HBox();

        randomm.getChildren().addAll(randomNameButton,slideShowModeButton,slideshowPaused);
        navButtons.getChildren().add(randomm);


        HBox mainInfo = new HBox();
        mainInfo.getChildren().addAll(input, length, height);
        navButtons.getChildren().add(mainInfo);

        if (useExperimentalFeatures>1) {
            navButtons.getChildren().add(joinLast);
            navButtons.getChildren().add(saveCurrent);

            navButtons.getChildren().add(saveCurrentMonochrome);
        }




        //    buttons.getChildren().add(saveLines);

        navButtons.getChildren().add(tools);



        navButtons.getChildren().add(pager);

        navButtons.getChildren().add(include);

        include.setPromptText("eg \"cool>1 and !rad=0\"");



        search.getChildren().add(refresh);




        bookmark = new Button("bookmark");
        views = new Text("views");
        lastUsedTagButton = new Button("last used: ");
        lastUsedTagButton.setOnAction(actionEvent -> {

            //timeout to prevent writing to the file too quickly

            updateTagCount(input.getText(), lastUsedTag, 1);
            lastUsedTagButton.setText("last used: " + lastUsedTag + " " + getTagCount(input.getText(), lastUsedTag));
            populateTagFieldButtons();


        });

        lastUsedTagButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                //timeout to prevent writing to the file too quickly

                updateTagCount(input.getText(), lastUsedTag, -1);
                lastUsedTagButton.setText("last used: " + lastUsedTag + " " + getTagCount(input.getText(), lastUsedTag));
                populateTagFieldButtons();

            }
        });

        views.setFill(Color.WHITE);



        search.getChildren().add(bookmark);
        search.getChildren().add(lastUsedTagButton);

        pager.getChildren().addAll(previousButton, nextButton, lastVisitText, views);
        final long[] lastClick = {0l};



        bookmark.setOnAction(actionEvent -> {
            //timeout to prevent writing to the file too quickly

            updateTagCount(input.getText(), "bookmark", 1);
            bookmark.setText("bookmark " + getTagCount(input.getText(), "bookmark"));

        });
        bookmark.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                updateTagCount(input.getText(), "bookmark", -1);
                bookmark.setText("bookmark " + getTagCount(input.getText(), "bookmark"));
            }
        });

        navButtons.getChildren().add(search);

        navButtons.getChildren().add(scrollPane);


        dialog.initOwner(primaryStage);
        //  VBox dialogVbox = new VBox(20);
        //      dialogVbox.getChildren().add(layout);
        URL url = Path.of(STYLE_FOLDER+CONNECTOR+"Style.css").toUri().toURL();
        if (url == null) {
            System.out.println("Resource not found. Aborting.");
            System.exit(-1);
        }
        String css = url.toExternalForm();

        Scene dialogScene = new Scene(navLayout, 1920, 1080);

        dialogScene.getStylesheets().add(css);


        //    dialog.setFullScreen(true);

        dialog.setScene(dialogScene);
        dialog.getIcons().add(loadObjective(STYLE_FOLDER+CONNECTOR+"Toolbar"));

        dialog.setTitle("Toolbar");
        dialog.show();


        dialog.setMaximized(true);

    }

    //0=untoggle selected
    //1 = keep selected
    //2 = go to first selected in the image browser (not yet implemented)
    //3 = do nothing
    private static int usedSelectionBehavior = 0;

    private static void usedSelection(String type) {
        switch (usedSelectionBehavior) {
            case 0 -> {
                tagSelection2 = -1;
                tagSelection1=-1;
                depopulateImageButtons(imageButtons);
                populateImageButtons(input, canvas, imageButtons, length, height);
            }
            case 1 -> {

                depopulateImageButtons(imageButtons);
                populateImageButtons(input, canvas, imageButtons, length, height);
            }
            case 2 -> {
                currPage=tagSelection1/Integer.parseInt(pageSizeField.getText());
                depopulateImageButtons(imageButtons);
                populateImageButtons(input, canvas, imageButtons, length, height);
            }
        }
    }


    private static void saveAs(String type) {
        String[] selection = getSelected(true);
        if (selection == null) {
            Alert notCryptic = new Alert(Alert.AlertType.INFORMATION, "no selected input", ButtonType.CLOSE);
            notCryptic.showAndWait();
            return;
        }
        prepareSave(canvas, length, height);
        String failed = "";
        boolean failedR = false;



                switch (type) {
                    case "cryptic" -> {
                        for (String s : selection) {
                            if (!isCryptic(s)) {
                                try {
                                    Image image = loadImage(s);
                                    convert(image, s);

                                    convert(getThumbnail(image), s.replace(".png","Thumbnail.png"));
                                } catch (IOException e) {
                                    failed = failed + "\n" + s + " error! ";
                                    failedR = true;
                                }
                            } else {
                                failed = failed + "\n" + s;
                                failedR = true;
                            }
                        }
                    }

                    case "png" -> {
                        for (String s : selection) {
                            if (!isCryptic(s)) {
                                saveCrypticToPNG(s);
                            } else {
                                failed = failed + "\n" + s;
                                failedR = true;
                            }
                        }
                    }
                    default ->{
                        failed = "invalid conversion type: " + type;
                        failedR= true;
                    }
                }


                Alert notCryptic = new Alert(Alert.AlertType.INFORMATION, failedR ? type + " type output is incompatible with input:" + failed : "Success", ButtonType.CLOSE);
                notCryptic.showAndWait();

                usedSelection("save");
    }

    private static void deleteSelection() {
        String filesToDelete ="";
        ArrayList<String> deleting = new ArrayList<>();
        for (String s : getSelected(false)) {
            String filepath = getFolder()+ CONNECTOR+ s;

            deleting.add(getFolder()+CONNECTOR+s);
            filesToDelete = filesToDelete + "\n" +filepath;

            if (isCryptic(s)) {
                deleting.add(getThumbnailName(filepath));
                filesToDelete = filesToDelete + "\n" + getThumbnailName(filepath);
            }
        }
        String stringMultipleCheck = deleting.size() > 1 ? deleting.size() + " files:" : "file:";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you wish to delete the following " + stringMultipleCheck+ "\n" + filesToDelete +" ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {

            ArrayList<String> failedDeletes = new ArrayList<>();
            ArrayList<String> dontExist = new ArrayList<>();
            ArrayList<String> success = new ArrayList<>();
            for (String s : deleting) {
                File file = new File(s);

                if (!file.exists()) {
                    dontExist.add(s);

                }else {
                    if (!file.delete()) {
                        failedDeletes.add(s);
                    } else {
                        success.add(s);
                    }
                }
            }

            String resultsOfDelete = String.join(" Failed\n, ", failedDeletes) +(!failedDeletes.isEmpty() ? " Failed" : "");
            resultsOfDelete = resultsOfDelete + "\n" + String.join(" Doesn't Exist\n, ", dontExist) +(!dontExist.isEmpty() ? " Doesn't Exist" : "");
            resultsOfDelete = resultsOfDelete + "\n" + String.join(" Successfully Deleted\n, ", success) +(!success.isEmpty() ? " Successfully Deleted" : "");
            Alert resultsDelete = new Alert(Alert.AlertType.INFORMATION, "Results: \n"+resultsOfDelete, ButtonType.CLOSE);
            resultsDelete.showAndWait();



            usedSelection("deleteImage");



        }
    }

    private static String[] getSelected(boolean forceEmpty) {
        if (tagSelection2 == -1) {
            return forceEmpty&&input.getText().isEmpty() ? null : new String[]{ getCurrentFileName()};
        } else {
            String[] output = new String[tagSelection2-tagSelection1+1];
            int upcounting = 0;
            for (int i = tagSelection1; i <= tagSelection2; i++) {
                output[upcounting] = acceptableFiles.get(i);
                upcounting++;
            }
            return output;
        }
    }

    private static String getThumbnailName(String name) {
        return name.replace("png.txt","Thumbnailpng.txt");
    }

    private static String getCurrentFileName() {
        if (input.getText().isEmpty()) {
            long yourmilliseconds = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss");
            Date resultdate = new Date(yourmilliseconds);
            return sdf.format(resultdate) + ".png";
        }
        return input.getText();
    }

    private static void saveCrypticToPNG(String name) {
        Image image = getImageFromConverted(name);
        BufferedImage bufferI = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bufferI,"png",new File(getFolder()+CONNECTOR+name.replace("png.txt",".png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static HBox canvasLayout;
    private static VBox navButtons;
    private static boolean leftpanelswap = false;
    private static void swapLeftPanel() {
        leftpanelswap = !leftpanelswap;

        if (leftpanelswap) {
            tagLayout.getChildren().setAll(navButtons);
            navLayout.getChildren().setAll(tagPage);
        } else {
            tagLayout.getChildren().setAll(tagPage);
            navLayout.getChildren().setAll(navButtons);
        }


      //  tagPage.getChildren().setAll(bufferlist2);
      //  navButtons.getChildren().setAll(bufferlist);
       // tagStage.getScene()
        //dialog.setScene(bufferscene);
    }

/*
    public static void migrateTags() {
        for (int i = 0; i < images.length; i++) {

            String imageName = file(images[i].getName());

            if (fileTags.get(imageName) != null) {
                System.out.println(imageName);

                List<String> taglist = List.of(fileTags.get(imageName));
                for (int j = 0; j < taglist.size(); j++) {
                    if (j != 1 && j != 2) {
                        updateTagCountSafe(imageName, taglist.get(j), 1);
                    }
                }
            }
        }
        System.out.println("migrated tags");
        writeTagCount();

    }

 */


    public static void updateTagCountSafe(String file, String type, int amount) {
        HashMap<String, Integer> current = getTagMapCount(file);
        current.put(type, current.getOrDefault(type, 0) + amount);
        String newEntry = "[,]";
        for (Map.Entry<String, Integer> oldentry :
                current.entrySet()) {
            newEntry = newEntry + "," + oldentry.getKey() + ";" + oldentry.getValue();
        }
        newEntry = newEntry.replace("[,],", "");
        newEntry = newEntry.replace("[,]", "");
        tagMap.put(file, newEntry);

    }



    public static void updateTagCount(String file, String type, int amount) {
        if (!type.equals("view") && !((SECURITY_LEVEL > 0) && type.equals("safe"))) lastUsedTag = type;
        lastUsedTagButton.setText("last used: " + lastUsedTag + " " + getTagCount(input.getText(), lastUsedTag));
        HashMap<String, Integer> current = getTagMapCount(file);
        current.put(type, current.getOrDefault(type, 0) + amount);
        String newEntry = "[,]";
        for (Map.Entry<String, Integer> oldentry :
                current.entrySet()) {
            newEntry = newEntry + "," + oldentry.getKey() + ";" + oldentry.getValue();
        }
        newEntry = newEntry.replace("[,],", "");
        newEntry = newEntry.replace("[,]", "");
        tagMap.put(file, newEntry);
        writeTagCount();
        refreshTagCounts();

    }



    public static ArrayList<String> getPureTagList(String file) {
        String value = tagMap.get(file);
        if (value == null) {
            value = "";
        }
        ArrayList<String> output = new ArrayList<>();

        String[] input = value.split(",");
        for (int i = 0; i < input.length; i++) {
            output.add(input[i].split(";")[0]);
        }
        return output;
    }



    public static HashMap<String, Integer> getTagMapCount(String file) {
        String value = tagMap.get(file);
        HashMap<String, Integer> output = new HashMap<>();
        String[] input = {value};
        if (value != null) {
            input = value.split(",");
            for (String s : input) {
                output.put(s.split(";")[0], Integer.parseInt(s.split(";")[1]));
            }
        }

        return output;
    }

    public static int getTagCount(String file, String tag) {
        return getTagMapCount(file).getOrDefault(tag, 0);
    }

    public static HashMap<String, String> heirarchy = new HashMap<>();
    public static HashMap<String, String> heirarchy2 = new HashMap<>();
    public static String heirarchyFile = "heirarchy.txt";

    public static final void writeHeirarchy() {
        File file = new File(getFolder()+heirarchyFile);

        BufferedWriter bf = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            bf = new BufferedWriter(new FileWriter(file));

            // iterate map entries
            for (Map.Entry<String, String> entry :
                    heirarchy.entrySet()) {

                // put key and value separated by a colon
                bf.write(entry.getKey() + ":"
                        + entry.getValue());

                // new line
                bf.newLine();
            }

            bf.flush();
        } catch (Exception e) {
            System.out.println("file not exists t");
            e.printStackTrace();
        } finally {

            try {

                // always close the writer
                bf.close();
            } catch (Exception e) {
            }
        }
    }

    public static final void readHeirarchy() {
        File file = new File(getFolder() +heirarchyFile);


        BufferedReader br = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            br = new BufferedReader(new FileReader(file));


            String line = null;
            while ((line = br.readLine()) != null) {

                // split the line by :
                String[] parts = line.split(":");

                // first part is name, second is number

                String name = parts[0].trim();
                String number = "";
                if (parts.length > 1) {
                    number = parts[1].trim();
                }

                // put name, number in HashMap if they are
                // not empty
                if (!name.isEmpty() && !number.isEmpty()) {
                    heirarchy.put(name, number);
                    heirarchy2.put(number, name);
                }
            }


        } catch (IOException ignored) {

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ignored) {

                }
            }
        }

    }


    public static final void readTagCount() {
        File file = new File(getFolder()+tagCountFile);
        tagMap = new HashMap<>();
        metamap = new HashMap<>();

        BufferedReader br = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            br = new BufferedReader(new FileReader(file));


            String line = null;
            while ((line = br.readLine()) != null) {

                // split the line by :
                String[] parts = line.split(":");

                // first part is name, second is value
                String name = parts[0].trim();
                String value = parts[1].trim();
                String metadata = "";
                if (parts.length > 2) {
                    metadata = parts[2].trim();
                }

                // put name, number in HashMap if they are
                // not empty
                if (!name.equals("") && !value.equals("")) {
                    tagMap.put(name, value);
                    metamap.put(name,metadata);
                }
            }


        } catch (IOException e) {

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {

                }
            }
        }

    }


    private static final String tagCountFile = "tagcount.txt";

    private static HashMap<String, String> tagMap = new HashMap<>();
    private static HashMap<String, String> metamap = new HashMap<>();
    public static final void refreshTagCounts() {


        tagKeys = new HashMap<>();
        tags = new ArrayList<>();
        for (Map.Entry<String, String> entry :
                tagMap.entrySet()) {

            ArrayList<String> localTags = getPureTagList(entry.getKey());
            if (getTagCount(entry.getKey(), "safe") >= SECURITY_LEVEL) {

                for (String s : localTags) {
                    if (!tags.contains(s)) {
                        tags.add(s);
                    }
                    ArrayList<String> newUpdate = tagKeys.getOrDefault(s, new ArrayList<>());

                    if (getTagCount(entry.getKey(), s) > 0) {
                        newUpdate.add(entry.getKey());
                    }
                    tagKeys.put(s, newUpdate);
                }

            }
        }
    }

    public static final void writeTagCount(ArrayList<String> items, String folder) {


        File file = new File(getFolder()+folder+tagCountFile);
        System.out.println(file.getAbsoluteFile());

        BufferedWriter bf = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            bf = new BufferedWriter(new FileWriter(file));


            // iterate map entries
            for(String s : items) {

                // put key and value separated by a colon
                bf.write(s + ":"
                        + tagMap.get(s)+ ":" + metamap.getOrDefault(s,""));

                // new line
                bf.newLine();
            }


            bf.flush();
        } catch (Exception e) {
            System.out.println("file not exists when writing to tagcount");
            e.printStackTrace();
        } finally {

            try {

                // always close the writer
                bf.close();
            } catch (Exception e) {
            }
        }

    }

    public static final void writeTagCount() {


        File file = new File(getFolder()+tagCountFile);
        System.out.println(file.getAbsoluteFile());

        BufferedWriter bf = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            bf = new BufferedWriter(new FileWriter(file));

            for (File f : images) {


                String name = f.getName();

                if (tagMap.containsKey(name )) {

                    System.out.println(name);

                    // put key and value separated by a colon
                    bf.write(name  + ":"
                            + tagMap.get(name ) + ":" + metamap.getOrDefault(name ,""));

                    // new line
                    bf.newLine();
                }
            }


            bf.flush();
        } catch (Exception e) {
            System.out.println("file not exists when writing to tagcount");
            e.printStackTrace();
        } finally {

            try {

                // always close the writer
                bf.close();
            } catch (Exception e) {
            }
        }

    }


    public static void delete(String name) {
        File file = new File(getFolder()+name);
        file.delete();
        File file2 = new File(getFolder()+name.replace(".png", "Thumbnailpng.txt"));
        file2.delete();

    }


    public static int getRandomImage() {

        int index = (int) (Math.random() * acceptableFiles.size());


        return index;
    }

    private static void saveCurrent(String input) {

        prepareSave(canvas, length, height);


        try {
            convert(lastSaved, input);
            convert(getThumbnail(), input.replace(".png", "Thumbnail.png").replace(".jpg", "Thumbnail.png").replace(".webp", "Thumbnail.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeHeirarchy();

    }

    private static void canvasMouseClicked(double x, double y, String buttonName) {
        if (!toolType.equals("cursor")) {
            isDragging = false;
            releaseTool(canvas);
            return;
        }
        if (buttonName.equals("MIDDLE")) {
            double scrollb = scroll;
            scroll = 1;
            double distanceToCenterBeforeX = x;
            double distanceToCenterBeforeY = y;

            double factor = scroll / scrollb;
            double distanceNowX = distanceToCenterBeforeX * factor;
            double distanceNowY = distanceToCenterBeforeY * factor;
            double xdiff = distanceNowX - distanceToCenterBeforeX;
            double ydiff = distanceNowY - distanceToCenterBeforeY;
            x -= xdiff / scroll;
            y -= ydiff / scroll;
            update(canvas);
            isDragging = false;
            return;
        }

        if (buttonName.equals("SECONDARY")) {
            lastSaved = undoImage;
            update(canvas);
        } else {

            if (!isDragging) {
              //  pasteImage(x, y, getImageFromClipboard());

            }
        }
        isDragging = false;
    }


    public static TextField pageSizeField;


    //debug later, seems to lag on linux
    private static void pasteImage(double x, double y, Image image) {

        WritableImage writableImage = new WritableImage((int) image.getWidth(), 2160);


        if (image != null) {
            clipboard.setText((int) image.getWidth() + " " + (int) image.getHeight());

            double xb = HelloApplication.x;
            double yb = HelloApplication.y;
            double scrollb = scroll;
            HelloApplication.x = 0;
            HelloApplication.y = 0;
            scroll = 1;
            update(canvas);
            undoImage = lastSaved;
            double imageWidth = image.getWidth();
            double imageHeight = image.getHeight();


            if (imageHeight > 2160) {
                double ratio = imageHeight / 2160.0;
                imageWidth = imageWidth / ratio;
                imageHeight = 2160;
            }
            if (toggleButton.isSelected()) {

                length.setText((int) imageWidth + "");
                height.setText((int) imageHeight + "");


                canvas.getGraphicsContext2D().drawImage(image, 0, 0, imageWidth, imageHeight);

            } else {
                canvas.getGraphicsContext2D().drawImage(image, x / scrollb - xb, y / scrollb - yb, imageWidth, imageHeight);
            }
            lastSaved = canvas.snapshot(null, writableImage);

            HelloApplication.x = xb;
            HelloApplication.y = yb;
            scroll = scrollb;
            update(canvas);
        }

    }

    private static Image getThumbnail(Image... image) {
        if (image == null) {
            image= new Image[]{canvas.snapshot(null, null)};
        }

        double imgWidth = image[0].getWidth() / 6;
        double imgHeight = image[0].getHeight() / 6;

        Canvas tempCanvas = new Canvas(imgWidth, imgHeight);
        tempCanvas.getGraphicsContext2D().drawImage(image[0], 0, 0, imgWidth, imgHeight);


        WritableImage tempWritableImage = new WritableImage((int) imgWidth, (int) imgHeight);

        //tempCanvas.getGraphicsContext2D().setImageSmoothing(true);


        return tempCanvas.snapshot(null, tempWritableImage);
    }

    private static void releaseTool(Canvas canvas) {
        if (toolType.equals("paint")) {
            if (scroll < 1) {
                return;
            }
            double xb = x;
            double yb = y;
            double scrollb = scroll;
            x = 0;
            y = 0;
            scroll = 1;
            carrot = 0;
            written = "";
            Image temp = canvas.snapshot(null, null);
            update(canvas);
            //    canvas.getGraphicsContext2D().drawImage(image, event.getX() / scrollb - xb, event.getY() / scrollb - yb, image.getWidth(), image.getHeight());
            canvas.getGraphicsContext2D().drawImage(temp, -xb, -yb, temp.getWidth() / scrollb, temp.getHeight() / scrollb);
            lastSaved = canvas.snapshot(null, null);

            x = xb;
            y = yb;
            scroll = scrollb;
            update(canvas);

        }
    }



    private static int collageIndex = 0;


    private static void constructCollage(Canvas canvas) {

        int currentWidth = 0;


        for (int i = 0; i < collage.size(); i++) {
            if (i >= collageIndex) {
                int j = i - collageIndex;
                String s = collage.get(i);

                //if (usedHeight < maxHeight) {

                Image image = null;
                int imageHeight = Integer.parseInt(fileTags.get(s)[2]);

                int imageWidth = Integer.parseInt(fileTags.get(s)[1]);

                if (currentWidth + imageWidth <= CANVAS_SIZE) {

                    image = getImageFromConverted(s);

                    canvas.getGraphicsContext2D().drawImage(image, x + currentWidth, y);
                    canvas.getGraphicsContext2D().setFont(new Font("monospace", 20));
                    canvas.getGraphicsContext2D().fillText(s, x + currentWidth + 20, y + 20);
                    currentWidth = currentWidth + imageWidth;

                }
            }

            // }

        }


    }

    //metadata should not use the following characters: - : and ()
    private static final String PADDING_CHAR = "------";
//hwo to get time: Calendar.getInstance().getTime()
    private static void setMetaData(String dataname,String data, String filename) {
        String databefore = data;
        data = data.replace("-", "_");
        data = data.replace(":", "_");
        data = data.replace("(", "_");
        data = data.replace(")", "_");
        data = data.replace("\n", "(!enter!)");
        if (!data.equals(databefore)) {
            System.out.println("Metadata illegal character detected for" + dataname + " " + data + " " + filename);

        }
        String leftdata;
        String rightdata;
        if (metamap.getOrDefault(filename, "wtf").equals("wtf")) {
            metamap.put(input.getText(), "(" +dataname+ ")" + data+"(" + dataname + ")");
        } else {

            String datas =  metamap.get(filename);
            if (datas.contains("(" +dataname+ ")")) {
                datas = datas + PADDING_CHAR;

                leftdata = datas.split("\\(" +dataname+ "\\)")[0];
                rightdata = datas.split("\\(" +dataname+ "\\)")[2].replace(PADDING_CHAR, "");
                metamap.put(filename, leftdata + "(" + dataname + ")" + data+"(" + dataname + ")" + rightdata);
            } else {
                metamap.put(filename, datas + "(" + dataname + ")" + data+"(" + dataname + ")");
            }


        }
    }

    public static String formatOutputMetadata(String naieveOutput) {
        return naieveOutput.replace("(!enter!)","\n");
    }

    private static String getMetaData(String dataname, String filename) {

        if (metamap.getOrDefault(filename, "wtf").equals("wtf")) {
            return null; //file meta does not exist
        } else {

            String datas =  metamap.get(filename);
            if (datas.contains("(" +dataname+ ")")) {
                return formatOutputMetadata(datas.split("\\(" +dataname+ "\\)")[1]);

            } else {
                return null; //meta exists for this file, but no data
            }


        }
    }


    private static void prepareSave(Canvas canvas, TextField length, TextField height) {
        x = 0;
        y = 0;
        scroll = 1;
        update(canvas);
        int length1 = 32;
        int height1 = 32;
        if (!(height.getText().isEmpty() || length.getText().isEmpty())) {
            length1 = Math.max((int) Integer.parseInt(height.getText()), 1);
            height1 = Math.max((int) Integer.parseInt(length.getText()), 1);
            WritableImage writableImage = new WritableImage(length1, height1);
            lastSaved = canvas.snapshot(null, writableImage);
        }

        update(canvas);
        carrot = 0;
    }

    private static void resetPos() {

        x = 0;
        y = 0;
        scroll = 2;
        boolean isWidth = primaryStage.getWidth() > primaryStage.getHeight();


        if (isWidth) {
            double value = lastSaved.getHeight();
            scroll = (primaryStage.getHeight() - 50) / value;
            canvas.requestFocus();
        } else {

            double value = lastSaved.getWidth();
            scroll = (primaryStage.getWidth() - 16) / value;
            canvas.requestFocus();
        }



    }

    private static void fillCanvas(Canvas canvas) {
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private static int sizeFactor = 15;

    private static void update(Canvas canvas) {

        canvas.getGraphicsContext2D().setFill(Color.BLACK);
        fillCanvas(canvas);
        canvas.getGraphicsContext2D().drawImage(lastSaved, x * scroll, y * scroll, lastSaved.getWidth() * scroll, lastSaved.getHeight() * scroll);
        canvas.getGraphicsContext2D().setFill(Color.PURPLE);
        canvas.getGraphicsContext2D().fillRect(x * scroll - sizeFactor, y * scroll - sizeFactor, lastSaved.getWidth() * scroll + sizeFactor * 2, sizeFactor);
        canvas.getGraphicsContext2D().fillRect(x * scroll - sizeFactor, y * scroll - sizeFactor, sizeFactor, lastSaved.getHeight() * scroll + sizeFactor * 2);
        canvas.getGraphicsContext2D().fillRect(lastSaved.getWidth() * scroll + x * scroll, y * scroll - sizeFactor, sizeFactor, lastSaved.getHeight() * scroll + sizeFactor * 2);
        canvas.getGraphicsContext2D().fillRect(x * scroll - sizeFactor, y * scroll + lastSaved.getHeight() * scroll, lastSaved.getWidth() * scroll + sizeFactor * 2, sizeFactor);


        for (int i = 0; i < CANVAS_SIZE / 1000 + 1; i++) {

            canvas.getGraphicsContext2D().fillText(i + "K", x * scroll + i * 1000 * scroll, y * scroll - 20);
        }
    }

    private static void draw(Canvas canvas, String input) {
        new Thread(
                new Runnable() {



                    public void run() {


                        resetPos();

                        lastSaved = getImageFromConverted(input);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                update(canvas);
                                canvas.requestFocus();
                            }

                        });
                    }
                }).start();



    }

    private static final String PREVIOUS_STRING = "previous:";
    private static final String NEXT_STRING = "next:";
    private static ArrayList<String> tags = new ArrayList<>();
    private static HashMap<String, ArrayList<String>> tagKeys = new HashMap<>();
    private static HashMap<String, String[]> fileTags = new HashMap<>();

    private static void addTags(File file) {
        String fileName = file.getName();
        if (!isCryptic(file.getName())) {
            return;
        }
        try {
            Scanner reader = new Scanner(file);
            String line = reader.nextLine();


            if (line.contains(NEXT_STRING)) {
                //

            }
            if (line.contains(PREVIOUS_STRING)) {
                //
            }

            //remove the next pages stuff from the end:
            if (line.contains(":")) {
                line = line.split(":")[0].replace("previous", "");

            }
            String[] info = line.split(" ");


            for (int i = 0; i < info.length; i++) {
                info[i] = info[i].toLowerCase(Locale.ROOT);
            }


            if (!fileTags.containsKey(fileName)) {

                fileTags.put(fileName, info);


            }


            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void depopulateImageButtons(FlowPane imageButtons) {
        while (imageButtons.getChildren().size() > 0) {
            imageButtons.getChildren().remove(0);
        }


    }

    private static HashMap<String, Boolean> search = new HashMap<>();
    private static int currPage = 0;

    public static File[] images;


    private static long lastScrollTime = -1;

    private static int defaultPageSize = 25;


    private static FlowPane imageButtons;


    //fix: should also have an arraylist of thumbnail names, and clear out the list up to the last 100


    private static ArrayList<String> acceptableFiles = new ArrayList<>();


    private static final int IMAGE_BUTTON_GAP = 1;


    private static ArrayList<Double[]> insetsStack2 = new ArrayList<Double[]>();

    private static void updateViewCount() {

        updateTagCount(input.getText(), "view", 1);
        setMetaData("last_visit_date", Calendar.getInstance().getTime().toString(), input.getText());


    }


    private static boolean isCryptic(String input) {
        return input.contains("png.");
    }

    private static ArrayList<File> listFolders () {

        File directory2 = new File(getAbsolouteFolder());
        collage = new ArrayList<>();
        File[] images = directory2.listFiles();
        ArrayList<File> files = new ArrayList<>();

        for (File f : images) {
            if (!f.getName().contains(".")) {
                System.out.println(f.getName());
                files.add(f);
            }
        }

        return files;
    }



    public static int tagSelection1 = -1;//or sequenced
    public static int tagSelection2 = -1;//or sequenced
    ///this code needs major refactoring. duplicated code and functions!
    private static void refreshImageButtons(TextField input, Canvas canvas, FlowPane imageButtons, TextField length, TextField height) {

        int includedFiles = 0;
        int upcounting = 0;
        int pageSize = (pageSizeField == null) ? defaultPageSize : Integer.parseInt(pageSizeField.getText());
        ArrayList<VBox> imageCards = new ArrayList<>();
        for (String s : acceptableFiles) {
            //   System.out.println("what are you doing");
            upcounting++;

            if (getTagCount(s,"filenameDate") > 0) {
                try {
                    applynotesDateTemp(s);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }


            if (upcounting > currPage * pageSize && includedFiles < pageSize) {
                includedFiles++;

                VBox imageCard = new VBox();
                Button imageButton = new Button();

                ImageView imageView = new ImageView();

                ArrayList<String> tags = getPureTagList(s);


                String time = "";
                String date = "";
                try {
                    FileTime creationTime = (FileTime) Files.getAttribute(Path.of(getFolder() +s), "creationTime"); //possible bug introduced from directory
                    date = creationTime.toString().split("T")[0];
                    time = creationTime.toString().split("T")[1].split("\\.")[0];
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Image thumbnail;

                if (isCryptic(s)) {
                    if (new File(getFolder()+CONNECTOR + getThumbnailName(s)).exists()) {
                        thumbnail =getImageFromConverted(getThumbnailName(s));
                    } else {
                        thumbnail = loadObjective(STYLE_FOLDER+CONNECTOR+"error");
                    }
                }else {




                    thumbnail = loadImageThumb(s);

                }

                imageView = new ImageView(thumbnail);


                imageView.setPreserveRatio(true);
                imageView.setFitWidth(234);
                imageView.setSmooth(false);

                imageButton.setGraphic(imageView);
                imageButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                if (s.equalsIgnoreCase(input.getText())) {

                    imageButton.setStyle("-fx-background-color: black;");
                }

                imageButton.setStyle("-fx-background-color: transparent;");
                if (tags.contains("sequel")) {
                    imageButton.setStyle("-fx-background-color: red;");
                }
                if (tagSelection1==upcounting-1 || tagSelection2==upcounting-1) {
                    imageButton.setStyle("-fx-background-color: purple;");

                } else if (tagSelection1 < upcounting-1&& tagSelection2>upcounting-1) {
                    imageButton.setStyle("-fx-background-color: blue;");

                }

                Tooltip tooltip = new Tooltip(s + "\n" + date + " " + time + "\n" + tags);
                // tooltip.setHideDelay(Duration.INDEFINITE);
                tooltip.setShowDelay(Duration.ZERO);
                imageButton.setTooltip(tooltip);


                int finalUpcounting1 = upcounting;
                imageButton.setOnMouseClicked(mouseE -> {
                    if (mouseE.getButton() == MouseButton.MIDDLE) {
                        if (tagSelection1 == -1) {
                            tagSelection1= finalUpcounting1-1;
                            updatePage();
                        } else if (acceptableFiles.get(tagSelection1).equals(s)) {

                            tagSelection1 = -1;
                            tagSelection2 = -1;
                            updatePage();
                        }else {

                            tagSelection2 = finalUpcounting1-1;
                            updatePage();
                        }
                    }
                    else if (mouseE.getButton() == MouseButton.SECONDARY) {
                        updateTagCount(s, "view", 1);


                        //copies the name to clipboard
                        Clipboard clipboard = Clipboard.getSystemClipboard();
                        ClipboardContent content = new ClipboardContent();
                        content.putString(s);
                        clipboard.setContent(content);
                        canvas.requestFocus();
                        writeTagCount();

                        //opens dialogue
                        final Stage dialog = new Stage();


                        VBox dialogVbox = new VBox(IMAGE_BUTTON_GAP);
                        Image image = getImageFromConverted(s);
                        ImageView view = new ImageView(image);
                        view.setPreserveRatio(true);
                        stages.add(view);


                        dialog.setOnCloseRequest(event -> {
                            stages.remove(view);

                        });
                        dialogVbox.getChildren().add(view);
                        Scene dialogScene = new Scene(dialogVbox, Math.min(image.getWidth(), 1920), 1080);

                        dialogScene.setOnKeyReleased(keyEvent -> {
                            if (keyEvent.getCode() == KeyCode.F11 || keyEvent.getCode() == KeyCode.ENTER) {
                                dialog.setFullScreen(!dialog.isFullScreen());


                            } else if (keyEvent.getCode() == KeyCode.F1) {
                                boolean isToggled = false;
                                System.out.println("f1");
                                if (!stages.isEmpty()) {
                                    isToggled = stages.get(0).getImage() != null;
                                }
                                if (isToggled) {
                                    for (ImageView curView : stages) {

                                        imageStack.add(curView.getImage());
                                        curView.setImage(null);

                                    }
                                } else {

                                    for (ImageView curView : stages) {
                                        curView.setImage(imageStack.get(0));
                                        imageStack.remove(0);

                                    }

                                }


                            } else if (keyEvent.getCode() == KeyCode.F2) {
                                boolean isToggled = false;
                                System.out.println("f2");
                                if (!stages.isEmpty()) {
                                    isToggled = stages.get(0).getImage() != null;
                                }
                                if (isToggled) {
                                    for (ImageView curView : stages) {

                                        imageStack.add(curView.getImage());
                                        insetsStack2.add(new Double[]{((VBox) curView.getParent()).getPadding().getLeft(), ((VBox) curView.getParent()).getPadding().getTop(), curView.getFitHeight()});
                                        curView.setImage(null);
                                    }
                                    for (ImageView curView : stages) {
                                        int secondIndex = Math.min(1, imageStack.size() - 1);
                                        curView.setImage(imageStack.get(secondIndex));
                                        curView.setFitHeight(insetsStack2.get(secondIndex)[2]);
                                        imageStack.remove(secondIndex);
                                        ((VBox) curView.getParent()).setPadding(new Insets(insetsStack2.get(secondIndex)[1], 0, 0, insetsStack2.get(secondIndex)[0]));
                                        insetsStack2.remove(secondIndex);

                                    }
                                } else {
                                    for (ImageView curView : stages) {
                                        curView.setImage(imageStack.get(0));
                                        imageStack.remove(0);

                                    }
                                }


                            }
                        });


                        dialogScene.setOnMouseDragged(mouseEvent -> {


                            if (!isDragging) {
                                startx = dialogVbox.getInsets().getLeft();
                                starty = dialogVbox.getInsets().getTop();
                                startMousex = mouseEvent.getX();
                                startMousey = mouseEvent.getY();
                                System.out.println("test");
                            }
                            isDragging = true;


                            dialogVbox.setPadding(new Insets((mouseEvent.getY() + starty - startMousey), 0, 0, (mouseEvent.getX() + startx - startMousex)));


                        });


                        dialogVbox.setOnDragOver(dragEvent -> {
                            if (dragEvent.getDragboard().hasImage()) {
                                dragEvent.acceptTransferModes(TransferMode.ANY);
                            }
                        });


                        dialogVbox.setOnDragDropped(dragEvent -> {
                            view.setImage(dragEvent.getDragboard().getImage());


                        });

                        dialogScene.setOnMouseReleased(mouseEvent -> {
                            isDragging = false;
                        });

                        dialogScene.setOnMouseDragReleased(mouseEvent -> {
                            isDragging = false;
                        });

                        dialogScene.setOnScroll(scrollEvent -> {

                            double factor;
                            if (scrollEvent.getDeltaY() > 0) {
                                factor = 1.1;
                            } else {
                                factor = 0.9;
                            }

                            scroll = scroll * factor;


                            view.setPreserveRatio(true);


                            double cursoryRatio = (scrollEvent.getY()-dialogVbox.getInsets().getTop())/(Math.max(2160 * scroll,1));
                            double cursorxRatio = (scrollEvent.getX()-dialogVbox.getInsets().getLeft())/((view.getImage().getWidth()/view.getImage().getHeight())*(Math.max(2160 * scroll,1)));
                            dialogVbox.setPadding(new Insets(dialogVbox.getInsets().getTop()+((2160 * scroll)/factor-(2160 * scroll))*cursoryRatio, 0, 0,
                                    dialogVbox.getInsets().getLeft()+((2160 * scroll)/factor-(2160 * scroll))*cursorxRatio));
                            view.setFitHeight(2160 * scroll);

                        });


                        //    dialog.setFullScreen(true);
                        dialog.setTitle(s);
                        dialog.setScene(dialogScene);
                        dialog.getIcons().add(loadObjective(STYLE_FOLDER+CONNECTOR+"Image.png"));
                        dialog.show();
                        //  Toolkit.getDefaultToolkit().getSystemClipboard().setContents(,null);

                    }
                });

                imageButton.setOnDragDetected(mouseEvent -> {
                    Dragboard db = imageButton.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();

                    content.putImage(getImageFromConverted(imageButton.getTooltip().getText().split("\n")[0]));
                    db.setContent(content);
                });
                imageButton.setOnMouseDragged(mouseEvent -> {
                    mouseEvent.setDragDetect(true);
                });

                int finalUpcounting = upcounting;
                imageButton.setOnAction(event -> {



                    imageButton.setStyle("-fx-background-color: black;");
                    currentIndex = finalUpcounting-1;

                    updateImageView(s);
                    if (!thumbnailMode) {

                        updateCounters();





                        if (isCryptic(s)) {
                            lastSaved = getImageFromConverted(s);
                        } else {
                            lastSaved = loadImage(s);
                        }

                        length.setText((int) lastSaved.getWidth() + "");
                        height.setText((int) lastSaved.getHeight() + "");

                        resetPos();
                        update(canvas);
                    //    draw(canvas, s);// has error
                        canvas.requestFocus();
                    }


                });
                imageButton.setFocusTraversable(false);
                imageCard.getChildren().add(imageView);
                imageCard.getChildren().add(imageButton);
                imageButtons.getChildren().add(imageCard);
             //   applyInsertSort(imageCards,imageCard,123);



            }



        }


        ArrayList<File> files = listFolders();

        for (File f : files) {
            Button folderpath = new Button(f.getName()+CONNECTOR);
            folderpath.setOnAction(actionEvent -> {
                directory = directory + f.getName()+CONNECTOR;
                readHeirarchy();
                readTagCount();

                refreshTagCounts();
                depopulateImageButtons(imageButtons);
                populateImageButtons(input,canvas,imageButtons,length,height);
            });
            imageButtons.getChildren().add(folderpath);
        }
        Button folderpath = new Button("<- parent");
        String[] sections = directory.split(CONNECTOR);
        if (sections.length > 0) {
            folderpath.setOnAction(actionEvent -> {

                String allbutlast = "";
                for (int x = 0; x < sections.length - 1; x++) {
                    allbutlast = allbutlast + sections[x] + CONNECTOR;
                }


                directory = allbutlast;

                readHeirarchy();
                readTagCount();
                refreshTagCounts();
                depopulateImageButtons(imageButtons);
                populateImageButtons(input, canvas, imageButtons, length, height);

            });
            imageButtons.getChildren().add(folderpath);
        }


        HBox pageNum = new HBox();
        Button prevPage = new Button("<");
        Button nextPage = new Button(">");
        Button firstPage = new Button("0");
        pageSizeField = new TextField(defaultPageSize + "");

        Button lastPage = new Button(fileCount / Integer.parseInt(pageSizeField.getText()) + "");
        TextField pageAdd = new TextField("1");

        cuPage = new Text(currPage + "");
        cuPage.setFill(Color.WHITE);


        prevPage.setOnAction(actionEvent ->

        {

            currPage = Math.max(currPage - Integer.parseInt(pageAdd.getText()), 0);
            updatePage();
            //  imageButtons.set

        });

        pageSizeField.setOnKeyTyped(keyEvent ->

        {
            defaultPageSize = Integer.parseInt(pageSizeField.getText());
        });
        nextPage.setOnAction(actionEvent ->

        {


            currPage = Math.min(currPage + Integer.parseInt(pageAdd.getText()), fileCount / Integer.parseInt(pageSizeField.getText()));
            updatePage();

            //  imageButtons.set
        });
        firstPage.setOnAction(actionEvent ->

        {

            currPage = 0;
            updatePage();
            //  imageButtons.set
        });
        lastPage.setOnAction(actionEvent ->

        {

            currPage = fileCount / Integer.parseInt(pageSizeField.getText());
            updatePage();
            //  imageButtons.set
        });
        pageNum.getChildren().

                addAll(pageSizeField, firstPage, prevPage, cuPage, nextPage, lastPage, pageAdd);


        HBox padding = new HBox();


        for (int j = 0; j < dialog.getWidth() / 16; j++) {
            padding.getChildren().add(new Button());
        }
        imageButtons.getChildren().add(pageNum);
        imageButtons.getChildren().add(padding);


        // writeHeirarchy();
        //migrateTags();
    }


    private static ArrayList<String> searchQuery(String query) {

        String[] searchTags = query.split(" ");


        int includedFiles = 0;

        int fileCount = 0;
        ArrayList<String> acceptableFiles = new ArrayList<>();

        for (int i = 0; i < images.length; i++) {



            if (isFormatSupported(images[i].getName())) {
                String name = images[i].getName();
                boolean shouldAdd = false;

                boolean forceStop = false;
                for (String tag : searchTags) {


                    boolean forceContainTag = tag.contains("&");
                    boolean dontContainTag = tag.contains("!");
                    tag = tag.replace("&", "");
                    tag = tag.replace("!", "");


                    if (!forceStop) {


                        String seperator = "";
                        if (tag.contains(">")) {
                            seperator = ">";
                        } else if (tag.contains("<")) {
                            seperator = "<";
                        } else if (tag.contains("=")) {
                            seperator = "=";
                        }

                        int num = 1;

                        if (!seperator.equals("")) {
                            num = Integer.parseInt(tag.split(seperator)[1]);

                        }

                        String type = tag;

                        if (!seperator.equals("")) {
                            type = tag.split(seperator)[0];


                        }

                        int typecount = 0;

                        HashMap<String, Integer> counts = getTagMapCount(name);
                        typecount = counts.getOrDefault(type, 0);
                        if (type.equals("*")) {
                            typecount = 1;
                        }


                        boolean caseMet = false;

                        switch (seperator) {
                            case "" -> {
                                if (typecount > 0) {
                                    shouldAdd = true;
                                    caseMet = true;
                                }
                            }
                            case ">" -> {
                                if (typecount > num) {
                                    shouldAdd = true;
                                    caseMet = true;
                                }
                            }
                            case "<" -> {
                                if (typecount < num) {
                                    shouldAdd = true;
                                    caseMet = true;
                                }
                            }
                            case "=" -> {
                                if (typecount == num) {
                                    shouldAdd = true;
                                    caseMet = true;
                                }
                            }
                        }
                        if (!caseMet && forceContainTag) {
                            shouldAdd = false;
                            forceStop = true;
                        } else if (caseMet && dontContainTag) {
                            shouldAdd = false;
                            forceStop = true;
                        }


                    }
                }

                if (shouldAdd) {
                    switch (SECURITY_LEVEL) {
                        case 0 -> {
                            //nothing
                        }
                        case 1 -> {
                            shouldAdd = (getTagCount(images[i].getName(), "safe") > 0);
                        }
                        case 2 -> {
                            shouldAdd = (getTagCount(images[i].getName(), "safe") > 1);
                        }
                    }
                }



                if (shouldAdd) {
                    fileCount++;
                    applyInsertSortFiles(images[i].getName(), acceptableFiles);

                    int pageSize = (pageSizeField == null) ? defaultPageSize : Integer.parseInt(pageSizeField.getText());
                    if (fileCount > currPage * pageSize && includedFiles < pageSize) {
                        includedFiles++;

                    }
                }
            }
        }
        return acceptableFiles;
    }


    private static void massStoreTag(String tag, String moveFolder) {
        System.out.println("checking all tags in list: " + tag);
        ArrayList<String> tagged = searchQuery(tag);

        String savedTags = "";
        File folder = new File(getAbsolouteFolder()+moveFolder);
        if (!folder.exists()) {
            folder.mkdir();
        }
        writeTagCount(tagged,moveFolder+CONNECTOR);
        for (String s : tagged) {

            if (isCryptic(s)) {
                String thumbName = s.replace("png.txt","Thumbnailpng.txt");
                File moveFileThumb = new File(getAbsolouteFolder()+thumbName);
                if (moveFileThumb.exists()) {
                    if (moveFileThumb.renameTo(new File(getAbsolouteFolder()+moveFolder+CONNECTOR+thumbName))) {
                        System.out.println(moveFileThumb.getAbsoluteFile().getName() + " moved!");

                    }
                }

            }



            File moveFile = new File(getAbsolouteFolder()+s);



            if (moveFile.renameTo(new File(getAbsolouteFolder()+moveFolder+CONNECTOR+s))) {
                System.out.println(moveFile.getAbsoluteFile().getName() + " moved!");

            }

       //     moveFile.renameTo(new File(getAbsolouteFolder()+"moveFolder"+CONNECTOR+s));

        }

    }

    private static void applynotesDateTemp(String inputS) throws ParseException {
        //20052023.png
        String name = inputS;
        inputS = inputS.replace(".png","");
        inputS= inputS.substring(36);
        SimpleDateFormat inputformat = new SimpleDateFormat("ddMMyyyy", Locale.ENGLISH);
        Date date = inputformat.parse(inputS);

        SimpleDateFormat outputformat = new SimpleDateFormat("yyyy MM dd HH_mm_ss", Locale.ENGLISH);
        setMetaData("notes",outputformat.format(date),name);

    }

    private static long getMillisFromStringDate(String date) {

        long output = 0l;
        if (date == null) {
            output = 1l;
        } else {
            String annoyingFormat = "EEE MMM dd HH_mm_ss z yyyy";
            if (date.length() == 19) {
                annoyingFormat = "yyyy MM dd HH_mm_ss";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(annoyingFormat, Locale.ENGLISH);
            try {
                output= sdf.parse(date).getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }


        return output;

    }


    private static boolean isFormatSupported(String input) {
        return(input.contains("png.") && !input.contains("Thumbnail")|| input.contains(".png") || input.contains(".jpg") || input.contains(".jpeg"));

    }
    private static void populateImageButtons(TextField input, Canvas canvas, FlowPane imageButtons, TextField length, TextField height) {
        fileTags = new HashMap<>();


        if (include.getText().equals("")) {
            populateTagButtons(input, canvas, imageButtons, length, height, include);
            return;
        }
        File directory2 = new File(getAbsolouteFolder());
        search = new HashMap<>();
        String[] searchArray = include.getText().split(" ");

        for (int i = 0; i < searchArray.length; i++) {
            search.put(searchArray[i], true);
        }
        Set<String> searchTags = search.keySet();

        images = directory2.listFiles();
        int imageLength = 0;
        if (images != null) {
            Arrays.sort(images, LASTMODIFIED_REVERSE);
            imageLength = images.length;
        }



        int includedFiles = 0;

        fileCount = 0;
        acceptableFiles = new ArrayList<>();

        String previousImage = "";
        for (int i = 0; i < imageLength; i++) {



            if (isFormatSupported(images[i].getName())) {

                addTags(images[i]); //check


                String name = images[i].getName();


                boolean shouldAdd = false;

                if (!heirarchy.containsKey(name)) {
                    heirarchy.put(name, previousImage);
                }

                previousImage = name;


                boolean forceStop = false;
                for (String tag : searchTags) {


                    boolean forceContainTag = tag.contains("&");
                    boolean dontContainTag = tag.contains("!");
                    tag = tag.replace("&", "");
                    tag = tag.replace("!", "");


                    if (!forceStop) {


                        String seperator = "";
                        if (tag.contains(">")) {
                            seperator = ">";
                        } else if (tag.contains("<")) {
                            seperator = "<";
                        } else if (tag.contains("=")) {
                            seperator = "=";
                        }

                        int num = 1;

                        if (!seperator.equals("")) {
                            num = Integer.parseInt(tag.split(seperator)[1]);

                        }

                        String type = tag;

                        if (!seperator.equals("")) {
                            type = tag.split(seperator)[0];


                        }

                        int typecount = 0;

                        HashMap<String, Integer> counts = getTagMapCount(name);
                        typecount = counts.getOrDefault(type, 0);
                        if (type.equals("*")) {
                            typecount = 1;
                        }


                        boolean caseMet = false;

                        switch (seperator) {
                            case "" -> {
                                if (typecount > 0) {
                                    shouldAdd = true;
                                    caseMet = true;
                                }
                            }
                            case ">" -> {
                                if (typecount > num) {
                                    shouldAdd = true;
                                    caseMet = true;
                                }
                            }
                            case "<" -> {
                                if (typecount < num) {
                                    shouldAdd = true;
                                    caseMet = true;
                                }
                            }
                            case "=" -> {
                                if (typecount == num) {
                                    shouldAdd = true;
                                    caseMet = true;
                                }
                            }
                        }
                        if (!caseMet && forceContainTag) {
                            shouldAdd = false;
                            forceStop = true;
                        } else if (caseMet && dontContainTag) {
                            shouldAdd = false;
                            forceStop = true;
                        }


                    }
                }

                if (shouldAdd) {
                    switch (SECURITY_LEVEL) {
                        case 0 -> {
                            //nothing
                        }
                        case 1 -> {
                            shouldAdd = (getTagCount(images[i].getName(), "safe") > 0);
                        }
                        case 2 -> {
                            shouldAdd = (getTagCount(images[i].getName(), "safe") > 1);
                        }
                    }
                }



                if (shouldAdd) {
                    fileCount++;
                    applyInsertSortFiles(images[i].getName(), acceptableFiles);

                    int pageSize = (pageSizeField == null) ? defaultPageSize : Integer.parseInt(pageSizeField.getText());
                    if (fileCount > currPage * pageSize && includedFiles < pageSize) {
                        includedFiles++;

                    }
                }
            }
        }

        HBox pageNum = new HBox();
        Button prevPage = new Button("<");
        Button nextPage = new Button(">");
        Button firstPage = new Button("0");
        pageSizeField = new TextField(defaultPageSize + "");

        Button lastPage = new Button(fileCount / Integer.parseInt(pageSizeField.getText()) + "");
        TextField pageAdd = new TextField("1");

        cuPage = new Text(currPage + "");
        cuPage.setFill(Color.WHITE);


        prevPage.setOnAction(actionEvent -> {

            currPage = Math.max(currPage - Integer.parseInt(pageAdd.getText()), 0);
            updatePage();
            //  imageButtons.set

        });

        pageSizeField.setOnKeyTyped(keyEvent -> {
            defaultPageSize = Integer.parseInt(pageSizeField.getText());
        });
        nextPage.setOnAction(actionEvent -> {


            currPage = Math.min(currPage + Integer.parseInt(pageAdd.getText()), fileCount / Integer.parseInt(pageSizeField.getText()));
            updatePage();

            //  imageButtons.set
        });
        firstPage.setOnAction(actionEvent -> {

            currPage = 0;
            updatePage();
            //  imageButtons.set
        });
        lastPage.setOnAction(actionEvent -> {

            currPage = fileCount / Integer.parseInt(pageSizeField.getText());
            updatePage();
            //  imageButtons.set
        });
        pageNum.getChildren().addAll(pageSizeField, firstPage, prevPage, cuPage, nextPage, lastPage, pageAdd);


        HBox padding = new HBox();


        for (int i = 0; i < dialog.getWidth() / 16; i++) {
            padding.getChildren().add(new Button());
        }
        imageButtons.getChildren().add(pageNum);
        imageButtons.getChildren().add(padding);

        refreshImageButtons(input, canvas, imageButtons, length, height);


     //   toJSon("megafly");
        // writeHeirarchy();
        //migrateTags();


    }




    private static void updatePage() {

        // defaultPageSize = Integer.parseInt(pageSizeField.getText());

        depopulateImageButtons(imageButtons);
        // populateImageButtons(input, canvas, imageButtons, length, height);
        refreshImageButtons(input, canvas, imageButtons, length, height);

        cuPage.setText(currPage + "");
    }

    private static void updateCounters() {


        for (String s : toggledTags) {
            updateTagCountSafe(input.getText(),s,1);
        }



        HashMap<String, Integer> counts = getTagMapCount(input.getText());

        views.setText("views " + counts.getOrDefault("view", 0));
        bookmark.setText("bookmark " + counts.getOrDefault("bookmark", 0));
        if (getMetaData("notes",input.getText()) != null) {
            notesField.setText(getMetaData("notes",input.getText()));
        } else {
            notesField.setText("");
        }
        if (getMetaData("last_visit_date",input.getText()) != null) {
            lastVisitText.setText(getMetaData("last_visit_date",input.getText()));
        } else {
            lastVisitText.setText("");
        }
        updateViewCount();
    }

    private static void updateImageView(String name) {
        input.setText(name);


        populateTagFieldButtons();
        refreshTagCounts();
    }

    private static void populateTagButtons(TextField input, Canvas canvas, FlowPane imageButtons, TextField length, TextField height, TextField include) {
        File directory2 = new File(getAbsolouteFolder());
        System.out.println(getAbsolouteFolder());
        search = new HashMap<>();


        String[] searchArray = include.getText().split(" ");

        for (int i = 0; i < searchArray.length; i++) {
            search.put(searchArray[i], true);
        }


        collage = new ArrayList<>();
        File[] images = directory2.listFiles();
        System.out.println(images.length);
        Arrays.sort(images, LASTMODIFIED_COMPARATOR);
        fileCount = 0;

        for (int i = 0; i < images.length; i++) {

            if (isFormatSupported(images[i].getName())) {
                switch (SECURITY_LEVEL) {
                    case 0 -> {
                        fileCount++;
                        addTags(images[i]);
                    }
                    case 1 -> {
                        if (getTagCount(images[i].getName(), "safe") > 0) {
                            fileCount++;
                            addTags(images[i]);
                        }
                    }
                    case 2 -> {
                        if (getTagCount(images[i].getName(), "safe") > 1) {
                            fileCount++;
                            addTags(images[i]);
                        }
                    }
                }


            }
        }
        Button starButton = new Button("*");
        applyTagButtonAction(starButton, input, canvas, imageButtons, length, height, include);
        VBox starBox = new VBox();
        starBox.getChildren().add(starButton);
        Text filecountText = new Text(fileCount + " items");
        filecountText.setFill(Color.WHITE);
        starBox.getChildren().add(filecountText);

        LinkedList<VBox> buttons = new LinkedList<>();
        buttons.add(starBox);
        for (String tag : tags) {
            if ( !(tag.equals("safe") && SECURITY_LEVEL > 0)) {


                Button button = new Button(tag);
                Text text = new Text(tagKeys.get(tag).size() + "");//later find out why its x2 in the first place
                Text text2 = new Text("");
                text.setFill(Color.LIGHTCYAN);

                applyTagButtonAction(button, input, canvas, imageButtons, length, height, include);

                VBox vBox = new VBox();
                vBox.setAlignment(Pos.TOP_CENTER);


                vBox.getChildren().addAll(button, text, text2);
                applyInsertSort(buttons, vBox, tagKeys.get(tag).size());
            }

        }

        for (VBox vBox : buttons) {
            imageButtons.getChildren().add(vBox);
        }

    }

    private static long getFileDateSort(String name) {
        if (sortByOptions.getSelectionModel().getSelectedItem().equals("visited")) {
            if (getTagCount(name, "notesDate") > 0) {
                return getMillisFromStringDate(getMetaData("notes", name));
            } else {
                return getMillisFromStringDate(getMetaData("last_visit_date",name));
            }

        } else {
            return 0;
        }
    }
    private static void applyInsertSortFiles(String newImage, ArrayList<String> acceptableFiles) {
        switch(sortByOptions.getSelectionModel().getSelectedItem()) {
            case("modified"):
                acceptableFiles.add(newImage);
                break;
            case("visited"):
                int lower = 0;
                int higher = Math.max(acceptableFiles.size(),0);
                int pointer = higher/2;
                int index = 0;
                //(new File(acceptableFiles.get(pointer))).lastModified() > (new File(newImage)).lastModified()
                while (lower !=higher && lower+1 !=higher) {
                    if (getFileDateSort(acceptableFiles.get(pointer))
                            < getFileDateSort(newImage)) {
                        higher = pointer;
                        pointer = (lower+higher)/2;
                    } else {
                        lower = pointer;
                        pointer = (lower+higher)/2;
                    }

                    //  index++;
                }
                if (lower+1 ==higher) {
                    if (getFileDateSort(acceptableFiles.get(lower))
                            < getFileDateSort(acceptableFiles.get(pointer))) {
                        pointer = lower;
                    } else {
                        pointer = higher;
                    }
                }
                if (pointer >= acceptableFiles.size()) {
                    acceptableFiles.add(newImage);
                } else {
                    acceptableFiles.add(pointer, newImage);

                }
            break;

        }



    }
    private static void applyInsertSort(LinkedList<VBox> buttons, VBox toInsert, int value) {
        int index = 0;

        while (index < buttons.size() && Integer.parseInt(((Text) buttons.get(index).getChildren().get(1)).getText().split(" ")[0]) > value) {
            index++;
        }
        if (index >= buttons.size()) {
            buttons.add(toInsert);
        } else {
            buttons.add(index, toInsert);

        }
    }

    private static void applyTagButtonAction(Button button, TextField input, Canvas canvas, FlowPane imageButtons, TextField length, TextField height, TextField include) {
        button.setOnAction(actionEvent -> {
            depopulateImageButtons(imageButtons);

            include.setText(button.getText());
            populateImageButtons(input, canvas, imageButtons, length, height);
        });
    }


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
    private static Image loadImageThumb(String fileName) {


        Image image;
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(getFolder() +fileName);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(inputstream, 234, 234, true, true);

        return image;
    }
    private static Image loadObjective(String fileName) {

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

    private static Image loadImage(String fileName) {


        Image image;
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(getFolder() +fileName);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(inputstream);

        return image;
    }

    private static Image loadImage(String fileName, int length, int width) {


        Image image;
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(getFolder() +fileName);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = new Image(inputstream, length, width, true, false);

        return image;
    }

    private static int addExtra(String current, int index) {

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


    private static Image getImageFromConverted(String name) {

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


    private static Image writeMonochromeImage(WritableImage image, ArrayList<String> lines, int length, int height) {

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


    private static Image writeToNormalImage(WritableImage image, ArrayList<String> lines, int length, int height) {
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

    private static Image writeToHalfColorImage(WritableImage image, ArrayList<String> lines, int length, int height) {
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

    private static WritableImage applyColorOverlay(WritableImage image1, ArrayList<String> lines, int[][] brightness) {

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

    private static ArrayList<String> loadConverted(String fileName) {


        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(getFolder() +fileName);


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
        File imageWidth = new File(getFolder() +name.replace(".", "").replace("jpg", "png") + ".txt");
        FileWriter writer = new FileWriter(imageWidth);
        writer.write("monochrome " + (int) image.getWidth() + " " + (int) image.getHeight() + " " + tags + " " + PREVIOUS_STRING + previous + " " + NEXT_STRING + next + "\n");

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
    //System.getProperty("user.dir")+CONNECTOR+directory;
    public static String getAbsolouteFolder() {
        return filepath+CONNECTOR+directory;
    }

    private static String filepath = "";
//selctedDir+CONNECTOR+directory;
    public static String getFolder() {
        return filepath+CONNECTOR+directory;
    }

    public static void convert(Image image, String name) throws IOException {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();


        File imageWidth = new File(getFolder()  +name.replace(".", "").replace("jpg", "png") + ".txt");
        FileWriter writer = new FileWriter(imageWidth);
        writer.write("normal " + (int) image.getWidth() + " " + (int) image.getHeight() + " " + tags + " " + PREVIOUS_STRING + previous + " " + NEXT_STRING + next + "\n");

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

    public static void writeFileText(String name, ArrayList<String> text) throws IOException {

        File imageWidth = new File(getFolder() +name);
        FileWriter writer = new FileWriter(imageWidth);
        for (String s : text) {
            writer.write(s + "\n");
        }

        writer.close();
    }


    public static void convertHalfColor(Image image, String name, String tags) throws IOException {
        int factor = 2;

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        File dir = new File(name);

        File imageWidth = new File(name.replace(".", "").replace("jpg", "png") + ".txt");
        FileWriter writer = new FileWriter(imageWidth);
        writer.write("half_color " + (int) image.getWidth() + " " + (int) image.getHeight() + " " + tags + "\n");

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
        System.out.println(text + " " + value);

    }

    private static void assignHashes() {
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



    //0=only finished features
    //1=include functional but unintuitive/ugly features
    //2=untested or broken features
    private static int useExperimentalFeatures = 1;

    public static void actionArgs(String arg) {
        String type = arg.split("-")[0];
        String input = arg.split("-")[1];

        switch(type) {
            case "security" -> SECURITY_LEVEL = Integer.parseInt(input);
            case "experimental" -> useExperimentalFeatures = Integer.parseInt(input);
        }
    }

    public static void main(String[] args) {
        for (String s : args) {
            actionArgs(s);
        }



        launch();
    }


}

