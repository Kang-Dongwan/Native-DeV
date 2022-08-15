package com.kbds.nativedev.kbchat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.kbds.nativedev.kbchat.MainActivity.OnChangeSettingFragment
import com.kbds.nativedev.kbchat.R

private lateinit var auth: FirebaseAuth

class SettingNaviFragment : Fragment() {
    companion object {
        fun newInstance(onChangeSettingFrag: OnChangeSettingFragment): SettingNaviFragment {
            this.onChangeSettingFrag = onChangeSettingFrag
            return SettingNaviFragment()
        }

        private lateinit var onChangeSettingFrag: OnChangeSettingFragment
        const val TAG: String = "SettingFragment2"
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_setting_navi, container, false)

        val btnModify = view.findViewById<Button>(R.id.btn_modify)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)


        btnModify!!.setOnClickListener{
            onChangeSettingFrag?.onModifyUser()
        }

        auth = Firebase.auth

        btnLogout!!.setOnClickListener{
            //Toast.makeText(requireContext(), "로그아웃 성공!", Toast.LENGTH_SHORT).show();

            auth?.signOut()
            onChangeSettingFrag?.onLogOut()
            /*
            val builder = AlertDialog.Builder(this)

            builder.setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", DialogInterface.OnClickListener { dialog, whichButton ->
                    onChangeSettingFrag?.onLogOut()
                })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, whichButton -> })
           builder.show()
           */

        }
        return view
    }

}