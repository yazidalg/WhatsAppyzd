package com.yazidal.whatsappyzd.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.yazidal.whatsappyzd.MainActivity
import com.yazidal.whatsappyzd.fragment.ChartsFragment
import com.yazidal.whatsappyzd.fragment.StatusListFragment
import com.yazidal.whatsappyzd.fragment.StatusUpdateFragment


class SectionPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val chatsFragment = ChartsFragment()
    private val statusUpdateFragment = StatusUpdateFragment()
    private val statusFragment = StatusListFragment()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> statusUpdateFragment // menempatkan StatusUpdateFragment di posisi pertama
            1 -> chatsFragment // ChatsFragment posisi kedua dalam adapter
            2 -> statusFragment // StatusListFragment posisi ketiga dalam adapter
            else -> chatsFragment
        }
    }

    override fun getCount(): Int {
        return 3
    }
}