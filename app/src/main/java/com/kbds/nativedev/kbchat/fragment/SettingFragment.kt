package com.kbds.nativedev.kbchat.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.kbds.nativedev.kbchat.R

class SettingFragment : Fragment() {

    companion object {
        fun newInstance() : SettingFragment {
            return SettingFragment()
        }
        const val TAG: String = "SettingFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.fragment_setting)

        //val btnlogout = findViewById<Button>(R.id.btn_logout)

        //btnlogout.setOnClickListener {
            //FirebaseAuth.getInstance().signOut()
        //}


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        val tvEmail = view.findViewById<TextView>(R.id.profile_textview_email)
        val name = view.findViewById<TextView>(R.id.profile_textview_name)
        val comment = view.findViewById<TextView>(R.id.profile_textview_comment)
        val button = view.findViewById<Button>(R.id.profile_button)

        // 현재 로그인된 계정 이메일 보여주기
        val auth = Firebase.auth
        tvEmail.text = auth.currentUser?.email.toString()

        val uid =  auth.currentUser?.uid.toString()
        //name.text = uid

        // database test
        //val database = Firebase.database
        //val userRef = database.getReference("User")
        //userRef.setValue("Hello, World!")
        val database = Firebase.database.reference
        database.child("user").child(uid).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            val map = it.value as HashMap<String, Any>
            name.text = map.get("name").toString()
            comment.text = map.get("comment").toString()

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
/*
        database.child("user").child("uid").child(uid).child("comment").get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            comment.text = "${it.value}"
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
 */

        button?.setOnClickListener{
            if(name?.text!!.isNotEmpty()) {
                database.child("user/$uid/name").setValue(name.text.toString())
                name.clearFocus()
                Toast.makeText(requireContext(), "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
            }
            if(comment?.text!!.isNotEmpty()) {
                database.child("user/$uid/comment").setValue(comment.text.toString())
                comment.clearFocus()
                Toast.makeText(requireContext(), "상태메시지가 변경되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        /*
        userRef.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue<String>()

               // email?.text = userProfile?.email
                name?.text = userProfile?.name
            }
        })
        */
/*
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<String>()
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
*/
        return view
    }

}