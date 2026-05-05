package com.nammamela.ui.fanwall

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammamela.data.model.FanPost
import com.nammamela.databinding.ItemFanPostBinding
import java.text.SimpleDateFormat
import java.util.*

class FanPostAdapter(private val isManager: Boolean, private val onDelete: (String) -> Unit) :
    ListAdapter<FanPost, FanPostAdapter.PostViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PostViewHolder(ItemFanPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) = holder.bind(getItem(position))

    inner class PostViewHolder(private val b: ItemFanPostBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(post: FanPost) {
            b.tvNickname.text = "👤 ${post.nickname}"
            b.tvMessage.text = post.message
            b.tvTimestamp.text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(post.timestamp))
            b.tvPosterName.text = "🎭 ${post.posterName}"
            b.btnDelete.visibility = if (isManager) View.VISIBLE else View.GONE
            b.btnDelete.setOnClickListener { onDelete(post.id) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<FanPost>() {
            override fun areItemsTheSame(o: FanPost, n: FanPost) = o.id == n.id
            override fun areContentsTheSame(o: FanPost, n: FanPost) = o == n
        }
    }
}
