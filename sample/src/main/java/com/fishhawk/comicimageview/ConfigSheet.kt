package com.fishhawk.comicimageview

import android.content.Context
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.switchmaterial.SwitchMaterial


open class ConfigSheet(context: Context) : BottomSheetDialog(context) {

    protected fun bind(
        switch: SwitchMaterial,
        value: Boolean,
        onChanged: (Boolean) -> Unit
    ) {
        switch.isChecked = value
        switch.setOnCheckedChangeListener { _, isChecked -> onChanged(isChecked) }
    }

    protected fun bind(
        spinner: AppCompatSpinner,
        value: Int,
        onChanged: (Int) -> Unit
    ) {
        spinner.setSelection(value, false)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                onChanged(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}