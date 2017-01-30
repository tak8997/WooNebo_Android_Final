package com.example.samsung.woonebo_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsung.woonebo_android.R;
import com.example.samsung.woonebo_android.adapter.ProductAdapter;
import com.example.samsung.woonebo_android.model.ProductData;
import com.example.samsung.woonebo_android.util.ShoppingCartHelper;

import java.util.ArrayList;

/**
 * Created by SAMSUNG on 2016-11-15.
 */
public class ShoppingCartActivity extends AppCompatActivity {
    private ProductData productData;
    private ArrayList<ProductData> mCartList;
    private String cartList;
    private ProductAdapter mProductAdapter;
    private ListView listViewCatalog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        // ActionBar에 타이틀 변경
        getSupportActionBar().setTitle("Woo_Ne_bo");

        Intent intent = getIntent();
        productData = (ProductData) intent.getSerializableExtra("productData");
        mCartList = ShoppingCartHelper.getCart();

        if(productData != null)
            mCartList.add(productData);

        mProductAdapter = new ProductAdapter(mCartList, getLayoutInflater(), getBaseContext(), true);

        listViewCatalog = (ListView) findViewById(R.id.ListViewCatalog);
        if(mCartList == null) {
            TextView textView = (TextView) findViewById(R.id.TextView01);
            listViewCatalog.setEmptyView(textView);
        }
        else {
            listViewCatalog.setAdapter(mProductAdapter);
            listViewCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                    Toast.makeText(getBaseContext(), "selected", Toast.LENGTH_LONG).show();
                    Log.i("toast", "selected");
                }
            });
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
            case R.id.logout:
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_01:
                Toast.makeText(this, "menu_01", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_02:
                Toast.makeText(this, "menu_02", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_03:
                Toast.makeText(this, "menu_03", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
