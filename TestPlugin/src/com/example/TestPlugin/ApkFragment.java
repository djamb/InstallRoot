package com.example.TestPlugin;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.morgoo.droidplugin.pm.PluginManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ApkFragment extends ListFragment implements ServiceConnection {
    private ArrayAdapter<ApkItem> adapter;
    final Handler handler = new Handler();

    public ApkFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        adapter = new ArrayAdapter<ApkItem>(getActivity(), 0) {
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.apk_item, null);
                }
                ApkItem item = getItem(position);

                ImageView icon = (ImageView) convertView.findViewById(R.id.imageView);
                icon.setImageDrawable(item.icon);

                TextView title = (TextView) convertView.findViewById(R.id.textView1);
                title.setText(item.title);

                TextView version = (TextView) convertView.findViewById(R.id.textView2);
                version.setText(String.format("%s(%s)", item.versionName, item.versionCode));

                TextView btn3 = (TextView) convertView.findViewById(R.id.button3);
                btn3.setText("borrar");
                btn3.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        onListItemClick(getListView(), view, position, getItemId(position));
                    }
                });
                TextView btn = (TextView) convertView.findViewById(R.id.button2);
                try {
                    if (item.installing) {
                        btn.setText("Instalaciones ING");
                    } else {
                        if (PluginManager.getInstance().isConnected()) {
                            btn.setText(PluginManager.getInstance().getPackageInfo(item.packageInfo.packageName, 0) != null ? "Ya instalado" : "Instalación");
                        } else {
                            btn.setText("Esperando para inicializar el servicio");
                        }
                    }
                } catch (Exception e) {
                    btn.setText("Instalación 1");
                }
                btn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        onListItemClick(getListView(), view, position, getItemId(position));
                    }
                });


                return convertView;
            }
        };

    }

    private void doUninstall(final ApkItem item) {
        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setTitle("Advertencia, ¿Seguro que deseas eliminarlo?");
        builder.setMessage("Advertencia, ¿Seguro que quieres eliminar" + item.title + "么？");
        builder.setNegativeButton("borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new File(item.apkfile).delete();
                adapter.remove(item);
                Toast.makeText(getActivity(), "eliminado correctamente", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNeutralButton("cancelar", null);
        builder.show();
    }

    boolean isViewCreated = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        setEmptyText("Ninguno apk en sdcard");
        setListAdapter(adapter);
        setListShown(false);
        getListView().setOnItemClickListener(null);
        if (PluginManager.getInstance().isConnected()) {
            startLoad();
        } else {
            PluginManager.getInstance().addServiceConnection(this);
        }
    }

    @Override
    public void onDestroyView() {
        isViewCreated = false;
        super.onDestroyView();
    }

    @Override
    public void setListShown(boolean shown) {
        if (isViewCreated) {
            super.setListShown(shown);
        }
    }

    private void startLoad() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setListShown(true);
            }
        });
        if (!isViewCreated) {
            return;
        }
        new Thread("ApkScanner") {
            @Override
            public void run() {
                File file = Environment.getExternalStorageDirectory();

                List<File> apks = new ArrayList<File>(10);
                for (File apk : file.listFiles()) {
                    if (apk.exists() && apk.getPath().toLowerCase().endsWith(".apk")) {
                        apks.add(apk);
                    }
                }

                file = new File(Environment.getExternalStorageDirectory(), "360Download");
                if (file.exists() && file.isDirectory()) {
                    for (File apk : file.listFiles()) {
                        if (apk.exists() && apk.getPath().toLowerCase().endsWith(".apk")) {
                            apks.add(apk);
                        }
                    }
                }
                PackageManager pm = getActivity().getPackageManager();
                for (final File apk : apks) {
                    try {
                        if (apk.exists() && apk.getPath().toLowerCase().endsWith(".apk")) {
                            final PackageInfo info = pm.getPackageArchiveInfo(apk.getPath(), 0);
                            if (info != null && isViewCreated) {
                                try {
                                   handler.post(new Runnable() {
                                       @Override
                                       public void run() {
                                           adapter.add(new ApkItem(getActivity(), info, apk.getPath()));
                                       }
                                   });
                                } catch (Exception e) {
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final ApkItem item = adapter.getItem(position);
        if (v.getId() == R.id.button2) {
            if (item.installing) {
                return;
            }
            if (!PluginManager.getInstance().isConnected()) {
                Toast.makeText(getActivity(), "Servicio Widget está inicializando, por favor intente de nuevo más tarde. . .", Toast.LENGTH_SHORT).show();
            }
            try {
                if (PluginManager.getInstance().getPackageInfo(item.packageInfo.packageName, 0) != null) {
                    Toast.makeText(getActivity(), "Ya instalado, no se puede instalar", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            doInstall(item);
                        }
                    }.start();

                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    PluginManager.getInstance().installPackage(item.apkfile, 0);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                adapter.remove(item);
            }
        } else if (v.getId() == R.id.button3) {
            doUninstall(item);
        }
    }

    private synchronized void doInstall(ApkItem item) {
        item.installing = true;

        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
        try {
            final int re = PluginManager.getInstance().installPackage(item.apkfile, 0);
            item.installing = false;

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), re == PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION ? "La instalación falla, los permisos de archivos solicitados también" : "La instalación se ha completado", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        startLoad();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }

    @Override
    public void onDestroy() {
        PluginManager.getInstance().removeServiceConnection(this);
        super.onDestroy();
    }
}
