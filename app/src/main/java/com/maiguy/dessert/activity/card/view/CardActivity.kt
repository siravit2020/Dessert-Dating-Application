package com.maiguy.dessert.activity.card.view

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.android.billingclient.api.*

import com.bumptech.glide.Glide
import com.github.demono.AutoScrollViewPager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.maiandguy.dessert.utils.ErrorDialog
import com.maiguy.dessert.activity.card.adapter.CardAdapter
import com.maiguy.dessert.activity.card.model.CardModel
import com.maiguy.dessert.activity.chat.view.ChatActivity
import com.maiguy.dessert.QAStore.DialogFragment
import com.maiguy.dessert.R
import com.maiguy.dessert.ViewModel.QuestionViewModel
import com.maiguy.dessert.activity.filter_setting.view.FilterSettingActivity
import com.maiguy.dessert.activity.main.view.MainActivity
import com.maiguy.dessert.activity.sign_in.view.SignInActivity
import com.maiguy.dessert.constants.VipDialogType
import com.maiguy.dessert.dialogs.adapter.VipSlideAdapter
import com.maiguy.dessert.model.PagerModel
import com.maiguy.dessert.dialogs.VipDialog
import com.maiguy.dessert.utils.CloseLoading
import com.maiguy.dessert.utils.GlobalVariable
import com.yuyakaido.android.cardstackview.*
import kotlinx.coroutines.*
import me.relex.circleindicator.CircleIndicator
import java.io.IOException
import java.lang.Runnable
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NAME_SHADOWING")
class CardActivity : Fragment(), View.OnClickListener {


