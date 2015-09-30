package com.example.TestPlugin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class MiServiceMessenger extends Service {
	
	public static final int MSG_SAY_HELLO = 1;
	final Messenger messenger = new Messenger(new MiHandler());
	private static Context context;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		context = this;
		Toast.makeText(this, "Service iniciado", Toast.LENGTH_SHORT).show();
		return messenger.getBinder();
	}
	
	
	@Override
    public void onDestroy(){
        Toast.makeText(this, "Service finalizado", Toast.LENGTH_SHORT).show();
    }
	
	
	
    private static class MiHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                	Toast.makeText(context, "¡ Hola !", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    
    
    
}