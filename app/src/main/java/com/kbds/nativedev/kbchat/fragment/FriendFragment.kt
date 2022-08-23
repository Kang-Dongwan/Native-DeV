package com.kbds.nativedev.kbchat.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
//import com.google.firebase.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kbds.nativedev.kbchat.AddFriendsActivity
import com.kbds.nativedev.kbchat.MainActivity
import com.kbds.nativedev.kbchat.R
import com.kbds.nativedev.kbchat.adapters.ListAdapter
import kotlinx.android.synthetic.main.fragment_friend.*
import com.google.firebase.database.DataSnapshot //toy
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.kbds.nativedev.kbchat.auth
import kotlinx.android.synthetic.main.item_data_list.*

class TestData(
    private var data1: String? = null,
    private var data2: String? = null,
    private var data3: String? = null,
    private var data4: String? = null //toy
){
    fun getData1(): String? {
        return data1
    }
    fun setData1(name: String) {
        this.data1 = data1
    }
    fun getData2(): String? {
        return data2
    }
    fun setData2(address: String) {
        this.data2 = data2
    }
    fun getData3(): String? {
        return data3
    }
    fun setData3(type: String) {
        this.data3 = data3
    }
    fun getData4(): String? { //toy
        return data4
    }
    fun setData4(type: String) { //toy
        this.data4 = data4
    }
}
class FriendFragment : Fragment() {
    // RecyclerView.adapter에 지정할 Adapter
    private lateinit var listAdapter: ListAdapter
    private lateinit var database: DatabaseReference
    private lateinit var data: MutableMap<String, String>
    private lateinit var data1: MutableMap<String, String>
    val database1 = Firebase.database
    val myRef = database1.getReference("friend")
    val userRef = database1.getReference("user")
    var db = FirebaseFirestore.getInstance()



    var dataList: ArrayList<TestData> = arrayListOf(
        /*TestData("첫 번째 데이터1", "두 번째 데이터1", "세 번째 데이터1"),
        TestData("첫 번째 데이터2", "두 번째 데이터2", "세 번째 데이터2"),
        TestData("첫 번째 데이터3", "두 번째 데이터3", "세 번째 데이터3"),
        TestData("첫 번째 데이터4", "두 번째 데이터4", "세 번째 데이터4"),
        TestData("첫 번째 데이터5", "두 번째 데이터5", "세 번째 데이터5"),
        TestData("첫 번째 데이터6", "두 번째 데이터6", "세 번째 데이터6"),
        TestData("첫 번째 데이터7", "두 번째 데이터7", "세 번째 데이터7"),
        TestData("첫 번째 데이터8", "두 번째 데이터8", "세 번째 데이터8"),
        TestData("첫 번째 데이터9", "두 번째 데이터9", "세 번째 데이터9"),
        TestData("첫 번째 데이터10", "두 번째 데이터10", "세 번째 데이터10"),
        TestData("첫 번째 데이터11", "두 번째 데이터11", "세 번째 데이터11"),
        TestData("첫 번째 데이터12", "두 번째 데이터12", "세 번째 데이터12"),
        TestData("첫 번째 데이터13", "두 번째 데이터13", "세 번째 데이터13"),
        TestData("첫 번째 데이터14", "두 번째 데이터14", "세 번째 데이터14")*/
    )

    companion object {
        fun newInstance() : FriendFragment {
            return FriendFragment()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var my_btn = getView()?.findViewById<Button>(R.id.addFriendBtn)
        dataList.clear();
        var list = dataList
        //var list: ArrayList<TestData> = requireActivity().intent!!.extras!!.get("dataList") as ArrayList<TestData>
        // Fragment에서 전달받은 list를 넘기면서 ListAdapter 생성
        database = FirebaseDatabase.getInstance().reference
        val user = auth.currentUser
        val myMutableList = mutableListOf(TestData("test1","test2","test3", "test4"))//toy
        val myUid = auth.currentUser?.uid.toString()
        Log.d("test", "LSM myUid = " + myUid)

        if (user != null) {
            Log.d("test" ,"phw userUid =" + user.uid)
        };

        if (user != null) {
            var imageUrl = ""

            //------------------------------------------
            // Friend 조회
            //------------------------------------------
            database.child("friend").child(user.uid).get().addOnSuccessListener { it ->
                if(it.exists()) {
                    data = it.value as MutableMap<String, String>
                    for (key in data.keys) {
                        database.child("friend").child(user.uid).child(key).get()
                            .addOnSuccessListener { it1 ->
                                data1 = it1.value as MutableMap<String, String>

                                Log.d("test", "phw dataList.size0 = " + data1.toString()) //uid=-NA-NA5aReWLpsU7sRhL, -NA-N0AH1g8x6AY_7O3L
                                Log.d("test", "LSM dataList.uid = " + data1.get("uid"))
                                Log.d("test", "LSM FRIEND.name = " + data1.get("name"))
                                var friendUid = it1.key
                                var imageHash = HashMap<String, Any>()
                                var friendName = data1.get("name")
                                var friendComment = data1.get("comment")

                                Log.d(
                                    "test", "LSM USER.friendUid.toString() = " + friendUid.toString()
                                )
                                val addValueEventListener =
                                    FirebaseDatabase.getInstance().reference.child("user")
                                        .addValueEventListener(object :
                                            ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                for (snapshot in dataSnapshot.children) {
                                                    var uid = snapshot.key
                                                    var name = snapshot.child("name")
                                                    var comment = snapshot.child("comment")
                                                    var freindUid02 = snapshot.child("uid")
                                                    var profileImageUrl = ""
                                                    if(snapshot.child("profileImageUrl").getValue() == null){
                                                        profileImageUrl = ""
                                                    } else {
                                                        profileImageUrl = snapshot.child("profileImageUrl").getValue().toString()
                                                    }
                                                    Log.d("test", "LSM USER.name = " + name)   //db
                                                    Log.d("test", "LSM USER.friendUid = " + friendUid)
                                                    Log.d("test", "LSM USER.uid = " + uid)
                                                    Log.d("test", "LSM USER.profileImageUrl = " + profileImageUrl)

                                                    if (uid.toString().equals(friendUid.toString()) == true) {
                                                        Log.d("test","LSM USER.name==FRIEND.name " + name.getValue().toString())
                                                        imageHash.put(name.getValue().toString(),profileImageUrl)
                                                        //imageUrl = profileImageUrl.toString()
                                                        Log.d("test", "imageHash.profileImageUrl = " + profileImageUrl)
                                                        var myMutableList1: ArrayList<TestData> = arrayListOf(
                                                            TestData(
                                                                friendName.toString(),
                                                                friendComment.toString(),
                                                                friendUid.toString(),
                                                                profileImageUrl
                                                            )
                                                        )

                                                        Log.d("test", "imageHash.get = " + imageHash.get(data1.get("name").toString()))
                                                        dataList.addAll(myMutableList1);
                                                        list = dataList;
                                                        Log.d("test", "phw dataList.size1 = " + data.keys)
                                                        Log.d("test", "phw dataList.size2 = " + dataList.size)
                                                        listAdapter = ListAdapter(list)
                                                        listView.layoutManager =
                                                            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                                                        // RecyclerView.adapter에 지정
                                                        listView.adapter = listAdapter
                                                    }

                                                }
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {}
                                        })


                            }
                    }
                }
            }

        }
        /*Toast.makeText(
                context, "dataStr = " + data,
                Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener{
        }*/

        addFriendBtn.setOnClickListener {
            val intent = Intent(getActivity(), AddFriendsActivity::class.java)
            startActivity(intent)

        }

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_friend, container, false)
        return view
    }
}