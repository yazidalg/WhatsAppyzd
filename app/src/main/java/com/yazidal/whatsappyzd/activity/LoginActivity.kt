package com.yazidal.whatsappyzd.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.yazidal.whatsappyzd.MainActivity
import com.yazidal.whatsappyzd.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)//Menghilangkan Action Bar di menu setting
        setContentView(R.layout.activity_login)


        setTextChangeListener(edt_email, til_email)
        setTextChangeListener(edt_password, til_password)

        btn_login.setOnClickListener {
            onLogin()
        }
        txt_signup.setOnClickListener {
            onSignUp()
        }
    }

    private fun setTextChangeListener(edt: TextInputEditText?, til: TextInputLayout?) {
        edt?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                til?.isErrorEnabled = false
            }

        })
    }

    private fun onSignUp() {
        startActivity(Intent(this, SignUpActivity::class.java))

    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener { firebaseAuthListener }
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.addAuthStateListener { firebaseAuthListener }
    }

    private fun onLogin() {
        var proces = true
        if (edt_email.text.isNullOrEmpty()) {
            til_email.error = "Membutuhkan Email"
            til_email.isErrorEnabled = true
            proces = false
        }
        if (edt_password.text.isNullOrEmpty()) {
            til_password.error = "Membutuhkan Email"
            til_password.isErrorEnabled = true
            proces = false
        }
        if (proces) {
            progress_layout.visibility = View.VISIBLE
            firebaseAuth.signInWithEmailAndPassword(
                edt_email.text.toString(),
                edt_password.text.toString()
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progress_layout.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Login Kakak :) ${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    )
                }
            }.addOnFailureListener { e ->
                progress_layout.visibility = View.GONE
                e.printStackTrace()
            }
        }
    }
}