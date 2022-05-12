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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }
    Button btnlog;
    EditText edemail;
    EditText edpass;
    ProgressBar bar;
    TextView forgot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewout = inflater.inflate(R.layout.fragment_login, container, false);
        btnlog = viewout.findViewById(R.id.btn_login);
        edemail = viewout.findViewById(R.id.et_email);
        edpass = viewout.findViewById(R.id.et_password);
        bar = viewout.findViewById(R.id.barlogin);
        forgot = viewout.findViewById(R.id.forgot);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String email = edemail.getText().toString();
                if ((!email.contains("@"))||!email.contains("."))
                    Toast.makeText(getContext(), R.string.email, Toast.LENGTH_SHORT).show();
                else{
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
                                }
                            }});}
            }
        });


        btnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edemail.getText().toString();
                if ((!email.contains("."))||(edpass.getText().toString().equals(""))||(!email.contains("@"))){
                    if ((!email.contains("@"))||!email.contains("."))
                        Toast.makeText(getContext(), R.string.email, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), R.string.inppass, Toast.LENGTH_SHORT).show();
                    }
                else{
                    bar.setVisibility(View.VISIBLE);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(email, edpass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "loginUserWithEmail:success");
                                        if (mAuth.getCurrentUser().isEmailVerified()){
                                        if(getActivity() != null) {
                                            getActivity().finish();
                                        }}else{
                                            Toast.makeText(getContext(), getResources().getString(R.string.verify), Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Log.d("TAG", "loginUserWithEmail:fail");
                                        Toast.makeText(getContext(),"Wrong", Toast.LENGTH_SHORT).show();
                                    }
                                bar.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });
        return viewout;
    }

}