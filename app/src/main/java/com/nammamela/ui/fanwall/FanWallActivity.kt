package com.nammamela.ui.fanwall

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammamela.NammaMelaApp
import com.nammamela.R
import com.nammamela.databinding.ActivityFanWallBinding
import com.nammamela.utils.ViewModelFactory

class FanWallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFanWallBinding
    private lateinit var viewModel: FanWallViewModel
    private lateinit var adapter: FanPostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFanWallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "👏 Fan Wall"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = ViewModelFactory((application as NammaMelaApp).repository)
        viewModel = ViewModelProvider(this, factory)[FanWallViewModel::class.java]

        adapter = FanPostAdapter(isManager = false) {}
        binding.rvPosts.layoutManager = LinearLayoutManager(this)
        binding.rvPosts.adapter = adapter

        viewModel.posts.observe(this) { adapter.submitList(it) }
        viewModel.postCount.observe(this) { binding.tvPostCount.text = "$it applause posts" }

        binding.btnAddPost.setOnClickListener { showAddPostDialog() }
    }

    private fun showAddPostDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_fan_post, null)
        val etNick = view.findViewById<EditText>(R.id.etNickname)
        val etMsg = view.findViewById<EditText>(R.id.etMessage)
        AlertDialog.Builder(this, R.style.ThemeDialog)
            .setTitle("👏 Post Applause")
            .setView(view)
            .setPositiveButton("Post") { _, _ ->
                val nick = etNick.text.toString().trim()
                val msg = etMsg.text.toString().trim()
                if (nick.isNotEmpty() && msg.isNotEmpty()) {
                    viewModel.addPost(nick, msg, "Tonight's Show")
                    Toast.makeText(this, "Applause posted!", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onSupportNavigateUp(): Boolean { onBackPressedDispatcher.onBackPressed(); return true }
}
