package com.kbds.nativedev.kbchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kbds.nativedev.kbchat.fragment.ChatFragment
import com.kbds.nativedev.kbchat.fragment.FriendFragment
import com.kbds.nativedev.kbchat.fragment.SettingFragment
import kotlinx.android.synthetic.main.activity_main.*

private lateinit var friendFragment: FriendFragment
private lateinit var chatFragmet: ChatFragment
private lateinit var settingFragmet: SettingFragment

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    //--2022.08.23 뒤로가기
    interface onBackPressedListener{
        fun onBackPressed()
    }

    //--2022.08.23 뒤로가기
    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("MainActivity Back test","commit")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_nav.setOnNavigationItemSelectedListener(BottomNavItemSelectedListener)
        friendFragment = FriendFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, friendFragment).commit()
    }

    private val BottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener{
        Log.d("itemId", "${it.itemId}")
        Log.d("menu_friend", "${R.id.menu_friend}")
        Log.d("menu_chat", "${R.id.menu_chat}")
        Log.d("menu_setting", "${R.id.menu_setting}")
        when(it.itemId){
            R.id.menu_friend -> {
                friendFragment = FriendFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, friendFragment).commit()
            }
            R.id.menu_chat -> {
                chatFragmet = ChatFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, chatFragmet).commit()
            }
            R.id.menu_setting -> {
                settingFragmet = SettingFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, settingFragmet).commit()
            }
        }
        true
    }
}