package com.example.contactbook

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.contactbook.screens.contacts.ContactsFragment
import com.example.contactbook.screens.contacts.EditContactFragment
import com.example.contactbook.screens.repository.RepositoryFragment

class MyPagerAdapter(fm: FragmentManager?, lifecycle: Lifecycle) : FragmentStateAdapter(fm!!, lifecycle) {
    private val int_items = 2
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = ContactsFragment()
            1 -> fragment = RepositoryFragment()
        }
        return fragment!!
    }
    override fun getItemCount(): Int {
        return int_items
    }
}