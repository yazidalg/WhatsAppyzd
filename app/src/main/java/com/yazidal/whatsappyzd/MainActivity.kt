package com.yazidal.whatsappyzd

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.yazidal.whatsappyzd.activity.ContactActivity
import com.yazidal.whatsappyzd.activity.LoginActivity
import com.yazidal.whatsappyzd.activity.ProfileActivity
import com.yazidal.whatsappyzd.adapter.SectionPagerAdapter
import com.yazidal.whatsappyzd.fragment.ChatFragment
import com.yazidal.whatsappyzd.listener.FailureCallback
import com.yazidal.whatsappyzd.util.DATA_USERS
import com.yazidal.whatsappyzd.util.DATA_USER_PHONE
import com.yazidal.whatsappyzd.util.PERMISSION_REQUEST_READ_CONTACT
import com.yazidal.whatsappyzd.util.REQUEST_NEW_CHATS
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainActivity : AppCompatActivity(), FailureCallback {

    companion object {
        const val PARAM_NAME = "name"
        const val PARAM_PHONE = "phone"
    }

    private val firebaseDb = FirebaseFirestore.getInstance()
    private val chatsFragment = ChatFragment()

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var sectionPagerAdapter: SectionPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatsFragment.setFailureCallbackListener(this)
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        resizeTabs()
        tabs.getTabAt(1)?.select()

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) { // ketika TabItem diklik atau TabItem aktif FloatingActionButton
                    0 -> fab.hide() // akan hilang pada Tab pertama
                    1 -> fab.show() // tetap ditampilkan pada Tab kedua
                    2 -> fab.hide() // akan hilang pada Tab ketiga
                }
            }

        })

        fab.setOnClickListener { // ketika FloatingActionButton diklik
            onNewChat()
        }

        setSupportActionBar(toolbar)
        sectionPagerAdapter = SectionPagerAdapter(supportFragmentManager)
        container.adapter = sectionPagerAdapter
    }

    private fun onNewChat() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) { // pengecheckan izin dari aplikasi // izin tidak diberikan
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Contacts Permission")
                    .setMessage("This App Requires Access to Your Contacts to Initiation A Concersation")
                    .setPositiveButton("Yes") { dialogInterface, which ->
                        requestContactPermission()
                    }
                    .setNegativeButton("No") { dialog, which ->
                    }
                    .show()
            } else {
                requestContactPermission()
            }
        } else {
            startNewActivity()
        }

    }

    private fun startNewActivity() {
        startActivityForResult(Intent(this, ContactActivity::class.java), REQUEST_NEW_CHATS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_NEW_CHATS -> {
                    val name = data?.getStringExtra(PARAM_NAME) ?: ""
                    val phone = data?.getStringExtra(PARAM_PHONE) ?: ""
                    checkNewChatUser(name, phone)
                }
            }
        }
    }

    private fun checkNewChatUser(name: String, phone: String) {
        if (!name.isNullOrEmpty() && !phone.isNullOrEmpty()) {
            firebaseDb.collection(DATA_USERS)
                .whereEqualTo(DATA_USER_PHONE, phone)
                .get()
                .addOnSuccessListener {
                    if (it.documents.size > 0) {
                        chatsFragment.newChat(it.documents[0].id)
                    } else {
                        AlertDialog.Builder(this).setTitle("User Not Found")
                            .setMessage("$name does not have account. Send them an SMS to install this app")
                            .setPositiveButton("OK") { dialog, which ->
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data =
                                    Uri.parse("sms:$phone") // query untuk mengirim pesan intent
                                intent.putExtra(
                                    "sms_body",
                                    "Hi I'm using this new cool WhatsAppClone app. You should install it too so we can chat there." // menambahkan text untuk isi pesan
                                )
                                startActivity(intent)
                            }
                            .setNegativeButton("Cancel", null)
                            .setCancelable(false)
                            .show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this,"An error occured. Please try again later",Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        }
    }

    private fun requestContactPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS), // meminta izin untuk membaca
            PERMISSION_REQUEST_READ_CONTACT // kontak dalam ponsel
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_CONTACT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startNewActivity()
            }
        }
    }

    private fun resizeTabs() {
        val layout = (tabs.getChildAt(0) as LinearLayout).getChildAt(0) as LinearLayout
        val layoutParams = layout.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 0.4f
        layout.layoutParams = layoutParams
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_wa, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> onLogout()
            R.id.profile -> onProfile()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }


    private fun onLogout() {
        firebaseAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    class PlaceHolderFragment : Fragment() {

        companion object {
            private val ARG_SECTION_MEMBER = "Section number"

            fun newIntent(sectionNumber: Int): PlaceHolderFragment {
                val fragment = PlaceHolderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_MEMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_main, container, false)

            view.sectionText.text = "Test ${arguments?.getInt(ARG_SECTION_MEMBER)}"

            return view
        }
    }
    override fun onUserEror(){
        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}