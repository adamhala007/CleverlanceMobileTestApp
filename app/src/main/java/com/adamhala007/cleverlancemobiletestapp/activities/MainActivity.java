package com.adamhala007.cleverlancemobiletestapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adamhala007.cleverlancemobiletestapp.utils.Constants;
import com.adamhala007.cleverlancemobiletestapp.R;
import com.adamhala007.cleverlancemobiletestapp.utils.request.RequestAsync;
import com.adamhala007.cleverlancemobiletestapp.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    ImageView ivImage;
    Button bDownload;
    ProgressBar pbDownloading;

    private RequestAsync requestAsync;
    private String imageMessage;

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.USERNAME,etUsername.getText().toString());
        editor.putString(Constants.PASSWORD,etPassword.getText().toString());
        editor.putString(Constants.IMAGE_SOURCE, imageMessage);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        etUsername.setText(sharedPref.getString(Constants.USERNAME,""));
        etPassword.setText(sharedPref.getString(Constants.PASSWORD,""));
        imageMessage = sharedPref.getString(Constants.IMAGE_SOURCE,"");
        try {
            processMessage(imageMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;
        setContentView(R.layout.activity_main);

        initComponents();
        loadSavedContent();
        setListeners();
    }

    private void initComponents(){
        bDownload = findViewById(R.id.bDownload);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        ivImage = findViewById(R.id.ivImage);
        pbDownloading = findViewById(R.id.horizontal_progress_bar);
    }

    private void loadSavedContent() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        etUsername.setText(sharedPref.getString(Constants.USERNAME,""));
        etPassword.setText(sharedPref.getString(Constants.PASSWORD,""));
        imageMessage = sharedPref.getString(Constants.IMAGE_SOURCE,"");
    }

    private void setListeners(){
        bDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCorrectFilledFields()){
                    pbDownloading.setVisibility(View.VISIBLE);
                    loginAndDownload(new User(etUsername.getText().toString(),  etPassword.getText().toString()));
                }else{
                    if (isEmptyUsernameField()) {
                        etUsername.setError(Constants.EMPTY_FIELD);
                    }

                    if (isEmptyPasswordField()){
                        etPassword.setError(Constants.EMPTY_FIELD);
                    }
                }

                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (isEmptyUsernameField()){
                        etUsername.setError(Constants.EMPTY_FIELD);
                    }
                }
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (isEmptyPasswordField()){
                        etPassword.setError(Constants.EMPTY_FIELD);
                    }
                }
            }
        });
    }

    private boolean isEmptyUsernameField(){
        return etUsername.getText().toString().equals("");
    }

    private boolean isEmptyPasswordField(){
        return etPassword.getText().toString().equals("");
    }

    private boolean isCorrectFilledFields(){
        return !isEmptyUsernameField() && !isEmptyPasswordField();
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler  = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String message = getStringFromMessage(msg);
            try {
                pbDownloading.setVisibility(View.GONE);
                if (isRequestErrorMessage(message)){
                    Toast.makeText(MainActivity.this,Constants.ERROR_REQUEST, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!connectionAvailable()){
                    Toast.makeText(MainActivity.this,Constants.ERROR_CONNECTION, Toast.LENGTH_SHORT).show();
                    return;
                }

                saveMessage(message);
                processMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private boolean connectionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void saveMessage(String message) {
        this.imageMessage = message;
    }

    private String getStringFromMessage(Message message){
        return getBundleFromMessage(message).getString(Constants.MSG_KEY);
    }

    private Bundle getBundleFromMessage(Message message){
        return message.getData();
    }

    private void processMessage(String message) throws JSONException {
        String base64ImageString = JSONtoBase64ImageString(message);
        Bitmap bitmap = convertToBitmap(base64ImageString);
        showImage(bitmap);
    }

    private boolean isRequestErrorMessage(String message){
        return message.equals(Constants.EXCEPTION + Constants.ERROR_REQUEST);
    }

    private String JSONtoBase64ImageString(String message) throws JSONException {
        JSONObject mainObject = new JSONObject(message);
        return mainObject.getString(Constants.IMAGE);
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


