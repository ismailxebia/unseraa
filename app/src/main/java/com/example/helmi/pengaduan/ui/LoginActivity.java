package com.example.helmi.pengaduan.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.helmi.pengaduan.MainActivity;
import com.example.helmi.pengaduan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by User on 6/19/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.btn_signup)LinearLayout btnSignUp;
    @BindView(R.id.login_btn)LinearLayout btnLogin;
    @BindView(R.id.ed_email)EditText etEmail;
    @BindView(R.id.ed_pass)EditText etPass;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mContext = LoginActivity.this;
        Log.d(TAG, "onCreate: started.");

        //mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        setupFirebaseAuth();
        init();

    }

    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string if null.");

        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    private void init(){

        //initialize the button for logging in
        btnLogin.setOnClickListener(view -> {
            Log.d(TAG, "onClick: attempting to log in.");

            String email = etEmail.getText().toString();
            String password = etPass.getText().toString();

            if(isStringNull(email) && isStringNull(password)){
                Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
            }else{
                mProgressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());

                                Toast.makeText(LoginActivity.this, getString(R.string.auth_faileds),
                                        Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                            }
                            else{
                                Log.d(TAG, "signInWithEmail: successful login");
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_success),
                                        Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(LoginActivity.this, MainPage.class);
                                startActivity(intent);
                                finish();
                            }

                            // ...
                        });
            }
        });

        btnSignUp.setOnClickListener(view -> {
            Log.d(TAG, "onClick: navigating to register screen");
            Intent intent = new Intent(LoginActivity.this, NewRegister.class);
            startActivity(intent);
        });

         /*
         If the user is logged in then navigate to HomeActivity and call 'finish()'
          */
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, MainPage.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

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
