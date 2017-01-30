package com.example.samsung.woonebo_android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsung.woonebo_android.R;
import com.example.samsung.woonebo_android.adapter.KioskProductAdapter;
import com.example.samsung.woonebo_android.model.KioskData;
import com.example.samsung.woonebo_android.model.KioskProductData;
import com.example.samsung.woonebo_android.service.KioskProductService;
import com.example.samsung.woonebo_android.service.ServiceGenerator;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KioskProductActivity extends AppCompatActivity {
    private ListView listView;
    private KioskProductAdapter kioskProductAdapter;
    private Handler timeHandler = new Handler();
    private ValidateTime timeThread;
    private int validate_time;
    private int kioskID;
    private String idToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_product);

        getSupportActionBar().setTitle("Woo_Ne_bo");

        Intent intent = getIntent();
        kioskID = intent.getIntExtra("kioskID", 0);
        idToken = intent.getStringExtra("idToken");

        getKioskProduct();

        kioskProductAdapter = new KioskProductAdapter(idToken);

        listView = (ListView) findViewById(R.id.kioskData);
        listView.setAdapter(kioskProductAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                KioskProductData kioskProductData = kioskProductAdapter.getItem(position);

                Intent intent = new Intent(KioskProductActivity.this, ProductViewActivity.class);
                intent.putExtra("productID", kioskProductData.getKioskProductID());
                intent.putExtra("idToken", idToken);
                startActivity(intent);
            }
        });
    }

    private void getKioskProduct() {
        KioskProductService kioskProductService = ServiceGenerator.createService(KioskProductService.class);

        Call<JsonObject> kioskProductItems = kioskProductService.getKioskProduct(kioskID, idToken);
        kioskProductItems.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 401) {
                    Log.i("autherror", "401");
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();

                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG);

                    Intent intent = new Intent(KioskProductActivity.this, LoginActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    Log.i("kioskProduct", response.body().toString());

                    int type = Integer.parseInt(response.body().getAsJsonObject().get("type").toString());

                    if(type == 1) {
                        String reference = response.body().getAsJsonObject().get("reference").toString();
                        reference = reference.split("\"")[1];
                        Log.i("reference", reference );

                        listView.setVisibility(View.INVISIBLE);

                        WebView webView = (WebView)findViewById(R.id.webView);
                        webView.setWebViewClient(new WebViewClient());
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.loadUrl(reference);

                        Log.i("urll", webView.getUrl() );
                    } else {
                        JsonArray jsonArray = response.body().getAsJsonArray("products");
                        validate_time = Integer.parseInt(response.body().getAsJsonObject().get("validate_time").toString());

                        Log.i("getKioskProductSuccess", jsonArray.toString() + "");

                        int kioskProductID = jsonArray.get(0).getAsJsonObject().get("id").getAsInt();
                        String kioskProductDesc = jsonArray.get(0).getAsJsonObject().get("desc").getAsString();
                        String kioskProductImage = jsonArray.get(0).getAsJsonObject().get("image").getAsString();
                        if (kioskProductImage.split("://").length == 1) {
                            kioskProductImage = "http://52.78.72.121:3000" + kioskProductImage;
                        }

                        KioskProductData kioskProductData = new KioskProductData(kioskProductID, kioskProductDesc, kioskProductImage);
                        kioskProductAdapter.addKiosk(kioskProductData);
                        kioskProductAdapter.notifyDataSetChanged();
                        setValidateTime();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("getKioskProductFail", "Failure");
                TextView textView = (TextView) findViewById(R.id.textView01);
                textView.setText("Empty");
                listView.setEmptyView(textView);
                Toast.makeText(KioskProductActivity.this, "서버 통신에 실패했습니다. 상단의 새로고침 버튼을 눌러주세요.", Toast.LENGTH_LONG);
            }
        });
    }

    private void setValidateTime() {
        timeThread = new ValidateTime();
        timeThread.setDaemon(true);
        timeThread.start();
    }

    private class ValidateTime extends Thread {
            @Override
            public void run() {
                int seconds = validate_time;
                Log.i("seconds", seconds + "");

            if (seconds == 0) {
                return;
            }

            while(seconds != 0) {
                try {
                    Log.i("thread1", "thread1");
                    Thread.sleep(1000);
                    seconds--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i("thread", "thread");

            timeHandler.post(new Runnable() {
                @Override
                public void run() {
                    getKioskProduct();
                }
            });
        }
    }

    private void kioskRefresh() {
        kioskProductAdapter.deleteAll();

        getKioskProduct();
    }

    private void getShoppingCart() {
        Intent intent = new Intent(KioskProductActivity.this, ShoppingCartActivity.class);
        startActivity(intent);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Log.i("logout", LoginManager.getInstance() + "");
        finish();
        startActivity(new Intent(KioskProductActivity.this, LoginActivity.class));
    }

    private void changeAccounts() {
        Intent intent = new Intent(KioskProductActivity.this, MemberActivity.class);
        startActivity(intent);
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
}
