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
import com.example.contactbook.database.entities.Contact
import com.example.contactbook.R
import com.example.contactbook.databinding.FragmentEditContactBinding
import com.example.contactbook.screens.editcontact.EditContactViewModel
import com.example.contactbook.screens.editcontact.EditContactViewModelFactory


class EditContactFragment : Fragment() {

    companion object{
        private const val TAG = "EditContactFragment"
    }

    private lateinit var viewModel: EditContactViewModel
    private lateinit var binding: FragmentEditContactBinding
    private lateinit var viewModelFactory: EditContactViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_contact, container, false)

        var contact = arguments?.getParcelable<Contact>("contact")

        viewModelFactory =
            EditContactViewModelFactory(
                requireActivity().application,
                contact
            )

        viewModel = ViewModelProvider(this, viewModelFactory).get(EditContactViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.editTextName.setText(contact?.name)
        binding.editTextNumber.setText((contact?.number))


        viewModel.toContactsFragment.observe(viewLifecycleOwner, Observer {state ->
            navigateToContactsFragment(state)
        })

        return binding.root
    }

    private fun navigateToContactsFragment(_state: Boolean) {
        if(_state) {
            findNavController().navigate(R.id.action_editContactFragment_to_contactsFragment)
            viewModel.endNavigateToContactsFragment()
        }
    }

}
