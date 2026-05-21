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
import com.nammamela.utils.ImageUtils
import com.nammamela.utils.PinManager
import com.nammamela.utils.ViewModelFactory
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import android.util.Log

class ManagerDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerDashboardBinding
    private lateinit var viewModel: ManagerViewModel
    private lateinit var fanViewModel: com.nammamela.ui.fanwall.FanWallViewModel
    private var currentPosterUrl: String = ""

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            // Show preview immediately from local URI
            Glide.with(this).load(uri).into(binding.ivPosterPreview)
            binding.btnUploadPoster.text = "⏳ Processing..."
            binding.btnUploadPoster.isEnabled = false

            // Convert to Base64 in background thread
            Thread {
                val base64 = ImageUtils.uriToBase64(this, uri)
                runOnUiThread {
                    if (base64 != null) {
                        currentPosterUrl = base64
                        Log.d("NammaMela", "Poster converted to Base64 (${base64.length} chars)")
                        binding.btnUploadPoster.text = "Change Poster"
                        Toast.makeText(this, "✅ Poster ready!", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.btnUploadPoster.text = "Upload Poster"
                        Toast.makeText(this, "❌ Failed to process image", Toast.LENGTH_LONG).show()
                    }
                    binding.btnUploadPoster.isEnabled = true
                }
            }.start()
        }
    }

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
                binding.etPlayDate.setText(play.date)
                binding.etShowTime.setText(play.showTime)
                binding.etPlayVenue.setText(play.venue)
                binding.etSynopsis.setText(play.synopsis)
                currentPosterUrl = play.posterUrl
                if (currentPosterUrl.isNotEmpty()) {
                    ImageUtils.loadImage(this, currentPosterUrl, binding.ivPosterPreview)
                    binding.btnUploadPoster.text = "Change Poster"
                }
            }
        }

        binding.btnUploadPoster.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSavePlay.setOnClickListener {
            val title = binding.etPlayTitle.text.toString().trim()
            if (title.isEmpty()) { Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            Log.d("NammaMela", "Saving play with posterUrl length: ${currentPosterUrl.length}")
            val play = Play(
                title = title, genre = binding.etPlayGenre.text.toString(),
                duration = binding.etPlayDuration.text.toString(),
                date = binding.etPlayDate.text.toString(),
                showTime = binding.etShowTime.text.toString(),
                venue = binding.etPlayVenue.text.toString(),
                synopsis = binding.etSynopsis.text.toString(),
                posterUrl = currentPosterUrl
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

    private var castPhotoPath: String = ""
    private var castPhotoPreview: android.widget.ImageView? = null

    private val pickCastPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            // Show preview immediately from local URI
            castPhotoPreview?.let {
                Glide.with(this).load(uri).circleCrop().into(it)
            }
            Toast.makeText(this, "⏳ Processing photo...", Toast.LENGTH_SHORT).show()

            // Convert to Base64 in background thread
            Thread {
                val base64 = ImageUtils.uriToBase64(this, uri)
                runOnUiThread {
                    if (base64 != null) {
                        castPhotoPath = base64
                        Log.d("NammaMela", "Cast photo converted to Base64 (${base64.length} chars)")
                        Toast.makeText(this, "✅ Photo ready!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "❌ Failed to process photo", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }
    }

    private fun showAddCastDialog() {
        castPhotoPath = ""
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_cast, null)
        val ivPreview = view.findViewById<android.widget.ImageView>(R.id.ivCastPhotoPreview)
        val btnPick = view.findViewById<android.widget.Button>(R.id.btnPickCastPhoto)
        val btnSave = view.findViewById<android.widget.Button>(R.id.btnCastSave)
        val btnCancel = view.findViewById<android.widget.Button>(R.id.btnCastCancel)
        castPhotoPreview = ivPreview

        btnSave.text = "Add"
        btnPick.setOnClickListener { pickCastPhotoLauncher.launch("image/*") }

        val dialog = AlertDialog.Builder(this, R.style.ThemeDialog).setView(view).create()

        btnSave.setOnClickListener {
            val name = view.findViewById<EditText>(R.id.etCastName).text.toString().trim()
            val role = view.findViewById<EditText>(R.id.etCastRole).text.toString().trim()
            val bio = view.findViewById<EditText>(R.id.etCastBio).text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.addCastMember(CastMember(name=name,role=role,bio=bio,photoUrl=castPhotoPath))
                dialog.dismiss()
            } else Toast.makeText(this,"Name required",Toast.LENGTH_SHORT).show()
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showEditCastDialog(cast: CastMember) {
        castPhotoPath = cast.photoUrl
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_cast, null)
        val etName = view.findViewById<EditText>(R.id.etCastName)
        val etRole = view.findViewById<EditText>(R.id.etCastRole)
        val etBio = view.findViewById<EditText>(R.id.etCastBio)
        val ivPreview = view.findViewById<android.widget.ImageView>(R.id.ivCastPhotoPreview)
        val btnPick = view.findViewById<android.widget.Button>(R.id.btnPickCastPhoto)
        val btnSave = view.findViewById<android.widget.Button>(R.id.btnCastSave)
        val btnCancel = view.findViewById<android.widget.Button>(R.id.btnCastCancel)
        castPhotoPreview = ivPreview

        btnSave.text = "Save"
        etName.setText(cast.name)
        etRole.setText(cast.role)
        etBio.setText(cast.bio)

        // Load existing photo if available
        if (cast.photoUrl.isNotEmpty()) {
            ImageUtils.loadImage(this, cast.photoUrl, ivPreview, circleCrop = true)
        }

        btnPick.setOnClickListener { pickCastPhotoLauncher.launch("image/*") }

        val dialog = AlertDialog.Builder(this, R.style.ThemeDialog).setView(view).create()

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val role = etRole.text.toString().trim()
            val bio = etBio.text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.updateCastMember(cast.copy(name=name,role=role,bio=bio,photoUrl=castPhotoPath))
                dialog.dismiss()
            } else Toast.makeText(this,"Name required",Toast.LENGTH_SHORT).show()
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showChangePinDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_change_pin, null)
        val etCurrent = view.findViewById<EditText>(R.id.etCurrentPin)
        val etNew = view.findViewById<EditText>(R.id.etNewPin)
        val etConfirm = view.findViewById<EditText>(R.id.etConfirmPin)
        val btnCancel = view.findViewById<android.widget.Button>(R.id.btnCancel)
        val btnChange = view.findViewById<android.widget.Button>(R.id.btnChange)

        val dialog = AlertDialog.Builder(this, R.style.ThemeDialog)
            .setTitle("Change Manager PIN")
            .setView(view)
            .create()

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnChange.setOnClickListener {
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
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean { onBackPressedDispatcher.onBackPressed(); return true }
}
