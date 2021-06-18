package com.siravit.dessert.activity.matches.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.siravit.dessert.R
import com.siravit.dessert.activity.main.view.MainActivity
import com.siravit.dessert.activity.matches.adapter.HiAdapter
import com.siravit.dessert.activity.matches.adapter.MatchesAdapter
import com.siravit.dessert.activity.matches.model.HiObject
import com.siravit.dessert.activity.matches.model.MatchesObject

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList

class MatchesActivity : Fragment() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mHiRecyclerView: RecyclerView
    private lateinit var mMatchesAdapter: RecyclerView.Adapter<*>
    private lateinit var mHiAdapter: RecyclerView.Adapter<*>
    private lateinit var mMatchesLayoutManager: RecyclerView.LayoutManager
    private lateinit var mHiLayout: RecyclerView.LayoutManager
    private lateinit var layoutChatNa: LinearLayout
    private lateinit var currentUserId: String
    private var count = 0
    private var startNode = 20
    private lateinit var textEmpty: TextView
    private lateinit var nest:NestedScrollView
    private lateinit var chatEmpty: TextView

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.activity_matches, container, false)
        super.onCreate(savedInstanceState)
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        textEmpty = view.findViewById(R.id.textempty)
        chatEmpty = view.findViewById(R.id.chatempty)
        layoutChatNa = view.findViewById(R.id.layoutRe)
        nest = view.findViewById(R.id.nest_scrollMatch)
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)
        mMatchesLayoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = mMatchesLayoutManager
        mMatchesAdapter = MatchesAdapter(getDataSetMatchesNode(), context, currentUserId)
        mRecyclerView.adapter = mMatchesAdapter
        mHiRecyclerView = view?.findViewById(R.id.recyclerView2)!!
        mHiRecyclerView.isNestedScrollingEnabled = false
        mHiRecyclerView.setHasFixedSize(true)
        mHiLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mHiRecyclerView.layoutManager = mHiLayout
        mHiAdapter = HiAdapter(getDataSetHi(), requireContext())
        mHiRecyclerView.adapter = mHiAdapter
        mRecyclerView.visibility = View.GONE
        chatNaCheck()
        checkFirst()
