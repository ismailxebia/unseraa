package com.example.helmi.pengaduan.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.helmi.pengaduan.R;
import com.example.helmi.pengaduan.model.UserAccountSettings;
import com.example.helmi.pengaduan.model.UserSettings;
import com.example.helmi.pengaduan.ui.AccountSettingsActivity;
import com.example.helmi.pengaduan.utils.FirebaseMethod;
import com.example.helmi.pengaduan.utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pixplicity.fontview.FontAppCompatTextView;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethod mFirebaseMethod;

    //private ProgressBar mProgressBar;
    private TextView mPosts, mDisplayName, mUsername, mDescription;
    private CircleImageView mProfilePhoto;
    private Context mContext;

    //BindView
    //@BindView(R.id.txtDisplayName)TextView mDisplayName;
    //@BindView(R.id.txtPost)TextView mPosts;
    //@BindView(R.id.txtUsername)TextView mUsername;
    //@BindView(R.id.txtDes)TextView mDescription;
    //@BindView(R.id.iv_photoProfile)CircleImageView mProfilePhoto;
    @BindView(R.id.btn_editProfile)FontAppCompatTextView btnEdit;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        mDisplayName = (TextView)view.findViewById(R.id.txtDisplayName);
        mUsername = (TextView) view.findViewById(R.id.txtUsername);
        mDescription = (TextView) view.findViewById(R.id.txtDes);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.iv_photoProfile);
        mPosts = (TextView) view.findViewById(R.id.txtPost);
        //mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        //mFollowers = (TextView) view.findViewById(R.id.tvFollowers);
        //mFollowing = (TextView) view.findViewById(R.id.tvFollowing);
        mContext = getActivity();
        mFirebaseMethod = new FirebaseMethod(getActivity());
        setupFirebaseAuth();
        Log.d(TAG, "onCreateView: stared.");

        btnEdit.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
            startActivity(intent);
            //getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        return view;
    }

    private void setProfileWidgets(UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());


        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        //UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mDescription.setText(settings.getDescription());
        mPosts.setText(String.valueOf(settings.getPosts()));
        //mProgressBar.setVisibility(View.GONE);


    }

    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();


            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            // ...
        };


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                setProfileWidgets(mFirebaseMethod.getUserSettings(dataSnapshot));

                //retrieve images for the user in question

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