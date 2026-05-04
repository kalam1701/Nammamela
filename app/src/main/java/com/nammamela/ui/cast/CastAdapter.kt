package com.nammamela.ui.cast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nammamela.R
import com.nammamela.data.model.CastMember
import com.nammamela.databinding.ItemCastBinding

class CastAdapter : ListAdapter<CastMember, CastAdapter.CastViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val binding = ItemCastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CastViewHolder(private val binding: ItemCastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cast: CastMember) {
            binding.tvCastName.text = cast.name
            binding.tvCastRole.text = cast.role
            binding.tvCastBio.text = cast.bio

            if (cast.photoUrl.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(cast.photoUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_person)
                    .into(binding.ivCastPhoto)
            } else {
                binding.ivCastPhoto.setImageResource(R.drawable.ic_person)
            }

            binding.root.setOnClickListener {
                val isExpanded = binding.tvCastBioFull.visibility == View.VISIBLE
                binding.tvCastBioFull.visibility = if (isExpanded) View.GONE else View.VISIBLE
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CastMember>() {
            override fun areItemsTheSame(oldItem: CastMember, newItem: CastMember) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: CastMember, newItem: CastMember) = oldItem == newItem
        }
    }
}
