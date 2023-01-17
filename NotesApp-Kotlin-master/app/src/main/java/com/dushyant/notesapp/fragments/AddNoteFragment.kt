package com.dushyant.notesapp.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dushyant.notesapp.R
import com.dushyant.notesapp.adapter.AddNoteAdapter
import com.dushyant.notesapp.model.NotesModel
import com.dushyant.notesapp.utils.Constants
import com.dushyant.notesapp.utils.CustomDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class AddNoteFragment : Fragment(),
    CustomDialog.OnDialogClickInterface,
    AddNoteAdapter.AddNoteAdapterInterface {

    private lateinit var addNoteInterface: AddNoteInterface
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var addNoteAdapter: AddNoteAdapter
    private var noteList: MutableList<NotesModel> = mutableListOf()
    private lateinit var database: DatabaseReference
    private val progressDots = arrayOfNulls<View>(4)
    private lateinit var progressLL: LinearLayout
    private lateinit var noItemsFound: TextView

    companion object {
        fun newInstance(addNoteInterface: AddNoteInterface) = AddNoteFragment()
            .apply {
                this.addNoteInterface = addNoteInterface
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_note_fragment, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        progressLL = view.findViewById(R.id.progress_ll)
        progressDots[0] = view.findViewById(R.id.progress_dot_1)
        progressDots[1] = view.findViewById(R.id.progress_dot_2)
        progressDots[2] = view.findViewById(R.id.progress_dot_3)
        progressDots[3] = view.findViewById(R.id.progress_dot_4)
        noItemsFound = view.findViewById(R.id.no_item_tv)
        Constants.animateProgressBar(progressDots)
        fab = view.findViewById(R.id.add_btn)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        addNoteAdapter =
            AddNoteAdapter(this, noteList)
        recyclerView.adapter = addNoteAdapter

        listenFirebase()

        fab.setOnClickListener {
            if (addNoteAdapter.getCheckedItems().isNotEmpty())
                showDeleteDialog()
            else
                CustomDialog(context!!, this).showDialog()
        }
        return view
    }


    private fun listenFirebase() {
        database = Firebase.database.reference
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                noteList.clear()
                for (dsp in dataSnapshot.children) {
                    noteList.add(
                        NotesModel(
                            dsp.child("title").value.toString(),
                            dsp.child("note").value.toString(),
                            dsp.child("timestamp").value.toString()
                        )
                    )
                }

                addNoteAdapter.addData(noteList)
                progressLL.visibility = View.GONE
                noItemsFound.visibility = if (noteList.isNotEmpty())  View.GONE else View.VISIBLE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.e("AddNote", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child(Constants.users).child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(postListener)
    }

    override fun onConfirmClick(
        title: String,
        content: String,
        timestamp: String?,
        isUpdate: Boolean
    ) {
        if (isUpdate)
            timestamp?.let { addNoteInterface.updateNotes(title, content, it) }
        else
            addNoteInterface.postNotes(title, content)
    }

    override fun onCancelClick() {
    }

    override fun onItemClick(notesModel: NotesModel) {
        CustomDialog(
            context!!,
            this,
            true,
            notesModel
        ).showDialog()
    }

    override fun onDeleteItem(isSelected: Boolean) {
        fab.setImageResource(if (!isSelected) R.drawable.ic_baseline_add_24 else R.drawable.ic_baseline_delete_24)
        fab.visibility = View.GONE
        val animation = ObjectAnimator.ofPropertyValuesHolder(
            fab,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 200.0f, -10.0f)
        );
        animation.duration = 800
        animation.startDelay = 100
        animation.start()
        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                fab.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {

            }
        })
    }

    private fun showDeleteDialog() {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_delete_custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val deleteBtn = dialog.findViewById(R.id.delete_btn) as Button

        deleteBtn.setOnClickListener {
            addNoteInterface.deleteNotes(addNoteAdapter.getCheckedItems())
            onDeleteItem(false)
            dialog.dismiss()
        }
        dialog.show()
    }

    interface AddNoteInterface {
        fun postNotes(title: String, content: String)
        fun updateNotes(title: String, content: String, timestamp: String)
        fun deleteNotes(notesList: MutableList<NotesModel>)
    }

}