package com.kbds.nativedev.kbchat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.kbds.nativedev.kbchat.MainActivity
import com.kbds.nativedev.kbchat.R

class SettingNaviFragment : Fragment() {

    companion object {
        fun newInstance(onChangeSettingFrag: MainActivity.OnChangeSettingFragment): SettingNaviFragment {
            this.onChangeSettingFrag = onChangeSettingFrag
            return SettingNaviFragment()
        }

        private lateinit var onChangeSettingFrag: MainActivity.OnChangeSettingFragment
        //const val TAG: String = "SettingFragment2"
    }

    override fun onCreateView(
        inflater: LayoutInflater,    // inflater : XML을 화면에 사용할 준비(XML -> VIEW)
        container: ViewGroup?,       // container : Fragment에서 사용될 XML의 부모뷰
        savedInstanceState: Bundle?  // 화면 왔다갔다 할때 기록남김
    ): View? {

        val view = inflater.inflate(R.layout.fragment_setting_navi, container, false)

        val btnModify = view.findViewById<Button>(R.id.btn_modify)
        //val btnLogout = view.findViewById<Button>(R.id.btn_logout)


        btnModify!!.setOnClickListener{
            SettingNaviFragment.onChangeSettingFrag?.onModifyUser()
        }

        //return inflater.inflate(R.layout.fragment_setting_navi, container, false)
        //return super.onCreateView(inflater, container, savedInstanceState)
        return view
    }
}