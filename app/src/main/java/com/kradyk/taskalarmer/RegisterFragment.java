package com.kradyk.taskalarmer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }
    Button btnreg;
    EditText edname;
    EditText edemail;
    EditText edpass;
    EditText edrepass;
    ProgressBar bar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewout = inflater.inflate(R.layout.fragment_register, container, false);
       btnreg = viewout.findViewById(R.id.btn_register);
       edname = viewout.findViewById(R.id.et_name);
        edemail = viewout.findViewById(R.id.et_email);
        edpass = viewout.findViewById(R.id.et_password);
        edrepass = viewout.findViewById(R.id.et_repassword);
        bar = viewout.findViewById(R.id.barreg);


        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edemail.getText().toString();
                if ((edpass.getText().toString().length()<6)||(edname.getText().toString().equals(""))||(!email.contains("."))||(edpass.getText().toString().equals(""))||(!edrepass.getText().toString().equals(edpass.getText().toString()))||(!email.contains("@"))){
                    if (edname.getText().toString().equals(""))
                        Toast.makeText(getContext(), R.string.name, Toast.LENGTH_SHORT).show();
                    else if (edpass.getText().toString().length()<6)
                        Toast.makeText(getContext(), R.string.passshort, Toast.LENGTH_SHORT).show();
                    else if ((!email.contains("@"))||!email.contains("."))
                        Toast.makeText(getContext(), R.string.email, Toast.LENGTH_SHORT).show();
                    else if (edpass.getText().toString().equals(""))
                        Toast.makeText(getContext(), R.string.inppass, Toast.LENGTH_SHORT).show();
                    else if (!edrepass.getText().toString().equals(edpass.getText().toString()))
                        Toast.makeText(getContext(), R.string.notequalpass, Toast.LENGTH_SHORT).show();}
                    else{
                        bar.setVisibility(View.VISIBLE);
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.createUserWithEmailAndPassword(email, edpass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                bar.setVisibility(View.GONE);
                                if (task.isSuccessful()){
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getContext(), getResources().getString(R.string.Hello)+" "+ edname.getText().toString()+getResources().getString(R.string.checkemail), Toast.LENGTH_SHORT ).show();
                                                Log.d("TAG", "createUserWithEmail:success");
                                            }else{
                                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT ).show();

                                            }
                                        }
                                    });

                                }
                            }
                        });
                }
        };
        });
        return viewout;


}}