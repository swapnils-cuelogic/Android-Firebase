package com.cuelogic.firebase.chat.fcm;

import com.cuelogic.firebase.chat.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FcmTopicBuilder {

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "FcmTopicBuilder";
    private static final String SERVER_API_KEY = "AIzaSyBTLdwMLey8d1YkEd3rSLEWsYxAwEYuBTM";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_KEY = "key=" + SERVER_API_KEY;
    private static final String TOPIC_BATCH_ADD_URL = "https://iid.googleapis.com/iid/v1:batchAdd";

    // json related keys
    private static final String KEY_TO = "to";
    private static final String KEY_REGISTRATION_TOKENS = "registration_tokens";

    private String topicName;
    private List<String> tokens;

    private FcmTopicBuilder() {

    }

    public static FcmTopicBuilder initialize() {
        return new FcmTopicBuilder();
    }

    public FcmTopicBuilder topicName(String topicName) {
        this.topicName = topicName;
        return this;
    }

    public FcmTopicBuilder tokens(List<String> tokens) {
        this.tokens = tokens;
        return this;
    }

    public void send() {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .addHeader(AUTHORIZATION, AUTH_KEY)
                .url(TOPIC_BATCH_ADD_URL)
                .post(requestBody)
                .build();

        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.eLog(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Logger.eLog(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    private JSONObject getValidJsonBody() throws JSONException {
        JSONObject jsonObjectBody = new JSONObject();
        jsonObjectBody.put(KEY_TO, topicName);
        JSONArray jaTokens = new JSONArray(tokens);
        jsonObjectBody.put(KEY_REGISTRATION_TOKENS, jaTokens);
        return jsonObjectBody;
    }
}
