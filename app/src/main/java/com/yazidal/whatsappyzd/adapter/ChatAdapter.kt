package com.yazidal.whatsappyzd.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yazidal.whatsappyzd.R
import com.yazidal.whatsappyzd.listener.ChatClickListener
import com.yazidal.whatsappyzd.util.populateImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat.*

class ChatAdapter(val chats: ArrayList<String>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private var chatClickListener: ChatClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_chat, parent, false
        )
    )
    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bindItem(chats[position], chatClickListener)
    }

    class ChatViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        fun bindItem(chatId: String, listener: ChatClickListener?) {
            populateImage(img_chats.context, "", img_chats, R.drawable.ic_user)
            txt_chats.text = chatId
        }
    }

    fun setOnItemClickListener(listener: ChatClickListener) {
        chatClickListener = listener
        notifyDataSetChanged()
    }

    fun updateChats(updatedChats: ArrayList<String>) {
        chats.clear()
        chats.addAll(updatedChats)
        notifyDataSetChanged()
    }
}