package com.dushyant.notesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dushyant.notesapp.R
import com.dushyant.notesapp.model.NotesModel
import com.ms.square.android.expandabletextview.ExpandableTextView

class AddNoteAdapter(
    var adapterInterface: AddNoteAdapterInterface,
    var notesList: MutableList<NotesModel>
) :
    RecyclerView.Adapter<AddNoteAdapter.AddNoteHolder>() {

    class AddNoteHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.recycler_item_view, parent, false)) {
        private var titleView: TextView = itemView.findViewById(R.id.title_tv)
        private var selectedIV: ImageView = itemView.findViewById(R.id.selected_iv)
        private var contentView: ExpandableTextView = itemView.findViewById(R.id.expand_text_view)
        var linearLayout: LinearLayout = itemView.findViewById(R.id.item_ll)

        fun bind(notesModel: NotesModel) {
            titleView.text = notesModel.title
            contentView.text = notesModel.note
            selectedIV.visibility = if (notesModel.isChecked) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddNoteHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AddNoteHolder(
            inflater,
            parent
        )
    }

    fun addData(notesList: MutableList<NotesModel>) {
        this.notesList = notesList;
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = notesList.size

    override fun onBindViewHolder(holder: AddNoteHolder, position: Int) {
        val notesModel = notesList[position]
        holder.bind(notesModel)
        holder.linearLayout.setOnClickListener {
            if (getCheckedItems().isNotEmpty()) {
                if (getCheckedItems().size == 1 && notesList[position].isChecked)
                    adapterInterface.onDeleteItem(false)
                this.notesList[position].isChecked = !notesList[position].isChecked
                notifyDataSetChanged()
                return@setOnClickListener
            }
            adapterInterface.onItemClick(notesModel)
        }
        holder.linearLayout.setOnLongClickListener {
            if (getCheckedItems().isEmpty()) {
                adapterInterface.onDeleteItem(true)
                this.notesList[position].isChecked = !notesList[position].isChecked
                notifyDataSetChanged()
            }
            true
        }
    }


    fun getCheckedItems(): MutableList<NotesModel> {
        val list: MutableList<NotesModel> = mutableListOf()
        for (model in notesList) {
            if (model.isChecked)
                list.add(model)
        }
        return list
    }

    interface AddNoteAdapterInterface {
        fun onItemClick(notesModel: NotesModel)
        fun onDeleteItem(isSelected: Boolean);
    }

}