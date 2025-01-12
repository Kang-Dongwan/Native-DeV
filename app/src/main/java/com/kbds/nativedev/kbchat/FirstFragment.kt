package com.kbds.nativedev.kbchat

//import com.google.firebase.auth.ktx.auth
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.kbds.nativedev.kbchat.adapters.ListAdapter
import com.kbds.nativedev.kbchat.databinding.FragmentFirstBinding
import com.kbds.nativedev.kbchat.fragment.FriendFragment
import kotlinx.android.synthetic.main.fragment_first.*


class TestData(
    private var data1: String? = null,
    private var data2: String? = null,
    private var data3: String? = null,

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

}

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
data class UserModel(
    var uid: String? = null,
    var name: String? = null,
    var comment: String? = null,
    var blockYn: String? = null
)
class FirstFragment : Fragment(), MainActivity.onBackPressedListener, View.OnClickListener {

    private var _binding: FragmentFirstBinding? = null
    val database = Firebase.database
    val myRef = database.getReference("friend")
    val databaseIns = FirebaseDatabase.getInstance().reference
    var db = FirebaseFirestore.getInstance()
    private lateinit var listAdapter: ListAdapter
    private lateinit var friendFragment: FriendFragment

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = FirebaseAuth.getInstance();
        val user = auth.currentUser

        /*binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }*/
        //메뉴 세팅
        setHasOptionsMenu(true)

        friendSerchBtn.setOnClickListener {
            var userId: String = idTv!!.text.toString()
            var userName: String = nameTv!!.text.toString()
            var userComment: String = commentTv!!.text.toString();

            FirebaseDatabase.getInstance().reference.child("user").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var friendUid = snapshot.key
                        var name = snapshot.child("email")
                        var comment = snapshot.child("comment")
                        Log.d("test", "test000 = "+ name)   //db
                        Log.d("test", "test111 = "+ userName) //input
                        Log.d("test", "test222 = "+ comment) //input
                        if(name.value?.equals(userName) == true) {
                            Log.d("test", "test123")
                            user?.let {
                                databaseIns.child("friend").child(user.uid).get().addOnSuccessListener {
                                    //val friendKey = myRef.child(user.uid).push().key
                                    val friendKey = friendUid
                                    //val friend = UserModel(friendKey, userName, userComment)
                                    val friend = UserModel(friendKey, name.getValue() as String?,
                                        comment.getValue() as String?, "N"
                                    )
                                    if (friendKey != null) {
                                        if (friendUid != null) {
                                            myRef.child(user.uid).child(friendUid).setValue(friend).addOnCompleteListener {
                                                Log.d("test", "Success")
                                                idTv!!.text.clear()
                                                nameTv!!.text.clear()
                                                commentTv!!.text.clear()
                                            }.addOnFailureListener { err ->
                                                err.message?.let { it1 -> Log.d("error", it1) }
                                            }
                                            activity?.let{
                                                val intent = Intent(context, MainActivity::class.java)
                                                startActivity(intent)
                                            }
                                        }
                                    }
                                    /*myRef.child(user.uid).push().setValue("test123")
                                    myRef.child(user.uid).child("test123").child("name").push().setValue(userName)
                                    myRef.child(user.uid).child("test123").child("comment").push().setValue(userComment)*/
                                }
                            }
                            Toast.makeText(getActivity(), "Success add User", Toast.LENGTH_LONG).show()
                            return;
                            /*val navController = findNavController()
                            navController.run {
                                popBackStack()
                                navigate(R.id.FirstFragment)
                            }*/

                            /*val fm: FragmentManager = activity!!.supportFragmentManager
                            fm.executePendingTransactions()
                            Log.d("phw fragment test 111", "phwtest00 "+ fm.toString())
                            var frg: Fragment? = null
                            frg =
                                fm.findFragmentByTag("friendTag")
                            Log.d("phw fragment test 111", "phwtest000 "+ frg.toString())
                            val ft: FragmentTransaction =
                                fm.beginTransaction()
                            if (frg != null) {
                                ft.detach(frg)
                                Log.d("phw fragment test 111", "phwtest001")
                            }
                            if (frg != null) {
                                ft.attach(frg)
                                Log.d("phw fragment test 222", "phwtest002")
                            }
                            ft.commit()*/


                        }

                        //val map = snapshot.getValue(Map::class.java) as Map<String, String>
                        //val comment = map.get("comment").toString()
                        //val name = map.get("name").toString()
                        Log.d("FirstFragment", "ValueEventListener : " + snapshot.value + " a = " + name + "a.value = "+ comment.value)
                    }
                    Toast.makeText(getActivity(), "Not Found User", Toast.LENGTH_LONG).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
        addBtn.setOnClickListener {

            //val intent = Intent(this, RegisterActivity::class.java)  // 인텐트를 생성해줌,
            //startActivity(intent)  // 화면 전환을 시켜줌
            var userId: String = idTv!!.text.toString()
            var userName: String = nameTv!!.text.toString()
            var userComment: String = commentTv!!.text.toString();
            /*if (userIdStr.isEmpty()) {
                Toast.makeText(this@FirstFragment, "Please check name", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            if (userEmailStr.isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Please check Email", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            if (userPassStr.isEmpty()) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Please check Password",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return
            }*/
            if (userId.isNotEmpty() && userName.isNotEmpty() && userComment.isNotEmpty()) {
                user?.let {
                    databaseIns.child("friend").child(user.uid).get().addOnSuccessListener {
                        val friendKey = myRef.child(user.uid).push().key
                        val friend = UserModel(friendKey, userName, userComment, "false")
                        if (friendKey != null) {
                            myRef.child(user.uid).child(friendKey).setValue(friend).addOnCompleteListener {
                                Log.d("test", "Success")
                                idTv!!.text.clear()
                                nameTv!!.text.clear()
                                commentTv!!.text.clear()
                            }.addOnFailureListener {
                                    err ->
                                err.message?.let { it1 -> Log.d("error", it1) }
                            }
                        }
                        /*myRef.child(user.uid).push().setValue("test123")
                        myRef.child(user.uid).child("test123").child("name").push().setValue(userName)
                        myRef.child(user.uid).child("test123").child("comment").push().setValue(userComment)*/
                    }
                }
                //val intent = Intent(getActivity(), FriendFragment::class.java)
                //startActivity(intent)

                /*addFriendBtn.setOnClickListener {
                    val intent = Intent(getActivity(), FriendFragment::class.java)
                    startActivity(intent)
                }*/
            }
        }

