package com.kbds.nativedev.kbchat.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kbds.nativedev.kbchat.R
import com.kbds.nativedev.kbchat.fragment.TestData

// 리스트 데이터를 넘겨받아야 한다.
class ListAdapter (private var list: MutableList<TestData>): RecyclerView.Adapter<ListAdapter.ListItemViewHolder> () {

    // inner class로 ViewHolder 정의
    inner class ListItemViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {

        var data1Text: TextView = itemView!!.findViewById(R.id.data1Text)
        var data2Text: TextView = itemView!!.findViewById(R.id.data2Text)
        var data3Text: TextView = itemView!!.findViewById(R.id.data3Text)
        var data4Text: TextView = itemView!!.findViewById(R.id.data4Text)//toy
        var data5Text: TextView = itemView!!.findViewById(R.id.data5Text)//blockYn
        var imageView: ImageView = itemView!!.findViewById(R.id.home_item_iv)//toy

        // onBindViewHolder의 역할을 대신한다.
        fun bind(data: TestData, position: Int) {
            data1Text.text = data.getData1()
            data2Text.text = data.getData2()
            data3Text.text = data.getData3()
            data5Text.text = data.getData5()
            //data4Text.text = data.getData4()//toy
            Log.d("test", "LSM USER.name==FRIEND.name01 :" + data1Text.text)
            Log.d("test", "LSM USER.name==FRIEND.imageUrl :" + data.getData4())

            Glide.with(this.imageView).load(data.getData4())
                .error(R.drawable.user) // 이미지로드 실패시 로컬 user.png
                .circleCrop()
                .into(imageView)

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,data,pos)
                }
            }
        }
    }

    // ViewHolder에게 item을 보여줄 View로 쓰일 item_data_list.xml를 넘기면서 ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_data_list, parent, false)
        return ListItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    // ViewHolder의 bind 메소드를 호출한다.
    override fun onBindViewHolder(holder: ListAdapter.ListItemViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    interface OnItemClickListener{
        fun onItemClick(v:View, data: TestData, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }
}
