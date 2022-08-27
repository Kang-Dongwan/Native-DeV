package com.kbds.nativedev.kbchat.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kbds.nativedev.kbchat.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.kbds.nativedev.kbchat.MainActivity
import com.kbds.nativedev.kbchat.model.ChatRoom
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

// 파이어베이스 접근하기 위한 객체 생성
//private lateinit var database: DatabaseReference
private val database = FirebaseDatabase.getInstance().reference


data class ChatListModel(
    var uid: String? = null,
    var chatId: String? = null,
    var chatName: String? = null
)

class ChatFragment : Fragment() {
    companion object {
        fun newInstance() : ChatFragment {
            return ChatFragment()
        }
    }


    //Database
    //val database = Firebase.database.reference


    // 메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    // 뷰가 생성되었을때
    // 프레그먼트와 레이아웃(xml)을 연결시켜주는 부분
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // inflater : xml로 정의된 view를 실제 객체화 시키는 용도
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.chatFragment_recyclerview)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecyclerViewAdapter()


        return view
    }

    // 리사이클러뷰에 데이터 연결
    // adpater의 역할 : 데이터 리스트를 실제 눈으로 볼 수 있게 item으로 변환하는 중간다리 역할
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>(){


        // Firebase 데이터베이스에서 데이터를 수신하고 ArrayList를 채운다, 그리고 ArrayList가 recyclerview 어댑터의 데이터소르로 사용됩니다
        // 채팅방 리스트
        //private val chatModel = ArrayList<ChatRoom>()
        private val chatModel = ArrayList<ChatListModel>()
        private var uid : String? = null        // ?는 uid가 null일수도 있음을 표시
        private val destinationUsers : ArrayList<String> = arrayListOf()


        init {
            // 사용자의 uid
            uid = Firebase.auth.currentUser?.uid.toString()
            println("uid :" + uid)


//            database.child("chatList").child("uid").child("chatId").child("chatName").get().addOnSuccessListener {
//                Log.i("firebase", "Got value ${it.value}")
//
//            }.addOnFailureListener{
//                Log.e("firebase", "Error getting data", it)
//            }

            //println("데이터 : " + database.child("chatList").child(uid!!).child("chatId").child("chatName").get().toString())



            // 자기 자신이 포함된 채팅방의 uid를 가져옴
            database.child("chatList").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatModel.clear()
                    println("onDataChange!!!!!")

                    for (data in snapshot.children){
                        println("data : " + data)
                        var uid = data.child("uid").toString()
                        var chatId = data.child("chatId").toString()
                        var chatName = data.child("chatName").toString()

                        val chatLstMdl = ChatListModel(uid, chatId, chatName)
                        chatModel.add(chatLstMdl)
                        //println("data : " + data)
                    }
                    notifyDataSetChanged()
                }


            })

        }


        // onCreateViewHolder는 RecyclerView의 아이템으로 만들어 두었던 list_item.xml을 LayoutInflater하여 뷰의 형태로 변환
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            println("onCreateViewHolder!!!!!")

            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false))
        }




        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.chat_item_imageview)
            val textView_title : TextView = itemView.findViewById(R.id.chat_textview_title)
            val textView_lastMessage : TextView = itemView.findViewById(R.id.chat_item_textview_lastmessage)
        }

        // recyclerview가 viewholder를 가져와 데이터를 연결할때 호출
        // 적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃을 채움
        // 항목뷰에 데이터를 연결
        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            var destinationUid: String? = null

            println("onBindViewHolder!!!!!")


            //채팅방에 있는 유저 모두 체크
            for (user in chatModel[position].uid.toString()) {
                if (!user.equals(uid)) {
                    destinationUid = user.toString()
                    destinationUsers.add(destinationUid)
                }
            }


            database.child("chatList").child(uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
//                    val friend = snapshot.getValue<Friend>()
//                    Glide.with(holder.itemView.context).load(friend?.profileImageUrl)
//                        .apply(RequestOptions().circleCrop())
//                        .into(holder.imageView)
//                    holder.textView_title.text = friend?.name
                }
            })

            //채팅창 선택시 이동
            holder.itemView.setOnClickListener {
                val intent = Intent(context, MainActivity::class.java)

                intent.putExtra("destinationUid", destinationUsers[position])
                context?.startActivity(intent)
            }

        }

        // 뿌려줄 데이터의 전체 길이를 리턴  (아이템갯수)
        override fun getItemCount(): Int {
            return chatModel.size
        }


    }


}