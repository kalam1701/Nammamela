package com.nammamela.ui.manager

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
import com.nammamela.data.model.CastMember
import com.nammamela.data.model.Play
import com.nammamela.databinding.ActivityManagerDashboardBinding
import com.nammamela.ui.fanwall.FanPostAdapter
import com.nammamela.utils.PinManager
import com.nammamela.utils.ViewModelFactory

class ManagerDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerDashboardBinding
    private lateinit var viewModel: ManagerViewModel
    private lateinit var fanViewModel: com.nammamela.ui.fanwall.FanWallViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "🎛 Manager Dashboard"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val factory = ViewModelFactory((application as NammaMelaApp).repository)
        viewModel = ViewModelProvider(this, factory)[ManagerViewModel::class.java]
        fanViewModel = ViewModelProvider(this, factory)[com.nammamela.ui.fanwall.FanWallViewModel::class.java]

        // Cast RecyclerView (manager version with edit/delete)
        val castAdapter = ManagerCastAdapter(
            onEdit = { cast -> showEditCastDialog(cast) },
            onDelete = { cast -> viewModel.deleteCastMember(cast) }
        )
        binding.rvManagerCast.layoutManager = LinearLayoutManager(this)
        binding.rvManagerCast.adapter = castAdapter
        viewModel.castList.observe(this) { castAdapter.submitList(it) }

        // Fan posts moderation
        val fanAdapter = FanPostAdapter(isManager = true) { id -> fanViewModel.deletePost(id) }
        binding.rvManagerPosts.layoutManager = LinearLayoutManager(this)
        binding.rvManagerPosts.adapter = fanAdapter
        fanViewModel.posts.observe(this) { fanAdapter.submitList(it) }

        // Tonight's play
        viewModel.tonightsPlay.observe(this) { play ->
            if (play != null) {
                binding.etPlayTitle.setText(play.title)
                binding.etPlayGenre.setText(play.genre)
                binding.etPlayDuration.setText(play.duration)
                binding.etShowTime.setText(play.showTime)
                binding.etSynopsis.setText(play.synopsis)
                binding.etPosterUrl.setText(play.posterUrl)
            }
        }

        binding.btnSavePlay.setOnClickListener {
            val title = binding.etPlayTitle.text.toString().trim()
            if (title.isEmpty()) { Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            val play = Play(
                title = title, genre = binding.etPlayGenre.text.toString(),
                duration = binding.etPlayDuration.text.toString(),
                showTime = binding.etShowTime.text.toString(),
                synopsis = binding.etSynopsis.text.toString(),
                posterUrl = binding.etPosterUrl.text.toString()
            )
            viewModel.savePlay(play)
            Toast.makeText(this, "✅ Play saved!", Toast.LENGTH_SHORT).show()
        }

        binding.btnResetSeats.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Reset All Seats?")
                .setMessage("This will make all seats available for the next show.")
                .setPositiveButton("Reset") { _, _ -> viewModel.resetSeats(); Toast.makeText(this, "Seats reset!", Toast.LENGTH_SHORT).show() }
                .setNegativeButton("Cancel", null).show()
        }

        binding.btnAddCast.setOnClickListener { showAddCastDialog() }
        binding.btnChangePin.setOnClickListener { showChangePinDialog() }
    }

    private fun showAddCastDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_cast, null)
        AlertDialog.Builder(this, R.style.ThemeDialog).setTitle("Add Cast Member").setView(view)
            .setPositiveButton("Add") { _, _ ->
                val name = view.findViewById<EditText>(R.id.etCastName).text.toString().trim()
                val role = view.findViewById<EditText>(R.id.etCastRole).text.toString().trim()
                val bio = view.findViewById<EditText>(R.id.etCastBio).text.toString().trim()
                val photo = view.findViewById<EditText>(R.id.etCastPhoto).text.toString().trim()
                if (name.isNotEmpty()) viewModel.addCastMember(CastMember(name=name,role=role,bio=bio,photoUrl=photo))
                else Toast.makeText(this,"Name required",Toast.LENGTH_SHORT).show()
            }.setNegativeButton("Cancel",null).show()
    }

    private fun showEditCastDialog(cast: CastMember) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_cast, null)
        val etName = view.findViewById<EditText>(R.id.etCastName)
        val etRole = view.findViewById<EditText>(R.id.etCastRole)
        val etBio = view.findViewById<EditText>(R.id.etCastBio)
        val etPhoto = view.findViewById<EditText>(R.id.etCastPhoto)
        etName.setText(cast.name)
        etRole.setText(cast.role)
        etBio.setText(cast.bio)
        etPhoto.setText(cast.photoUrl)
        AlertDialog.Builder(this, R.style.ThemeDialog).setTitle("Edit Cast Member").setView(view)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                val role = etRole.text.toString().trim()
                val bio = etBio.text.toString().trim()
                val photo = etPhoto.text.toString().trim()
                if (name.isNotEmpty()) viewModel.updateCastMember(cast.copy(name=name,role=role,bio=bio,photoUrl=photo))
                else Toast.makeText(this,"Name required",Toast.LENGTH_SHORT).show()
            }.setNegativeButton("Cancel",null).show()
    }

    private fun showChangePinDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_change_pin, null)
        val etCurrent = view.findViewById<EditText>(R.id.etCurrentPin)
        val etNew = view.findViewById<EditText>(R.id.etNewPin)
        val etConfirm = view.findViewById<EditText>(R.id.etConfirmPin)
        AlertDialog.Builder(this, R.style.ThemeDialog).setTitle("Change Manager PIN").setView(view)
            .setPositiveButton("Change") { _, _ ->
                val current = etCurrent.text.toString()
                val newPin = etNew.text.toString()
                val confirm = etConfirm.text.toString()
                when {
                    !PinManager.verifyPin(this, current) ->
                        Toast.makeText(this, "❌ Current PIN is incorrect", Toast.LENGTH_SHORT).show()
                    newPin.length < 4 ->
                        Toast.makeText(this, "New PIN must be at least 4 digits", Toast.LENGTH_SHORT).show()
                    newPin != confirm ->
                        Toast.makeText(this, "New PINs don't match", Toast.LENGTH_SHORT).show()
                    else -> {
                        PinManager.setPin(this, newPin)
                        Toast.makeText(this, "✅ PIN changed successfully!", Toast.LENGTH_SHORT).show()
                    }
                }
            }.setNegativeButton("Cancel",null).show()
    }

    override fun onSupportNavigateUp(): Boolean { onBackPressedDispatcher.onBackPressed(); return true }
}
