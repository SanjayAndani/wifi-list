package com.software.wifiinformation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 1001;

    private WifiManager wifiManager;
    private ListView wifiList;
    private Button refreshButton;

    private List<ScanResult> scanResults;
    private List<String> wifiNames = new ArrayList<>();

    private ArrayAdapter<String> adapter;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiList = findViewById(R.id.wifi_list);
        refreshButton = findViewById(R.id.refresh_button);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifiNames);
        wifiList.setAdapter(adapter);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    refreshWifiList();
                } else {
                    requestPermissions();
                }
            }
        });

        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String wifiName = wifiNames.get(position);
                Toast.makeText(MainActivity.this, wifiName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshWifiList() {
        wifiNames.clear();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        scanResults = wifiManager.getScanResults();
        for (ScanResult scanResult : scanResults) {
            wifiNames.add(scanResult.SSID);
        }
        adapter.notifyDataSetChanged();
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                refreshWifiList();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
