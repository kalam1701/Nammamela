package com.nammamela.ui.manager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nammamela.R
import com.nammamela.data.model.CastMember
import com.nammamela.databinding.ItemManagerCastBinding

class ManagerCastAdapter(
    private val onEdit: (CastMember) -> Unit,
    private val onDelete: (CastMember) -> Unit
) : ListAdapter<CastMember, ManagerCastAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemManagerCastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemManagerCastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cast: CastMember) {
            binding.tvName.text = cast.name
            binding.tvRole.text = cast.role
            if (cast.photoUrl.isNotEmpty()) {
                Glide.with(binding.root.context).load(cast.photoUrl)
                    .circleCrop().placeholder(R.drawable.ic_person).into(binding.ivPhoto)
            } else {
                binding.ivPhoto.setImageResource(R.drawable.ic_person)
            }
            binding.btnEdit.setOnClickListener { onEdit(cast) }
            binding.btnDelete.setOnClickListener { onDelete(cast) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CastMember>() {
            override fun areItemsTheSame(o: CastMember, n: CastMember) = o.id == n.id
            override fun areContentsTheSame(o: CastMember, n: CastMember) = o == n
        }
    }
}
