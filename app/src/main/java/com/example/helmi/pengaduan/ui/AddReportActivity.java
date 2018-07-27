package com.example.helmi.pengaduan.ui;


import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.helmi.pengaduan.R;
import com.example.helmi.pengaduan.utils.MorphTransform;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by Admin on 6/5/2018.
 */

public class AddReportActivity extends AppCompatActivity {
    LinearLayout btn_kategori;

    @BindView(R.id.tv_kategori)
    TextView tvKategori;
    private static final int SECOND_ACTIVITY_RESULT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        ButterKnife.bind(this);

        LinearLayout btn_kategori = (LinearLayout)findViewById(R.id.btn_kategori);
        btn_kategori.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Intent login = new Intent(AddReportActivity.this, DialogKategori.class);
                MorphTransform.addExtras(login,
                        ContextCompat.getColor(AddReportActivity.this, R.color.grey),
                        getResources().getDimensionPixelSize(R.dimen.dialog_corners));
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation
                        (AddReportActivity.this, btn_kategori, getString(R.string.transition_pantau_morph));
                startActivityForResult(login,SECOND_ACTIVITY_RESULT_CODE,options.toBundle());}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data ) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == SECOND_ACTIVITY_RESULT_CODE) {
            String returnString = data.getStringExtra("keyname");
            tvKategori.setText(returnString);
        }
    }
}
