package com.example.imageconstructor;

import javafx.stage.PopupWindow;

import java.io.*;

public class Config {


    private static final String CONFIG_FILE = "config.txt";

    private static String configSettings = "";
    public static void setConfigValues() {

        try {

            BufferedWriter writeConfig = new BufferedWriter(new FileWriter(CONFIG_FILE));

            writeConfig.write(configSettings);
            writeConfig.flush();
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }

    public static String getConfigRaw() {
        return configSettings;
    }

    public static void setConfigValue(String value, String type) {
        configSettings = Util.appendMarkupSetting(configSettings,value,type);
    }
    public static void setConfig(String s) {
        configSettings = s;
    }


    public static String getConfigValue(String input) {
        if (configSettings.contains(input)) {
            return configSettings.split(input)[1];
        } else {
            return -1 + "";
        }


    }

    public static void loadConfigValues() {
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
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {

                }
            }
        }


    }
}
