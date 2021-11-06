package com.example.imageapp.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.imageapp.BuildConfig
import com.example.imageapp.R
import com.example.imageapp.databinding.FragmentHomeBinding
import com.example.imageapp.model.Constants
import java.io.File
import java.io.IOException
import java.util.Objects

class HomeScreenFragment : Fragment(R.layout.fragment_home) {

    private var currentPhotoPath = ""
    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val requestPermissionCode = 1

    private lateinit var currentImageUri: Uri

    private val startForResultToLoadImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val selectedImage: Uri? = result.data?.data
                    if (selectedImage != null) {
                        // From Gallery
                        binding.imageview.setImageURI(selectedImage)
                        currentPhotoPath = selectedImage.toString()
                    } else {
                        // From Camera
                        val bitmap =
                            BitmapFactory.decodeStream(context?.contentResolver?.openInputStream(currentImageUri))
                        binding.imageview.setImageBitmap(bitmap)
                    }
                    binding.editbutton.isEnabled = true
                } catch (error: Exception) {
                    Log.d("TAG==>>", "Error : ${error.localizedMessage}")
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        binding.camerabutton.setOnClickListener {
            askCameraPermission()
        }

        binding.gallerybutton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startForResultToLoadImage.launch(intent)
        }

        binding.editbutton.setOnClickListener {
            EditFragment().apply {
                arguments = Bundle().apply { putString(Constants.Image, currentPhotoPath) }
            }.also {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun askCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.CAMERA
                ), requestPermissionCode
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        try {
            val photoURI: Uri? = context?.let {
                createImageFile().let { it1 ->
                    FileProvider.getUriForFile(
                        it,
                        requireContext().applicationContext.packageName.toString() + ".provider",
                        it1
                    )
                }
            }
            photoURI?.let {
                currentImageUri = it
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, it)
                startForResultToLoadImage.launch(intent)
            }
        } catch (ex: java.lang.Exception) {
            Log.e("HomeScreenFragment", ex.toString())
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        FileProvider.getUriForFile(
            Objects.requireNonNull(requireActivity()),
            BuildConfig.APPLICATION_ID + ".provider", storageDir
        )
        return File.createTempFile(
            "JPEG_TEMP_+${System.currentTimeMillis()}", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}