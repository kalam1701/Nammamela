package com.nammamela.ui.cast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammamela.NammaMelaApp
import com.nammamela.databinding.ActivityCastBinding
import com.nammamela.utils.ViewModelFactory

class CastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCastBinding
    private lateinit var viewModel: CastViewModel
    private lateinit var adapter: CastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "🎭 Tonight's Cast"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = ViewModelFactory((application as NammaMelaApp).repository)
        viewModel = ViewModelProvider(this, factory)[CastViewModel::class.java]

        adapter = CastAdapter()
        binding.rvCast.layoutManager = LinearLayoutManager(this)
        binding.rvCast.adapter = adapter

        viewModel.castList.observe(this) { castList ->
            adapter.submitList(castList)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
