package com.example.helmi.pengaduan.ui;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.example.helmi.pengaduan.MainActivity;
import com.example.helmi.pengaduan.R;
import com.example.helmi.pengaduan.utils.FirebaseMethod;
import com.example.helmi.pengaduan.utils.MorphTransform;
import com.example.helmi.pengaduan.utils.Permissions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener,OnMapReadyCallback{
    private static final String TAG = "ShareActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethod mFirebaseMethods;

    //vars
    private String mAppend = "file:/";
    private int imageCount = 0;
    private String imgUrl;
    private Bitmap bitmap;
    private Intent intent;

    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private static final int SECOND_ACTIVITY_RESULT_CODE = 0;

    @BindView(R.id.rl_btn)
    RelativeLayout rlButton;
    @BindView(R.id.iv_ktp)
    SelectableRoundedImageView ivImage2;
    @BindView(R.id.linearPhotoKtp)
    LinearLayout iconImage;
    @BindView(R.id.tv_kategori)
    TextView tvKategori;
    @BindView(R.id.pesanText)
    EditText mCaption;
    @BindView(R.id.lokasiText)
    EditText mLocationText;
    @BindView(R.id.submit_btn)
    LinearLayout btnSubmit;

    private Context mContext = ShareActivity.this;

    private GoogleMap mMap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: started.");
        mFirebaseMethods = new FirebaseMethod(ShareActivity.this);
        setupFirebaseAuth();

        init();
    }

    @SuppressLint("RestrictedApi")
    private void init() {
        //Maps SetUp
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(ShareActivity.this);

        //ImagePicker SetUp
        rlButton.setOnClickListener(view -> {
            BSImagePicker pickerDialog = new BSImagePicker.Builder("com.example.helmi.pengaduan.ui.fileprovider")
                    .setSpanCount(2) //Default: 3. This is the number of columns
                    .build();
            pickerDialog.show(getSupportFragmentManager(), "picker");
        });
        if(checkPermissionsArray(Permissions.PERMISSIONS)){
            Toast.makeText(mContext, "Success Permissions", Toast.LENGTH_SHORT).show();
            //
        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }

        //Submit SetUp
        btnSubmit.setOnClickListener(view -> {
            Log.d(TAG, "onClick: navigating to the final share screen.");
            //upload the image to firebase
            Toast.makeText(ShareActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
            String kategori = tvKategori.getText().toString();
            String caption = mCaption.getText().toString();
            String locationText = mLocationText.getText().toString();
            mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), kategori,caption,locationText, imageCount, imgUrl,null);
        });

        //Kategori SetUp
        LinearLayout btn_kategori = (LinearLayout)findViewById(R.id.btn_kategori);
        btn_kategori.setOnClickListener(view -> {
            Intent login = new Intent(ShareActivity.this, DialogKategori.class);
            MorphTransform.addExtras(login,
                    ContextCompat.getColor(ShareActivity.this, R.color.grey),
                    getResources().getDimensionPixelSize(R.dimen.dialog_corners));
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation
                    (ShareActivity.this, btn_kategori, getString(R.string.transition_pantau_morph));
            startActivityForResult(login,SECOND_ACTIVITY_RESULT_CODE,options.toBundle());});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data ) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == SECOND_ACTIVITY_RESULT_CODE) {
            String returnString = data.getStringExtra("keyname");
            tvKategori.setText(returnString);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng madrid = new LatLng(40.428462, -3.704952);

        mMap.addMarker(new MarkerOptions().position(madrid).title("Madrid"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid,17));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(madrid)             // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    //GetImage
    @Override
    public void onSingleImageSelected(Uri uri) {
        iconImage.setVisibility(View.GONE);
        Glide.with(ShareActivity.this).load(uri).into(ivImage2);

        /*
        Picasso.get()
                .load(uri)
                .into(ivImage2);
        */
    }

    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     * Check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been verified
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count: " + imageCount);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: " + imageCount);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
