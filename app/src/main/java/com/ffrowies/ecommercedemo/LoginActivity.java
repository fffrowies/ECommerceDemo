package com.ffrowies.ecommercedemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ffrowies.ecommercedemo.Model.Users;
import com.ffrowies.ecommercedemo.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText edtLoginPhone, edtLoginPassword;
    private CheckBox chbRememberMe;
    private TextView txvAdminLink, txvNotAdminLink;

    private ProgressDialog loadingBar;

    private String parentDBName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btn_login);
        edtLoginPhone = (EditText) findViewById(R.id.edt_login_phone);
        edtLoginPassword = (EditText) findViewById(R.id.edt_login_password);
        chbRememberMe = (CheckBox) findViewById(R.id.chb_remember_me);
        txvAdminLink = (TextView) findViewById(R.id.txv_admin_link);
        txvNotAdminLink = (TextView) findViewById(R.id.txv_not_admin_link);

        Paper.init(this);

        loadingBar = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        txvAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setText("Login Admin");
                txvAdminLink.setVisibility(View.INVISIBLE);
                txvNotAdminLink.setVisibility(View.VISIBLE);
                parentDBName = "Admins";
            }
        });

        txvNotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setText("Login");
                txvAdminLink.setVisibility(View.VISIBLE);
                txvNotAdminLink.setVisibility(View.INVISIBLE);
                parentDBName = "Users";
            }
        });
    }

    private void LoginUser() {
        String phone = edtLoginPhone.getText().toString();
        String password = edtLoginPassword.getText().toString();

        if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please write your phone...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we're checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone, password);
        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if (chbRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.userPhoneKey, phone);
            Paper.book().write(Prevalent.userPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDBName).child(phone).exists())
                {
                    Users usersData = dataSnapshot.child(parentDBName).child(phone).getValue(Users.class);

                    if (usersData.getPassword().equals(password))
                    {
                        if (parentDBName.equals("Admins"))
                        {
                            Toast.makeText(LoginActivity.this, "Welcome Admin, you are logged in successfully...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(LoginActivity.this, AdminAddNewProductActivity.class);
                            startActivity(intent);
                        }
                        else if (parentDBName.equals("Users"))
                        {
                            Toast.makeText(LoginActivity.this, "Logged in Successfully...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Password is incorrect...", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        edtLoginPassword.setText("");
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Account " + phone + " do not exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
