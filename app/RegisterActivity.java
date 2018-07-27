package com.example.helmi.pengaduan.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.helmi.pengaduan.R;
import com.example.helmi.pengaduan.model.User;
import com.example.helmi.pengaduan.utils.FirebaseMethod;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.etTelp)EditText etTelp;
    @BindView(R.id.etNama)EditText etNama;
    @BindView(R.id.etPassword)EditText etPass;
    @BindView(R.id.login_btn)LinearLayout btnSignUp;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mContext = this;

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //Ambil Json User
        final DatabaseReference table_user = firebaseDatabase.getReference("user");

        btnSignUp.setOnClickListener(view -> {
            final ProgressDialog mDialog = new ProgressDialog(RegisterActivity.this);
            mDialog.setMessage("Please Wait");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(etTelp.getText().toString()).exists())
                    {
                        mDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "No Telpon sudah di gunakan", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mDialog.dismiss();
                        User user = new User(etNama.getText().toString(),etPass.getText().toString());
                        table_user.child(etTelp.getText().toString()).setValue(user);
                        Toast.makeText(RegisterActivity.this, "SignUp Berhasil", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

    }
}
