package com.example.TestPlugin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

public class MiServiceForeground extends Service {
	
	private NotificationManager NM;
	private final int id_notificacion = 521;
	
	private final IBinder mBinder = new MiBinderForeground();
	
	
	@Override
    public void onCreate(){}
	
	
	@Override
    public int onStartCommand(Intent intent, int flags,int startId){
        return Service.START_STICKY;    
    }

	
	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(this, "Service iniciado", Toast.LENGTH_SHORT).show();
		
		NM = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		startForeground(id_notificacion, notificacion());
		
		return mBinder;
	}
	
	
	@Override
    public void onDestroy(){
		NM.cancel(id_notificacion); 
        Toast.makeText(this, "Service finalizado", Toast.LENGTH_SHORT).show();
    }
	
	
	
	private Notification notificacion() {
		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
		
		nBuilder.setSmallIcon(R.drawable.ic_launcher);
		nBuilder.setContentTitle("App Services");
		nBuilder.setContentText("Servicio Iniciado");
		
		Intent intent = new Intent(this, MenuPrincipal.class);
		
		TaskStackBuilder tStack = TaskStackBuilder.create(this);
		tStack.addNextIntent(intent);
		
		PendingIntent pIntent = tStack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		nBuilder.setContentIntent(pIntent);
		
		return nBuilder.build();
	}

	
	
	public class MiBinderForeground extends Binder {
        MiServiceForeground getService() {
            return MiServiceForeground.this;
        }
    }
	
	
	
} 