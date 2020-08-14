package com.example.contactbook.screens.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.contactbook.MyPagerAdapter
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.R
import com.example.contactbook.database.entities.ContactExtras
import com.example.contactbook.databinding.FragmentEditContactBinding
import com.example.contactbook.databinding.FragmentExtrasBinding
import com.example.contactbook.screens.editcontact.EditContactViewModel
import com.example.contactbook.screens.editcontact.EditContactViewModelFactory
import com.example.contactbook.screens.extras.ExtrasViewModel
import com.example.contactbook.screens.extras.ExtrasViewModelFactory
import com.example.contactbook.util.hideKeyboard
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_contacts.*

class ExtrasFragment : Fragment() {

    companion object{
        private const val TAG = "ExtrasFragment"
    }

    private lateinit var viewModel: ExtrasViewModel
    private lateinit var binding: FragmentExtrasBinding
    private lateinit var viewModelFactory: ExtrasViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_extras, container, false)

        var contactExtras = arguments?.getParcelable<ContactExtras>("contactExtras")

        viewModelFactory =
            ExtrasViewModelFactory(
                requireActivity().application,
                contactExtras
            )

        viewModel = ViewModelProvider(this, viewModelFactory).get(ExtrasViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.editTextAddress.setText(contactExtras?.address)
        binding.editTextEmail.setText((contactExtras?.email))

        binding.editTextAddress.setOnFocusChangeListener{ view, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(view, context!!)
            }
        }

        binding.editTextEmail.setOnFocusChangeListener{ view, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(view, context!!)
            }
        }


        viewModel.toContactsFragment.observe(viewLifecycleOwner, Observer {state ->
            navigateToContactsFragment(state)
        })

        return binding.root
    }

    private fun navigateToContactsFragment(_state: Boolean) {
        if(_state) {
            findNavController().navigate(R.id.action_extrasFragment_to_pageContainerFragment2)
            viewModel.endNavigateToContactsFragment()
        }
    }

}
