package com.example.samsung.woonebo_android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsung.woonebo_android.R;
import com.example.samsung.woonebo_android.model.KioskProductData;
import com.example.samsung.woonebo_android.model.ProductData;
import com.example.samsung.woonebo_android.service.ProductService;
import com.example.samsung.woonebo_android.service.ServiceGenerator;
import com.example.samsung.woonebo_android.util.ShoppingCartHelper;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.scaleHeight;
import static android.R.attr.scaleWidth;

/**
 * Created by SAMSUNG on 2016-11-03.
 */
public class ProductViewActivity extends AppCompatActivity {
    private ProductData productData;
    private TextView productName;
    private ImageView productImage;
    private TextView productDesc;
    private TextView productPrice;
    private Button buyItemBtn;
    private Button basketItemBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        getSupportActionBar().setTitle("Woo__Ne_Bo");

        Intent intent = getIntent();
        int productID = intent.getIntExtra("productID", 0);
        String idToken = intent.getStringExtra("idToken");

        productName = (TextView) findViewById(R.id.productName);
        productPrice = (TextView) findViewById(R.id.productPrice);
        productDesc = (TextView) findViewById(R.id.productDesc);
        productImage = (ImageView) findViewById(R.id.productBitmap);
        buyItemBtn = (Button) findViewById(R.id.buyItem);
        basketItemBtn = (Button) findViewById(R.id.basketItem);

        getProduct(productID, idToken);
    }

    private void getProduct(int productID, String idToken) {
        ProductService productService = ServiceGenerator.createService(ProductService.class);

        Call<ProductData> productItems = productService.getProduct(productID, idToken);
        productItems.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(Call<ProductData> call, Response<ProductData> response) {
                if(response.code() == 401) {
                    Log.i("autherror", "401");
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();

                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG);

                    Intent intent = new Intent(ProductViewActivity.this, LoginActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    Log.i("productDetail", response.body() + "");

                    productData = response.body();

                    productName.setText(productData.getName());
                    productPrice.setText(productData.getPrice() + "");
                    productDesc.setText(productData.getDesc());
                    String productImageUrl = productData.getImage();
                    if (productImageUrl.split("://").length == 1) {
                        productImageUrl = "http://52.78.72.121:3000" + productImageUrl;
                    }
                    Picasso.with(ProductViewActivity.this).load(productImageUrl).into(productImage);
                    buyItemBtn.setOnClickListener(BuyItemClickListener);
                    basketItemBtn.setOnClickListener(BasketItemClickListener);
                }
            }

            @Override
            public void onFailure(Call<ProductData> call, Throwable t) {

            }
        });
    }

    //ADD TO CART 버튼
    private View.OnClickListener BasketItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ProductViewActivity.this, ShoppingCartActivity.class);
            intent.putExtra("productData", productData);
            startActivity(intent);
        }
    };

    //Buy Now 버튼
    private View.OnClickListener BuyItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(productData.getUrl()));
            startActivity(intent);
        }
    };

    private void getShoppingCart() {
        Intent intent = new Intent(ProductViewActivity.this, ShoppingCartActivity.class);
        startActivity(intent);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Log.i("logout", LoginManager.getInstance() + "");
        finish();
        startActivity(new Intent(ProductViewActivity.this, LoginActivity.class));
    }

    private void changeAccounts() {
        Intent intent = new Intent(ProductViewActivity.this, MemberActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int curId = item.getItemId();
        switch(curId) {
            case R.id.basket:
                getShoppingCart();
                break;
            case R.id.logout:
                logOut();
                break;
            case R.id.menu_01:
                Toast.makeText(this, "menu_01", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_02:
                Toast.makeText(this, "menu_02", Toast.LENGTH_SHORT).show();
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
