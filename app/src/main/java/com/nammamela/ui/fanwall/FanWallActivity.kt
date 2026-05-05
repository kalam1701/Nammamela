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

        binding.btnAddPost.setOnClickListener {
            val nick = binding.etNickname.text.toString().trim()
            val msg = binding.etMessage.text.toString().trim()
            if (nick.isNotEmpty() && msg.isNotEmpty()) {
                viewModel.addPost(nick, msg, "Tonight's Show")
                Toast.makeText(this, "Applause posted!", Toast.LENGTH_SHORT).show()
                // Clear the input fields after posting
                binding.etNickname.text.clear()
                binding.etMessage.text.clear()
                
                // Scroll to bottom
                binding.rvPosts.scrollToPosition(adapter.itemCount)
            } else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { onBackPressedDispatcher.onBackPressed(); return true }
}
