package com.maiguy.dessert.activity.profile_information_opposite.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate

import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

import com.google.android.material.floatingactionbutton.FloatingActionButton


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.maiguy.dessert.*
import com.maiguy.dessert.activity.chat.view.ChatActivity
import com.maiguy.dessert.R
import com.maiguy.dessert.ViewModel.QuestionViewModel
import com.maiguy.dessert.constants.VipDialogType
import com.maiguy.dessert.dialogs.DialogAskQuestion
import com.maiguy.dessert.dialogs.LoadingDialog
import com.maiguy.dessert.dialogs.ReportUser
import com.maiguy.dessert.dialogs.VipDialog
import com.maiguy.dessert.utils.adapter.ScreenAdapter
import com.maiguy.dessert.utils.*

import kotlinx.android.synthetic.main.activity_profile_user_opposite2.*
import kotlinx.coroutines.*
import java.io.IOException
import java.text.DecimalFormat
import java.util.*

@Suppress("NAME_SHADOWING")
class ProfileInformationOppositeUserActivity : AppCompatActivity() {
    private lateinit var currentUserId: String
    private lateinit var matchId: String
    private lateinit var mUserDatabase: DatabaseReference
    private lateinit var mLinear: LinearLayout
    private lateinit var l1: LinearLayout
    private lateinit var l2: LinearLayout
    private lateinit var l3: LinearLayout
    private lateinit var l4: LinearLayout
    private lateinit var l5: LinearLayout
    private lateinit var l6: LinearLayout
    private lateinit var madoo: FlexboxLayout
    private var url0: String? = null
    private var url1: String? = null
    private var url2: String? = null
    private var url3: String? = null
    private var url4: String? = null
    private var url5: String? = null

    private lateinit var currentUid: String
    private var send: String? = null

