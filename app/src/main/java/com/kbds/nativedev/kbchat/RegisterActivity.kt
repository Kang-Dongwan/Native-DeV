package com.kbds.nativedev.kbchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kbds.nativedev.kbchat.databinding.ActivityRegisterBinding

private lateinit var auth: FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

//        val email = findViewById<EditText>(R.id.email)
//        val pwd = findViewById<EditText>(R.id.et_pwd)
//        val pwdAgain = findViewById<EditText>(R.id.et_re_pwd)
//        val btnRegi = findViewById<Button>(R.id.btn_regi)

        binding.btnRegi.setOnClickListener {
            if(binding.email.text.isEmpty() || binding.etPwd.text.isEmpty() ){
                Toast.makeText(this, "아이디와 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.email.text).matches()) {
                Toast.makeText(this, "아이디를 이메일 형식으로 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if(binding.etPwd.length() < 6 || binding.etRePwd.length() < 6) {
                Toast.makeText(this, "비밀번호는 6자리 이상 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if(binding.etPwd.text.toString() != binding.etRePwd.text.toString()) {
                Toast.makeText(this, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(binding.email.text.toString(), binding.etPwd.text.toString())
            }
        }
    }

    private fun registerUser(email:String, pwd: String) {
        auth.createUserWithEmailAndPassword(email, pwd)
            .addOnCompleteListener(this) {
                task -> if (task.isSuccessful) {
                finish()
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}