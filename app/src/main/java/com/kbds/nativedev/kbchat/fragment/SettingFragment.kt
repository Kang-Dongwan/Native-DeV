package com.kbds.nativedev.kbchat.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kbds.nativedev.kbchat.R
import android.provider.MediaStore
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.ActivityResult
import kotlinx.android.synthetic.main.fragment_setting.*

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

        //setContentView(R.layout.fragment_setting)

        //val btnlogout = findViewById<Button>(R.id.btn_logout)

        //btnlogout.setOnClickListener {
            //FirebaseAuth.getInstance().signOut()
        //}


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                imageUri = result.data?.data //이미지 경로 원본
                profile_imageview.setImageURI(imageUri) //이미지 뷰를 바꿈
/*
                    //fireStorage.child("userImages/$uid/photo").delete().addOnSuccessListener {
                        fireStorage.child("userImages/$uid/photo").putFile(imageUri!!)
                            .addOnSuccessListener {
                                fireStorage.child("userImages/$uid/photo").downloadUrl.addOnSuccessListener {
                                    val photoUri: Uri = it
                                    println("$photoUri")
                                    fireDatabase.child("user/$uid/profileImageUrl")
                                        .setValue(photoUri.toString())
                                    Toast.makeText(requireContext(), "프로필사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    //}
*/
                Log.d("이미지", "성공")
                //Toast.makeText(requireContext(), "프로필사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
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
        //name.text = uid

        // database test
        //val database = Firebase.database
        //val userRef = database.getReference("User")
        //userRef.setValue("Hello, World!")
        val database = Firebase.database.reference

        //firebaseStorage 인스턴스 생성
        //하나의 Storage와 연동되어 있는 경우, getInstance()의 파라미터는 공백으로 두어도 됨
        //하나의 앱이 두개 이상의 Storage와 연동이 되어있 경우, 원하는 저장소의 스킴을 입력
        //getInstance()의 파라미터는 firebase console에서 확인 가능('gs:// ... ')
        //firebaseStorage 인스턴스 생성
        //하나의 Storage와 연동되어 있는 경우, getInstance()의 파라미터는 공백으로 두어도 됨
        //하나의 앱이 두개 이상의 Storage와 연동이 되어있 경우, 원하는 저장소의 스킴을 입력
        //getInstance()의 파라미터는 firebase console에서 확인 가능('gs:// ... ')
        val storage: FirebaseStorage = FirebaseStorage.getInstance()

        //생성된 FirebaseStorage를 참조하는 storage 생성
        //val storageRef = storage.reference
        val storageRef = storage.getReferenceFromUrl("gs://native-dev-chat.appspot.com")
        //Storage 내부의 images 폴더 안의 image.jpg 파일명을 가리키는 참조 생성

        //Storage 내부의 images 폴더 안의 image.jpg 파일명을 가리키는 참조 생성
        //val pathReference = storageRef.child("IMG_2412.jpg")
/*
        storageRef.child("userImages/${Companion.uid}/photo").downloadUrl
            .addOnSuccessListener{
                var imageUrl = it
                Log.i("fireStorage", "Got value ${imageUrl}")
                // Download directly from StorageReference using Glide
                // (See MyAppGlideModule for Loader registration)
                Glide.with(this /* context */)
                    .load(it)
                    .into(photo!!)
            }
            .addOnFailureListener{
                Log.e("fireStorage", "Error getting data", it)
            }
*/

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
                }
            }


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