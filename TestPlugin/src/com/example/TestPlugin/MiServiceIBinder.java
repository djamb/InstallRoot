package com.example.TestPlugin;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import com.morgoo.droidplugin.pm.PluginManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiServiceIBinder extends Service implements ServiceConnection {
  private ArrayList<ApkItem> arrayApkItem = new ArrayList<>();
  private final IBinder iBinder = new MiBinderIBinder();
  final Handler handler = new Handler();
  private MyBroadcastReceiver mMyBroadcastReceiver = new MyBroadcastReceiver();

  public class MiBinderIBinder extends Binder {
    public MiServiceIBinder getService() {
      return MiServiceIBinder.this;
    }
  }

  @Override
  public IBinder onBind(Intent arg0) {
    Toast.makeText(this, "Service iniciado", Toast.LENGTH_SHORT).show();

    return iBinder;
  }

  @Override
  public void onDestroy() {
    Toast.makeText(this, "Service finalizado", Toast.LENGTH_SHORT).show();
  }

  public void DoShortCut() {
    ShortcutIcon();
  }

  @Override
  public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
    startLoad();
  }

  @Override
  public void onServiceDisconnected(ComponentName componentName) {
  }

  public void runApplication() {
    //Toast.makeText(this, "Hace Random", Toast.LENGTH_SHORT).show();
    //Intent intent = new Intent();
    //intent.setAction(Intent.ACTION_MAIN);
    //intent.addCategory(Intent.CATEGORY_LAUNCHER);
    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //ComponentName cn = new ComponentName(this, MyActivity.class);
    //intent.setComponent(cn);
    //startActivity(intent);

    mMyBroadcastReceiver.registerReceiver(this.getApplication());
    if (PluginManager.getInstance().isConnected()) {
      startLoad();
    } else {
      PluginManager.getInstance().addServiceConnection(this);
    }
  }


  public void createApplication() {

  }





  private void ShortcutIcon() {
    Log.e("", "Create icon");
    Intent shortcutIntent = new Intent(getApplicationContext(), TransitActivityToService.class);
    //shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    shortcutIntent.setAction(Intent.ACTION_MAIN);
    Intent addIntent = new Intent();
    addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
    addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "prueba");
    //addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_launcher));
    addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
    getApplicationContext().sendBroadcast(addIntent);
  }

  private class MyBroadcastReceiver extends BroadcastReceiver {
    Context context;

    void registerReceiver(Context con) {
      IntentFilter f = new IntentFilter();
      f.addAction(PluginManager.ACTION_PACKAGE_ADDED);
      f.addAction(PluginManager.ACTION_PACKAGE_REMOVED);
      f.addDataScheme("package");
      context = con;
      con.registerReceiver(this, f);
    }

    void unregisterReceiver(Context con) {
      con.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
      if (PluginManager.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
        try {
          PackageManager pm = context.getPackageManager();
          String pkg = intent.getData().getAuthority();
          PackageInfo info = PluginManager.getInstance().getPackageInfo(pkg, 0);
          arrayApkItem.add(new ApkItem(pm, info, info.applicationInfo.publicSourceDir));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void startLoad() {
    new Thread("ApkScanner") {
      @Override
      public void run() {
        try {
          final List<PackageInfo> infos = PluginManager.getInstance().getInstalledPackages(0);
          final PackageManager pm = getApplicationContext().getPackageManager();
          for (final PackageInfo info : infos) {
            handler.post(new Runnable() {
              @Override
              public void run() {
                arrayApkItem.add(new ApkItem(pm, info, info.applicationInfo.publicSourceDir));
              }
            });
          }
        } catch (RemoteException e) {
          e.printStackTrace();
        }

        handler.post(new Runnable() {
          @Override
          public void run() {
            PackageManager pm = getApplicationContext().getPackageManager();
            Intent intent1 =
                pm.getLaunchIntentForPackage(arrayApkItem.get(0).packageInfo.packageName);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
          }
        });
      }
    }.start();
  }
}