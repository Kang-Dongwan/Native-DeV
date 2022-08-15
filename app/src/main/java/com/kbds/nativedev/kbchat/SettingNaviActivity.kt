package com.kbds.nativedev.kbchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.kbds.nativedev.kbchat.MainActivity.OnChangeSettingFragment
import com.kbds.nativedev.kbchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.widget.Toast
import com.kbds.nativedev.kbchat.MainActivity
import com.kbds.nativedev.kbchat.LoginActivity
import com.kbds.nativedev.kbchat.fragment.SettingNaviFragment
import android.content.Intent as Intent1

private lateinit var auth: FirebaseAuth
private lateinit var settingNaviFragment: SettingNaviFragment

class SettingNaviActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_setting_navi)

        //val view = inflater.inflate(R.layout.fragment_setting_navi, container, false)


        val fragmentManager = supportFragmentManager
        val fragmentSettingNavi = SettingNaviFragment()

        val btnModify = findViewById<Button>(R.id.btn_modify)
        val btnLogout = findViewById<Button>(R.id.btn_logout)

        //settingNaviFragment = SettingNaviFragment.newInstance(callback)
        //supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, settingNaviFragment).commit()

        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.btn_logout, fragmentSettingNavi)
        //Transaction 별로
        (findViewById<Button>(R.id.btn_logout)).setOnClickListener {

            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "로그아웃 성공!!!", Toast.LENGTH_SHORT).show();
            val intentMain = Intent(this, LoginActivity::class.java)
            startActivity(intentMain)
            //transaction.commit()
        }
        btnModify.setOnClickListener {
            //    onChangeSettingFrag?.onModifyUser()
            //SettingNaviFragment.onChangeSettingFrag?.onModifyUser()
            //MainActivity.OnChangeSettingFragment.onModifyUser()
            //OnChangeSettingFragment.onModifyUser
            //MainActivity.OnChangeSettingFragment?.onModifyUser()
            //OnChangeSettingFragment.onModifyUser()

        }

        //auth = Firebase.auth
/*
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            //auth.signOut()
            //Firebase.auth.signOut()
            //private lateinit var googleSignInClient : googleSignInClient
            //googleSignInClient.signOut().addOnCompleteListener(this) {
            //}

            Toast.makeText(this, "로그아웃 진짜 성공!", Toast.LENGTH_SHORT).show();
            val intentMain = Intent(this, LoginActivity::class.java)
            startActivity(intentMain)
        }
*/
    }

    interface OnChangeSettingFragment {
        fun onModifyUser()
    }

}