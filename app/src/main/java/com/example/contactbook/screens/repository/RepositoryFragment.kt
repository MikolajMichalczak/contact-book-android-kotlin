package com.example.contactbook.screens.repository

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactbook.R
import com.example.contactbook.database.entities.Repository
import com.example.contactbook.databinding.FragmentRepositoryBinding


class RepositoryFragment: Fragment(), SearchView.OnQueryTextListener  {

    companion object{
        private const val TAG = "RepositoryFragment"
    }

    private lateinit var _adapter: RepositoryAdapter
    private lateinit var viewModel: RepositoryViewModel
    private lateinit var binding: FragmentRepositoryBinding
    private lateinit var menu: Menu


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.repos_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        this.menu = menu
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        viewModel.filterText.value = query
        binding.reposList.scrollToPosition(0)
        return true
    }

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

        viewModel.reposList?.observe(viewLifecycleOwner, Observer { reposList->
            _adapter.submitList(reposList)
            //_adapter.setList(reposList)
            Log.i("RepoBoundaryCallback", reposList.size.toString())
        })
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        val searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.onActionViewCollapsed()
    }
}