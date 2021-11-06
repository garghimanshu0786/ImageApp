package com.example.imageapp.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.imageapp.R
import com.example.imageapp.databinding.FragmentEditImageBinding
import com.example.imageapp.model.Constants
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class EditFragment : Fragment(R.layout.fragment_edit_image) {

    private var _binding: FragmentEditImageBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private var angle = 0.0f
    private val PIC_CROP = 1
    private var imageUri = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditImageBinding.bind(view)
        imageUri = requireArguments().getString(Constants.Image)!!
        binding.imageview.setImageURI(Uri.parse(imageUri))
        binding.rotatebutton.setOnClickListener {
            angle += 90.0f
            binding.imageview.rotation = angle
        }
        binding.cropbutton.setOnClickListener {
            performCrop(Uri.parse(imageUri))
        }
        binding.savebutton.setOnClickListener {
            saveImage()
        }
    }

    private fun saveImage() {
        try {
            binding.imageview.isDrawingCacheEnabled = true
            val b = binding.imageview.drawingCache
            val timeStamp: String = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
            val title = "ImageApp$timeStamp"
            val description = "Image from image app"
            Images.Media.insertImage(requireActivity().contentResolver, b, title, description)
            Toast.makeText(requireContext(), "Saved image", Toast.LENGTH_LONG).show()
        } catch (ex: Exception) {
            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIC_CROP) {
            if (data != null) {
                // get the returned data
                val extras = data.extras
                // get the cropped bitmap
                val selectedBitmap = extras!!.getParcelable<Bitmap>("data")
                try {
                    FileOutputStream(imageUri).use { out ->
                        selectedBitmap!!.compress(
                            Bitmap.CompressFormat.PNG,
                            100,
                            out
                        ) // bmp is your Bitmap instance
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                binding.imageview.setImageBitmap(selectedBitmap)
            }
        }
    }

    private fun performCrop(picUri: Uri) {
        try {
            val cropIntent = Intent("com.android.camera.action.CROP")
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*")
            // set crop properties here
            cropIntent.putExtra("crop", true)
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128)
            cropIntent.putExtra("outputY", 128)
            // retrieve data on return
            cropIntent.putExtra("return-data", true)
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP)
        } // respond to users whose devices do not support the crop action
        catch (anfe: ActivityNotFoundException) {
            // display an error message
            Toast.makeText(
                requireContext(),
                "Whoops - your device doesn't support the crop action!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}