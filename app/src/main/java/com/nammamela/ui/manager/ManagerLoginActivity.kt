package com.nammamela.ui.manager

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nammamela.databinding.ActivityManagerLoginBinding
import com.nammamela.utils.PinManager

class ManagerLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "🔐 Manager Login"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnLogin.setOnClickListener {
            val pin = binding.etPin.text.toString()
            if (pin.length < 4) { Toast.makeText(this, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            if (PinManager.verifyPin(this, pin)) {
                startActivity(Intent(this, ManagerDashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "❌ Incorrect PIN", Toast.LENGTH_SHORT).show()
                binding.etPin.text?.clear()
            }
        }
        binding.tvDefaultPin.text = "Default PIN: 1234"
    }

    override fun onSupportNavigateUp(): Boolean { onBackPressedDispatcher.onBackPressed(); return true }
}
