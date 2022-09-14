package com.kbds.nativedev.kbchat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.kbds.nativedev.kbchat.adapters.Message
import com.kbds.nativedev.kbchat.adapters.MessageAdapter
import com.kbds.nativedev.kbchat.databinding.ActivityChatBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity: AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private var message = arrayListOf<Message>()
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var chatRoomUid : String? = null
    private var destinationUid : String? = null
    private var uid : String? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //메세지를 보낸 시간
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()

        destinationUid = intent.getStringExtra("destinationUid")
        uid = Firebase.auth.currentUser?.uid.toString()
        Log.d("test", "채팅 진입")

        binding.chatActivityButton.setOnClickListener {
            Log.d("클릭 시 dest", "$destinationUid")
            /*
            val chatModel = ChatModel()
            chatModel.users.put(uid.toString(), true)
            chatModel.users.put(destinationUid!!, true)
            val comment = Comment(uid, editText.text.toString(), curTime)
            */
            /*
            if(chatRoomUid == null){
                imageView.isEnabled = false
                fireDatabase.child("chatrooms").push().setValue(chatModel).addOnSuccessListener {
                    //채팅방 생성
                    checkChatRoom()
                    //메세지 보내기
                    Handler().postDelayed({
                        println(chatRoomUid)
                        fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                        messageActivity_editText.text = null
                    }, 1000L)
                    Log.d("chatUidNull dest", "$destinationUid")
                }
            }else{
                fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                messageActivity_editText.text = null
                Log.d("chatUidNotNull dest", "$destinationUid")
            }

             */
        }
        checkChatRoom()
    }

    private fun checkChatRoom(){
        Log.d("test", "채팅 진입")
        fireDatabase.child("chat").child("s3T6nlgYa0QJsIqyDwj2AukmhUi212341234").orderByChild("time")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("test", "DB 조회")
                    for (item in snapshot.children){
                        var cuid = item.child("uid").getValue().toString()
                        var message1 = item.child("message").getValue().toString()
                        var time = item.child("time").getValue().toString()
                        Log.d("test", "cuid = $cuid")
                        Log.d("test", "message = $message1")
                        Log.d("test", "time = $time")
                        val data = item.getValue(Message::class.java)
                        if (data != null) {
                            message.add(data)
                        }
                        binding.messageActivityRecyclerview.layoutManager = LinearLayoutManager(this@ChatActivity)
                        binding.messageActivityRecyclerview.adapter = MessageAdapter(message)
                        /*
                        val chatModel = item.getValue<ChatModel>()
                        if(chatModel?.users!!.containsKey(destinationUid)){

                        }
                        */
                    }
                }
            })
    }
}