package com.maiguy.dessert.activity.list_card.view

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.maiguy.dessert.activity.list_card.adapter.ListCardAdapter
import com.maiguy.dessert.activity.list_card.model.ListCardModel
import com.maiguy.dessert.utils.GlobalVariable
import com.maiguy.dessert.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ListCardActivity : Fragment() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMatchesAdapter: RecyclerView.Adapter<*>
    private lateinit var mMatchesLayoutManager: RecyclerView.LayoutManager
    private var xUser = 0.0
    private var yUser = 0.0
    private var isScroll = false

    private lateinit var currentUserId: String
    private var oppositeUserSex: String? = null
    private var startNode = 20
    private var oppositeUserAgeMin = 0
    private var oppositeUserAgeMax = 0
    private var distanceUser = 0.0
    private var currentItem = 0
    private var totalItem = 0
    private var scrollOutItem = 0
    private lateinit var pro: ProgressBar
    private lateinit var search: ConstraintLayout
    private lateinit var anime1: ImageView
    private lateinit var anime2: ImageView
    private lateinit var handler: Handler
    private var functions = Firebase.functions
    private lateinit var resultLimit: ArrayList<*>
    private var countLimit = 1000
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_listcard, container, false)
        super.onCreate(savedInstanceState)

        pro = view.findViewById(R.id.view_pro)
        search = view.findViewById(R.id.layout_in)
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)
        mMatchesLayoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = mMatchesLayoutManager
        mMatchesAdapter = ListCardAdapter(getDataSetMatches(), requireContext())
        mRecyclerView.adapter = mMatchesAdapter
        anime1 = view.findViewById(R.id.anime1)
        anime2 = view.findViewById(R.id.anime2)
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) { // launch a new coroutine in background and continue
            percentage()
            detectUserCloseAccount()
        }

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScroll = true
                }
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItem = mMatchesLayoutManager.childCount
                totalItem = mMatchesLayoutManager.itemCount
                scrollOutItem = (mMatchesLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                if (isScroll && currentItem + scrollOutItem == totalItem && totalItem >= 20) {
                    isScroll = false
                    if (startNode < countLimit) {
                        Log.d("ffgh", "$startNode,$countLimit,$currentItem,$totalItem,$scrollOutItem")
                        getUser(resultLimit, startNode, false, resultMatches.size - 1)
                        startNode += 20
                    }
                    if ((currentItem + scrollOutItem) % countLimit == 0) {
                        callFunction(countLimit, false, resultMatches.size)
                        startNode = 20
                    }
                }
            }
        })

        mRecyclerView.addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                pro.visibility = View.GONE
            }
            override fun onChildViewDetachedFromWindow(view: View) {}
        })
        return view
    }


    private val runnable: Runnable? = object : Runnable {
        override fun run() {
            anime1.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(800).withEndAction {
                anime1.scaleX = 1f
                anime1.scaleY = 1f
                anime1.alpha = 1f
            }
            anime2.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1200).withEndAction {
                anime2.scaleX = 1f
                anime2.scaleY = 1f
                anime2.alpha = 1f
            }
            handler.postDelayed(this, 1500)
        }
    }
    private fun detectUserCloseAccount(){
        val db = FirebaseDatabase.getInstance().reference
        db.child("BlackList").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                snapshot.childrenCount.toInt().let {
                    db.child("BlackList").addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                           if(++count > it){
                               deleteListDataFromBlackList(snapshot.key!!)
                           }
                        }
                        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                        override fun onChildRemoved(snapshot: DataSnapshot) {}
                        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun deleteListDataFromBlackList(key: String) {
        Log.d("TAG_CHILD",key)
        Log.d("TAG_CHILD",resultMatches.toString())
        val index = resultMatches.map { T -> T!!.userId.equals(key) }.indexOf(true)
        if(index != -1){
            resultMatches.removeAt(index)
            mMatchesAdapter.notifyItemRemoved(index)
            mMatchesAdapter.notifyItemRangeChanged(index,resultMatches.size)
            Log.d("TAG_CHILD",index.toString())
        }
    }

    private fun getStartAt() {
        getUserGender()
    }

    private fun getUserGender() {
        oppositeUserSex = GlobalVariable.oppositeUserSex
        oppositeUserAgeMin = GlobalVariable.oppositeUserAgeMin
        oppositeUserAgeMax = GlobalVariable.oppositeUserAgeMax
        xUser = GlobalVariable.x.toDouble()
        yUser = GlobalVariable.y.toDouble()
        distanceUser = when (GlobalVariable.distance) {
            "true" -> {
                10000.0
            }
            "Untitled" -> {
                10000.0
            }
            else -> {
                GlobalVariable.distance.toDouble()
            }
        }
        callFunction(countLimit, true, 0)
    }

    private fun percentage(){
            val data = hashMapOf(
                    "question" to "Questions"
            )
            functions
                    .getHttpsCallable("getPercentageMatching")
                    .call(data)
                    .addOnSuccessListener { task ->
                        val dataResult = task.data as Map<*, *>
                        Log.d("testDatatatat", dataResult.toString())

                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            getStartAt()
                        }
                    }
                    .addOnFailureListener {
                        Log.d("testDatatatat", "error")
                    }
    }


    private fun callFunction(limit: Int, type: Boolean, count: Int) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) { // launch a new coroutine in background and continue
            val data = hashMapOf(
                    "sex" to oppositeUserSex,
                    "min" to oppositeUserAgeMin,
                    "max" to oppositeUserAgeMax,
                    "x_user" to xUser,
                    "y_user" to yUser,
                    "distance" to distanceUser,
                    "limit" to count + limit,
                    "prelimit" to count
            )

            functions.getHttpsCallable("getUserList")
                    .call(data)
                    .addOnFailureListener { Log.d("ghu", "failed") }
                    .addOnSuccessListener { task ->
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                            val result1 = task.data as Map<*, *>
                            Log.d("ghu", result1.toString())
                            resultLimit = result1["o"] as ArrayList<*>
                            if (resultLimit.isNotEmpty())
                                if (type){
                                    getUser(resultLimit, 0, type, 0)
                                }
                                else{
                                    getUser(resultLimit, 0, type, resultMatches.size - 1)
                                }
                        }

                    }
        }
    }

    private fun getUser(result2: ArrayList<*>, start: Int, type: Boolean, startNoti: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.Default){
                var max = start + 20
                var typeTime = ""
                var time = ""
                Log.d("max", (start + 20).toString() + " " + result2.size)
                if (result2.size < start + 20) {
                    max = result2.size
                }
                for (x in start until max) {
                    val user = result2[x] as Map<*, *>
                    var myself = ""
                    var offStatus = false
                    Log.d("ghu", user["name"].toString() + " , " + user["distance_other"].toString())

                    if (user["typeTime"] != null) {
                        typeTime = user["typeTime"].toString()
                        Log.d("type55", "0")
                    }
                    if (user["time"] != null) {
                        time = user["time"].toString()
                    }
                    if (user["myself"] != null) {
                        myself = user["myself"].toString()
                    }
                    if (user["off_status"] != null) {
                        offStatus = true
                    }
                    (user["ProfileImage"] as Map<*, *>)["profileImageUrl0"]
                    val profileImageUrl = (user["ProfileImage"] as Map<*, *>)["profileImageUrl0"].toString()

                    var status = "offline"
                    if (user["status"] == 1) {
                        status = "online"
                    }
                    val df2 = DecimalFormat("#.#")
                    val dis = df2.format(user["distance_other"])

                    val obj = ListCardModel(user["key"].toString(), user["name"].toString(), profileImageUrl, dis, status, user["Age"].toString(), user["sex"].toString(), myself, offStatus, typeTime, time)
                    resultMatches.add(obj)

                }
            }
            withContext(Dispatchers.Unconfined){
                if (resultMatches.size > 0) {
                        pro.visibility = View.GONE
                }
                Log.d("sss", "$startNoti " + resultMatches.size)
                if (type) {
                    mMatchesAdapter.notifyDataSetChanged()
                    mRecyclerView.scheduleLayoutAnimation()
                } else {
                    mMatchesAdapter.notifyItemRangeChanged(startNoti, resultMatches.size)
                   // mRecyclerView.scheduleLayoutAnimation()
                }
            }

        }
    }

    private val resultMatches: ArrayList<ListCardModel?> = ArrayList()
    private fun getDataSetMatches(): ArrayList<ListCardModel?> {
        return resultMatches
    }


}



