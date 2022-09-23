package com.kbds.nativedev.kbchat.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.kbds.nativedev.kbchat.R
import kotlinx.android.synthetic.main.activity_profile_detail.*
import kotlinx.android.synthetic.main.fragment_setting.*
import java.io.ByteArrayOutputStream
import java.io.IOException


data class UserInfo(
    val uid : String? = null,
    val email : String? = null,
    val name : String? = null,
    val comment : String? = null)

data class UserImageInfo(
    val uid : String? = null,
    val email : String? = null,
    val name : String? = null,
    val comment : String? = null,
    val profileImageUrl : String? = null)

class SettingFragment : Fragment() {

    companion object {

        private var imageUri : Uri? = null
        private val fireStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://native-dev-chat.appspot.com")
        private val fireDatabase = Firebase.database.reference
        private val user = Firebase.auth.currentUser
        private val uid = user?.uid.toString()

        fun newInstance() : SettingFragment {
            return SettingFragment()
        }
        const val TAG: String = "SettingFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                imageUri = result.data?.data //이미지 경로 원본

                Glide.with(this /* context */)
                    .load(imageUri)
                    .fallback(R.drawable.user)
                    .circleCrop()
                    .into(profile_imageview!!)

                Log.d("이미지", "성공")
            }
            else{
                Log.d("이미지", "실패")
            }
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

        val photo = view?.findViewById<ImageView>(R.id.profile_imageview)

        // 현재 로그인된 계정 이메일 보여주기
        val auth = Firebase.auth
        tvEmail.text = auth.currentUser?.email.toString()

        val uid =  auth.currentUser?.uid.toString()

        val database = Firebase.database.reference

        database.child("user").child(uid).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            if (it.value != null){
                val map = it.value as HashMap<String, Any>

                name.text = map["name"]?.toString()
                comment.text = map["comment"]?.toString()

                var imageUrl = map["profileImageUrl"]?.toString()

                val bitmapRef = fireStorage.child("userImages/${uid}/photo")
                bitmapRef.downloadUrl.addOnCompleteListener { it ->
                    if(it.isSuccessful){
                        Glide.with(this /* context */)
                            .load(if(imageUrl != null) it.result else R.drawable.user)
                            .fallback(R.drawable.user)
                            .placeholder(R.drawable.user)
                            .circleCrop()
                            .into(profile_imageview!!)
                    } else {
                        Glide.with(this /* context */)
                            .load(R.drawable.user)
                            .circleCrop()
                            .into(profile_imageview!!)
                    }
                }
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

        //프로필사진 바꾸기
        photo?.setOnClickListener{
            /*
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT // ACTION_PICK은 사용하지 말것, deprecated + formally
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intent)
            */

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"//MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intent)

            //Toast.makeText(requireContext(), "이미지!", Toast.LENGTH_SHORT).show();
        }

        button?.setOnClickListener{
            if(imageUri != null) {
                registerUserImage(
                    tvEmail.text.toString(),
                    name.text.toString(),
                    comment.text.toString()
                )
            }else{
                registerUser(
                    tvEmail.text.toString(),
                    name.text.toString(),
                    comment.text.toString()
                )
            }

        }

        return view
    }
    private fun registerUser(email:String, name:String, comment: String) {
        val database = Firebase.database.reference

        val taskMap = HashMap<String, Any>()

        taskMap["user/$uid/name"] = name
        taskMap["user/$uid/comment"] = comment

        database.updateChildren(taskMap);

        Toast.makeText(requireContext(), "회원정보가 변경되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun registerUserImage(email:String, name:String, comment: String) {
        val database = Firebase.database.reference
        val user = Firebase.auth.currentUser
        val userId = user?.uid
        val userIdSt = userId.toString()

        val bitmap = compressBitmap(imageUri!!)
        val baos = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            bitmap!!.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, baos)
        } else {
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
        }
        val data = baos.toByteArray()

        val bitmapRef = fireStorage.child("userImages/${uid}/photo")
        val uploadTask: UploadTask = bitmapRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Log.d("uploadTask", "Faliure")
        }.addOnSuccessListener {
            Log.d("uploadTask", "Success = " + bitmapRef.path)

            //다시 진입시 빠르게 이미지 로딩을 하기 위해 이미지뷰에 한번 그려준다.
            bitmapRef.downloadUrl.addOnCompleteListener { it ->
                if(it.isSuccessful){
                    Glide.with(this /* context */)
                        .load(it.result)
                        .fallback(R.drawable.user)
                        .circleCrop()
                        .into(profile_imageview!!)

                    val taskMap = HashMap<String, Any>()

                    taskMap["user/$uid/name"] = name
                    taskMap["user/$uid/comment"] = comment
                    taskMap["user/$uid/profileImageUrl"] = it.result

                    database.updateChildren(taskMap);
                } else {
                    Glide.with(this /* context */)
                        .load(R.drawable.user)
                        .circleCrop()
                        .into(profile_imageview!!)
                }
            }

            Toast.makeText(requireContext(), "회원정보가 변경되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun compressBitmap(imageUri : Uri): Bitmap? {
        try {
            var bitmap = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireActivity().contentResolver, imageUri!!))
            } else {
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
            }

            val stream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, stream)
            val byteArray: ByteArray = stream.toByteArray()
            return getResizedBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size), 500, 500)
        } catch (e: IOException){

        }
        return null
    }

    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()
        return resizedBitmap
    }
}
