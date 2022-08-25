package com.kbds.nativedev.kbchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kbds.nativedev.kbchat.adapters.ListAdapter
import com.kbds.nativedev.kbchat.fragment.TestData
import kotlinx.android.synthetic.main.activity_profile_detail.*
import kotlinx.android.synthetic.main.fragment_friend.*

class ProfileDetailActivity : AppCompatActivity() {
    private val imageView: Any
        get() {
            TODO()
        }
    private lateinit var listAdapter: ListAdapter
    var dataList: ArrayList<TestData> = arrayListOf(
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_detail)
        dataList.clear();
        var list = dataList

        var imageView = R.id.profileImage

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
    }

}