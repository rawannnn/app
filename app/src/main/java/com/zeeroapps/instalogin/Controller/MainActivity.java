package com.zeeroapps.instalogin.Controller;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.zeeroapps.instalogin.R;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static com.zeeroapps.instalogin.Model.CONSTANTS.AUTHURL;
import static com.zeeroapps.instalogin.Model.CONSTANTS.CLIENT_ID;
import static com.zeeroapps.instalogin.Model.CONSTANTS.CLIENT_SECRET;
import static com.zeeroapps.instalogin.Model.CONSTANTS.REDIRECT_URI;
import static com.zeeroapps.instalogin.Model.CONSTANTS.SP;
import static com.zeeroapps.instalogin.Model.CONSTANTS.SP_DP;
import static com.zeeroapps.instalogin.Model.CONSTANTS.SP_NAME;
import static com.zeeroapps.instalogin.Model.CONSTANTS.SP_TOKEN;
import static com.zeeroapps.instalogin.Model.CONSTANTS.TOKENURL;

public class MainActivity extends AppCompatActivity {
TwitterLoginButton loginButton;

    private String TAG = "MyApp";
    private String authURLFull;
    private String tokenURLFull;
    private String code;
    private String accessTokenString;
    private String dp;
    private String fullName;

    private Dialog dialog;
    ProgressBar progressBar;
    Button b1;
    Button TwitterLogin;
    Button b2;
    Button InstaLogin;
    SharedPreferences spUser;
    SharedPreferences.Editor spEdit;
    TwitterSession session;
    public Button but1;
    public Button but2;
    TwitterAuthToken authToken;
    long userID;
    public void init(){
        but1= findViewById(R.id.button);
        but1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent toy = new Intent(MainActivity.this,TimelineActivity.class );
                //Create the bundle
                Bundle bundle = new Bundle();
                //Add your data to bundle
                bundle.putString("username", session.getUserName());
                //Add the bundle to the intent
                toy.putExtras(bundle);
                startActivity(toy);

            }
        });
        but2= findViewById(R.id.button2);
        but2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent toy1 = new Intent(MainActivity.this,SearchActivity.class );
                //Create the bundle
                Bundle bundle1 = new Bundle();
                //Add your data to bundle
                bundle1.putString("userID", userID+"");
                //Add the bundle to the intent
                toy1.putExtras(bundle1);
                startActivity(toy1);

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);

        init();

        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
       /* loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                 session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

                login(session);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(MainActivity.this,"Authentication Failed!",Toast.LENGTH_LONG).show();
            }
        });*/
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                authToken = session.getAuthToken();
//              String userName = result.data.getUserName();
                userID = result.data.getId();
//
//                // The Token and Secret is not fixed. Every single time, it changes but it is handled by your code,
//                String token = authToken.token;
//                String secret = authToken.secret;
//
//
//                ((TextView) findViewById(R.id.display)).setText(
//                        "User Name: " + userName +
//                                "\nUser ID: " + userID +
//                                "\nToken Key: " + token +
//                                "\nT.Secret: " + secret);

                // use Retrofit2 to retrieve data with a Long parameter for userID
                //loadTwitterAPI(userID);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.e("Failed", exception.toString());
            }
        });





        spUser = getSharedPreferences(SP, MODE_PRIVATE);
        authURLFull = AUTHURL + "client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code&display=touch";
        tokenURLFull = TOKENURL + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&redirect_uri=" + REDIRECT_URI + "&grant_type=authorization_code";
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        b1 = findViewById(R.id.btn_twitter_logout);
        b2 = findViewById(R.id.btn_insta_logout);
        InstaLogin= findViewById(R.id.btn_insta_login);


        if (isLoggedIn()){
            b2.setVisibility(View.VISIBLE);
            InstaLogin.setVisibility(View.INVISIBLE);
            // startActivity(new Intent(this, WelcomeActivity.class));
            //finish();
        }

       /* authURLFull = AUTHURL + "client_id=" + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=code&display=touch";
        tokenURLFull = TOKENURL + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&redirect_uri=" + REDIRECT_URI + "&grant_type=authorization_code";
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        b1 = findViewById(R.id.btn_twitter_logout);
        b2 = findViewById(R.id.btn_insta_logout);*/


    }


    public void login(TwitterSession session){
        String username = session.getUserName();
        b1.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);

        //Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
        //intent.putExtra("username",username);
        //startActivity(intent);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    /*****  When login button is clicked **************************************/
    public void onClickLogin(View v) {
        setupWebviewDialog(authURLFull);
        progressBar.setVisibility(View.VISIBLE);

    }

    /*****  Show Instagram login page in a dialog *****************************/
    public void setupWebviewDialog(String url) {
        dialog = new Dialog(this);
        dialog.setTitle("Insta Login");

        WebView webView = new WebView(this);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new MyWVClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        dialog.setContentView(webView);
    }

    /*****  A client to know about WebView navigations  ***********************/
    class MyWVClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (request.getUrl().toString().startsWith(REDIRECT_URI)) {
                handleUrl(request.getUrl().toString());
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(REDIRECT_URI)) {
                handleUrl(url);
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.INVISIBLE);
            b2.setVisibility(View.VISIBLE);
            dialog.show();
        }
    }

    /*****  Check webview url for access token code or error ******************/
    public void handleUrl(String url) {

        if (url.contains("code")) {
            String temp[] = url.split("=");
            code = temp[1];
            new MyAsyncTask(code).execute();

        } else if (url.contains("error")) {
            String temp[] = url.split("=");
            Log.e(TAG, "Login error: "+temp[temp.length - 1]);
        }
    }

    /*****  AsyncTast to get user details after successful authorization ******/
    public class MyAsyncTask extends AsyncTask<URL, Integer, Long> {
        String code;

        public MyAsyncTask(String code) {
            this.code = code;
        }

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected Long doInBackground(URL... urls) {
            long result = 0;

            try {
                URL url = new URL(tokenURLFull);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                outputStreamWriter.write("client_id=" + CLIENT_ID +
                        "&client_secret=" + CLIENT_SECRET +
                        "&grant_type=authorization_code" +
                        "&redirect_uri=" + REDIRECT_URI +
                        "&code=" + code);

                outputStreamWriter.flush();
                String response = streamToString(httpsURLConnection.getInputStream());
                JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
                accessTokenString = jsonObject.getString("access_token"); //Here is your ACCESS TOKEN
                dp = jsonObject.getJSONObject("user").getString("profile_picture");
                fullName = jsonObject.getJSONObject("user").getString("full_name"); //This is how you can get the user info. You can explore the JSON sent by Instagram as well to know what info you got in a response

                spEdit = spUser.edit();
                spEdit.putString(SP_TOKEN, accessTokenString);
                spEdit.putString(SP_NAME, fullName);
                spEdit.putString(SP_DP, dp);
                spEdit.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(Long result) {
            dialog.dismiss();
            progressBar.setVisibility(View.INVISIBLE);
            //startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            //finish();
        }
    }

    /*****  Converting stream to string ***************************************/
    public static String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

            } finally {
                is.close();
            }
            str = sb.toString();
        }
        return str;
    }

    private boolean isLoggedIn(){
        String token = spUser.getString(SP_TOKEN, null);
        if (token != null){
            return true;

        }
        return false;
    }

    public void onClickLogout(View v){

        CookieSyncManager.createInstance(getApplicationContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        spUser.edit().putString(SP_TOKEN, null).commit();
        InstaLogin.setVisibility(View.VISIBLE);
        b2.setVisibility(View.INVISIBLE);
    }
}
