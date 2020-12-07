package com.yazidal.whatsappyzd.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yazidal.whatsappyzd.MainActivity
import com.yazidal.whatsappyzd.R
import com.yazidal.whatsappyzd.adapter.ChatAdapter
import com.yazidal.whatsappyzd.data.Chat
import com.yazidal.whatsappyzd.listener.ChatClickListener
import com.yazidal.whatsappyzd.listener.FailureCallback
import com.yazidal.whatsappyzd.util.DATA_CHATS
import com.yazidal.whatsappyzd.util.DATA_USERS
import com.yazidal.whatsappyzd.util.DATA_USER_CHATS
import kotlinx.android.synthetic.main.fragment_charts.*

class ChatFragment : Fragment(), ChatClickListener {

    private var failureCallback: FailureCallback? = null
    private var chatsAdapter = ChatAdapter(arrayListOf())
    private val firebaseDb = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (userId.isNullOrEmpty()) {
            failureCallback?.onUserEror()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatsAdapter.setOnItemClickListener(this)
        rv_chats.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = chatsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        firebaseDb.collection(DATA_USERS).document(userId!!)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException == null) {
                    refreshChats() // memperbarui data jika tidak terdapat error
                }
            }
    }

    override fun onChatClicked(
        name: String?,
        otherUserId: String?,
        chatsImageUrl: String?,
        chatsName: String?
    ) {
        Toast.makeText(context, "$name clicked", Toast.LENGTH_SHORT).show()
    }

    fun newChat(partnerId: String) {
        firebaseDb.collection(DATA_USERS).document(userId!!).get()
            .addOnSuccessListener { userDocument ->
                val userChatsPartner = hashMapOf<String, String>()
                if (userDocument[DATA_USER_CHATS] != null && userDocument[DATA_USER_CHATS] is HashMap<*, *>) {
                    val userDocumentMap = userDocument[DATA_USER_CHATS] as HashMap<String, String>
                    if (userDocumentMap.containsKey(partnerId)){
                        return@addOnSuccessListener
                    }else{
                        userChatsPartner.putAll(userDocumentMap)
                    }
                }
                firebaseDb.collection(DATA_USERS)
                    .document(partnerId)
                    .get()
                    .addOnSuccessListener { partnerDocument ->
                        val partnerChatPartners = hashMapOf<String, String>()
                        if (partnerDocument[DATA_USER_CHATS] != null &&
                            partnerDocument[DATA_USER_CHATS] is HashMap<*, *>
                        ) {
                            val partnerDocumentMap =
                                partnerDocument[DATA_USER_CHATS] as HashMap<String, String>
                            partnerChatPartners.putAll(partnerDocumentMap)
                        }
                        val chatParticipants = arrayListOf(userId, partnerId)
                        val chat = Chat(chatParticipants)
                        val chatRef = firebaseDb.collection(DATA_CHATS).document()
                        val userRef = firebaseDb.collection(DATA_USERS).document(userId)
                        val partnerRef =
                            firebaseDb.collection(DATA_USERS).document(partnerId)
                        userChatsPartner[partnerId] = chatRef.id
                        partnerChatPartners[userId] = chatRef.id
                        val batch = firebaseDb.batch()
                        batch.set(chatRef, chat)
                        batch.update(userRef, DATA_USER_CHATS, userChatsPartner)
                        batch.update(partnerRef, DATA_USER_CHATS, partnerChatPartners)
                        batch.commit()
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
    private fun refreshChats() {
        firebaseDb.collection(DATA_USERS).document(userId!!).get()
            .addOnSuccessListener {
                if (it.contains(DATA_USER_CHATS)) {
                    val partners = it[DATA_USER_CHATS]
                    val chats = arrayListOf<String>()
                    for (partner in (partners as HashMap<String, String>).keys) {
                        if (partners[partner] != null) { // melakukan pengulangan untuk memperbarui
                            chats.add(partners[partner]!!) // data dalam userChats
                        }
                    }
                    chatsAdapter.updateChats(chats) // hapus data sebelum memperbarui data
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    fun setFailureCallbackListener(listener: MainActivity) {
        failureCallback = listener
    }
}
