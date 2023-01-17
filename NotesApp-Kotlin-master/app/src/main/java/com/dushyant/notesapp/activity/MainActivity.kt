package com.dushyant.notesapp.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dushyant.notesapp.R
import com.dushyant.notesapp.fragments.AddNoteFragment
import com.dushyant.notesapp.fragments.SignInFragment
import com.dushyant.notesapp.model.NotesModel
import com.dushyant.notesapp.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(),
    SignInFragment.SignInInterface,
    AddNoteFragment.AddNoteInterface {

    private val RC_SIGN_IN: Int = 1
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()
        configureGoogleSignIn()
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.root_container,
                    AddNoteFragment.newInstance(
                        this
                    )
                )
                .commitAllowingStateLoss()
        } else {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.root_container,
                    SignInFragment.newInstance(
                        this
                    )
                )
                .commitAllowingStateLoss()
        }
    }

    //region Option Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                //region Logout
                showLogoutDialog()
                //endregion
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_logout_custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val logoutBtn = dialog.findViewById(R.id.logout_btn) as Button

        logoutBtn.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(
                applicationContext,
                "Bye, Soldier! We had a good time.",
                Toast.LENGTH_LONG
            ).show()
            FirebaseAuth.getInstance().signOut()
            val intent = intent
            finish()
            startActivity(intent)
        }
        dialog.show()
    }

    //endregion

    override fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
//                startActivity(HomeActivity.getLaunchIntent(this))
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.root_container,
                        AddNoteFragment.newInstance(
                            this
                        )
                    )
                    .commitAllowingStateLoss()
            } else {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun postNotes(title: String, content: String) {
        firebaseAuth.uid?.let {
            database = Firebase.database.reference
            val currentTime = System.currentTimeMillis().toString()
            val user =
                NotesModel(
                    title,
                    content,
                    currentTime
                )
            database.child(Constants.users).child(it).child(currentTime).setValue(user)
        }
    }

    override fun updateNotes(title: String, content: String, timestamp: String) {
        firebaseAuth.uid?.let {
            database = Firebase.database.reference
            val user =
                NotesModel(title, content, timestamp)
            database.child(Constants.users).child(it).child(timestamp).setValue(user)
        }
    }

    override fun deleteNotes(notesList: MutableList<NotesModel>) {
        notesList.forEach { note ->
            firebaseAuth.uid?.let {
                database = Firebase.database.reference
                database.child(Constants.users).child(it).child(note.timestamp!!).removeValue()
            }

        }
    }


}