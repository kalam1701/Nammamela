package com.nammamela.ui.seat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammamela.R
import com.nammamela.data.model.Seat
import com.nammamela.data.model.SeatStatus
import com.nammamela.databinding.ItemSeatBinding

class SeatAdapter(private val onSeatClick: (Seat) -> Unit) :
    ListAdapter<Seat, SeatAdapter.SeatViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val binding = ItemSeatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SeatViewHolder(private val binding: ItemSeatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(seat: Seat) {
            binding.tvSeatLabel.text = seat.seatLabel
            val color = when (seat.status) {
                SeatStatus.AVAILABLE -> R.color.seat_available
                SeatStatus.RESERVED -> R.color.seat_reserved
                SeatStatus.SELECTED -> R.color.seat_selected
            }
            binding.root.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, color))
            binding.root.setOnClickListener { onSeatClick(seat) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Seat>() {
            override fun areItemsTheSame(o: Seat, n: Seat) = o.id == n.id
            override fun areContentsTheSame(o: Seat, n: Seat) = o == n
        }
    }
}
