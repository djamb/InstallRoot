package com.example.TestPlugin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

/**
 * @author Antonio Miñano
 */
public class TransitActivityToService extends Activity {
  private MiServiceIBinder mServiceIBinder;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = new Intent(this, MiServiceIBinder.class);
    bindService(intent, sConnectionIB, Context.BIND_AUTO_CREATE);
  }

  private ServiceConnection sConnectionIB = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      MiServiceIBinder.MiBinderIBinder binder = (MiServiceIBinder.MiBinderIBinder) service;
      mServiceIBinder = binder.getService();
      mServiceIBinder.runApplication();
      finish();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
  };
  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (sConnectionIB != null) {
      unbindService(sConnectionIB);
    }
  }

}
