package com.dushyant.notesapp.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class NotesModel(
    var title: String? = "",
    var note: String? = "",
    var timestamp: String? = "",
    var isChecked: Boolean = false
)