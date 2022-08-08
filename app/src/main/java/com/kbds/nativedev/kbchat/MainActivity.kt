package com.kbds.nativedev.kbchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.kbds.nativedev.kbchat.fragment.ChatFragment
import com.kbds.nativedev.kbchat.fragment.FriendFragment
import com.kbds.nativedev.kbchat.fragment.SettingFragment
import com.kbds.nativedev.kbchat.fragment.SettingNaviFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setting_navi.*

private lateinit var friendFragment: FriendFragment
private lateinit var chatFragmet: ChatFragment
private lateinit var settingFragmet: SettingFragment
private lateinit var settingNaviFragment: SettingNaviFragment

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_nav.setOnNavigationItemSelectedListener(BottomNavItemSelectedListener)
        friendFragment = FriendFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, friendFragment).commit()

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
                }
                settingFragmet = SettingFragment.newInstance()
                settingNaviFragment = SettingNaviFragment.newInstance(callback)

                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, settingNaviFragment).commit()
                val intentMain = Intent(this, SettingNaviActivity::class.java)
                startActivity(intentMain)
/*
                settingNaviFragment.btn_logout.setOnClickListener {
                    val intentMain = Intent(this, SettingNaviActivity::class.java)
                    startActivity(intentMain)
                }*/
/*
                settingNaviFragment.btn_modify.setOnClickListener {
                    //this.OnChangeSettingFragment
                    //SettingNaviFragment.onChangeSettingFrag?.onModifyUser()
                    //MainActivity.OnChangeSettingFragment.onModifyUser()
                    //OnChangeSettingFragment.onModifyUser
                    //MainActivity.OnChangeSettingFragment?.

                }
*/

            }
        }
        true
    }

    interface OnChangeSettingFragment {
        fun onModifyUser()
    }


}