package com.kbds.nativedev.kbchat.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.kbds.nativedev.kbchat.R
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

data class Message(var uid:String = "", var message:String = "", var time:String = "")

class MessageAdapter(private val message: List<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //    class MessageViewHolder(val binding: ItemMessageLeftBinding): RecyclerView.ViewHolder(binding.root)
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private val dateFormatter = SimpleDateFormat ("yyyyMMddHHmmssSS")

    class RightMessageViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var tvMessage: TextView = view.findViewById(R.id.messageItem_textView_message)
        var tvTime: TextView = view.findViewById(R.id.messageItem_textView_time)
    }
    class LeftMessageViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var llDestination: LinearLayout = view.findViewById(R.id.messageItem_layout_destination)
        var ivProfile: ImageView = view.findViewById(R.id.messageItem_imageview_profile)
        var tvName : TextView = view.findViewById(R.id.messageItem_textview_name)
        var tvMessage: TextView = view.findViewById(R.id.messageItem_textView_message)
        var tvTime: TextView = view.findViewById(R.id.messageItem_textView_time)
    }
    class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var tvMessage: TextView = view.findViewById(R.id.messageItem_textView_message)
        var tvTime: TextView = view.findViewById(R.id.messageItem_textView_time)
    }


    enum class MessageItemType {
        TYPE_MESSAGE_RIGHT,
        TYPE_MESSAGE_LEFT,
        TYPE_NOTICE,
        TYPE_ETC
    }

    private var uid = Firebase.auth.currentUser?.uid.toString()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_message_left, viewGroup, false)
//        return MessageViewHolder(ItemMessageLeftBinding.bind(view))
        return when (viewType) {
            MessageItemType.TYPE_MESSAGE_RIGHT.ordinal -> RightMessageViewHolder(
                LayoutInflater.from(viewGroup.context).inflate(R.layout.item_message_right, viewGroup, false)
            )
            MessageItemType.TYPE_MESSAGE_LEFT.ordinal -> LeftMessageViewHolder(
                LayoutInflater.from(viewGroup.context).inflate(R.layout.item_message_left, viewGroup, false)
            )
            else -> NoticeViewHolder(
                LayoutInflater.from(viewGroup.context).inflate(R.layout.item_notice, viewGroup, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        holder.binding.messageItemTextViewMessage.textSize = 20F
//        holder.binding.messageItemTextViewMessage.text = message[position].message
//        holder.binding.messageItemTextViewTime.text = message[position].time
//        if(message[position].uid.equals(uid)) {
//            // 본인 작성 메시지
//            holder.binding.messageItemTextViewMessage.setBackgroundResource(R.drawable.rightbubble)
//            holder.binding.messageItemTextviewName.visibility = View.INVISIBLE
//            holder.binding.messageItemLayoutDestination.visibility = View.INVISIBLE
//            holder.binding.messageItemLinearlayoutMain.gravity = Gravity.RIGHT
//        } else {
//            // 상대방 작성 메시지
//            holder.binding.messageItemTextViewMessage.setBackgroundResource(R.drawable.leftbubble)
//            holder.binding.messageItemTextviewName.visibility = View.VISIBLE
//            holder.binding.messageItemLayoutDestination.visibility = View.VISIBLE
//            holder.binding.messageItemLinearlayoutMain.gravity = Gravity.LEFT
//        }
//        holder.binding.messageItemTextviewName.text = message[position].uid
        when (holder) {
            is RightMessageViewHolder -> {
                var time: String = ""
                time = SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(dateFormatter.parse(message[position].time))
                holder.tvMessage.textSize = 20F
                holder.tvMessage.text = message[position].message
                holder.tvTime.text = time

            }
            is LeftMessageViewHolder -> {
                fireDatabase.child("user").child(message[position].uid).get().addOnSuccessListener {
                    var name: String? = ""
                    var imgUrl: String? = ""
                    var time: String = ""
                    time = SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(dateFormatter.parse(message[position].time))

                    name = it.child("name").getValue().toString()
                    imgUrl = it.child("profileImageUrl").getValue().toString()

                    holder.tvMessage.textSize = 20F
                    holder.tvMessage.text = message[position].message
                    holder.tvTime.text = time
                    holder.tvName.text = name
                    Glide.with(holder.itemView.context)
                        .load(imgUrl)
                        .error(R.drawable.user) // 이미지로드 실패시 로컬 user.png
                        .circleCrop()
                        .into(holder.ivProfile)
                }

            }
            is NoticeViewHolder -> {
                var time: String = ""
                time = SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(dateFormatter.parse(message[position].time))
                holder.tvMessage.textSize = 20F
                holder.tvTime.text = time
            }
            else -> {
            }
        }
    }

    override fun getItemCount(): Int {
        return message.count()
    }

    override fun getItemViewType(position: Int): Int {
        return if(message[position].uid == uid){
            MessageItemType.TYPE_MESSAGE_RIGHT.ordinal
        } else if(message[position].uid != uid) {
            MessageItemType.TYPE_MESSAGE_LEFT.ordinal
        } else {
            MessageItemType.TYPE_NOTICE.ordinal
        }
    }
}