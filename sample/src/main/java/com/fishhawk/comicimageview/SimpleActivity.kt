package com.fishhawk.comicimageview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SimpleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        System.loadLibrary("opencv_java4")
        setContentView(R.layout.activity_simple)
    }
}