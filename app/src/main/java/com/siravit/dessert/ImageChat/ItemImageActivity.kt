package com.siravit.dessert.ImageChat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.siravit.dessert.R
import com.siravit.dessert.utils.TimeStampToDate
import java.util.*

class ItemImageActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var mRecyclerview: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var mLinearView: LinearLayout
    private lateinit var mLinearRe: LinearLayout
    private lateinit var screenAdapterImage: ScreenAdapterImage
    private lateinit var nest: NestedScrollView
    private lateinit var allImageClick: ImageView
    private lateinit var mCountImg: TextView
    private lateinit var backBtn: ImageView
    private lateinit var setDate: TextView
    private lateinit var nameSender: TextView
    private lateinit var findImage: DatabaseReference
    private lateinit var mDatabaseName: DatabaseReference
    private lateinit var currentUid: String
    private lateinit var chatId: String
    private lateinit var nameUser: String
    private lateinit var nameMatch: String
    private lateinit var matchId: String

//    private var countImg = 0
    private var count = 0
    private var countReal: Int? = 0
    private var chk1time = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_image)
        chatId = intent.extras?.getString("chatId").toString()
        matchId = intent.extras?.getString("matchId").toString()
        val countSt = intent.extras!!.getString("ChkImage")
        allImageClick = findViewById(R.id.button_all_image)
        countReal = Integer.valueOf(countSt.toString())
        nameSender = findViewById(R.id.name_sender_image)
        currentUid = FirebaseAuth.getInstance().uid.toString()
        viewPager = findViewById(R.id.page_image_all)
        backBtn = findViewById(R.id.button_back_image_gallery)
        screenAdapterImage = ScreenAdapterImage(this@ItemImageActivity, getDataSetImage())
        mDatabaseName = FirebaseDatabase.getInstance().reference.child("Users")
        findImage = FirebaseDatabase.getInstance().reference.child("Chat").child(chatId.toString())
        mCountImg = findViewById(R.id.count_image)
        setDate = findViewById(R.id.image_date_front)
        nest = findViewById(R.id.nest_scroll)
        mLinearView = findViewById(R.id.linear_viewpager)
        mLinearRe = findViewById(R.id.linear_recycler_image)
        mRecyclerview = findViewById(R.id.recyclerView_image)
        mRecyclerview.isNestedScrollingEnabled = false
        mRecyclerview.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        mRecyclerview.layoutManager = layoutManager
        adapter = ImageAllAdapter(getDataSetImage(), this)
        backBtn.setOnClickListener {
            finish()
        }
        allImageClick.setOnClickListener{
            allImageClick.visibility = View.GONE
            mLinearView.visibility = View.GONE
            mCountImg.visibility = View.GONE
            nameSender.visibility = View.GONE
            setDate.visibility = View.GONE
            mLinearRe.visibility = View.VISIBLE
            mRecyclerview.adapter = adapter
            nest.post(Runnable { nest.fullScroll(View.FOCUS_DOWN) })
        }
        getName()
        chk1time = true
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position == 0 && chk1time) {
                    chk1time = if (resultImage!![position]!!.create) {
                        nameSender.text = nameUser
                        setDate.text = (TimeStampToDate(resultImage[position]?.date!!).date()  + " " + TimeStampToDate(resultImage[position]?.date!!).time())
                        false
                    } else {
                        nameSender.text = nameMatch
                        setDate.text = (TimeStampToDate(resultImage[position]?.date!!).date() + " " + TimeStampToDate(resultImage[position]?.date!!).time())
                        false
                    }
                }
            }

            override fun onPageSelected(position: Int) {
                val dd = count.toString()
                mCountImg.text = ((position + 1).toString() + "/" + dd)
                setDate.hint = TimeStampToDate(resultImage!![position]?.date!!).date() + " " + TimeStampToDate(resultImage[position]?.date!!).time()
                if (resultImage.elementAt(position)!!.create) {
                    nameSender.text = nameUser
                } else {
                    nameSender.text = nameMatch
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun getName() {
        mDatabaseName.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(currentUid)) {
                    nameUser = dataSnapshot.child(currentUid).child("name").value.toString()
                }
                if (dataSnapshot.hasChild(matchId)) {
                    nameMatch = dataSnapshot.child(matchId).child("name").value.toString()
                }
                findImage()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun findImage() {
        val prefs = getSharedPreferences(chatId, Context.MODE_PRIVATE).all.keys
        for (chatKey in prefs) {
            val myInNode = getSharedPreferences(chatKey.toString(), Context.MODE_PRIVATE)
            (myInNode.getString("image", "default")).let {
                if(it != "default") {
                    var checkUser = false
                    val create = myInNode.getString("createByUser", "default")
                    val date = myInNode.getLong("time", System.currentTimeMillis())
                    if(create == currentUid) checkUser = true
                    ++count
                    val obj = ScreenObject(it.toString(), date, checkUser, chatId, matchId)
                    resultImage!!.add(obj)
                }
            }
        }
        mCountImg.text = ("$countReal/$count")
        adapter.notifyDataSetChanged()
        screenAdapterImage.notifyDataSetChanged()
        viewPager.adapter = screenAdapterImage
        mRecyclerview.scrollToPosition(count / 3 - 1)
        viewPager.currentItem = countReal!! - 1
        viewPager.visibility = View.VISIBLE
    }

//    private fun findImage() {
//        findImage.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
//                if (dataSnapshot.child("image").value != null) {
//                    val url = dataSnapshot.child("image").value.toString()
//                    val date = dataSnapshot.child("date").value as Long
//                    val create = dataSnapshot.child("createByUser").value.toString()
//                    var checkUser = false
//                    if (create == currentUid) {
//                        checkUser = true
//                    }
//                    val dd = ScreenObject(url, date, checkUser, chatId, matchId)
//                    resultImage!!.add(dd)
//                    ++countImg
//                }
//                if (count == countImg) {
//                    Log.d("ImageAdapter" ,countImg.toString())
//                    adapter.notifyDataSetChanged()
//                    screenAdapterImage.notifyDataSetChanged()
//                    viewPager.adapter = screenAdapterImage
//                    mRecyclerview.scrollToPosition(count / 3 - 1)
//                    viewPager.currentItem = countReal!! - 1
//                    viewPager.visibility = View.VISIBLE
//                }
//            }
//
//            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
//            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
//            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
//            override fun onCancelled(databaseError: DatabaseError) {}
//        })
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private val resultImage: ArrayList<ScreenObject?>? = ArrayList()
    private fun getDataSetImage(): MutableList<ScreenObject?>? {
        return resultImage
    }
}