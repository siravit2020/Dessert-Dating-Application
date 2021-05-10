package com.jabirdeveloper.tinderswipe.LikeYou

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.jabirdeveloper.tinderswipe.Functions.*
import com.jabirdeveloper.tinderswipe.R
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_like_you.*
import kotlinx.coroutines.*
import java.util.*

@Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class LikeYouActivity : AppCompatActivity() {
    private lateinit var LikeYouRecycleview: RecyclerView
    private lateinit var LikeYouAdapter: RecyclerView.Adapter<*>
    private lateinit var LikeYouLayoutManager: RecyclerView.LayoutManager
    private lateinit var currentUserId: String
    private lateinit var connectionDb: DatabaseReference
    private lateinit var userDb: DatabaseReference
    private var x_user = 0.0
    private var y_user = 0.0
    private lateinit var blurView: BlurView
    private lateinit var button: Button
    private lateinit var empty: TextView
    private lateinit var dialog: Dialog
    private lateinit var ff: Geocoder
    private var activity = this
    var toolbar: Toolbar? = null
    private var functions = Firebase.functions
    private lateinit var language: String
    private var countUser = 0
    private var countU = 0
    private var c = 0
    private var s = 0
    private var limit = 0
    private var status = false
    private lateinit var co: CoroutineScope
    private lateinit var resultlimit: ArrayList<data>
    private var isScroll = false
    private var currentItem = 0
    private var totalItem = 0
    private var scrollOutItem = 0
    private var startNode = 0
    private var starTarget = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like_you)
        resultlimit = ArrayList()
        button = findViewById(R.id.buttonsee)
        empty = findViewById(R.id.empty)
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        button.setOnClickListener { openDialog() }
        blurView = findViewById(R.id.blurView)
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        userDb = FirebaseDatabase.getInstance().reference.child("Users")
        connectionDb = userDb.child(currentUserId).child("connection").child("yep")
        s = GlobalVariable.s//intent.getIntExtra("See", 0)
        c = GlobalVariable.c//intent.getIntExtra("Like", 0)
        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        language = preferences.getString("My_Lang", "").toString()
        ff = if (language == "th") {
            Geocoder(this@LikeYouActivity)
        } else {
            Geocoder(this@LikeYouActivity, Locale.UK)
        }

        co = CoroutineScope(Job())
        if (intent.hasExtra("See")) {
            limit = s
            if (s > 0) status = true
            intent.extras!!.remove("See")
            connectionDb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId).child("see_profile")
            empty.setText(R.string.see_empty)
            supportActionBar!!.setTitle(R.string.People_view)
        } else {
            limit = c
            if (c > 0) status = true
            button.setText(R.string.see_like)
            empty.setText(R.string.like_empty)
            supportActionBar!!.setTitle(R.string.People_like_you)
        }
        val radius = 10f
        val decorView = window.decorView
        val windowBackground = decorView.background
        blurView.setupWith(findViewById(R.id.like_you_recycle))
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(RenderScriptBlur(this@LikeYouActivity))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true)

        co.launch {
            withContext(Dispatchers.Default) {
                GlobalVariable.apply {
                    if (!vip)
                        if (!intent.hasExtra("See")) {
                            if (buyLike) {
                                blurView.visibility = View.VISIBLE
                                button.visibility = View.VISIBLE
                                progress_like.visibility = View.GONE
                            }
                        }
                    x_user = x.toDouble()
                    y_user = y.toDouble()
                }
            }
            if (status)
                connectionDb.orderByChild("date").addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {

                        if (dataSnapshot.exists()) {

                            var time: Long = 0
                            if (dataSnapshot.hasChild("date")) {
                                time = dataSnapshot.child("date").value.toString().toLong()
                            }

                            resultlimit.add(data(dataSnapshot.key.toString(), time))

                            Log.d("ttt", "" + resultlimit.size + " :" + limit)
                            if (resultlimit.size == limit) {
                                resultlimit.sortWith { t1, t2 ->
                                    (t2.time - t1.time).toInt()
                                }
                                starTarget = startNode + 20
                                if(limit <= 20) starTarget = limit
                                for (i in startNode until starTarget) {
                                    Log.d("num", i.toString())
                                    fecthHi(resultlimit[i].key, resultlimit[i].time, 0, 0)
                                }
                            }

                        } else {
                            empty.visibility = View.VISIBLE
                            button.visibility = View.GONE
                        }

                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onChildRemoved(snapshot: DataSnapshot) {}
                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                })
            else {
                empty.visibility = View.VISIBLE
                button.visibility = View.GONE
                progress_like.visibility = View.GONE
            }


        }
        LikeYouRecycleview = findViewById(R.id.like_you_recycle)
        LikeYouRecycleview.isNestedScrollingEnabled = false
        LikeYouRecycleview.setHasFixedSize(true)
        LikeYouLayoutManager = LinearLayoutManager(this@LikeYouActivity)
        LikeYouRecycleview.layoutManager = LikeYouLayoutManager
        LikeYouAdapter = LikeYouAdapter(getDataSetMatches(), this@LikeYouActivity)
        LikeYouRecycleview.adapter = LikeYouAdapter
        LikeYouRecycleview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScroll = true

                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItem = LikeYouLayoutManager.childCount
                totalItem = LikeYouLayoutManager.itemCount
                scrollOutItem = (LikeYouLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                if (isScroll && currentItem + scrollOutItem == totalItem && totalItem >= countU) {
                    countU = 0
                    isScroll = false
                    if (startNode < limit) {
                        startNode += 20
                        Log.d("ffgh", "$startNode,$limit,$currentItem,$totalItem,$scrollOutItem")
                        var target = startNode + 20
                        if (startNode + 20 > limit)
                            target = limit
                        for (i in startNode until target) {
                            if (i == target - 1)
                                fecthHi(resultlimit[i].key, resultlimit[i].time, resultLike.size - 1, 1)
                            else
                                fecthHi(resultlimit[i].key, resultlimit[i].time, resultLike.size - 1, 0)

                        }

                    }

                }

            }
        })


    }

    private fun openDialog() {
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.money, null)
        dialog = Dialog(this@LikeYouActivity)
        val imageView = view.findViewById<ImageView>(R.id.image_vip)
        val textView = view.findViewById<TextView>(R.id.text)
        val textView2 = view.findViewById<TextView>(R.id.text_second)
        val b1 = view.findViewById<Button>(R.id.buy)
        imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_love2))
        textView.text = "ใครถูกใจคุณ"
        textView2.text = "ดูว่าใครบ้างที่เข้ามากดถูกใจให้คุณ"
        b1.setOnClickListener {
            GlobalVariable.buyLike = true
            blurView.visibility = View.GONE
            button.visibility = View.GONE
            dialog.dismiss()
            buyLike()
//        if (intent.hasExtra("See")) {
//            imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_vision))
//            textView.text = "ใครเข้ามาดูโปรไฟล์คุณ"
//            textView2.text = "ดูว่าใครบ้างที่เข้าชมโปรไฟล์ของคุณ"
//            b1.text = "฿20.00 / เดือน"
//            b1.setOnClickListener {
//                dialog.dismiss()
//                buySee()
//            }
//        }
//        else {
//            imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_love2))
//            textView.text = "ใครถูกใจคุณ"
//            textView2.text = "ดูว่าใครบ้างที่เข้ามากดถูกใจให้คุณ"
//            b1.setOnClickListener {
//                val myUser = getSharedPreferences("MyUser", Context.MODE_PRIVATE).edit()
//                myUser.putBoolean("buy_like", true)
//                myUser.apply()
//                blurView.visibility = View.GONE
//                button.visibility = View.GONE
//                dialog.dismiss()
//                buyLike()
//            }
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    private fun fecthHi(key: String, time: Long, count: Int, li: Int) {

        userDb.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                Log.d("count", "" + resultLike.size + "==" + countUser)
                countUser++
                if (dataSnapshot.exists()) {
                    countU++
                    var profileImageUrl = ""
                    profileImageUrl = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()
                    var city = ""
                    var myself = ""
                    val userId = dataSnapshot.key
                    val name = dataSnapshot.child("name").value.toString()
                    val status = dataSnapshot.child("status").value.toString()
                    val age: String = dataSnapshot.child("Age").value.toString()
                    val gender: String = dataSnapshot.child("sex").value.toString()
                    if (dataSnapshot.hasChild("myself")) {
                        myself = dataSnapshot.child("myself").value.toString()
                    }
                    val x: Double = dataSnapshot.child("Location").child("X").value.toString().toDouble()
                    val y: Double = dataSnapshot.child("Location").child("Y").value.toString().toDouble()
                    val distance = CalculateDistance.calculate(x_user, y_user, x, y)
                    city = City(language, this@LikeYouActivity, x, y).invoke()
                    resultLike.add(LikeYouObject(
                            userId, profileImageUrl, name, status, age, gender, myself, distance, city, time))
                    Log.d("add", name)
                } else {
                    Log.d("delete",dataSnapshot.key.toString())
                    connectionDb.child(dataSnapshot.key.toString()).removeValue().addOnSuccessListener {
                        if (intent.hasExtra("See")) GlobalVariable.s-- else GlobalVariable.c--
                    }
                }


                Log.d("start", startNode.toString() + resultLike.size)
                if (countUser == starTarget) {
                   // startNode = resultLike.size
                    LikeYouAdapter.notifyDataSetChanged()
                    CloseLoading(this@LikeYouActivity, progress_like).invoke()


                }
                if (li == 1) LikeYouAdapter.notifyItemRangeChanged(count, resultLike.size)

                //LikeYouRecycleview.scheduleLayoutAnimation()


            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private val resultLike: ArrayList<LikeYouObject> = ArrayList()
    private fun getDataSetMatches(): MutableList<LikeYouObject> {
        return resultLike
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun buySee() {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
        databaseReference.child("buy_see").setValue(true).addOnSuccessListener {
            activity.finish()
            activity.overridePendingTransition(0, 0)
            activity.startActivity(activity.intent)
            activity.overridePendingTransition(0, 0)
        }
    }

    private fun buyLike() {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)
        databaseReference.child("buy_like").setValue(true).addOnSuccessListener {
            activity.finish()
            activity.overridePendingTransition(0, 0)
            activity.startActivity(activity.intent)
            activity.overridePendingTransition(0, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 11){
            if(resultCode == 11){
                Log.d("result","re11")
                val status = data!!.getBooleanExtra("status",false)
                if(status){
                    Log.d("result",LikeYouAdapter.itemCount.toString() + resultLike.size)
                    val result = data.getIntExtra("result",0)
                    resultLike.removeAt(result)
                    LikeYouAdapter.notifyDataSetChanged()
                    Log.d("result",LikeYouAdapter.itemCount.toString() +resultLike.size)

                }
            }
            Log.d("result","11")
        }
    }

}