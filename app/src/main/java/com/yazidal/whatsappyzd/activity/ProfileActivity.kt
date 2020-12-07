package com.yazidal.whatsappyzd.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.yazidal.whatsappyzd.R
import com.yazidal.whatsappyzd.data.User
import com.yazidal.whatsappyzd.util.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (userId.isNullOrEmpty()) {
            finish()
        }
        progress_layout.setOnTouchListener { v, event -> true }
        btn_apply.setOnClickListener {
            onApply()
        }
        btn_delete_account.setOnClickListener {
            onDelete()
        }
        populateInfo()

    }


    private fun populateInfo() {
        progress_layout.visibility = View.VISIBLE
        firebaseDB.collection(DATA_USERS).document(userId!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                edt_name_profile.setText(user?.name, TextView.BufferType.EDITABLE)
                edt_email_profile.setText(user?.email, TextView.BufferType.EDITABLE)
                edt_phone_profile.setText(user?.phone, TextView.BufferType.EDITABLE)
                progress_layout.visibility = View.GONE
            }.addOnFailureListener { e ->
                e.printStackTrace()
                finish()
            }
    }

    private fun onDelete() {
        progress_layout.visibility = View.VISIBLE
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Its will delete your account, Are you sure?")
            .setPositiveButton("Yes") { dialog, which ->
                firebaseDB.collection(DATA_USERS).document(userId!!).delete()
                firebaseStorage.child(DATA_IMAGES).child(userId).delete()
                firebaseAuth.currentUser?.delete()?.addOnSuccessListener {
                    finish()
                }
                    ?.addOnFailureListener {
                        finish()
                    }
                progress_layout.visibility = View.GONE
                Toast.makeText(this, "Profile Deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("No") { dialog, which ->
                progress_layout.visibility = View.GONE
            }
            .setCancelable(false).show()
    }

    private fun onApply() {
        progress_layout.visibility = View.GONE
        val name = edt_name_profile.text.toString()
        val email = edt_email_profile.text.toString()
        val phone = edt_phone_profile.text.toString()
        val map = HashMap<String, Any>() // menjadi String lalu ditampung di variabel
        map[DATA_USER_NAME] = name  // yang nantinya di koleksi oleh HashMap
        map[DATA_USER_EMAIL] = email  // untuk kemudian dikirimkan ke table user
        map[DATA_USER_PHONE] = phone  // di database Firebase sebagai pembaruan

        firebaseDB.collection(DATA_USERS).document(userId!!).update(map) //perintah update
            .addOnSuccessListener {
                Toast.makeText(this, "Update Succesfully", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
                progress_layout.visibility = View.GONE
            }
    }
}