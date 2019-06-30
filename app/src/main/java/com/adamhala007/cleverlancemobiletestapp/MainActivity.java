package com.adamhala007.cleverlancemobiletestapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    ImageView ivImage;
    Button bDownload;

    private RequestAsync requestAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();

        setListeners();




        //https://prodevsblog.com/view/android-httpurlconnection-post-and-get-request-tutorial/
        //https://stackoverflow.com/questions/12732422/adding-header-for-httpurlconnection
        //https://stackoverflow.com/questions/5980658/how-to-sha1-hash-a-string-in-android
    }

    private void initComponents(){
        bDownload = findViewById(R.id.bDownload);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        ivImage = findViewById(R.id.ivImage);
    }

    private void setListeners(){
        bDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAndDownload(new User(etUsername.getText().toString(),  etPassword.getText().toString()));
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

            }
        });
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler  = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String message = getStringFromMessage(msg);
            try {
                processMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private String getStringFromMessage(Message message){
        return getBundleFromMessage(message).getString(Constants.MSG_KEY);
    }

    private Bundle getBundleFromMessage(Message message){
        return message.getData();
    }

    private void processMessage(String message) throws JSONException {
        Log.d("IMAGE", message);
        if (isRequestErrorMessage(message)){
            Toast.makeText(this, Constants.ERROR_REQUEST, Toast.LENGTH_SHORT).show();
            return;
        }
        String base64ImageString = JSONtoBase64ImageString(message);
        Bitmap bitmap = convertToBitmap(base64ImageString);
        showImage(bitmap);
    }

    private boolean isRequestErrorMessage(String message){
        return message.equals(Constants.EXCEPTION + Constants.ERROR_REQUEST);
    }

    private String JSONtoBase64ImageString(String message) throws JSONException {
        JSONObject mainObject = new JSONObject(message);
        return mainObject.getString("image");
    }

    private Bitmap convertToBitmap(String message){
        byte[] decodedString = Base64.decode(message.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void showImage(Bitmap bitmap){
        ivImage.setImageBitmap(bitmap);
    }

    private void loginAndDownload(User user){
        requestAsync = new RequestAsync(handler, user);
        requestAsync.execute();
    }



}


