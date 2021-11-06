package com.example.imageapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentTransaction
import com.example.imageapp.view.HomeScreenFragment
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment = HomeScreenFragment()
        setContentView(R.layout.fragment_container_layout)
        transaction.replace(R.id.fragment_container, fragment, fragment.toString())
        transaction.addToBackStack(null)
        transaction.commit()
//        val takePicture = this.registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
//            if (success) {
//                // The image was saved into the given Uri -> do something with it
//            }
//        }
//
//        val f = File(Environment.DIRECTORY_PICTURES, "temp.jpg")
//        val imageUri: Uri = Uri.fromFile(f)
//        FileProvider.getUriForFile(
//            this,
//            "com.example.ImageApp.fileprovider",
//            photoFile
//        );
//        findViewById<R.id.>().setOnClickListener {
//            takePicture.launch(imageUri)
//        }
    }
}

























































































































