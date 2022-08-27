package com.kbds.nativedev.kbchat.model

import kotlin.collections.HashMap

class ChatRoom (val uid: HashMap<String, Uid> = HashMap()) {
    class Uid(val chatId: HashMap<String, ChatId> = HashMap()){
        class ChatId(val chatName: String){

        }
    }
}