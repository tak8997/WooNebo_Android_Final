package com.example.samsung.woonebo_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.samsung.woonebo_android.R;
import com.example.samsung.woonebo_android.activity.ProductViewActivity;
import com.example.samsung.woonebo_android.model.ProductData;
import com.example.samsung.woonebo_android.util.ShoppingCartHelper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by SAMSUNG on 2016-11-15.
 */
public class ProductAdapter extends BaseAdapter {
    private List<ProductData> mProductList;
    private LayoutInflater mInflater;
    private Context mContext;
    private boolean mShowCheckbox;

    public ProductAdapter(List<ProductData> list, LayoutInflater inflater, Context context, boolean ShowCheckbox) {
        mProductList = list;
        mInflater = inflater;
        mContext = context;
        mShowCheckbox = ShowCheckbox;
    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewItem item;
        final Context context = parent.getContext();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item, null);

            item = new ViewItem();

            item.productBitmap = (ImageView) convertView.findViewById(R.id.productBitmap);
            item.productName = (TextView) convertView.findViewById(R.id.productName);
            item.productDesc = (TextView) convertView.findViewById(R.id.productDesc);
            item.productPrice = (TextView) convertView.findViewById(R.id.productPrice);
            item.productRemove = (Button) convertView.findViewById(R.id.productRemove);
            item.productUrl = (Button) convertView.findViewById(R.id.productUrl);

            convertView.setTag(item);
        } else {
            item = (ViewItem) convertView.getTag();
        }

        final ProductData curProduct = mProductList.get(position);

        String productImageUrl = curProduct.getImage();
        if (productImageUrl.split("://").length == 1) {
            productImageUrl = "http://52.78.72.121:3000" + productImageUrl;
        }
        Picasso.with(context).load(productImageUrl).into(item.productBitmap);//Picasso 라이브러리를 활용하여 상품 이미지의 URL을 통해 다시 이미지를 불러옴
        item.productName.setText(curProduct.getName());
        item.productDesc.setText(curProduct.getDesc());
        item.productPrice.setText(curProduct.getPrice() + "원");
        item.productRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductList.remove(position);

                notifyDataSetChanged();
            }
        });
        item.productUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(curProduct.getUrl()));
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    private class ViewItem {
        ImageView productBitmap;
        TextView productName;
        TextView productDesc;
        TextView productPrice;
        Button productRemove;
        Button productUrl;
        CheckBox productCheckbox;
    }
}