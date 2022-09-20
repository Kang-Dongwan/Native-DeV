package com.kbds.nativedev.kbchat.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.kbds.nativedev.kbchat.R
import kotlinx.android.synthetic.main.activity_profile_detail.*
import kotlinx.android.synthetic.main.fragment_setting.*

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

                profile_imageview.setImageURI(imageUri) //이미지 뷰를 바꿈

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

                name.text = map.get("name")?.toString()
                comment.text = map.get("comment")?.toString()

                var imageUrl = map.get("profileImageUrl")?.toString()
                //Log.i("imageUrl", "Got value ${imageUrl}")


                if(imageUrl != null){
                    Glide.with(this /* context */)
                        .load(imageUrl)
                        .into(photo!!)

                }else{
                    Glide.with(this).load(R.drawable.user)
                        .error(R.drawable.user) // 이미지로드 실패시 로컬 user.png
                        .circleCrop()
                        .into(photo!!)
                }

/*
                Glide.with(this).load(imageUrl)
                    .error(R.drawable.user) // 이미지로드 실패시 로컬 user.png
                    .circleCrop()
                    .into(photo!!)
*/
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

                  if(name?.text!!.isNotEmpty()) {
                      Log.d("tag", name.text.toString())
                      database.child("user/$uid/name").setValue(name.text.toString())
                      name.clearFocus()
                      Toast.makeText(requireContext(), "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                  }
                  if(comment?.text!!.isNotEmpty()) {
                      database.child("user/$uid/comment").setValue(comment.text.toString())
                      comment.clearFocus()
                      Toast.makeText(requireContext(), "상태메시지가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                  }
                  if(imageUri != null){
                      fireStorage.child("userImages/${Companion.uid}/photo").putFile(imageUri!!)
                          .addOnSuccessListener {
                              fireStorage.child("userImages/${Companion.uid}/photo").downloadUrl.addOnSuccessListener {
                                  val photoUri: Uri = it
                                  println("$photoUri")
                                  fireDatabase.child("user/${Companion.uid}/profileImageUrl")
                                      .setValue(photoUri.toString())
                                  Toast.makeText(requireContext(), "프로필사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                              }
                          }
                  }
        }

        return view
    }

}
