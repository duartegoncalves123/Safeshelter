package com.example.safeshelter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText mTextName, mTextChildName, mTextEmail, mTextPassword;
    EditText mTextCnfPassword;
    Button mButtonRegister;

    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("SafeShelter");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //init
        mTextName = findViewById(R.id.editText_name);
        mTextChildName = findViewById(R.id.editText_childName);
        mTextEmail = findViewById(R.id.editText_email);
        mTextPassword = findViewById(R.id.editText_password);
        mTextCnfPassword = findViewById(R.id.editText_cnf_password);
        mButtonRegister = findViewById(R.id.button_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("A registar utilizador...");

        //register btn click
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input email,password
                String email = mTextEmail.getText().toString().trim();
                String name = mTextName.getText().toString().trim();
                String childName = mTextChildName.getText().toString().trim();
                String password = mTextPassword.getText().toString().trim();
                String cnfPassword = mTextCnfPassword.getText().toString().trim();
                //validate
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //set error to email editText
                    mTextEmail.setError("Email invalido");
                    mTextEmail.setFocusable(true);
                } else if (name.length() == 0) {
                    //set error to name editText
                    mTextName.setError("O nome do pai deve ser inserido");
                    mTextName.setFocusable(true);
                } else if (childName.length() == 0) {
                    //set error to child's name editText
                    mTextChildName.setError("O nome do filho deve ser inserido");
                    mTextChildName.setFocusable(true);
                } else if (password.length() < 6) {
                    //set error to password editText
                    mTextPassword.setError("Password deve ter pelo menos 6 caracteres");
                    mTextPassword.setFocusable(true);
                } else if (!(password.equals(cnfPassword))) {
                    //set error to confirmation password editText
                    mTextCnfPassword.setError("Password de confirmação não coinside com a password");
                } else {
                    registerUser(email, password);
                }
            }
        });


    }

    private void registerUser(String email, String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();

                            FirebaseUser user = mAuth.getCurrentUser();
                            //Get user email and user id from auth
                            String email = user.getEmail();
                            String uid = user.getUid();
                            //When user is registered, store user info in firebase realtime database
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //put info in hashmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("parentName", mTextName.getText().toString().trim());
                            hashMap.put("childrenName", mTextChildName.getText().toString().trim());
                            hashMap.put("parentalCode", "");
                            //firebase database instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //path to store user data named "Users"
                            DatabaseReference reference = database.getReference("Users");
                            //put data with hashmap in database
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(RegisterActivity.this, "Registado...\n" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, ChooseParentalCode.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Falha no registo.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
