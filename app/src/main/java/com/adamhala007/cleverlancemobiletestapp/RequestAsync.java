package com.adamhala007.cleverlancemobiletestapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestAsync extends AsyncTask<String,String,String> {

    private Handler handler;
    private User user;

    public RequestAsync(Handler handler, User user) {
        this.handler = handler;
        this.user = user;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            return RequestHandler.sendPost(Constants.SERVICE_URL, user);
        }
        catch(Exception e){
            return "Exception: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if(s!=null){
            Log.e("image", s);
            sendResultToUI(s);
        }
    }

    private void sendResultToUI(String result){
        handler.sendMessage(createMessage(result));
    }

    private Message createMessage(String result){
        Message msg = handler.obtainMessage();
        msg.setData(createBundle(result));
        return msg;
    }

    private Bundle createBundle(String messageString){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MSG_KEY, messageString);
        return bundle;
    }
}