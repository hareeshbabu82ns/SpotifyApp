package com.har.dev.spotifyapp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.ArrayList;

/**
 * Created by hareesh on 6/27/15.
 */
public class SpotifyApp extends Application {

  private static SpotifyApp application;

  private static PlayerService mPlayer;

  ArrayList<OnBindPlayer> onBindPlayers = new ArrayList<>();
  private ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mPlayer = ((PlayerService.PlayerBinder) service).getService();
      synchronized (onBindPlayers) {
        for (OnBindPlayer onBindPlayer : onBindPlayers) {
          onBindPlayer.onBind(mPlayer);
        }
        onBindPlayers.clear();
      }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mPlayer = null;
    }
  };

  public static final PlayerService getPlayerService() {
    if (mPlayer != null) {
      return mPlayer;
    } else
      throw new InstantiationError("Player Service is not initialized yet");
  }

  public static final SpotifyApp getApplication() {
    return application;
  }

  public void bindPlayer(OnBindPlayer onBindPlayer) {
    if (mPlayer == null) {
      bindService(new Intent(this, PlayerService.class), mConnection, BIND_AUTO_CREATE);
      if (onBindPlayer != null)
        onBindPlayers.add(onBindPlayer);
    } else {
      if (onBindPlayer != null)
        onBindPlayer.onBind(mPlayer);
    }
  }

  public void unbindPlayer() {
    unbindService(mConnection);
    mPlayer = null;
    onBindPlayers.clear();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    application = this;
  }

  public interface OnBindPlayer {
    void onBind(PlayerService playerService);
  }
}