        //--toy
        deleteBtn.setOnClickListener {
            Log.d("FirstFragment", "deleteBtn !")
            var userId: String = idTv!!.text.toString()
            var userName: String = nameTv!!.text.toString()
            var userComment: String = commentTv!!.text.toString();
            Log.d("USER NAME", "USER NAME 01 = "+ userName)   //input

            if (userName.isNotEmpty()) {
                Log.d("USER NAME", "USER NAME 02 = "+ userName)   //input
                FirebaseDatabase.getInstance().reference.child("user").addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            var friendUid = snapshot.key
                            var name = snapshot.child("name")
                            var comment = snapshot.child("comment")
                            var freindUid02 = snapshot.child("uid")

                            Log.d("test", "test000 = "+ name)   //db
                            Log.d("test", "test111 = "+ userName) //input
                            Log.d("test", "test222 = "+ freindUid02) //input
                            if(name.value?.equals(userName) == true) {
                                Log.d("test", "test123")

                                user?.let {
                                    Log.d("FirstFragment", "deleteBtn:userUid:" + user.uid)
                                    myRef.child(user.uid).child(friendUid.toString()).removeValue()
                                }
                                Toast.makeText(getActivity(), "Success Delte User", Toast.LENGTH_LONG).show()
                                return
                            }

                            //val map = snapshot.getValue(Map::class.java) as Map<String, String>
                            //val comment = map.get("comment").toString()
                            //val name = map.get("name").toString()
                            Log.d("FirstFragment", "ValueEventListener : " + snapshot.value + " a = " + name + "a.value = "+ comment)
                        }
                        Toast.makeText(getActivity(), "Not Found User", Toast.LENGTH_LONG).show()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
            /*var userId: String = idTv!!.text.toString()
            var userName: String = nameTv!!.text.toString()
            var userComment: String = commentTv!!.text.toString();

            if (userName.isNotEmpty()) {
                user?.let {
                    Log.d("FirstFragment", "deleteBtn:userName:" + userName)

                    databaseIns.child("friend").child(user.uid).get().addOnSuccessListener {
                        val friendKey = myRef.child(user.uid).push().key
                        val friend = UserModel(friendKey, userName, userComment)

                        if (friendKey != null) {
                            Log.d("FirstFragment", "deleteBtn:friendKey:" + friendKey)
                            myRef.child(user.uid).child(friendKey)
                                .setValue(friend).addOnCompleteListener

                            mStorage.getReference().child("userImages").child("uid/")
                                .child(contentslist.get(position).photoName).delete()
                        }
                    }
                }
            }*/

        }//end deleteBtn

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        val auth = FirebaseAuth.getInstance();
        val user = auth.currentUser
        Log.d("test", "phw333")
        when (v?.id) {
            R.id.addBtn -> {
                //val intent = Intent(this, RegisterActivity::class.java)  // 인텐트를 생성해줌,
                //startActivity(intent)  // 화면 전환을 시켜줌
                var userId: String = idTv!!.text.toString()
                var userName: String = nameTv!!.text.toString()
                var userComment: String = commentTv!!.text.toString();
                /*if (userIdStr.isEmpty()) {
                    Toast.makeText(this@FirstFragment, "Please check name", Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                if (userEmailStr.isEmpty()) {
                    Toast.makeText(this@RegisterActivity, "Please check Email", Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                if (userPassStr.isEmpty()) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please check Password",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return
                }*/
                if (userId.isNotEmpty() && userName.isNotEmpty() && userComment.isNotEmpty()) {
                    user?.let {
                        databaseIns.child("friend").child(user.uid).get().addOnSuccessListener {
                            myRef.child(user.uid).push().setValue(auth.uid)
                            myRef.child(user.uid).child("name").push().setValue(userName)
                            myRef.child(user.uid).child("comment").push().setValue(userComment)
                        }
                    }
                    val intent = Intent(getActivity(), FriendFragment::class.java)
                    startActivity(intent)

                    /*addFriendBtn.setOnClickListener {
                        val intent = Intent(getActivity(), FriendFragment::class.java)
                        startActivity(intent)
                    }*/
                }
            }
        }
    }
    //--2022.08.23 뒤로가기
    override fun onBackPressed(){

        if(this is FirstFragment){
            activity?.finish()
        }
    }

    //--2022.08.23 end
}


