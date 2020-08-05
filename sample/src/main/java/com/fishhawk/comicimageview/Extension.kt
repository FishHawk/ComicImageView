package com.fishhawk.comicimageview

import android.view.View
import android.widget.Toast


fun View.makeToast(content: String) {
    Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
}