package com.kradyk.taskalarmer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Suppress("DEPRECATION")
class LoginFragment : Fragment() {
    private lateinit var btnlog: Button
    private lateinit var edemail: EditText
    private lateinit var edpass: EditText
    private lateinit var bar: ProgressBar
    private lateinit var forgot: TextView
    private lateinit var imgGoogle: ImageButton
    private var mAuth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewout = inflater.inflate(R.layout.fragment_login, container, false)
        btnlog = viewout.findViewById(R.id.btn_login)
        edemail = viewout.findViewById(R.id.et_email)
        edpass = viewout.findViewById(R.id.et_password)
        bar = viewout.findViewById(R.id.barlogin)
        forgot = viewout.findViewById(R.id.forgot)
        imgGoogle = viewout.findViewById(R.id.googleauth)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("665824354496-19ucjcv4e6dvctn1gade7rvdv9du5j98.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
        imgGoogle.setOnClickListener {
            bar.visibility = View.VISIBLE
            mAuth = FirebaseAuth.getInstance()
            val intent = googleSignInClient!!.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
        forgot.setOnClickListener {
            mAuth = FirebaseAuth.getInstance()
            val email = edemail.text.toString()
            if (!email.contains("@") || !email.contains(".")) Toast.makeText(
                context,
                R.string.email,
                Toast.LENGTH_SHORT
            ).show() else {
                mAuth!!.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        btnlog.setOnClickListener {
            val email = edemail.text.toString()
            if (!email.contains(".") || edpass.text.toString() == "" || !email.contains("@")) {
                if (!email.contains("@") || !email.contains(".")) Toast.makeText(
                    context,
                    R.string.email,
                    Toast.LENGTH_SHORT
                ).show() else Toast.makeText(
                    context, R.string.inppass, Toast.LENGTH_SHORT
                ).show()
            } else {
                bar.visibility = View.VISIBLE
                val mAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email, edpass.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "loginUserWithEmail:success")
                            if (mAuth.currentUser!!.isEmailVerified) {
                                if (activity != null) {
                                    requireActivity().finish()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    resources.getString(R.string.verify),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Log.d("TAG", "loginUserWithEmail:fail")
                            Toast.makeText(context, "Wrong", Toast.LENGTH_SHORT).show()
                        }
                        bar.visibility = View.GONE
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
                c.printStackTrace()
            }
        }
    }

    private fun firebaseWithGoogleAccount(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        println("hello")
        mAuth!!.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val firebaseUser = mAuth!!.currentUser
                firebaseUser!!.uid
                firebaseUser.email
                if (authResult.additionalUserInfo!!.isNewUser) {
                    Toast.makeText(context, "Account created", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Exist", Toast.LENGTH_SHORT).show()
                }
                bar.visibility = View.GONE
                requireActivity().finish()
            }
            .addOnFailureListener { bar.visibility = View.GONE }
    }

    companion object {
        const val RC_SIGN_IN = 100
    }
}