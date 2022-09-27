package com.kbds.nativedev.kbchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kbds.nativedev.kbchat.databinding.ActivityRegisterBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.database.DatabaseReference
import com.kbds.nativedev.kbchat.fragment.SettingFragment
import kotlinx.android.synthetic.main.fragment_setting.*
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.bumptech.glide.Glide
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.widget.ImageView

private lateinit var auth: FirebaseAuth
lateinit var database: DatabaseReference

data class UserInfo(
    val email : String? = null,
    val name : String? = null,
    val uid : String? = null)

data class UserImageInfo(
    val email : String? = null,
    val name : String? = null,
    val profileImageUrl : String? = null,
    val uid : String? = null)

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var imageUri : Uri? = null

    private fun navigateGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        /* 갤러리에서 이미지를 선택한 후, 프로필 이미지뷰를 수정하기 위해 갤러리에서 수행한 값을 받아오는 startActivityForeResult를 사용한다.*/
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 예외처리
        if (resultCode != RESULT_OK)
            return

        when (requestCode) {
            // 2000: 이미지 컨텐츠를 가져오는 액티비티를 수행한 후 실행되는 Activity 일 때만 수행하기 위해서
            2000 -> {
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    imageUri = selectedImageUri
                    binding.etImageview.setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    // 권한 요청 승인 이후 실행되는 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    navigateGallery()
                else
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                //
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        database = Firebase.database.reference

//        val email = findViewById<EditText>(R.id.email)
//        val pwd = findViewById<EditText>(R.id.et_pwd)
//        val pwdAgain = findViewById<EditText>(R.id.et_re_pwd)
//        val btnRegi = findViewById<Button>(R.id.btn_regi)

        //프로필사진 바꾸기
        binding.etImageview.setOnClickListener{
            when {
                // 갤러리 접근 권한이 있는 경우
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                -> {
                    navigateGallery()
                }

                // 갤러리 접근 권한이 없는 경우 & 교육용 팝업을 보여줘야 하는 경우
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                -> {
                    showPermissionContextPopup()
                }

                // 권한 요청 하기(requestPermissions) -> 갤러리 접근(onRequestPermissionResult)
                else -> requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            }

        }

        binding.btnRegi.setOnClickListener {
            if(binding.email.text.isEmpty() || binding.etPwd.text.isEmpty()){
                Toast.makeText(this, "아이디와 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.email.text).matches()) {
                Toast.makeText(this, "아이디를 이메일 형식으로 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if(binding.etPwd.length() < 6 || binding.etRePwd.length() < 6) {
                Toast.makeText(this, "비밀번호는 6자리 이상 입력해 주세요", Toast.LENGTH_SHORT).show()
            } else if(binding.etPwd.text.toString() != binding.etRePwd.text.toString()) {
                Toast.makeText(this, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()
            } else if(binding.etTextviewName.text.isEmpty()){
                Toast.makeText(this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("email", "$binding.email.text")
                if(imageUri==null) {
                    registerUser(
                        binding.email.text.toString(),
                        binding.etPwd.text.toString(),
                        binding.etTextviewName.text.toString()
                    )
                }else{
                    registerUserImage(
                        binding.email.text.toString(),
                        binding.etPwd.text.toString(),
                        binding.etTextviewName.text.toString()
                    )
                }
            }
        }
    }

    private fun registerUser(email:String, pwd: String, name: String) {
        auth.createUserWithEmailAndPassword(email, pwd)
            .addOnCompleteListener(this) {
                    task -> if (task.isSuccessful) {
                finish()
                val user = Firebase.auth.currentUser
                val userId = user?.uid
                val userIdSt = userId.toString()

                val userInfo = UserInfo(email.toString(), name.toString(), userIdSt)
                database.child("user").child(userId.toString()).setValue(userInfo)

                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
         }
    }

    private fun registerUserImage(email:String, pwd: String, name: String) {
        auth.createUserWithEmailAndPassword(email, pwd)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = Firebase.auth.currentUser
                    val userId = user?.uid
                    val userIdSt = userId.toString()
                    Log.d("이미지 URI", "$imageUri")
                    FirebaseStorage.getInstance()
                        .reference.child("userImages").child("$userIdSt/photo").putFile(imageUri!!)
                        .addOnSuccessListener {
                            var userProfile: Uri? = null
                            FirebaseStorage.getInstance().reference.child("userImages")
                                .child("$userIdSt/photo").downloadUrl
                                .addOnSuccessListener {
                                    userProfile = it
                                    Log.d("이미지 URL", "$userProfile")
                                    val userInfo = UserImageInfo(
                                        email.toString(),
                                        name.toString(),
                                        userProfile.toString(),
                                        userId.toString()
                                    )
                                    database.child("user").child(userId.toString())
                                        .setValue(userInfo)
                                    finish()
                                }
                        }

                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

