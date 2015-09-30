package com.example.TestPlugin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MenuPrincipal extends Activity {

  private BroadcastReceiver receiver;
  private MiServiceForeground mServiceForeground;
  private MiServiceIBinder mServiceIBinder;
  private Messenger messenger = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.menu_principal);

    // CONFIGURACION SERVICE IBINDER

    final TextView texto = (TextView) findViewById(R.id.tx_service_ibinder);

    Button bt_Start_Service_IB = (Button) findViewById(R.id.bt_start_service_ibinder);
    bt_Start_Service_IB.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent intent = new Intent(MenuPrincipal.this, MiServiceIBinder.class);
        bindService(intent, sConnectionIB, Context.BIND_AUTO_CREATE);

        //
        //ComponentName myService = startService(new Intent(MenuPrincipal.this, MiServiceIBinder.class));
        //bindService(new Intent(MenuPrincipal.this, MiServiceIBinder.class), sConnectionIB, BIND_AUTO_CREATE);

      }
    });

    Button bt_Resultado_IB = (Button) findViewById(R.id.bt_get_result);
    bt_Resultado_IB.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        if (mServiceIBinder != null) {
          mServiceIBinder.DoShortCut();
          //String resultado = Integer.toString(mServiceIBinder.getResultado());
          //texto.setText("Su resuldato es: " + resultado);
        }
      }
    });

    Button bt_Stop_Service_IB = (Button) findViewById(R.id.bt_stop_service_ibinder);
    bt_Stop_Service_IB.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        if (mServiceIBinder != null) {
          mServiceIBinder.stopForeground(true);
          unbindService(sConnectionIB);
          mServiceIBinder = null;
        }
      }
    });
  }

  @Override
  protected void onDestroy() {
    //unregisterReceiver(receiver);
    super.onDestroy();
    if (sConnectionIB != null) {
      unbindService(sConnectionIB);
    }
  }

  // CONFIGURACION INTERFACE SERVICECONNECTION IBINDER
  private ServiceConnection sConnectionIB = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      MiServiceIBinder.MiBinderIBinder binder = (MiServiceIBinder.MiBinderIBinder) service;
      mServiceIBinder = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
  };
}