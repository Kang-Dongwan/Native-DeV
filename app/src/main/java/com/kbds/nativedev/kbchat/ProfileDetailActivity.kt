package com.kbds.nativedev.kbchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_profile_detail.*

class ProfileDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_detail)

        val name = intent.getStringExtra("name")
        val comment = intent.getStringExtra("comment")
        val profileImageUrl = intent.getStringExtra("profileImageUrl")

        textView1.text = "ProfileImageUrl: $profileImageUrl"
        textView2.text = "Name: $name"
        textView3.text = "Comment: $comment"

    }
}