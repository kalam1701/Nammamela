package com.nammamela.ui.seat

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nammamela.NammaMelaApp
import com.nammamela.R
import com.nammamela.data.model.SeatStatus
import com.nammamela.databinding.ActivitySeatMapBinding
import com.nammamela.utils.ViewModelFactory

class SeatMapActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeatMapBinding
    private lateinit var viewModel: SeatViewModel
    private lateinit var adapter: SeatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "🪑 Select Your Seat"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = ViewModelFactory((application as NammaMelaApp).repository)
        viewModel = ViewModelProvider(this, factory)[SeatViewModel::class.java]

        adapter = SeatAdapter { seat ->
            if (seat.status == SeatStatus.AVAILABLE.name) {
                showBookingDialog(seat.id, seat.seatLabel)
            } else if (seat.status == SeatStatus.RESERVED.name) {
                Toast.makeText(this, "Seat ${seat.seatLabel} is already booked by ${seat.bookedByName}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rvSeats.layoutManager = GridLayoutManager(this, 12)
        binding.rvSeats.adapter = adapter

        viewModel.seats.observe(this) { seats ->
            adapter.submitList(seats)
        }

        viewModel.availableCount.observe(this) { available ->
            viewModel.totalCount.value?.let { total ->
                binding.tvSeatCount.text = "$available / $total seats available"
            }
        }

        viewModel.totalCount.observe(this) { total ->
            viewModel.availableCount.value?.let { available ->
                binding.tvSeatCount.text = "$available / $total seats available"
            }
        }
    }

    private fun showBookingDialog(seatId: String, seatLabel: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_booking, null)
        val etName = dialogView.findViewById<EditText>(R.id.etFanName)
        val btnConfirm = dialogView.findViewById<android.widget.Button>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<android.widget.Button>(R.id.btnCancel)

        // Set dynamic title text if needed or remove if the dialog title is enough
        val tvTitle = dialogView.findViewById<android.widget.TextView>(R.id.tvTitle)
        if (tvTitle != null) {
            tvTitle.text = "Book Seat $seatLabel"
        }

        val dialog = AlertDialog.Builder(this, R.style.ThemeDialog)
            .setView(dialogView)
            .create()

        btnConfirm.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.reserveSeat(seatId, name)
                Toast.makeText(this, "🎉 Seat $seatLabel booked for $name!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