//        nest.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
//            if (scrollY == (v.getChildAt(0).measuredHeight - v.measuredHeight)) {
//                if(resultMatches.size > (startNode+20)){
//                    startNode += 20
//                    setRecyclerView()
//                }
//             /**
//              * add somethings went scroll to the bottom,.
//              * */
//            }
//        })
        return view
    }

    private var checkFirstRemove: String? = "null"
    private var userMatchCount = 0

    private fun checkFirst(){
        val matchDbCheck = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId).child("connection").child("matches")
        matchDbCheck.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    chatEmpty.visibility = View.GONE
                    getUserMarchId()
                } else {
                    getUserMarchId()
                    chatEmpty.visibility = View.VISIBLE
                    mRecyclerView.visibility = View.GONE } }
        })
    }

    private fun getUserMarchId() {
        FirebaseDatabase.getInstance().reference.child("Users")
                .child(currentUserId)
                .child("connection")
                .child("matches")
                .addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                ++userMatchCount
                if(userMatchCount == 1) chatEmpty.visibility = View.GONE
                val chatID = dataSnapshot.child("ChatId").value.toString()
                Log.d("MatchingUser",dataSnapshot.key.toString())
                getChatNode(chatID, dataSnapshot.key.toString()) }
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                if (checkFirstRemove == "null") { --userMatchCount
                    if (userMatchCount == 0) {
                        chatEmpty.visibility = View.VISIBLE
                        mRecyclerView.visibility = View.GONE }
                    checkFirstRemove = dataSnapshot.key
                    unMatch(dataSnapshot.key)
                } else if (checkFirstRemove != dataSnapshot.key) {
                    --userMatchCount
                    if (userMatchCount == 0) {
                        chatEmpty.visibility = View.VISIBLE
                        mRecyclerView.visibility = View.GONE }
                    checkFirstRemove = dataSnapshot.key
                    unMatch(dataSnapshot.key) } }
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    private fun unMatch(key: String?) {
        val index:Int = resultMatchNode.map { T -> T.userId.equals(key) }.indexOf(element = true)
        val matchIDStored:SharedPreferences = mContext!!.getSharedPreferences(currentUserId + "Match_first", Context.MODE_PRIVATE)
        val editorMatching:SharedPreferences.Editor = matchIDStored.edit()
        editorMatching.remove(key).apply()
        val unreadShared:SharedPreferences = mContext!!.getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
        val unreadTotal:Int = unreadShared.getInt("total", 0)
        var unread:Int = resultMatchNode[index].count_unread
        if (unread == -1) unread = 0
        val total:Int = unreadTotal - unread
        (mContext as MainActivity?)!!.setCurrentIndex(total)
        val editorRead:SharedPreferences.Editor = unreadShared.edit()
        editorRead.putInt("total", total)
        editorRead.apply()
        resultMatchNode.removeAt(index)
        mMatchesAdapter.notifyItemRemoved(index)
        mMatchesAdapter.notifyItemRangeChanged(index, resultMatchNode.size)
    }
    private fun getChatNode(chatId:String,uid:String){
        FirebaseDatabase.getInstance().reference
                .child("Chat")
                .child(chatId).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            latestChat(uid, chatId)
                        }else{
                            latestChat(uid, chatId)
                            fetchMatchFormation(uid, last_chat = "", time=null, count_unread = -1)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
    }
    private var createByBoolean:Boolean = true
    private fun latestChat(key: String?, chatID: String?) {
        val chatDb = FirebaseDatabase.getInstance().reference.child("Chat").child(chatID.toString()).orderByKey().limitToLast(1)
        chatDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                Log.d("ChatAdd",dataSnapshot.child("text").value.toString() )
                val lastChat = dataSnapshot.child("text").value.toString()
                val time = dataSnapshot.child("date").value as Long
                val createBy = dataSnapshot.child("createByUser").value.toString()
                createByBoolean = dataSnapshot.child("createByUser").value.toString() != (currentUserId)
                if (createBy != currentUserId) {
                    chatCheckRead(chatID, key, time, lastChat)
                } else {
                    fetchMatchFormation(key,  lastChat, time, 0) } }
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                Log.d("CHILD_CHANGE_DETECT__matchList",dataSnapshot.value.toString())
            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var countRead = 0
    private fun chatCheckRead(ChatId: String?, key: String?, time: Long?, last_chat: String?) {
        val mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat").child(ChatId.toString())//
        val countUnread = mDatabaseChat.orderByChild("read").equalTo("Unread")
        countUnread.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val countRead = dataSnapshot.childrenCount.toInt()
                fetchMatchFormation(key, last_chat, time, countRead) }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    private var userChatNaCount = 0
    private var checkFirstRemoveChatNa: String? = "null"
    private fun chatNaCheck() {
        val dBChatNa = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId).child("connection").child("chatna")
        dBChatNa.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) { chatNa() }
                    else {
                        chatNa()
                        textEmpty.visibility = View.VISIBLE
                        mHiRecyclerView.visibility = View.GONE } }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun chatNa() {
        val dBChatNa = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId).child("connection").child("chatna")
        dBChatNa.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                ++userChatNaCount
                val chatId = dataSnapshot.value.toString()
                lastChatHi(dataSnapshot.key, chatId) }
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                if (checkFirstRemoveChatNa == "null") {
                    --userChatNaCount
                    if (userChatNaCount == 0) {
                        textEmpty.visibility = View.VISIBLE
                        mHiRecyclerView.visibility = View.GONE }
                    checkFirstRemoveChatNa = dataSnapshot.key
                    deleteChatNA(dataSnapshot.key)
                } else if (checkFirstRemoveChatNa != dataSnapshot.key) {
                    --userChatNaCount
                    if (userChatNaCount == 0) {
                        textEmpty.visibility = View.VISIBLE
                        mHiRecyclerView.visibility = View.GONE }
                    checkFirstRemoveChatNa = dataSnapshot.key
                    deleteChatNA(dataSnapshot.key) } }
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun deleteChatNA(uIdChatNa: String?) {
        for (i in resultHi.indices) {
            if (resultHi[i].userId == uIdChatNa) {
                if (resultHi.size == 1) {
                    mHiRecyclerView.visibility = (View.GONE)
                    textEmpty.visibility = (View.VISIBLE) }
                resultHi.removeAt(i)
                mHiAdapter.notifyItemRemoved(i)
                mHiAdapter.notifyItemRangeChanged(i, resultHi.size) } }
    }
    private var checkHi = false
    private var sentBack = false
    private var local = 0
    private fun lastChatHi(key: String?, ChatId: String?) {
        val chatDb:Query = FirebaseDatabase.getInstance().reference.child("Chat").child(ChatId.toString()).orderByKey().limitToLast(1)
        chatDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val lastChat:String = dataSnapshot.child("text").value.toString()
                val time = dataSnapshot.child("date").value as Long
                checkSentBack(key, lastChat, time, ChatId) }
            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    private fun checkSentBack(key: String?, lastChat: String?, time: Long?, ChatId: String?) {
        val chatDb:Query = FirebaseDatabase.getInstance().reference.child("Chat").child(ChatId.toString()).orderByChild("createByUser").equalTo(currentUserId).limitToLast(1)
        chatDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(key.toString())
                            .child("connection")
                            .child("matches")
                            .child(currentUserId)
                            .child("ChatId")
                            .setValue(ChatId)
                    FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(currentUserId)
                            .child("connection")
                            .child("matches")
                            .child(key.toString())
                            .child("ChatId")
                            .setValue(ChatId)
                    FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(currentUserId)
                            .child("connection")
                            .child("chatna")
                            .child(key.toString())
                            .removeValue()
                    sentBack = true
                    checkHi = true
                    fetchHi(key, lastChat, time, ChatId)
                } else { fetchHi(key, lastChat, time, ChatId) } }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var inception = false
    private fun fetchHi(key: String?, lastChat: String?, time: Long?, ChatId: String?) {
        FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(key.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")) {
                    val profileImageUrl:String?
                    profileImageUrl = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()
                    val userId:String? = dataSnapshot.key
                    val name:String = dataSnapshot.child("name").value.toString()
                    val gender:String = dataSnapshot.child("sex").value.toString()
                    if (sentBack) {
                        inception = false
                        sentBack =false
                        fetchMatchFormation(key, lastChat, time, 0)
                    } else {
                        val obj2 = HiObject(userId, profileImageUrl, name, gender)
                        resultHi.add(obj2)
                        if(resultHi.size > 0) {
                            mHiRecyclerView.visibility = (View.VISIBLE)
                            textEmpty.visibility = (View.GONE)
                        }
                        mHiAdapter.notifyDataSetChanged() } } }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    /** add data when scroll nest to bottom **/
    private fun setRecyclerView(){
        when {
            resultMatches.size < 20 -> {
                for (i in resultMatches){
                    resultMatchNode.add(i)
                }
            }
            startNode == 20 -> {
                for (i in 0 until 20){
                    resultMatchNode.add(resultMatches.elementAt(i))
                }
                mMatchesAdapter.notifyDataSetChanged()
            }
            else -> {
                val size = resultMatchNode.size
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Default) {
                        for (i in size until startNode) {
                            resultMatchNode.add(resultMatches.elementAt(i))
                        }
                    }
                    withContext(Dispatchers.Main) {
                        mMatchesAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }


    private fun fetchMatchFormation(key: String?, last_chat: String?, time: Long?, count_unread: Int) {
        FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(key.toString())
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userId = dataSnapshot.key
                    var name = ""
                    var profileImageUrl = ""
                    var status = false
                    if (dataSnapshot.child("name").value != null) {
                        name = dataSnapshot.child("name").value.toString()
                    }
                    if (dataSnapshot.child("status").value != null) {
                            (dataSnapshot.child("status").value == 1).let { if(it) status = true }
                    }
                    if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")) {
                        profileImageUrl = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()
                    }
                    val obj: MatchesObject?
                    if (countRead != 0) {
                        obj = MatchesObject(userId, name, profileImageUrl, status, last_chat, time, countRead)
                        val myUnread = mContext?.getSharedPreferences("NotificationMessage", Context.MODE_PRIVATE)
                        val editorRead = myUnread?.edit()
                        editorRead?.putInt(key, countRead)
                        editorRead?.apply()
                        countRead = 0
                    } else {
                        obj = MatchesObject(userId, name, profileImageUrl, status, last_chat, time, count_unread)
                    }

                    mMatchesAdapter.notifyDataSetChanged()
                    if (checkHi) {
                        mRecyclerView.visibility = View.VISIBLE
                        chatEmpty.visibility = View.GONE
                    }
                    // NOTE
                    resultMatchNode.find {
                        it.userId == obj.userId
                    }.let {
                        Log.d("MatchingUser",it.toString())
                        if(createByBoolean) addBadgeOnTabBar()
                        if(it != null){
                            it.status = obj.status
                            it.late = obj.late
                            it.count_unread = obj.count_unread
                            it.time = obj.time
                        }else{
                            resultMatchNode.add(obj)
                        }
                        resultMatchNode.sortWith { o1, o2 ->
                            when {
                                o1.time === null -> -1
                                o2.time === null -> 1
                                else -> o2.time!!.compareTo(o1.time!!)
                            }
                        }
                        mMatchesAdapter.notifyDataSetChanged()
                    }
                }
                if (resultMatchNode.size == userMatchCount) mRecyclerView.visibility = View.VISIBLE
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var resultMatchNode: ArrayList<MatchesObject> = ArrayList()
    private fun getDataSetMatchesNode(): ArrayList<MatchesObject> { return resultMatchNode }
    private var resultMatches: ArrayList<MatchesObject> = ArrayList()
    private val resultHi: ArrayList<HiObject> = ArrayList()
    private fun getDataSetHi(): ArrayList<HiObject> { return resultHi }

    private fun addBadgeOnTabBar() {
        val shareUnread = mContext!!.getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
        var unread  = shareUnread.getInt("total", 0)
        (mContext as MainActivity).setCurrentIndex(++unread)
        val editorRead = shareUnread.edit()
        editorRead.putInt("total", unread)
        editorRead.apply()
    }

    override fun onResume() {
        super.onResume()
        val myUnread = mContext!!.getSharedPreferences("NotificationActive", Context.MODE_PRIVATE)
        val s1 = myUnread.getString("ID", "null")
        if (s1 != "null") { val index = resultMatchNode.map { T -> T.userId.equals(s1) }.indexOf(element = true)
            if (resultMatchNode.size > 0) {
                resultMatchNode.elementAt(index).count_unread = 0
                mMatchesAdapter.notifyDataSetChanged()
            } else {
                resultMatchNode.elementAt(0).count_unread = 0
                mMatchesAdapter.notifyDataSetChanged() }
            myUnread.edit().clear().apply() }
        val myDelete = mContext!!.getSharedPreferences("DeleteChatActive", Context.MODE_PRIVATE)
        val s2 = myDelete.getString("ID", "null")
        if (s2 != "null") {
            val index = resultMatchNode.map { T -> T.userId.equals(s2) }.indexOf(element = true)
            if (resultMatchNode.size > 0) {
                resultMatchNode.apply {
                    elementAt(index).late = ""
                    elementAt(index).time = null
                }
                mMatchesAdapter.notifyDataSetChanged()
            } else {
                resultMatchNode.apply {
                    elementAt(0).late = ""
                    elementAt(0).time = null
                }
                mMatchesAdapter.notifyDataSetChanged() }
            myDelete.edit().clear().apply() }
    }

    private var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context }

    override fun onPause() {
        super.onPause()
        countRead = 0 }
}