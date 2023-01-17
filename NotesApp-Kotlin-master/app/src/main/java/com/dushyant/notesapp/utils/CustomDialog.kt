package com.dushyant.notesapp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.dushyant.notesapp.R
import com.dushyant.notesapp.model.NotesModel

class CustomDialog(
    private val context: Context,
    private val onDialogClickInterface: OnDialogClickInterface,
    private val isUpdate: Boolean = false,
    private val notesModel: NotesModel = NotesModel()
) {


    fun showDialog() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val title = dialog.findViewById(R.id.title_et) as EditText
        val body = dialog.findViewById(R.id.content_et) as EditText
        val okBtn = dialog.findViewById(R.id.ok_btn) as TextView
        val noBtn = dialog.findViewById(R.id.cancel_btn) as TextView

        if (notesModel.title!!.isNotEmpty())
            title.setText(notesModel.title)

        if (notesModel.note!!.isNotEmpty())
            body.setText(notesModel.note)

        okBtn.setOnClickListener {
            if (title.text.toString().isEmpty() || body.text.toString().isEmpty()) {
                Toast.makeText(context, "Empty Fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dialog.dismiss()
            onDialogClickInterface.onConfirmClick(title.text.toString(), body.text.toString(), notesModel.timestamp, isUpdate)
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            onDialogClickInterface.onCancelClick()
        }
        dialog.show()
    }


    interface OnDialogClickInterface {
        fun onConfirmClick(title: String, content: String, timestamp: String?, isUpdate: Boolean)
        fun onCancelClick()
    }

}