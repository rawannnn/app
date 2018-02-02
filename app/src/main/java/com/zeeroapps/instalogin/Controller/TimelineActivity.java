package com.zeeroapps.instalogin.Controller;

/**
 * Created by razan on 1/31/18.
 */

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;
import com.zeeroapps.instalogin.R;

public class TimelineActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
//Get the bundle
        Bundle bundle = getIntent().getExtras();

//Extract the data…
        String username = bundle.getString("username");
       // final UserTimeline userTimeline = new UserTimeline.Builder()
         //       .screenName(username)
           //     .build();

        SearchTimeline searchTimeline = new SearchTimeline.Builder()
                .query("#خسوفـالقمر")
                .build();

        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(searchTimeline)
                .build();
        setListAdapter(adapter);
    }
}