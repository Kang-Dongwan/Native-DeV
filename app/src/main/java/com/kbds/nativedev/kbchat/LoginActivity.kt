package com.kbds.nativedev.kbchat

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private lateinit var auth: FirebaseAuth

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)

        val login_btn = findViewById<Button>(R.id.login)
        val signUp_btn = findViewById<Button>(R.id.signUp)

        login_btn.setOnClickListener {
            if(email.text.isEmpty() || password.text.isEmpty()){
                Toast.makeText(this, "아이디와 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            } else {
                signIn(email.text.toString(), password.text.toString())
            }
        }

        signUp_btn.setOnClickListener {
            val intentMain = Intent(this, RegisterActivity::class.java)
            startActivity(intentMain)
        }
    }

    private fun signIn(email: String, password: String) {
        val intentMain = Intent(this, MainActivity::class.java)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                task -> if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                    finish()
                    startActivity(intentMain)
                } else {
                    Toast.makeText(this, "아이디 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        val password = findViewById<EditText>(R.id.password)

        if(user == null) {
            password.setText("")
        }
    }
    /*
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null) {
            reload();
        }
    }

    private fun reload() {

    }
     */
}