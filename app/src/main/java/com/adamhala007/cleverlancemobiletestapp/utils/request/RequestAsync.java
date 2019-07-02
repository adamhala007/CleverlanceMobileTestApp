package com.adamhala007.cleverlancemobiletestapp.utils.request;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.adamhala007.cleverlancemobiletestapp.models.User;
import com.adamhala007.cleverlancemobiletestapp.utils.Constants;

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
            return Constants.EXCEPTION + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if(s!=null){
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