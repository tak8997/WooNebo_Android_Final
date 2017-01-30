package com.example.samsung.woonebo_android.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.samsung.woonebo_android.R;
import com.example.samsung.woonebo_android.model.KioskData;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SAMSUNG on 2016-10-27.
 */
public class KioskAdapter extends BaseAdapter {
    private ArrayList<KioskData> arrayList;

    public KioskAdapter() {
        arrayList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public KioskData getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_kiosk, parent, false);

        ImageView productImage = (ImageView) convertView.findViewById(R.id.kioskImage);
        TextView productDesc = (TextView) convertView.findViewById(R.id.kioskDesc);

        KioskData kioskData = arrayList.get(position);

        Picasso.with(context).load(kioskData.getKioskImageUrl()).into(productImage);    //Picasso 라이브러리를 활용하여 kioskImageUrl을 통해 이미지를 불러
        productDesc.setText(kioskData.getKioskDesc());

        return convertView;
    }

    public void addKiosk(KioskData kioskData) {
        arrayList.add(kioskData);
    }

    public void deleteAll() {
        arrayList.clear();
    }
}
