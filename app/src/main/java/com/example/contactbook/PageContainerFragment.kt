package com.example.contactbook

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.contactbook.MyPagerAdapter
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.R
import com.example.contactbook.database.entities.ContactExtras
import com.example.contactbook.databinding.FragmentEditContactBinding
import com.example.contactbook.databinding.FragmentExtrasBinding
import com.example.contactbook.databinding.FragmentPageContainerBinding
import com.example.contactbook.screens.contacts.ContactsFragment
import com.example.contactbook.screens.editcontact.EditContactViewModel
import com.example.contactbook.screens.editcontact.EditContactViewModelFactory
import com.example.contactbook.screens.extras.ExtrasViewModel
import com.example.contactbook.screens.extras.ExtrasViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_contacts.*

class PageContainerFragment : Fragment() {

    companion object{
        private const val TAG = "PageContainerFragment"
    }

    private var viewPager: ViewPager2? = null

    private lateinit var binding: FragmentPageContainerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_page_container, container, false)

        binding.lifecycleOwner = this

        viewPager = binding.viewpagerMain
        viewPager!!.adapter = MyPagerAdapter(childFragmentManager, lifecycle)
        viewPager!!.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val tabLayout = binding.tabsMain
        TabLayoutMediator(tabLayout, viewPager!!) { tab, position ->
            when (position) {
                0 -> {tab.text = "Contacts"
                binding}
                1 -> {tab.text = "Repositories"}
            }
        }.attach()

        return binding.root
    }
}
