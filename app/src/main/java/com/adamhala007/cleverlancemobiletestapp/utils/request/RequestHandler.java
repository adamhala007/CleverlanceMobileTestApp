package com.adamhala007.cleverlancemobiletestapp.utils.request;

import android.util.Log;

import com.adamhala007.cleverlancemobiletestapp.models.User;
import com.adamhala007.cleverlancemobiletestapp.utils.Constants;
import com.adamhala007.cleverlancemobiletestapp.utils.SHA1;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class RequestHandler {

    private static User user;


    public static String sendPost(String r_url , User user) throws Exception {
        URL url = new URL(r_url);
        RequestHandler.user = user;

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        setupRequest(conn);
        createRequestBody(conn);

        if (requestSucceeded(conn)) {
           return createResponse(conn);

        }else{
            throw new Exception(Constants.ERROR_REQUEST);
        }
    }

    private static JSONObject createDataParamsJSON() throws JSONException {
        JSONObject postDataParams = new JSONObject();
        postDataParams.put(Constants.USERNAME, user.getUsername());
        return postDataParams;
    }

    private static void setupRequest(HttpURLConnection connection) throws ProtocolException, NoSuchAlgorithmException {
        connection.setReadTimeout(Constants.READ_TIMEOUT);
        connection.setConnectTimeout(Constants.CONNECT_TIMEOUT);
        connection.setRequestProperty (Constants.AUTHORIZATION, SHA1.encrypt(user.getPassword()));
        connection.setRequestMethod(Constants.METHOD_POST);
        connection.setRequestProperty(Constants.CONTENT_TYPE_KEY, Constants.CONTENT_TYPE_VALUE);
        connection.setDoInput(true);
        connection.setDoOutput(true);
    }

    private static void createRequestBody(HttpURLConnection connection) throws Exception {
        JSONObject postDataParams = createDataParamsJSON();

        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, StandardCharsets.UTF_8));
        writer.write(encodeParams(postDataParams));
        writer.flush();
        writer.close();
        os.close();
    }

    private static boolean requestSucceeded(HttpURLConnection connection) throws IOException {
        int responseCode=connection.getResponseCode();
        return responseCode == HttpsURLConnection.HTTP_OK;
    }

    private static String createResponse(HttpURLConnection connection) throws IOException {
        BufferedReader in=new BufferedReader( new InputStreamReader(connection.getInputStream()));
        StringBuffer sb = new StringBuffer("");
        String line="";
        while((line = in.readLine()) != null) {
            sb.append(line);
            break;
        }
        in.close();
        return sb.toString();
    }

    private static String encodeParams(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while(itr.hasNext()){
            String key= itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, Constants.ENCODING_UTF8));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), Constants.ENCODING_UTF8));
        }
        return result.toString();
    }
}
