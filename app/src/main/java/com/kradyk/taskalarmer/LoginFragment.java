package com.kradyk.taskalarmer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executor;


public class LoginFragment extends Fragment {


    public LoginFragment() {
    }
    Button btnlog;
    EditText edemail;
    EditText edpass;
    ProgressBar bar;
    TextView forgot;

    ImageButton imgGoogle;
    FirebaseAuth mAuth;
    static final int RC_SIGN_IN =100;
    GoogleSignInClient googleSignInClient;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewout = inflater.inflate(R.layout.fragment_login, container, false);
        btnlog = viewout.findViewById(R.id.btn_login);
        edemail = viewout.findViewById(R.id.et_email);
        edpass = viewout.findViewById(R.id.et_password);
        bar = viewout.findViewById(R.id.barlogin);
        forgot = viewout.findViewById(R.id.forgot);
        imgGoogle = viewout.findViewById(R.id.googleauth);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("665824354496-19ucjcv4e6dvctn1gade7rvdv9du5j98.apps.googleusercontent.com")
                        .requestEmail()
                                .build();
        googleSignInClient   = GoogleSignIn.getClient(getContext(),googleSignInOptions);

        imgGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                mAuth = FirebaseAuth.getInstance();
             Intent intent = googleSignInClient.getSignInIntent();
             startActivityForResult(intent,RC_SIGN_IN);

            }
        });





        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseWithGoogleAccount(account);
            }catch (Exception c){
                c.printStackTrace();
            }
        }
    }

    private void firebaseWithGoogleAccount(GoogleSignInAccount account) {
         AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        System.out.println("hello");
         mAuth.signInWithCredential(credential)
                 .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                     @Override
                     public void onSuccess(AuthResult authResult) {

                         FirebaseUser firebaseUser = mAuth.getCurrentUser();
                         String uid = firebaseUser.getUid();
                         String email = firebaseUser.getEmail();
                         if (authResult.getAdditionalUserInfo().isNewUser()){
                             Toast.makeText(getContext(), "Account created", Toast.LENGTH_SHORT).show();
                         }else{
                             Toast.makeText(getContext(), "Exist", Toast.LENGTH_SHORT).show();
                         }
                         bar.setVisibility(View.GONE);
                        getActivity().finish();
                     }
                 })
                 .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         bar.setVisibility(View.GONE);

                     }
                 });
    }
}
