package com.kbds.nativedev.kbchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kbds.nativedev.kbchat.fragment.ChatFragment
import com.kbds.nativedev.kbchat.fragment.FriendFragment
import com.kbds.nativedev.kbchat.fragment.SettingFragment
import com.kbds.nativedev.kbchat.fragment.SettingNaviFragment
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent

private lateinit var friendFragment: FriendFragment
private lateinit var chatFragmet: ChatFragment
private lateinit var settingFragmet: SettingFragment
private lateinit var settingNaviFragment: SettingNaviFragment
private lateinit var nextIntent: Intent

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_nav.setOnNavigationItemSelectedListener(BottomNavItemSelectedListener)
        friendFragment = FriendFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, friendFragment).commit()
        nextIntent = Intent(this, LoginActivity::class.java)
    }

    private lateinit var callback: OnChangeSettingFragment

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
                callback = object : OnChangeSettingFragment {
                    override fun onModifyUser() {
                        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, settingFragmet).commit()
                    }
                    override fun onLogOut() {
                        //val nextIntent = Intent(this, LoginActivity::class.java)
                        startActivity(nextIntent)
                    }
                }
                settingFragmet = SettingFragment.newInstance()
                settingNaviFragment = SettingNaviFragment.newInstance(callback)

                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, settingNaviFragment).commit()
            }
        }
        true
    }

    interface OnChangeSettingFragment {
        fun onModifyUser()
        fun onLogOut()
    }
}