    private lateinit var mAuth: FirebaseAuth
    private var dis: String? = null
    private lateinit var cardAdapter: CardAdapter
    private lateinit var usersDb: DatabaseReference
    private var distance = 0.0
    private lateinit var like: Button
    private lateinit var dislike: Button
    private lateinit var star: Button
    private lateinit var layoutGps: ConstraintLayout
    private lateinit var anime1: ImageView
    private lateinit var anime2: ImageView
    private lateinit var textgps: TextView
    private lateinit var textGps2: TextView
    private lateinit var handler: Handler
    private lateinit var dialog: Dialog
    private lateinit var rowItem: ArrayList<CardModel>
    private lateinit var po: CardModel
    private lateinit var currentUid: String
    private var timeSend: String? = null
    private lateinit var touchGps: ImageView
    private var notificationMatch: String? = null
    private lateinit var cardStackView: CardStackView
    lateinit var manager: CardStackLayoutManager
    private var functions = Firebase.functions
    private var rewardedAd: RewardedAd? = null
    private lateinit var billingClient: BillingClient
    private var countLimit = 0
    private var countLimit2 = 0
    private var countLimit3 = 1
    private var countDataSet = 60
    private lateinit var resultlimit: ArrayList<*>
    private var checkEmpty = false
    private var empty = 0
    private var countEmpty = 0
    private lateinit var localizationDelegate: LocalizationActivityDelegate
    private lateinit var questionViewModel: QuestionViewModel
    private lateinit var errorDialog: ErrorDialog


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.activity_card, container, false)
        checkStart()


        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser == null) {
            val intent = Intent(context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
       // BillingService(requireActivity()).checkStatusBilling()

        localizationDelegate = LocalizationActivityDelegate(requireActivity())
        layoutGps = view.findViewById(R.id.layout_in)
        textgps = view.findViewById(R.id.textView8)
        textGps2 = view.findViewById(R.id.textView9)
        touchGps = view.findViewById(R.id.imageView3)
        textgps.setText(R.string.touch_settings)
        textGps2.setText(R.string.area_not_found)
        like = view.findViewById(R.id.like_button)
        dislike = view.findViewById(R.id.dislike_button)
        star = view.findViewById(R.id.star_button)
        anime1 = view.findViewById(R.id.anime1)
        anime2 = view.findViewById(R.id.anime2)
        rowItem = ArrayList()
        currentUid = mAuth.currentUser!!.uid
        usersDb = FirebaseDatabase.getInstance().reference.child("Users")
        cardStack()
        cardStackView = view.findViewById(R.id.frame)
        cardAdapter = CardAdapter(rowItem, context, this)
        cardStackView.layoutManager = manager
        cardStackView.adapter = cardAdapter
        cardStackView.itemAnimator = DefaultItemAnimator()
        like.setOnClickListener(this)
        dislike.setOnClickListener(this)
        touchGps.setOnClickListener(this)
        star.setOnClickListener(this)
        handler = Handler()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return QuestionViewModel(requireContext()) as T
            }
        }).get(QuestionViewModel::class.java)

        questionViewModel.fetchQA.observe(requireActivity(), {
            Log.d("GET_QUESTION", it.size.toString())
            if (it.size > 0) {
                val dialogFragment = DialogFragment()
                dialogFragment.setData(it, "Question")
                dialogFragment.show(requireActivity().supportFragmentManager, "Question dialog")
            } else {
                errorDialog = ErrorDialog(requireContext())
                errorDialog.outOfQuestionDialog().show()
            }

        })
    }

    private fun cardStack() {
        manager = CardStackLayoutManager(context, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {}
            override fun onCardSwiped(direction: Direction?) {
                po = rowItem[manager.topPosition - 1]
                val userId = po.userId!!
                Log.d("GLOBAL_MAX_LIKE__cardActivity", GlobalVariable.maxLike.toString())
                if (direction == Direction.Right) {
                    if (GlobalVariable.maxLike > 0 || GlobalVariable.vip) {
                        val datetime = hashMapOf<String, Any>()
                        datetime["date"] = ServerValue.TIMESTAMP
                        usersDb.child(userId).child("connection").child("yep").child(currentUid).updateChildren(datetime)
                        GlobalVariable.maxLike = --GlobalVariable.maxLike
                        usersDb.child(currentUid).child("MaxLike").setValue(GlobalVariable.maxLike)
                        isConnectionMatches(userId)
                    } else {
                        handler.postDelayed(Runnable { cardStackView.rewind() }, 200)
                        //openDialog()
                        val dialog = VipDialog(activity!!,VipDialogType.Card)
                        dialog.setViewModel(localizationDelegate.getLanguage(requireContext()).toLanguageTag(),questionViewModel)
                        dialog.openDialog()
                    }
                }
                if (direction == Direction.Left) {
                    usersDb.child(userId).child("connection").child("nope").child(currentUid).setValue(true)
                }
                if (direction == Direction.Top) {
                    if (GlobalVariable.maxStar > 0) {
                        val datetime = hashMapOf<String, Any>()
                        datetime["date"] = ServerValue.TIMESTAMP
                        datetime["super"] = true
                        usersDb.child(userId).child("connection").child("yep").child(currentUid).updateChildren(datetime)
                        usersDb.child(currentUid).child("star_s").child(userId).setValue(true)
                        GlobalVariable.maxStar--
                        usersDb.child(currentUid).child("MaxStar").setValue(GlobalVariable.maxStar)
                        isConnectionMatches(userId)
                    } else {
                        handler.postDelayed(Runnable { cardStackView.rewind() }, 200)
                        //openDialog()
                        val dialog = VipDialog(activity!!,VipDialogType.Card)
                        dialog.setViewModel(localizationDelegate.getLanguage(requireContext()).toLanguageTag(),questionViewModel)
                        dialog.openDialog()
                    }
                }
            }

            override fun onCardRewound() {}
            override fun onCardCanceled() {}
            override fun onCardAppeared(view: View?, position: Int) {
                Log.d("ggg", "$position $countLimit $countLimit3 " + rowItem.size)

                if (countLimit2 == 5 && countLimit < countDataSet) {
                    getUser(resultlimit, false, rowItem.size - 1, 5)
                    countLimit2 = 0
                }
                if (countLimit3 % countDataSet == 0 && countLimit3 > 0) {
                    val handler = Handler()
                    handler.postDelayed({
                        callFunctions(countDataSet, false, rowItem.size - 1)
                        countLimit = 0
                        countLimit2 = 0
                    }, 300)

                }
                if (cardAdapter.itemCount >= 1) {
                    layoutGps.visibility = View.GONE
                }
            }

            override fun onCardDisappeared(view: View?, position: Int) {
                Log.d("ggg", "$position $countLimit $countLimit3 " + rowItem.size)
                if (checkEmpty) {
                    Log.d("ggg2", "$countEmpty $empty")
                    if (countEmpty == empty - 1) {
                        runnable!!.run()
                        layoutGps.visibility = View.VISIBLE
                    }
                    countEmpty++
                }
                countLimit2++
                countLimit3++
            }
        })
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(2)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
    }

    fun createAndLoadRewardedAd(b2: Button) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(requireContext(), "ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedAd = null
            }

            override fun onAdLoaded(reward: RewardedAd) {
                rewardedAd = reward
                b2.text = getString(R.string.ads_rewards)
            }
        })

    }

    private fun questionAskDialog(): Dialog {
        val view = layoutInflater.inflate(R.layout.question_ask_dialog, null)
        val btnConfirm = view.findViewById<Button>(R.id.confirm_button_askDialog)
        val btnDismiss = view.findViewById<Button>(R.id.dismiss_button_askDialog)
        val dialog = Dialog(requireContext())
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        btnDismiss.setOnClickListener {
            Log.d("CONFIRM_DIALOG","Cancel");
            dialog.dismiss()
        }
        btnConfirm.setOnClickListener {
            Log.d("CONFIRM_DIALOG","Click");
            questionViewModel.response(localizationDelegate.getLanguage(requireContext()).toLanguageTag(),2)
            dialog.dismiss()
        }
        return dialog
    }

    fun openDialog() {
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.vip_dialog, null)
        val bQA = view.findViewById<Button>(R.id.question_call)
        val b1 = view.findViewById<Button>(R.id.buy)
        val b2 = view.findViewById<Button>(R.id.admob)
        val text = view.findViewById<TextView>(R.id.test_de)
        if (GlobalVariable.maxAdmob <= 0) {
            text.text = getString(R.string.ads_out_stock)
            b2.visibility = View.GONE
        }
        createAndLoadRewardedAd(b2)

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(TAG, "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")

                createAndLoadRewardedAd(b2)

            }

        }
        bQA.setOnClickListener {
            Log.d("CONFIRM_DIALOG","Open");
            questionAskDialog().show()
            dialog.dismiss()
        }
        b2.setOnClickListener {
            if (rewardedAd != null) {
                rewardedAd?.show(requireActivity(), OnUserEarnedRewardListener() {
                    ++GlobalVariable.maxLike
                    GlobalVariable.maxAdmob -= 1
                    if (GlobalVariable.maxLike >= 10)
                        dialog.dismiss()
                    else if (GlobalVariable.maxAdmob <= 0) {
                        b2.visibility = View.GONE
                    }

                    usersDb.child(currentUid).child("MaxLike").setValue(GlobalVariable.maxLike)
                    usersDb.child(currentUid).child("MaxAdmob").setValue(GlobalVariable.maxAdmob)

                })
            } else {
                Log.d(TAG, "The rewarded ad wasn't ready yet.")
            }
        }
        b1.setOnClickListener {

            usersDb.child(currentUid).child("Vip").setValue(1)
            dialog.dismiss()
            requireActivity().finish()
            requireActivity().overridePendingTransition(0, 0)
            requireActivity().startActivity(requireActivity().intent)
            requireActivity().overridePendingTransition(0, 0)
        }
        dialog = Dialog(requireContext())
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        val pagerModels: ArrayList<PagerModel?> = ArrayList()
        with(pagerModels) {
            add(PagerModel(getString(R.string.like_out_stock), getString(R.string.full_swipe), R.drawable.ic_heart))
            add(PagerModel(getString(R.string.get_5_star), getString(R.string.you_send_star), R.drawable.ic_starss))
            add(PagerModel(getString(R.string.unlimited_say_hi_2), getString(R.string.unlimited_say_hi_3), R.drawable.ic_hand))
            add(PagerModel(getString(R.string.who_like_you), getString(R.string.see_who_has_like), R.drawable.ic_love2))

        }
        val adapter = VipSlideAdapter(requireContext(), pagerModels)
        val pager: AutoScrollViewPager = dialog.findViewById(R.id.viewpage)
        pager.adapter = adapter
        pager.startAutoScroll()
        val indicator: CircleIndicator = view.findViewById(R.id.indicator)
        indicator.setViewPager(pager)
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()
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

    private fun isConnectionMatches(userId: String) {
        val currentUserConnectionDb = usersDb.child(currentUid).child("connection").child("yep").child(userId)
        currentUserConnectionDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val key = FirebaseDatabase.getInstance().reference.child("Chat").push().key
                    usersDb.child(dataSnapshot.key!!)
                            .child("connection").child("matches")
                            .child(currentUid).child("ChatId")
                            .setValue(key)
                    usersDb.child(currentUid)
                            .child("connection")
                            .child("matches")
                            .child(dataSnapshot.key!!)
                            .child("ChatId").setValue(key)
                    usersDb.child(dataSnapshot.key!!)
                            .child("connection")
                            .child("yep")
                            .child(currentUid)
                            .setValue(null)
                    usersDb.child(currentUid)
                            .child("connection")
                            .child("yep")
                            .child(dataSnapshot.key!!)
                            .setValue(null)
                    if (notificationMatch == "1") {
                        dialog = Dialog(requireContext())
                        val inflater = layoutInflater
                        val view = inflater.inflate(R.layout.show_match, null)
                        val imageView = view.findViewById<ImageView>(R.id.image_match)

                        val textView = view.findViewById<TextView>(R.id.textMatch)
                        val textView2 = view.findViewById<TextView>(R.id.io)
                        val textView4 = view.findViewById<TextView>(R.id.textMatch2)
                        val button = view.findViewById<Button>(R.id.mess)
                        button.setOnClickListener {
                            dialog.dismiss()
                            val intent = Intent(context, ChatActivity::class.java)
                            val b = Bundle()
                            b.putString("matchId", po.userId)
                            b.putString("nameMatch", po.name)
                            b.putString("first_chat", "")
                            b.putString("unread", "0")
                            intent.putExtras(b)
                            requireContext().startActivity(intent)
                        }
                        textView2.setOnClickListener { dialog.dismiss() }
                        textView.text = po.name
                        if (dataSnapshot.hasChild("super")) {
                            star.visibility = View.VISIBLE
                            textView4.text = "  " + getString(R.string.send_star)
                        } else textView4.text = "  " + getString(R.string.like_you_too)
                        Glide.with(requireContext()).load(po.profileImageUrl).into(imageView)
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.setContentView(view)
                        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                        dialog.show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }



    private fun getDis() {


        distance = when (GlobalVariable.distance) {
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
    }

    private fun callFunctions(limit: Int, type: Boolean, count: Int) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val preferences2 = requireActivity().getSharedPreferences("notification_match", Context.MODE_PRIVATE)
            notificationMatch = preferences2.getString("noti", "1")
            var pre = 0
            if (!type) pre = 0

            val data = hashMapOf(
                    "sex" to GlobalVariable.oppositeUserSex,
                    "min" to GlobalVariable.oppositeUserAgeMin,
                    "max" to GlobalVariable.oppositeUserAgeMax,
                    "x_user" to GlobalVariable.x.toDouble(),
                    "y_user" to GlobalVariable.y.toDouble(),
                    "distance" to distance,
                    "limit" to pre + limit,
                    "prelimit" to pre
            )
            Log.d("tagkl", data.toString())

            functions.getHttpsCallable("getUserCard")
                    .call(data)
                    .addOnFailureListener { Log.d("ghj", "failed") }
                    .addOnSuccessListener { task ->

                        val result1 = task.data as Map<*, *>
                        resultlimit = result1["o"] as ArrayList<*>
                        if (resultlimit.isNotEmpty()) {
                            Log.d("iii", resultlimit.size.toString())
                            getUser(resultlimit, type, count, 10)
                        } else {
                            val logoMoveAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_out2)
                            val load = (activity as MainActivity).load
                            logoMoveAnimation.setAnimationListener(object : Animation.AnimationListener {
                                override fun onAnimationStart(animation: Animation?) {

                                }

                                override fun onAnimationEnd(animation: Animation?) {
                                    load.visibility = View.GONE
                                }

                                override fun onAnimationRepeat(animation: Animation?) {

                                }
                            })
                            load.startAnimation(logoMoveAnimation)
                            layoutGps.visibility = View.VISIBLE
                            runnable!!.run()
                        }


                    }
        }

    }

    private fun getUser(result2: ArrayList<*>, type: Boolean, count: Int, limit: Int) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Default) {
                val preferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)


                var addresses: MutableList<Address>
                val language = LocalizationActivityDelegate(requireActivity()).getLanguage(requireContext()).toString()
                val geoCoder: Geocoder = if (language == "th") {
                    Geocoder(context)
                } else {
                    Geocoder(context, Locale.UK)
                }
                Log.d("iop", "")
                var a = countLimit + limit
                if (result2.size < countLimit + limit) {
                    a = result2.size
                    checkEmpty = true
                    empty = result2.size
                }
                for (x in countLimit until a) {
                    countLimit++
                    Log.d("iop", "$countLimit ${result2.size}")
                    val user = result2[x] as Map<*, *>
                    Log.d("ghj", user["name"].toString() + " , " + user["distance_other"].toString())
                    var myself = ""
                    var citysend: String? = ""
                    var offStatus = false
                    var vip = false
                    var starS = false

                    val location = user["Location"] as Map<*, *>
                    try {
                        addresses = geoCoder.getFromLocation(location["X"].toString().toDouble(), location["Y"].toString().toDouble(), 1)
                        val city = addresses[0].adminArea
                        citysend = city
                    } catch (e: IOException) {
                        e.printStackTrace()
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
                    if (user["Vip"] == 1) {
                        vip = true
                    }
                    if (user["star_s"] != null) {
                        if ((user["star_s"] as Map<*, *>)[currentUid] != null)
                            starS = true
                    }
                    dis = df2.format(user["distance_other"])
                    rowItem.add(CardModel(user["key"].toString(), user["name"].toString(), profileImageUrl, user["Age"].toString(), dis, citysend, status, myself, offStatus, vip, starS ,
                        user["percent"] as Int
                    ))

                }
            }
            withContext(Dispatchers.Main) {
                if (type) {
                    cardAdapter.notifyDataSetChanged()
                    val load = (activity as MainActivity).load
                    CloseLoading(context, load).invoke()

                } else {
                    cardAdapter.notifyItemRangeChanged(count, rowItem.size)
                }
            }
        }

    }


    private val df2: DecimalFormat = DecimalFormat("#.#")

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1112) {
//            handler.postDelayed(Runnable {
//                requireActivity().finish()
//                requireActivity().overridePendingTransition(0, 0)
//                requireActivity().startActivity(requireActivity().intent)
//                requireActivity().overridePendingTransition(0, 0)
//            }, 400)
        }
        if (requestCode == 115) {
            when (resultCode) {
                1 -> likeDelay()
                2 -> disLikeDelay()
                3 -> starDelay()
            }
        }

    }

    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requireActivity().recreate()
                getDis()
            }
        }
    }*/


    private fun likeDelay() {
        val handler = Handler()
        handler.postDelayed({
            like()
        }, 300)
    }

    private fun like() {
        val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
        manager.setSwipeAnimationSetting(setting)
        cardStackView.swipe()
    }

    private fun disLikeDelay() {
        val handler = Handler()
        handler.postDelayed({
            disLike()
        }, 300)
    }

    private fun disLike() {
        val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
        manager.setSwipeAnimationSetting(setting)
        cardStackView.swipe()
    }


    private fun starDelay() {
        val handler = Handler()
        handler.postDelayed({
            star()
        }, 300)

    }

    private fun star() {
        val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Top)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
        manager.setSwipeAnimationSetting(setting)
        cardStackView.swipe()
    }


    private fun checkStart() {
        /*mLocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf<String?>(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            ), 1)
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0f, this)*/
        viewLifecycleOwner.lifecycleScope.launch { // launch a new coroutine in background and continue
            withContext(Dispatchers.Default) { // background thread
                getDis()
            }
            withContext(Dispatchers.IO) { // background thread
                callFunctions(countDataSet, true, 0)
            }
        }

        //}
    }

    override fun onClick(v: View?) {
        if (v == touchGps) {
            startActivityForResult(Intent(context, FilterSettingActivity::class.java), 1112)
        }
        if (v == like) {
            like()
        }
        if (v == dislike) {
            disLike()
        }
        if (v == star) {
            star()
        }
    }

}