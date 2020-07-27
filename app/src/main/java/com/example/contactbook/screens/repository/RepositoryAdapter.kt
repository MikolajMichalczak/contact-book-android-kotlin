package com.example.contactbook.screens.repository

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.contactbook.databinding.RepositoryListItemBinding
import com.example.contactbook.database.entities.Repository
import kotlinx.android.synthetic.main.repository_list_item.view.*

class RepositoryAdapter() : PagedListAdapter<Repository, RepositoryAdapter.RepositoryViewHolder>(DIFF_CALLBACK) {

    //private var list: List<Repository> = emptyList()

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Repository>() {
            override fun areItemsTheSame(oldItem: Repository, newItem: Repository) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Repository, newItem: Repository) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RepositoryListItemBinding.inflate(inflater, parent, false)
        return RepositoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repository: Repository? = getItem(position)
        holder.bind(repository)
    }

    inner class RepositoryViewHolder(private val binding: RepositoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var nameView: TextView? = null
        private var descriptionView: TextView? = null
        private var languageView: TextView? = null
        private var repository: Repository? = null

        init {
            nameView = itemView.repository_name
            descriptionView = itemView.description_textview
            languageView = itemView.language_textview
            itemView.setOnClickListener(this)
        }

        fun bind(repository: Repository?) {
            binding.repositoryName.text = "google/<" + repository?.name + ">"

            if(repository?.description.isNullOrBlank())
                binding.descriptionTextview.text = "No description"
            else
                binding.descriptionTextview.text = "Description: " + repository?.description

            if(repository?.language.isNullOrBlank())
                binding.languageTextview.text = "No language"
            else
                binding.languageTextview.text = "Language: " + repository?.language
            this.repository = repository
        }

        override fun onClick(p0: View?) {
            if(binding.descriptionTextview.visibility == View.GONE) {
                binding.descriptionTextview.visibility = View.VISIBLE
                binding.languageTextview.visibility = View.VISIBLE
            }
            else{
                binding.descriptionTextview.visibility = View.GONE
                binding.languageTextview.visibility = View.GONE
            }
        }
    }

}