    private var notificationMatch: String? = null
    private lateinit var listItems: Array<String?>
    private lateinit var listItems2: Array<String?>
    private lateinit var listItems3: Array<String?>
    private lateinit var name: TextView
    private lateinit var age: TextView
    private lateinit var gender: TextView
    private lateinit var study: TextView
    private lateinit var career: TextView
    private lateinit var religion: TextView
    private lateinit var myself: TextView
    private lateinit var language: TextView
    private lateinit var city1: TextView
    private lateinit var report: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var flexboxLayout: FlexboxLayout
    private lateinit var params: GridLayout.LayoutParams
    private lateinit var fab: FloatingActionButton
    private var click = true
    private var xOpposite = 0.0
    private var yOpposite = 0.0
    private lateinit var like: Button
    private lateinit var dislike: Button
    private lateinit var star: Button
    private var i = 0
    private var chk = 11
    private lateinit var viewPager: WrapContentHeightViewPager
    private lateinit var dialog: Dialog
    private var text: String? = null
    private lateinit var adapter: ScreenAdapter
    private lateinit var mDatabaseChat: DatabaseReference
    private lateinit var usersDb: DatabaseReference
    private lateinit var containerPercent: LinearLayout
    private lateinit var percentText : TextView
    private lateinit var detailEqualQA: ImageView
    private var drawableGender = 0
    private var delete = false
    private var rewardedAd: RewardedAd? = null
    private var percentTwo: Int = 0
    private lateinit var questionViewModel: QuestionViewModel
    private lateinit var localizationDelegate: LocalizationActivityDelegate
    private lateinit var loadingDialog:Dialog
    private val functions = Firebase.functions

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_user_opposite2)
        loadingDialog = LoadingDialog(this@ProfileInformationOppositeUserActivity).dialog()
        localizationDelegate = LocalizationActivityDelegate(activity = this@ProfileInformationOppositeUserActivity)
        questionViewModel = ViewModelProvider(this,object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return QuestionViewModel(this@ProfileInformationOppositeUserActivity) as T
            }
        }).get(QuestionViewModel::class.java)
        questionViewModel.fetchEqualsQA.observe(this@ProfileInformationOppositeUserActivity,{
            DialogAskQuestion(this@ProfileInformationOppositeUserActivity).equalsDialog(it).show()
        })
        mAuth = FirebaseAuth.getInstance()
        matchId = intent.extras!!.getString("User_opposite")!!
        currentUserId = mAuth.uid.toString()
        ChangLanguage(this).setLanguage()
        currentUid = mAuth.currentUser!!.uid
        usersDb = FirebaseDatabase.getInstance().reference.child("Users")
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(matchId)
        mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat")
        flexboxLayout = findViewById(R.id.Grid_profile)
        flexboxLayout.flexDirection = FlexDirection.ROW
        containerPercent = findViewById(R.id.profile_percent_container)
        percentText = findViewById(R.id.checkEqualQA)
        params = GridLayout.LayoutParams()
        detailEqualQA = findViewById(R.id.button_equals)
        name = findViewById(R.id.name_profile)
        age = findViewById(R.id.age_profile)
        gender = findViewById(R.id.gender_profile)
        study = findViewById(R.id.study_profile)
        career = findViewById(R.id.career_profile)
        religion = findViewById(R.id.religion_profile)
        myself = findViewById(R.id.myself_profile)
        language = findViewById(R.id.language_profile)
        city1 = findViewById(R.id.city_profile)
        viewPager = findViewById(R.id.slide_main)
        mLinear = findViewById(R.id.main)
        fab = findViewById(R.id.floatingActionButton)
        report = findViewById(R.id.report)
        like = findViewById(R.id.like_button)
        dislike = findViewById(R.id.dislike_button)
        star = findViewById(R.id.star_button)
        dialog = Dialog(this@ProfileInformationOppositeUserActivity)
        listItems = resources.getStringArray(R.array.shopping_item)
        listItems2 = resources.getStringArray(R.array.pasa_item)
        listItems3 = resources.getStringArray(R.array.religion_item)
        l1 = findViewById(R.id.linearLayout33)
        l2 = findViewById(R.id.linearLayout34)
        l3 = findViewById(R.id.linearLayout35)
        l4 = findViewById(R.id.linearLayout36)
        l5 = findViewById(R.id.linearLayout37)
        l6 = findViewById(R.id.linearLayout38)
        l1.visibility = View.GONE
        l2.visibility = View.GONE
        l3.visibility = View.GONE
        l4.visibility = View.GONE
        l5.visibility = View.GONE
        l6.visibility = View.GONE
        val preferences2 = getSharedPreferences("notification_match", Context.MODE_PRIVATE)
        notificationMatch = preferences2.getString("noti", "1")
        getUserinfo()

        fab.visibility = View.GONE
        madoo = findViewById(R.id.linearLayout17)
        detailEqualQA.setOnClickListener {
            Log.d("DATA_FORM_ON_CALL","On clicks")
            questionViewModel.responseEqualsQA(localizationDelegate.getLanguage(this@ProfileInformationOppositeUserActivity).toLanguageTag(),matchId)
        }
        if (intent.hasExtra("form_main")) {
            val percent = intent.extras!!.getInt("percent")
            madoo.visibility = View.VISIBLE
            containerPercent.visibility = View.VISIBLE
            percentText.text = "" + getString(R.string.equals_question_des) +" ${percent}%"
        }
        if (intent.hasExtra("form_like")) {
            madoo.visibility = View.VISIBLE
        }
        if(intent.hasExtra("form_chat")){
            getPercentApi()
            containerPercent.visibility = View.VISIBLE
            percentText.text = "" + getString(R.string.equals_question_des) +" ${percentTwo}%"
        }
        if (intent.hasExtra("form_list")) {
            val percent = intent.extras!!.getInt("percent")
            percentText.text = "" + getString(R.string.equals_question_des) +" ${percent}%"
            containerPercent.visibility = View.VISIBLE

            fab.setOnClickListener {
                if (click || GlobalVariable.vip) {
                    val inflater = layoutInflater
                    val view2 = inflater.inflate(R.layout.sayhi_dialog, null)
                    val b1 = view2.findViewById<Button>(R.id.buy)
                    val textSend = view2.findViewById<EditText>(R.id.text_send)
                    b1.setOnClickListener {
                        text = textSend.text.toString()
                        if (text!!.trim { it <= ' ' }.isNotEmpty()) {
                            Toast.makeText(this@ProfileInformationOppositeUserActivity, text, Toast.LENGTH_SHORT).show()
                            send = text
                            fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ProfileInformationOppositeUserActivity, R.color.text_gray))
                            fab.isClickable = false
                            val key = FirebaseDatabase.getInstance().reference.child("Chat").push().key!!
                            usersDb.child(matchId).child("connection").child("chatna").child(currentUid).setValue(key)
                            val newMessageDb = mDatabaseChat.child(key).push()
                            val newMessage = hashMapOf<String, Any>()
                            newMessage["createByUser"] = currentUid
                            newMessage["text"] = text!!
                            newMessage["date"] = ServerValue.TIMESTAMP
                            newMessage["read"] = "Unread"
                            newMessageDb.updateChildren(newMessage)
                            dialog.dismiss()
                            GlobalVariable.maxChat--
                            usersDb.child(currentUid).child("MaxChat").setValue(GlobalVariable.maxChat)
                        } else {
                            Toast.makeText(this@ProfileInformationOppositeUserActivity, "พิมพ์ข้อความสักหน่อยสิ", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.setContentView(view2)
                    val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
                    dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
                    dialog.show()
                } else {

                    VipDialog(this, VipDialogType.List).openDialog()
                }
            }
        }
        usersDb.child(matchId).child("connection").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("yep").exists())
                        if (dataSnapshot.child("yep").hasChild(currentUid)) {
                            madoo.visibility = View.GONE

                        }
                    if (dataSnapshot.child("nope").exists())
                        if (dataSnapshot.child("nope").hasChild(currentUid)) {
                            madoo.visibility = View.GONE
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        like.setOnClickListener {
            if (!intent.hasExtra("form_like")) {
                setResult(1)

            } else {
                if (GlobalVariable.maxLike > 0 || GlobalVariable.vip) {
                    val dateTime = hashMapOf<String, Any>()
                    dateTime["date"] = ServerValue.TIMESTAMP
                    usersDb.child(matchId).child("connection").child("yep").child(currentUid).updateChildren(dateTime)
                    GlobalVariable.maxLike--
                    usersDb.child(currentUid).child("MaxLike").setValue(GlobalVariable.maxLike)
                    isConnectionMatches2()
                } else {
                    VipDialog(this, VipDialogType.Card).openDialog()
                }
            }
        }
        dislike.setOnClickListener(View.OnClickListener {
            if (!intent.hasExtra("form_like")) {
                setResult(2)
            } else {
                usersDb.child(matchId).child("connection").child("nope").child(currentUid).setValue(true)

            }
            onBackPressed()
        })
        star.setOnClickListener(View.OnClickListener {
            if (!intent.hasExtra("form_like")) {
                setResult(3)
            } else {
                if (GlobalVariable.maxStar > 0) {
                    val datetime = hashMapOf<String, Any>()
                    datetime["date"] = ServerValue.TIMESTAMP
                    datetime["super"] = true
                    usersDb.child(matchId).child("connection").child("yep").child(currentUid).updateChildren(datetime)
                    GlobalVariable.maxStar--
                    usersDb.child(currentUid).child("MaxStar").setValue(GlobalVariable.maxStar)
                    isConnectionMatches2()
                } else {
                    VipDialog(this, VipDialogType.Card).openDialog()
                }
            }

        })
        report.setOnClickListener {
            val mDialog = ReportUser(this@ProfileInformationOppositeUserActivity, matchId).reportDialog()
            mDialog.show()
        }

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (chk == 11) {
                    if (i > 1) {
                        val dd = findViewById<View?>(position) as LinearLayout
                        dd.background = ContextCompat.getDrawable(this@ProfileInformationOppositeUserActivity, R.drawable.image_selected)
                    }
                }
                chk = 0
            }

            override fun onPageSelected(position: Int) {
                val total = adapter.count
                for (jk in total - 1 downTo 0) {
                    if (jk == position) {
                        val dd = findViewById<View?>(position) as LinearLayout
                        dd.background = ContextCompat.getDrawable(this@ProfileInformationOppositeUserActivity, R.drawable.image_selected)
                    } else {
                        val dd = findViewById<View?>(jk) as LinearLayout
                        dd.background = ContextCompat.getDrawable(this@ProfileInformationOppositeUserActivity, R.drawable.image_notselector)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

    }

    @SuppressLint("SetTextI18n")
    private fun getPercentApi() {
        val data = hashMapOf(
            "oppositeUid" to matchId,
        )
        loadingDialog.show()
        functions.getHttpsCallable("getPercentTwoUsers")
            .call(data)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val data:Map<*,*> = it.result.data as Map<*, *>
                    Log.d("UNREAD_HANDLER",data.toString())
                    percentTwo = data["result"] as Int
                    percentText.text = "" + getString(R.string.equals_question_des) +" ${percentTwo}%"

                }else{
                    percentTwo = 0
                }
                loadingDialog.dismiss()
            }
    }

    private fun createAndLoadRewardedAd() {
        val adRequest = AdRequest.Builder().build()


        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("aaa", adError.message)

                rewardedAd = null
            }

            override fun onAdLoaded(reward: RewardedAd) {
                Log.d("aaa", "Ad was loaded.")
                val view = layoutInflater.inflate(R.layout.vip_dialog, null)
                val b2 = view.findViewById<Button>(R.id.admob)
                b2.text = "ดูโฆษณา"
                rewardedAd = reward
            }
        })

    }

    private fun isConnectionMatches2() {
        val currentuserConnectionDb = usersDb.child(currentUid).child("connection").child("yep").child(matchId)
        currentuserConnectionDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    delete = true
                    GlobalVariable.likeYou = GlobalVariable.likeYou - 1
                    val key = FirebaseDatabase.getInstance().reference.child("Chat").push().key
                    usersDb.child(dataSnapshot.key!!).child("connection").child("matches").child(currentUid).child("ChatId").setValue(key)
                    usersDb.child(currentUid).child("connection").child("matches").child(dataSnapshot.key!!).child("ChatId").setValue(key)
                    usersDb.child(dataSnapshot.key!!).child("connection").child("yep").child(currentUid).setValue(null)
                    usersDb.child(currentUid).child("connection").child("yep").child(dataSnapshot.key!!).setValue(null)
                    if (notificationMatch == "1") {
                        dialog = Dialog(this@ProfileInformationOppositeUserActivity)
                        val inflater = layoutInflater
                        val view = inflater.inflate(R.layout.show_match, null)
                        val imageView = view.findViewById<ImageView>(R.id.image_match)
                        val textView = view.findViewById<TextView>(R.id.textMatch)
                        val textView2 = view.findViewById<TextView>(R.id.io)
                        val textView4 = view.findViewById<TextView>(R.id.textMatch2)
                        val button = view.findViewById<Button>(R.id.mess)
                        button.setOnClickListener {
                            dialog.dismiss()
                            val intent = Intent(this@ProfileInformationOppositeUserActivity, ChatActivity::class.java)
                            val b = Bundle()
                            b.putString("matchId", matchId)
                            b.putString("nameMatch", name.text.toString())
                            b.putString("first_chat", "")
                            b.putString("unread", "0")
                            intent.putExtras(b)
                            startActivity(intent)
                        }
                        textView2.setOnClickListener { dialog.dismiss() }
                        textView.text = name.text.toString()
                        if (dataSnapshot.hasChild("super")) {
                            star.visibility = View.VISIBLE
                            textView4.text = " ส่งดาวให้คุณให้คุณ"
                        } else textView4.text = " ถูกใจคุณเหมือนกัน"
                        Glide.with(this@ProfileInformationOppositeUserActivity).load(url0).into(imageView)
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.setContentView(view)
                        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                        dialog.show()
                    }
                } else finish()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private val df2: DecimalFormat = DecimalFormat("#.#")
    private fun getUserinfo() {
        CoroutineScope(Job() + Dispatchers.Unconfined).launch {
            if (intent.hasExtra("form_list")) {
                val task2 = async { // background thread
                    usersDb.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            GlobalVariable.maxChat = dataSnapshot.child(currentUid).child("MaxChat").value.toString().toInt();
                            if (dataSnapshot.child(currentUid).child("Vip").value == 1) {
                                GlobalVariable.vip = true
                            }
                            if (GlobalVariable.maxChat <= 0) {
                                click = false
                            }
                            if (dataSnapshot.child(matchId).child("connection").child("chatna").hasChild(currentUid)) {
                                fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ProfileInformationOppositeUserActivity, R.color.text_gray))
                                fab.isClickable = false
                            }
                            fab.visibility = View.VISIBLE


                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }
            }

            val task1 = async { // background thread
                mUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.child("sex").exists()) {
                            if (dataSnapshot.child("ProfileImage").exists()) {
                                if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")) {
                                    url0 = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()
                                    i++
                                    if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl1")) {
                                        url1 = dataSnapshot.child("ProfileImage").child("profileImageUrl1").value.toString()
                                        i++
                                    } else {
                                        url1 = "null"
                                    }
                                    if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl2")) {
                                        url2 = dataSnapshot.child("ProfileImage").child("profileImageUrl2").value.toString()
                                        i++
                                    } else {
                                        url2 = "null"
                                    }
                                    if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl3")) {
                                        url3 = dataSnapshot.child("ProfileImage").child("profileImageUrl3").value.toString()
                                        i++
                                    } else {
                                        url3 = "null"
                                    }
                                    if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl4")) {
                                        url4 = dataSnapshot.child("ProfileImage").child("profileImageUrl4").value.toString()
                                        i++
                                    } else {
                                        url4 = "null"
                                    }
                                    if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl5")) {
                                        url5 = dataSnapshot.child("ProfileImage").child("profileImageUrl5").value.toString()
                                        i++
                                    } else {
                                        url5 = "null"
                                    }
                                    if (i > 1) {
                                        for (j in 0 until i) {
                                            val layoutParams = LinearLayout.LayoutParams(
                                                    50, 12, 0.5f)
                                            layoutParams.setMargins(5, 0, 5, 0)
                                            val layout = LinearLayout(this@ProfileInformationOppositeUserActivity)
                                            layout.background = ContextCompat.getDrawable(this@ProfileInformationOppositeUserActivity, R.drawable.image_notselector)
                                            layout.layoutParams = LinearLayout.LayoutParams(50, 12)
                                            layout.id = j
                                            mLinear.addView(layout, layoutParams)
                                        }
                                    }
                                    adapter = ScreenAdapter(this@ProfileInformationOppositeUserActivity, i, url0, url1, url2, url3, url4, url5, 0)
                                    Log.d("111", "1")
                                } else {
                                    adapter = ScreenAdapter(this@ProfileInformationOppositeUserActivity, 1, url0, url1, url2, url3, url4, url5, drawableGender)
                                }
                            } else {
                                adapter = ScreenAdapter(this@ProfileInformationOppositeUserActivity, 1, url0, url1, url2, url3, url4, url5, drawableGender)
                            }
                            viewPager.adapter = adapter
                            if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                                val map = dataSnapshot.value as MutableMap<*, *>
                                xOpposite = dataSnapshot.child("Location").child("X").value.toString().toDouble()
                                yOpposite = dataSnapshot.child("Location").child("Y").value.toString().toDouble()
                                val distance = CalculateDistance.calculate(GlobalVariable.x.toDouble(), GlobalVariable.y.toDouble(), xOpposite, yOpposite)
                                val distance1 = df2.format(distance)
                                val lang = LocalizationActivityDelegate(this@ProfileInformationOppositeUserActivity).getLanguage(this@ProfileInformationOppositeUserActivity).toString()
                                val geoCoder: Geocoder = if (lang == "th") {
                                    Geocoder(this@ProfileInformationOppositeUserActivity)
                                } else {
                                    Geocoder(this@ProfileInformationOppositeUserActivity, Locale.UK)
                                }
                                val addresses: MutableList<Address?>?
                                try {
                                    addresses = geoCoder.getFromLocation(xOpposite, yOpposite, 1)
                                    val city = addresses[0]!!.adminArea
                                    city1.text = "$city ,  $distance1 ${getString(R.string.kilometer)}"
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }


                                if (map["myself"] != null && map["myself"] != "") {
                                    l3.visibility = View.VISIBLE
                                    myself.text = map["myself"].toString()
                                }
                                if (map["sex"] != null) {
                                    drawableGender = if (map["sex"].toString() == "Male") {
                                        gender.setText(R.string.male)
                                        R.drawable.ic_man
                                    } else {
                                        gender.setText(R.string.female)
                                        R.drawable.ic_woman
                                    }
                                }
                                if (map["Age"] != null) {
                                    age.text = map["Age"].toString()
                                }
                                if (map["name"] != null) {
                                    name.text = map["name"].toString()
                                    report.text = "${getString(R.string.dialog_report)}  ${map["name"].toString()}"
                                }
                                if (map["career"] != null && map["career"] != "") {
                                    l1.visibility = View.VISIBLE
                                    career.text = map["career"].toString()
                                }
                                if (map["study"] != null && map["study"] != "") {
                                    l2.visibility = View.VISIBLE
                                    study.text = map["study"].toString()
                                }
                                if (map["language"] != null && map["language"] != "") {
                                    l4.visibility = View.VISIBLE
                                    val size = dataSnapshot.child("language").childrenCount.toInt()
                                    var str = ""
                                    for (u in 0 until size) {
                                        val position = dataSnapshot.child("language").child("language$u").value.toString().toInt()
                                        str += listItems2[position]
                                        if (u != size - 1) {
                                            str = "$str, "
                                        }
                                    }
                                    language.text = str
                                }
                                if (map["religion"] != null && map["religion"] != "") {
                                    l5.visibility = View.VISIBLE
                                    religion.text = listItems3[map["religion"].toString().toInt()]
                                }
                                if (map["hobby"] != null && map["hobby"] != "") {
                                    l6.visibility = View.VISIBLE
                                    val size = dataSnapshot.child("hobby").childrenCount.toInt()
                                    val str = ""
                                    for (u in 0 until size) {
                                        val position = dataSnapshot.child("hobby").child("hobby$u").value.toString().toInt()
                                        addT(listItems[position])
                                    }
                                }
                                val logoMoveAnimation: Animation = AnimationUtils.loadAnimation(this@ProfileInformationOppositeUserActivity, R.anim.fade_in2)
                                information.visibility = View.VISIBLE
                                informationIn.visibility = View.VISIBLE
                                informationIn.animation = logoMoveAnimation
                            }
                        } else {
                            userNotFound.visibility = View.VISIBLE
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }


        }
    }

    private fun addT(string: String?) {
        flexboxLayout = findViewById(R.id.Grid_profile)
        params = GridLayout.LayoutParams()
        val textView = TextView(this)
        textView.text = string
        textView.setTextColor(ContextCompat.getColor(applicationContext, R.color.c2))
        textView.setPadding(13, 10, 13, 10)
        textView.setBackgroundResource(R.drawable.tag)
        params.setMargins(10, 10, 10, 10)
        textView.layoutParams = params
        flexboxLayout.addView(textView)
    }


    override fun onBackPressed() {
        val returnIntent = Intent()
        returnIntent.putExtra("result", intent.getIntExtra("position", 0))
        returnIntent.putExtra("status", delete)
        setResult(11, returnIntent)
        super.onBackPressed()
        //finish()
    }


}