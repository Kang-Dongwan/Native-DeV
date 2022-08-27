package com.kbds.nativedev.kbchat

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kbds.nativedev.kbchat.adapters.ListAdapter
import com.kbds.nativedev.kbchat.fragment.TestData
import kotlinx.android.synthetic.main.activity_profile_detail.*
import kotlinx.android.synthetic.main.fragment_friend.*

class ProfileDetailActivity : AppCompatActivity() {

    private lateinit var deleteFriendBtn: Button
    val database = Firebase.database
    val myRef = database.getReference("friend")
    val databaseIns = FirebaseDatabase.getInstance().reference
    val user = auth.currentUser


    private lateinit var listAdapter: ListAdapter
    var dataList: ArrayList<TestData> = arrayListOf(
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_detail)
        dataList.clear();
        var list = dataList
        var imageView = R.id.profileImage
        deleteFriendBtn = findViewById(R.id.deleteFriendBtn)


        val name = intent.getStringExtra("name")
        val comment = intent.getStringExtra("comment")
        val friendUid = intent.getStringExtra("friendUid")
        val profileImageUrl = intent.getStringExtra("profileImageUrl")

        textView1.text = "$name"
        textView2.text = "$comment"
        //textView3.text = "ProfileImageUrl: $profileImageUrl"
        //textView4.text = "FriendUid: $friendUid"
        Glide.with(this).load("$profileImageUrl")
            .error(R.drawable.user) // 이미지로드 실패시 로컬 user.png
            .circleCrop()
            .into(profileImage)

        deleteFriendBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("친구삭제")
                .setMessage("현재 친구를 삭제하시겠습니까?")
                .setPositiveButton("확인",
                DialogInterface.OnClickListener{ dialog, id ->
                    FirebaseDatabase.getInstance().reference.child("user").addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                var friendUid = snapshot.key
                                var name1 = snapshot.child("name")
                                var comment = snapshot.child("comment")
                                var freindUid02 = snapshot.child("uid")

                                Log.d("test", "test000 = "+ name)   //db
                                //Log.d("test", "test111 = "+ userName) //input
                                Log.d("test", "test222 = "+ freindUid02) //input
                                if(name1.value?.equals(name) == true) {
                                    Log.d("test", "test123")
                                    user?.let {
                                        Log.d("FirstFragment", "deleteBtn:userUid:" + user.uid)
                                        myRef.child(user.uid).child(friendUid.toString()).removeValue()
                                        Toast.makeText(this@ProfileDetailActivity, "친구삭제 성공", Toast.LENGTH_LONG).show()
                                        val nextIntent = Intent(this@ProfileDetailActivity, MainActivity::class.java)
                                        startActivity(nextIntent)
                                        return
                                    }
                                }

                                //val map = snapshot.getValue(Map::class.java) as Map<String, String>
                                //val comment = map.get("comment").toString()
                                //val name = map.get("name").toString()
                                Log.d("FirstFragment", "ValueEventListener : " + snapshot.value + " a = " + name + "a.value = "+ comment)
                            }
                            Toast.makeText(this@ProfileDetailActivity, "친구삭제 실패", Toast.LENGTH_LONG).show()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                    //Toast.makeText(this, "친구삭제가 완료돠었습니다.", Toast.LENGTH_SHORT)
                    //    .show()
                })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener{ dialog, id ->
                        Toast.makeText(this, "친구삭제에 실패하였습니다.", Toast.LENGTH_SHORT)
                            .show()
                    })
            builder.show()
        }
    }

}