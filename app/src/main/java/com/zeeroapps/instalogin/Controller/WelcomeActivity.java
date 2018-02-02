package com.zeeroapps.instalogin.Controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.zeeroapps.instalogin.R;

import static com.zeeroapps.instalogin.Model.CONSTANTS.SP;
import static com.zeeroapps.instalogin.Model.CONSTANTS.SP_DP;
import static com.zeeroapps.instalogin.Model.CONSTANTS.SP_NAME;
import static com.zeeroapps.instalogin.Model.CONSTANTS.SP_TOKEN;

public class WelcomeActivity extends AppCompatActivity {

    SharedPreferences spUser;

    ImageView ivProfile;
    TextView tvName;

    String name, dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        String username = getIntent().getStringExtra("username");
        TextView uname = findViewById(R.id.tv_username);
        uname.setText(username);

        tvName = (TextView) findViewById(R.id.tv_full_name);
        ivProfile = (ImageView) findViewById(R.id.iv_dp);

        spUser = getSharedPreferences(SP, MODE_PRIVATE);
        name = spUser.getString(SP_NAME, null);
        dp = spUser.getString(SP_DP, null);

        if (name != null){
            tvName.setText(name);
            Glide.with(this).load(dp).into(ivProfile);
        }
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_add:
                    Toast.makeText(WelcomeActivity.this, "Action add clicked", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.action_edit:
                    Toast.makeText(WelcomeActivity.this, "Action edit clicked", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.action_remove:
                    Toast.makeText(WelcomeActivity.this, "Action remove clicked", Toast.LENGTH_SHORT).show();
                    break;


            }
     return true;   }

        });
    }
    public void onClickLogout(View v){

        CookieSyncManager.createInstance(getApplicationContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        spUser.edit().putString(SP_TOKEN, null).commit();
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }
}
