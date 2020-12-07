package com.yazidal.whatsappyzd.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.yazidal.whatsappyzd.MainActivity
import com.yazidal.whatsappyzd.R
import com.yazidal.whatsappyzd.data.User
import com.yazidal.whatsappyzd.util.DATA_USERS
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.edt_email
import kotlinx.android.synthetic.main.activity_sign_up.edt_password
import kotlinx.android.synthetic.main.activity_sign_up.til_email
import kotlinx.android.synthetic.main.activity_sign_up.til_password

class SignUpActivity : AppCompatActivity() {

    private val firebaseDb = FirebaseFirestore.getInstance()
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
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_sign_up)

        setTextChangedListener(edt_email, til_email)
        setTextChangedListener(edt_password, til_password)
        setTextChangedListener(edt_name, til_name)
        setTextChangedListener(edt_phone, til_phone)


        txt_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        btn_signup.setOnClickListener {
            onSignUp()
        }
    }

    private fun setTextChangedListener(edt: EditText, til: TextInputLayout) {
        edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                til.isErrorEnabled = false
            }
        })

    }

    private fun onSignUp() {
        var proceed = true
        if (edt_name.text.isNullOrEmpty()) {
            til_name.error = "Require Name"
            til_name.isErrorEnabled = true
            proceed = false
        }
        if (edt_phone.text.isNullOrEmpty()) {
            til_phone.error = "Require Phone"
            til_phone.isErrorEnabled = true
            proceed = false
        }
        if (edt_email.text.isNullOrEmpty()) {
            til_email.error = "Require Password"
            til_email.isErrorEnabled = true
            proceed = false
        }
        if (edt_password.text.isNullOrEmpty()) {
            til_password.error = "Require Password"
            til_password.isErrorEnabled = true
            proceed = false
        }
        if (proceed) {
            progress_layout_sign.visibility = View.VISIBLE
            firebaseAuth.createUserWithEmailAndPassword(
                edt_email.text.toString(),
                edt_password.text.toString()
            ).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    progress_layout_sign.visibility = View.GONE
                    Toast.makeText(
                        this@SignUpActivity, "Sign Up Eror :${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (firebaseAuth.uid != null) {
                    val email = edt_email.text.toString()
                    val phone = edt_phone.text.toString()
                    val name = edt_name.text.toString()
                    val user = User(email, phone, name, "", "Hello I'm New", "", "")
                    firebaseDb.collection(DATA_USERS)
                        .document(firebaseAuth.uid!!).set(user)
                }
                progress_layout_sign.visibility = View.GONE
            }.addOnFailureListener {
                progress_layout_sign.visibility = View.GONE
                it.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }
}