package com.yazidal.whatsappyzd.listener

interface ContactListener {
    fun onContactClicked(name: String?, phone: String?)
}

interface ChatClickListener {
    fun onChatClicked(name: String?, otherUserId: String?, chatsImageUrl: String?,
                      chatsName: String?)
}