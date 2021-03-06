package com.har.dev.spotifyapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class AlbumsActivity extends AppCompatActivity implements AlbumsFragment.OnAlbumSelectedListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_albums);

    Toolbar bar = (Toolbar) findViewById(R.id.app_bar);
    bar.setTitle(R.string.title_albums);
    setSupportActionBar(bar);

    if (savedInstanceState == null) {
      Uri artistUri = getIntent().getParcelableExtra(Utils.EXTRA_ARTIST_URI);

      getFragmentManager().beginTransaction()
          .replace(R.id.fragment_albums_placeholder, AlbumsFragment.newInstance(artistUri), null)
          .commit();

    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_albums, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onAlbumSelected(Uri uri) {
    //TODO: navigate to Tracks Activity
  }
}
