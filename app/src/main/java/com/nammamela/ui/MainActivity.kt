package com.nammamela.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.nammamela.NammaMelaApp
import com.nammamela.R
import com.nammamela.databinding.ActivityMainBinding
import com.nammamela.ui.cast.CastActivity
import com.nammamela.ui.fanwall.FanWallActivity
import com.nammamela.ui.home.HomeViewModel
import com.nammamela.ui.manager.ManagerLoginActivity
import com.nammamela.ui.seat.SeatMapActivity
import com.nammamela.utils.PinManager
import com.nammamela.utils.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PinManager.ensureDefaultPin(this)

        val factory = ViewModelFactory((application as NammaMelaApp).repository)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        viewModel.initData()
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.tonightsPlay.observe(this) { play ->
            if (play != null) {
                binding.tvNoShow.visibility = View.GONE
                binding.cardPlayInfo.visibility = View.VISIBLE
                binding.tvPlayTitle.text = play.title
                // Dynamic venue
                if (play.venue.isNotEmpty()) {
                    binding.tvVenue.text = play.venue
                }
                // Dynamic genre chips
                binding.chipGroupGenre.removeAllViews()
                if (play.genre.isNotEmpty()) {
                    play.genre.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { genre ->
                        val chip = Chip(this)
                        chip.text = genre
                        chip.isClickable = false
                        chip.setChipBackgroundColorResource(R.color.bg_mint)
                        chip.setTextColor(resources.getColor(R.color.forest_green, theme))
                        binding.chipGroupGenre.addView(chip)
                    }
                }
                binding.tvDuration.text = "${play.duration} mins"
                binding.tvShowTime.text = play.showTime
                binding.tvSynopsis.text = play.synopsis
                if (play.posterUrl.isNotEmpty()) {
                    val posterSource: Any = if (play.posterUrl.startsWith("/")) {
                        java.io.File(play.posterUrl)
                    } else {
                        play.posterUrl
                    }
                    Glide.with(this)
                        .load(posterSource)
                        .into(binding.ivPoster)
                } else {
                    binding.ivPoster.setImageDrawable(null)
                }
            } else {
                binding.tvNoShow.visibility = View.VISIBLE
                binding.cardPlayInfo.visibility = View.GONE
            }
        }

        viewModel.availableSeats.observe(this) { available ->
            viewModel.totalSeats.value?.let { total ->
                binding.tvSeatBadge.text = "$available / $total seats left"
            }
        }

        viewModel.totalSeats.observe(this) { total ->
            viewModel.availableSeats.value?.let { available ->
                binding.tvSeatBadge.text = "$available / $total seats left"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBookSeats.setOnClickListener {
            startActivity(Intent(this, SeatMapActivity::class.java))
        }
        binding.btnViewCast.setOnClickListener {
            startActivity(Intent(this, CastActivity::class.java))
        }
        binding.btnFanWall.setOnClickListener {
            startActivity(Intent(this, FanWallActivity::class.java))
        }
        binding.fabManager.setOnClickListener {
            startActivity(Intent(this, ManagerLoginActivity::class.java))
        }
    }

}
