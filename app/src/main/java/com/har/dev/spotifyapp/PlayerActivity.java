package com.har.dev.spotifyapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by hareesh on 6/29/15.
 */
public class PlayerActivity extends AppCompatActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);

    Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
    bar.setTitle(R.string.title_player);
    setSupportActionBar(bar);

    if (savedInstanceState == null) {
      final Uri trackUri = getIntent().getParcelableExtra(Utils.EXTRA_TRACK_URI);
      getFragmentManager().beginTransaction()
          .replace(R.id.fragment_placeholder,
              PlayerFragment.newInstance(trackUri))
          .commit();
    }
  }
}
