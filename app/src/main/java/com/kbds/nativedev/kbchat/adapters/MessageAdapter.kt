package com.kbds.nativedev.kbchat.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kbds.nativedev.kbchat.R
import com.kbds.nativedev.kbchat.databinding.ItemMessageBinding
import kotlinx.android.synthetic.main.activity_chat.view.*

data class Message(var uid:String = "", var message:String = "", var time:String = "")

class MessageAdapter(private val message: List<Message>): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    class MessageViewHolder(val binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root)

    private var uid = Firebase.auth.currentUser?.uid.toString()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_message, viewGroup, false)
        return MessageViewHolder(ItemMessageBinding.bind(view))
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.binding.messageItemTextViewMessage.textSize = 20F
        holder.binding.messageItemTextViewMessage.text = message[position].message
        holder.binding.messageItemTextViewTime.text = message[position].time
        if(message[position].uid.equals(uid)) {
            // 본인 작성 메시지
            holder.binding.messageItemTextViewMessage.setBackgroundResource(R.drawable.rightbubble)
            holder.binding.messageItemTextviewName.visibility = View.INVISIBLE
            holder.binding.messageItemLayoutDestination.visibility = View.INVISIBLE
            holder.binding.messageItemLinearlayoutMain.gravity = Gravity.RIGHT
        } else {
            // 상대방 작성 메시지
            holder.binding.messageItemTextViewMessage.setBackgroundResource(R.drawable.leftbubble)
            holder.binding.messageItemTextviewName.visibility = View.VISIBLE
            holder.binding.messageItemLayoutDestination.visibility = View.VISIBLE
            holder.binding.messageItemLinearlayoutMain.gravity = Gravity.LEFT
        }
        holder.binding.messageItemTextviewName.text = message[position].uid

    }

    override fun getItemCount(): Int {
        return message.count()
    }
}