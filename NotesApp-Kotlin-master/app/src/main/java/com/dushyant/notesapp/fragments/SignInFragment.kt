package com.dushyant.notesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dushyant.notesapp.R
import com.google.android.gms.common.SignInButton


class SignInFragment : Fragment() {

    private lateinit var signInInterface: SignInInterface

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        view.findViewById<SignInButton>(R.id.google_button).setOnClickListener {
            signInInterface.signInGoogle()
        }

        return view;
    }


    companion object {
        @JvmStatic
        fun newInstance(signInterface: SignInInterface) = SignInFragment()
            .apply {
                this.signInInterface = signInterface
            }
    }

    interface SignInInterface {
        fun signInGoogle()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}