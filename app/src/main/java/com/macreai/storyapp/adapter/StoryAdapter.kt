package com.macreai.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.macreai.storyapp.R
import com.macreai.storyapp.databinding.ItemRowStoryBinding
import com.macreai.storyapp.model.remote.response.ListStoryItem
import com.macreai.storyapp.view.detail.DetailStoryActivity

class StoryAdapter
    : PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private val binding: ItemRowStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem){
            val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.anim)

            binding.apply {
                tvName.text = story.name
                tvDescription.text = story.description
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .centerCrop()
                    .into(storyPhoto)
                root.startAnimation(animation)
            }

            itemView.setOnClickListener {
                val intentToDetail = Intent(itemView.context, DetailStoryActivity::class.java)
                intentToDetail.putExtra("Story", story)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.storyPhoto, "profile"),
                        Pair(binding.tvName, "name"),
                        Pair(binding.tvDescription, "description")
                    )
                itemView.context.startActivity(intentToDetail, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null){
            holder.bind(data)
        }
    }

    /*
    fun setData(newList: List<ListStoryItem>){
        val diffUtil = MyDiffUtil(listStory, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        listStory = newList
        diffResult.dispatchUpdatesTo(this)
    }
     */

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}