package com.kradyk.taskalarmer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.forms.sti.progresslitieigb.Inteface.IProgressLoadingIGB
import com.forms.sti.progresslitieigb.Model.JSetting
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Suppress("DEPRECATION", "NAME_SHADOWING")
class RegisterFragment : Fragment() {
    private lateinit var btnreg: Button
    private lateinit var edname: EditText
    private lateinit var edemail: EditText
    private lateinit var edpass: EditText
    private lateinit var edrepass: EditText
    private lateinit var imgGoogle: ImageButton
    private lateinit var mAuth: FirebaseAuth
    private var googleSignInClient: GoogleSignInClient? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ProgressLoadingJIGB.setupLoading = IProgressLoadingIGB { setup: JSetting ->
            setup.srcLottieJson = R.raw.loading_a // Your Source JSON Lottie
            setup.timer = 0 // Time of live for progress.
            setup.width = 1000 // Optional
            setup.hight = 1000 // Optional

        }
        val viewout = inflater.inflate(R.layout.fragment_register, container, false)
        btnreg = viewout.findViewById(R.id.btn_register)
        edname = viewout.findViewById(R.id.et_name)
        edemail = viewout.findViewById(R.id.et_email)
        edpass = viewout.findViewById(R.id.et_password)
        edrepass = viewout.findViewById(R.id.et_repassword)
        imgGoogle = viewout.findViewById(R.id.googleauth)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("665824354496-19ucjcv4e6dvctn1gade7rvdv9du5j98.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient((context)!!, googleSignInOptions)
        googleSignInClient!!.signOut()
        imgGoogle.setOnClickListener {
            ProgressLoadingJIGB.startLoading(it.context)
            mAuth = FirebaseAuth.getInstance()
            val intent = googleSignInClient!!.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
        btnreg.setOnClickListener {
            val email = edemail.text.toString()
            if ((edpass.text.toString().length < 6) || ((edname.text
                    .toString() == "")) || (!email.contains(".")) || ((edpass.text
                    .toString() == "")) || (edrepass.text.toString() != edpass.text
                    .toString()) || (!email.contains("@"))
            ) {
                if ((edname.text.toString() == "")) Toast.makeText(
                    context,
                    R.string.name,
                    Toast.LENGTH_SHORT
                ).show() else if (edpass.text.toString().length < 6) Toast.makeText(
                    context, R.string.passshort, Toast.LENGTH_SHORT
                )
                    .show() else if ((!email.contains("@")) || !email.contains(".")) Toast.makeText(
                    context, R.string.email, Toast.LENGTH_SHORT
                ).show() else if ((edpass.text.toString() == "")) Toast.makeText(
                    context, R.string.inppass, Toast.LENGTH_SHORT
                ).show() else if (edrepass.text.toString() != edpass.text
                        .toString()
                ) Toast.makeText(
                    context, R.string.notequalpass, Toast.LENGTH_SHORT
                ).show()
            } else {
                ProgressLoadingJIGB.startLoading(it.context)
                val mAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email, edpass.text.toString())
                    .addOnCompleteListener { task ->
                        ProgressLoadingJIGB.finishLoadingJIGB(it.context)
                        if (task.isSuccessful) {
                            mAuth.currentUser!!.sendEmailVerification()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            resources.getString(R.string.Hello) + " " + edname.text
                                                .toString() + resources.getString(R.string.checkemail),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d("TAG", "createUserWithEmail:success")
                                    } else {
                                        Toast.makeText(
                                            context,
                                            task.exception!!.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    }
            }
        }
        return viewout
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = accountTask.getResult(
                    ApiException::class.java
                )
                firebaseWithGoogleAccount(account)
            } catch (c: Exception) {
                ProgressLoadingJIGB.finishLoadingJIGB(context)
                c.printStackTrace()
            }
        }
    }

    private fun firebaseWithGoogleAccount(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        println("hello")
        mAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val firebaseUser = mAuth.currentUser
                firebaseUser!!.uid
                firebaseUser.email
                if (authResult.additionalUserInfo!!.isNewUser) {
                    Toast.makeText(context, "Account created", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Exist", Toast.LENGTH_SHORT).show()
                }
                ProgressLoadingJIGB.finishLoadingJIGB(context)
                requireActivity().finish()
            }
            .addOnFailureListener {                         ProgressLoadingJIGB.finishLoadingJIGB(context)
            }
    }

    companion object {
        const val RC_SIGN_IN = 100
    }
}