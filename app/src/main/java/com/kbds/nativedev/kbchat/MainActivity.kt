package com.kbds.nativedev.kbchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kbds.nativedev.kbchat.databinding.ActivityMainBinding
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
private lateinit var nextIntent: Intent

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val friendUid = intent.getStringExtra("friendUid")
        if(friendUid != null) {
            bottom_nav.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
            chatFragmet = ChatFragment.newInstance()
            val bundle = Bundle()
            bundle.putString("friendUid", friendUid)
            chatFragmet.arguments = bundle
            //ChatFragment 에서 val data = arguments?.getString("friendUid") 로 꺼내서 사용
            supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, chatFragmet).commit()
            nextIntent = Intent(this, LoginActivity::class.java)
            nextIntent.putExtra("friendUid", friendUid)
        } else {
            bottom_nav.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)
            friendFragment = FriendFragment.newInstance()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, friendFragment)
                .commit()
            nextIntent = Intent(this, LoginActivity::class.java)
        }
    }

    private lateinit var callback: OnChangeSettingFragment

    private val bottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener{
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


                // 주석 처리 합니다
//                val intentMain = Intent(this, SettingNaviActivity::class.java)
//                startActivity(intentMain)
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
        fun onLogOut()
    }

}