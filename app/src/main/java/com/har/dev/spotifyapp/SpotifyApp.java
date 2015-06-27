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

  public void bindPlayer(OnBindPlayer onBindPlayer) {
    if (mPlayer == null) {
      bindService(new Intent(this, PlayerService.class), mConnection, BIND_AUTO_CREATE);
      onBindPlayers.add(onBindPlayer);
    } else
      onBindPlayer.onBind(mPlayer);
  }

  public void unbindPlayer() {
    unbindService(mConnection);
    mPlayer = null;
    onBindPlayers.clear();
  }

  public interface OnBindPlayer {
    void onBind(PlayerService playerService);
  }
}
