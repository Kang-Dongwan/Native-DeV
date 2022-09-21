package com.kbds.nativedev.kbchat

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

data class ChatList(var charImageUrl:String = "", var chatName:String = "", var delYn:String = "N", var lastMessage:String = "", var msgCnt:String = "0", var visitYn:String = "Y")

class ChatActivity: AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private var messageList = arrayListOf<Message>()
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var chatId : String? = null
    private var friendUid : String? = null
    private var uid : String? = null
    private val dateFormatter = SimpleDateFormat ("yyyyMMddHHmmssSS")

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        friendUid = intent.getStringExtra("friendUid")
        chatId = intent.getStringExtra("chatId")
        uid = Firebase.auth.currentUser?.uid.toString()
        Log.d("test", "채팅 진입")

        binding.chatActivityButton.setOnClickListener {
            val msgMap : HashMap<String, Message> = HashMap()
            val message = Message()
            message.uid = uid.toString()
            message.message = binding.chatActivityEditText.text.toString()
            dateFormatter.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            message.time = dateFormatter.format(Date().time)
            msgMap.put("msg", message)

            if(chatId == null) {
                Log.d("test", "채팅룸 없음")
                val chatList = ChatList()
                fireDatabase.child("user").child("$friendUid").get().addOnSuccessListener {
                    var name: String? = ""
                    var imgUrl: String? = ""
                    name = it.child("name").getValue().toString()
                    imgUrl = it.child("profileImageUrl").getValue().toString()

                    chatList.chatName = name
                    chatList.charImageUrl = imgUrl

                    //fireDatabase.child("chatList").child("$uid").push().setValue(chatList)
                    chatId = fireDatabase.child("chatList").child("$uid").push().getKey()
                    fireDatabase.child("chatList").child("$uid").child("$chatId").setValue(chatList)
                    fireDatabase.child("chatList").child("$friendUid").child("$chatId").setValue(chatList)

                    messageList()

                    Handler(Looper.getMainLooper()).postDelayed({
                        fireDatabase.child("chat").child("$chatId").push().setValue(message)
                    }, 1000L)
                }
            } else {
                Log.d("test", "채팅룸 존재")
                fireDatabase.child("chat").child("$chatId").push().setValue(message)
            }
            binding.chatActivityEditText.text = null
        }
        messageList()
    }

    private fun messageList(){
        Log.d("test", "채팅 진입")
        fireDatabase.child("chat").child("$chatId").orderByChild("time")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("test", "DB 조회")
                    messageList.clear()
                    for (item in snapshot.children){
                        var cuid = item.child("uid").getValue().toString()
                        var message1 = item.child("message").getValue().toString()
                        var time = item.child("time").getValue().toString()
                        Log.d("test", "cuid = $cuid")
                        Log.d("test", "message = $message1")
                        Log.d("test", "time = $time")
                        val data = item.getValue(Message::class.java)
                        if (data != null) {
                            messageList.add(data)
                        }
                        binding.messageActivityRecyclerview.layoutManager = LinearLayoutManager(this@ChatActivity)
                        binding.messageActivityRecyclerview.adapter = MessageAdapter(messageList)
                        binding.messageActivityRecyclerview.scrollToPosition(messageList.size - 1)
                    }
                }
            })
    }
}
