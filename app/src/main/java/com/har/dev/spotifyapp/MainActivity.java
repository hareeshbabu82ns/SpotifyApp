package com.har.dev.spotifyapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
    implements ArtistFinderFragment.OnArtistSelectedListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar bar = (Toolbar) findViewById(R.id.app_bar);
    bar.setTitle(R.string.title_artists);
    setSupportActionBar(bar);
    SpotifyApp.getApplication().bindPlayer(null);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
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
  public void onArtistSelected(Uri uri) {
    //call activity to show Albums of selected Artist
//    final Intent intent = new Intent(this, AlbumsActivity.class);
    final Intent intent = new Intent(this, ArtistTracksActivity.class);
    intent.putExtra(Utils.EXTRA_ARTIST_URI, uri);
    startActivity(intent);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    SpotifyApp.getApplication().unbindPlayer();
  }
}
