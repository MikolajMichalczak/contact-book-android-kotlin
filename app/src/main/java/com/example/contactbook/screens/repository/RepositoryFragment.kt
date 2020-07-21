package com.example.contactbook.screens.repository

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactbook.R
import com.example.contactbook.databinding.FragmentRepositoryBinding


class RepositoryFragment: Fragment() {

    companion object{
        private const val TAG = "RepositoryFragment"
    }

    private lateinit var _adapter: RepositoryAdapter
    private lateinit var viewModel: RepositoryViewModel
    private lateinit var binding: FragmentRepositoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_repository, container, false)

        viewModel = ViewModelProvider(this).get(RepositoryViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        var reposList = binding.reposList
        _adapter = RepositoryAdapter()

        reposList.apply{
            layoutManager = LinearLayoutManager(activity)
            adapter = _adapter
        }

        viewModel.reposList.observe(viewLifecycleOwner, Observer { reposList->
            _adapter.setList(reposList)
        })


        return binding.root
    }
}