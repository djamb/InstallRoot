package com.example.TestPlugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class MiService extends Service {
	
	
	@Override
    public void onCreate(){}
	
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId){
		Toast.makeText(this, "Service iniciado", Toast.LENGTH_SHORT).show();
        return Service.START_STICKY;    
    }
	
	
	
	@Override
    public void onDestroy(){
        Toast.makeText(this, "Service finalizado", Toast.LENGTH_SHORT).show();
    }



	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	
	
	
} 