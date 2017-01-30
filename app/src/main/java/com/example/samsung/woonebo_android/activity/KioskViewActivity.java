package com.example.samsung.woonebo_android.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.samsung.woonebo_android.R;
import com.example.samsung.woonebo_android.adapter.KioskAdapter;
import com.example.samsung.woonebo_android.model.BeaconData;
import com.example.samsung.woonebo_android.model.KioskData;
import com.example.samsung.woonebo_android.scan.DeviceScan;
import com.example.samsung.woonebo_android.service.KioskService;
import com.example.samsung.woonebo_android.service.ServiceGenerator;
import com.example.samsung.woonebo_android.util.BackPressed;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KioskViewActivity extends AppCompatActivity {
    private ListView listView;
    private KioskData kioskData;
    private KioskAdapter kioskAdapter;
    private Handler scanHandler = new Handler();
    private BackPressed back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_view);

        getSupportActionBar().setTitle("Woo_Ne_bo");

        Intent intent = getIntent();
        final String idToken = intent.getStringExtra("idToken");

        checkBluetooth();

        scanHandler.post(scanDevice);

        kioskAdapter = new KioskAdapter();

        listView = (ListView) findViewById(R.id.kioskDataList);
        listView.setAdapter(kioskAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                KioskData kioskData = kioskAdapter.getItem(position);

                Intent kioskIntent = new Intent(KioskViewActivity.this, KioskProductActivity.class);
                kioskIntent.putExtra("kioskID", kioskData.getKioskID());
                kioskIntent.putExtra("idToken", idToken);
                startActivity(kioskIntent);
            }
        });

        back = new BackPressed(this);
    }

    private Runnable scanDevice = new Runnable() {
        @Override
        public void run() {
            scanDevice();

            scanHandler.postDelayed(this, 10*1000);
        }
    };

    private void scanDevice() {
        ArrayList<BeaconData> beaconDatas = DeviceScan.getBLE();
        Location location;

        // 1.비콘을 통해 키오스크를 가져온다.
        if(beaconDatas != null) {
            List<String> ble = new ArrayList<>();
            for(int i=0; i<beaconDatas.size(); i++) {
                ble.add(beaconDatas.get(i).getMajor() + "-" + beaconDatas.get(i).getMinor());
                Log.i("beaconScan", beaconDatas.get(i).getMajor() + "-" + beaconDatas.get(i).getMinor());
            }

            getKioskBeacon(ble);
        }

        // 2.비콘의 문제가 있을 경우, GPS를 통해서 위치를 잡는다.
        if(DeviceScan.location != null) {
            location = DeviceScan.location;
            getKioskGPS(location.getLatitude(), location.getLongitude());
            Log.i("GPSScan", location.getLatitude() + ", " + location.getLongitude());
        }
        else {
            location = DeviceScan.lastLocation;
            getKioskGPS(location.getLatitude(), location.getLongitude());
            Log.i("GPSScan", location.getLatitude() + ", " + location.getLongitude());
        }
    }

    private void getKioskBeacon(List<String> ble) {
        KioskService kioskService = ServiceGenerator.createService(KioskService.class);
        Call<JsonObject> kioskItems = kioskService.getKioskBeacon(ble);
        kioskItems.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 401) {
                    Log.i("autherror", "401");
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();

                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG);

                    Intent intent = new Intent(KioskViewActivity.this, LoginActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    JsonArray jsonArray = response.body().getAsJsonArray("kiosks");
                    Log.i("getKioskSuccess", jsonArray.size() + ", " + jsonArray.toString());

                    for(int i=0; i<jsonArray.size(); i++) {
                        int kioskID = jsonArray.get(i).getAsJsonObject().get("id").getAsInt();
                        String kioskImageUrl = jsonArray.get(i).getAsJsonObject().get("image").getAsString();
                        if (kioskImageUrl.split("://").length == 1) {
                            kioskImageUrl = "http://52.78.72.121:3000" + kioskImageUrl;
                        }
                        String kioskDesc = jsonArray.get(i).getAsJsonObject().get("desc").getAsString();

                        kioskData = new KioskData(kioskID, kioskImageUrl, kioskDesc);

                        int check = checkKiosk(kioskID);

                        if(check ==1) {
                            kioskAdapter.addKiosk(kioskData);
                        }
                    }
                    kioskAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("getKioskFail", "Failure");
                Toast.makeText(KioskViewActivity.this, "서버 통신에 실패했습니다. 상단의 새로고침 버튼을 눌러주세요.", Toast.LENGTH_LONG);
            }
        });
    }//getKioskBeacon() END....

    private void getKioskGPS(Double latitude, Double longitude) {
        KioskService kioskService = ServiceGenerator.createService(KioskService.class);

        Map<String, String> data = new HashMap<>();
        data.put("lat", String.valueOf(latitude));
        data.put("lng", String.valueOf(longitude));

        Call<JsonObject> kioskItems = kioskService.getKioskGPS(data);
        kioskItems.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 401) {
                    Log.i("autherror", "401");
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();

                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG);

                    Intent intent = new Intent(KioskViewActivity.this, LoginActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    JsonArray jsonArray = response.body().getAsJsonArray("kiosks");
                    Log.i("getKioskSuccessGPS", jsonArray.toString());

                    for(int i=0; i<jsonArray.size(); i++) {
                        int kioskID = jsonArray.get(i).getAsJsonObject().get("id").getAsInt();
                        String kioskImageUrl = jsonArray.get(i).getAsJsonObject().get("image").getAsString();
                        if (kioskImageUrl.split("://").length == 1) {
                            kioskImageUrl = "http://52.78.72.121:3000" + kioskImageUrl;
                        }
                        String kioskDesc = jsonArray.get(i).getAsJsonObject().get("desc").getAsString();

                        kioskData = new KioskData(kioskID, kioskImageUrl, kioskDesc);

                        int check = checkKiosk(kioskID);

                        if(check ==1) {
                            kioskAdapter.addKiosk(kioskData);
                        }
                    }
                    kioskAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("getKioskFail", "Failure");
                Toast.makeText(KioskViewActivity.this, "서버 통신에 실패했습니다. 상단의 새로고침 버튼을 눌러주세요.", Toast.LENGTH_LONG);
            }
        });
    }//getKioskTask() END....

    private int checkKiosk(int kioskID) {
        if(kioskAdapter.getCount() == 0)
            return 1;
        else {
            for(int j=0; j<kioskAdapter.getCount(); j++)
                if(kioskAdapter.getItem(j).getKioskID() == kioskID)
                    return -1;
        }
        return 1;
    }

    public void kioskRefresh() {
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
        scanDevice();   //scanDevice()가 쌓인다?
    }

    private void getShoppingCart() {
        Intent intent = new Intent(KioskViewActivity.this, ShoppingCartActivity.class);
        startActivity(intent);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Log.i("logout", LoginManager.getInstance() + "");
        finish();
        startActivity(new Intent(KioskViewActivity.this, LoginActivity.class));
    }

    private void changeAccounts() {
        Intent intent = new Intent(KioskViewActivity.this, MemberActivity.class);
        startActivity(intent);
    }

    private void checkBluetooth() {
        if (!DeviceScan.btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int curId = item.getItemId();
        switch(curId) {
            case R.id.refresh:
                kioskRefresh();
                break;
            case R.id.basket:
                getShoppingCart();
                break;
            case R.id.logout:
                logOut();
                break;
            case R.id.menu_01:

                break;
            case R.id.menu_02:

                break;
            case R.id.menu_03:
                changeAccounts();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        back.onBackPressed();
    }
}
