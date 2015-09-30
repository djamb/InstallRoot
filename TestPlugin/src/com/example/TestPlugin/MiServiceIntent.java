package com.example.TestPlugin;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

public class MiServiceIntent extends IntentService {
	
	public static final String INTENTSERVICE = "intentservice";
	
	public MiServiceIntent() {
		super("MiIntentService");
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    Toast.makeText(this, "IntentService iniciado", Toast.LENGTH_SHORT).show();
	    return super.onStartCommand(intent,flags,startId);
	}
	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		for (int i = 0; i <= 20; i++) {
			try {
				Thread.sleep(150);
			} catch (Exception e) {}
			
			Intent set_progreso = new Intent(INTENTSERVICE);
			set_progreso.putExtra("progreso", i*5);
            sendBroadcast(set_progreso);
		}
	}
	
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "IntentService finalizado", Toast.LENGTH_SHORT).show();
	}
	
	
	
}