package com.example.samsung.woonebo_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsung.woonebo_android.R;
import com.example.samsung.woonebo_android.activity.ShoppingCartActivity;
import com.example.samsung.woonebo_android.model.KioskData;
import com.example.samsung.woonebo_android.model.KioskProductData;
import com.example.samsung.woonebo_android.service.ServiceGenerator;
import com.example.samsung.woonebo_android.service.TargetingService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SAMSUNG on 2016-10-27.
 */
public class KioskProductAdapter extends BaseAdapter {
    private ArrayList<KioskProductData> arrayList;
    private boolean isChecked = false;
    private String idToken;

    public KioskProductAdapter(String idToken) {
        this.arrayList = new ArrayList<>();
        this.idToken = idToken;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public KioskProductData getItem(int position) {
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
        convertView = inflater.inflate(R.layout.list_kiosk_product, parent, false);

        final LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.kioskProductLayout);
        final ImageView kioskProductImage = (ImageView) convertView.findViewById(R.id.kioskProductImage);
        final TextView kioskProductDesc = (TextView) convertView.findViewById(R.id.kioskProductDesc);
        final ImageView heartBtn = (ImageView) convertView.findViewById(R.id.heart);
        final ImageView deleteBtn = (ImageView) convertView.findViewById(R.id.delete);

        final KioskProductData kioskProductData = arrayList.get(position);

        Picasso.with(context).load(kioskProductData.getKioskProductImage()).into(kioskProductImage);    //Picasso 라이브러리를 활용하여 kioskImageUrl을 통해 이미지를 불러
        kioskProductDesc.setText(kioskProductData.getKioskProductDesc());
        heartBtn.setImageResource(R.drawable.blank_heart);
        deleteBtn.setImageResource(R.drawable.delete);

        heartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isChecked == false) {
                    heartBtn.setImageResource(R.drawable.heart);
                    isChecked = true;

                    addInterest(kioskProductData.getKioskProductID());
                } else {
                    heartBtn.setImageResource(R.drawable.blank_heart);
                    isChecked = false;
                }
            }
        });


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_out);
//                linearLayout.startAnimation(animation);
                arrayList.remove(pos);  //listview 끌어와야함
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private void addInterest(int kioskProductID) {
        TargetingService targetService = ServiceGenerator.createService(TargetingService.class);
        targetService.addInterest(kioskProductID, idToken).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("addInterest", "addSuccess");
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Log.i("addInterest", "failure");
            }
        });
    }

    public void addKiosk(KioskProductData kioskProductData) {
        if(arrayList.size() == 0)
            arrayList.add(kioskProductData);
        else {
            for(int i=0; i<arrayList.size(); i++)
                if (kioskProductData.getKioskProductID() == arrayList.get(i).getKioskProductID())
                   return;

            arrayList.add(kioskProductData);
            Collections.reverse(arrayList); //반대로 쌓이게 함
        }
    }

    public void deleteAll() {
        arrayList.clear();
    }
}
