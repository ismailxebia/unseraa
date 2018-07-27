package com.example.helmi.pengaduan.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.helmi.pengaduan.R;
import com.example.helmi.pengaduan.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_btn)
    LinearLayout btnSignIn;
    @BindView(R.id.btn_signup)
    LinearLayout btnSignUp;

    @BindView(R.id.ed_hp)
    EditText edHp;
    @BindView(R.id.ed_pass)
    EditText edPass;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mContext = this;

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //Ambil Json User
        final DatabaseReference table_user = firebaseDatabase.getReference("user");

        btnSignUp.setOnClickListener(view -> {
            startActivity(new Intent(mContext, RegisterActivity.class));
        });
        btnSignIn.setOnClickListener(view -> {
            //startActivity(new Intent(mContext, RegisterActivity.class));
            final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setMessage("Please Wait");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Cek Jika tidak tersedia di database
                    if (dataSnapshot.child(edHp.getText().toString()).exists()) {
                        mDialog.dismiss();
                        User user = dataSnapshot.child(edHp.getText().toString()).getValue(User.class);
                        if (user.getPassword().equals(edPass.getText().toString())) {
                            Toast.makeText(LoginActivity.this, "SignIn Succesfull!", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        } else {
                            Toast.makeText(LoginActivity.this, "SignIn Failed!", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "User tidak terdaftar", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
    }
}
