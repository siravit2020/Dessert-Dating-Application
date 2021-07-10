package com.maiguy.dessert.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.github.demono.AutoScrollViewPager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.maiguy.dessert.R
import com.maiguy.dessert.ViewModel.QuestionViewModel
import com.maiguy.dessert.constants.VipDialogType
import com.maiguy.dessert.dialogs.adapter.VipSlideAdapter
import com.maiguy.dessert.model.PagerModel
import com.maiguy.dessert.services.BillingService
import com.maiguy.dessert.utils.GlobalVariable
import me.relex.circleindicator.CircleIndicator

class VipDialog(private val activity: Activity, private var type: VipDialogType) {
    private var dialog: Dialog
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var usersDb: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
    private var currentUid: String = mAuth.currentUser!!.uid
    private var rewardedAd: RewardedAd? = null
    private var questionViewModel:QuestionViewModel? = null
    private var lang:String = "th"
    init {

        dialog = Dialog(activity)
    }

    fun openDialog() {

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.vip_dialog, null)
        val bQA = view.findViewById<Button>(R.id.question_call)
        val b1 = view.findViewById<Button>(R.id.buy)
        val b2 = view.findViewById<Button>(R.id.admob)
        val text = view.findViewById<TextView>(R.id.test_de)
        if (GlobalVariable.maxAdmob <= 0) {
            text.text = activity.getString(R.string.ads_out_stock)
            b2.visibility = View.GONE
        }
        if(GlobalVariable.outOfQuestion){
            bQA.visibility = View.GONE
        }
        createAndLoadRewardedAd(b2)

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(ContentValues.TAG, "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(ContentValues.TAG, "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(ContentValues.TAG, "Ad showed fullscreen content.")

//                createAndLoadRewardedAd(b2)

            }

        }
        bQA.setOnClickListener {
            questionAskDialog().show()
            dialog.dismiss()
        }
        b2.setOnClickListener {
            if (rewardedAd != null) {
                b2.text = activity.getString(R.string.loading_ads)
                rewardedAd?.show(activity, OnUserEarnedRewardListener() {
                    createAndLoadRewardedAd(b2)
                    if (type == VipDialogType.Card) {
                        GlobalVariable.maxLike++
                        GlobalVariable.maxAdmob -= 1
                        if (GlobalVariable.maxLike >= 10)
                            dialog.dismiss()
                        else if (GlobalVariable.maxAdmob <= 0) {
                            b2.visibility = View.GONE
                        }

                        usersDb.child(currentUid).child("MaxLike").setValue(GlobalVariable.maxLike)
                        usersDb.child(currentUid).child("MaxAdmob").setValue(GlobalVariable.maxAdmob)
                    }
                    if (type == VipDialogType.List) {
                        GlobalVariable.maxChat++
                        GlobalVariable.maxAdmob--
                        if (GlobalVariable.maxChat >= 10)
                            dialog.dismiss()
                        else if (GlobalVariable.maxAdmob <= 0)
                            b2.visibility = View.GONE
                        usersDb.child(currentUid).child("MaxChat").setValue(GlobalVariable.maxChat)
                        usersDb.child(currentUid).child("MaxAdmob").setValue(GlobalVariable.maxAdmob)
                    }


                })
            } else {
                Log.d(ContentValues.TAG, "The rewarded ad wasn't ready yet.")
            }
        }
        b1.setOnClickListener {

//            usersDb.child(currentUid).child("Vip").setValue(1)
            dialog.dismiss()
//            activity.finish()
//            activity.overridePendingTransition(0, 0)
//            activity.startActivity(activity.intent)
//            activity.overridePendingTransition(0, 0)
            BillingService(activity).billing()
        }
        dialog = Dialog(activity)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        val pagerModels: ArrayList<PagerModel?> = ArrayList()
        with(activity) {
            if (type == VipDialogType.Card) {
                pagerModels.add(PagerModel(getString(R.string.like_out_stock), getString(R.string.full_swipe), R.drawable.ic_heart))
                pagerModels.add(PagerModel(getString(R.string.get_5_star), getString(R.string.you_send_star), R.drawable.ic_starss))
                pagerModels.add(PagerModel(getString(R.string.unlimited_say_hi_2), getString(R.string.unlimited_say_hi_3), R.drawable.ic_hand))
                pagerModels.add(PagerModel(getString(R.string.who_like_you), getString(R.string.see_who_has_like), R.drawable.ic_love2))
            }
            if (type == VipDialogType.List) {
                pagerModels.add(PagerModel(getString(R.string.say_hi_out), getString(R.string.unlimited_say_hi), R.drawable.ic_hand))
                pagerModels.add(PagerModel(getString(R.string.unlimited_like), getString(R.string.full_right_swipe), R.drawable.ic_heart))
                pagerModels.add(PagerModel(getString(R.string.get_5_star), getString(R.string.you_send_star), R.drawable.ic_starss))
                pagerModels.add(PagerModel(getString(R.string.who_like_you), getString(R.string.see_who_has_like), R.drawable.ic_love2))
            }
        }
        val adapter = VipSlideAdapter(activity, pagerModels)
        val pager: AutoScrollViewPager = dialog.findViewById(R.id.viewpage)
        pager.adapter = adapter
        pager.startAutoScroll()
        val indicator: CircleIndicator = view.findViewById(R.id.indicator)
        indicator.setViewPager(pager)
        val width = (activity.resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    fun setViewModel(lang:String,questionViewModel: QuestionViewModel){
        this.questionViewModel = questionViewModel
        this.lang = lang
    }

    fun createAndLoadRewardedAd(b2: Button) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(activity, GlobalVariable.idAds, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedAd = null
            }

            override fun onAdLoaded(reward: RewardedAd) {
                rewardedAd = reward
                b2.text = activity.getString(R.string.ads_rewards)
            }
        })

    }

    private fun questionAskDialog(): Dialog {
        val view = LayoutInflater.from(activity).inflate(R.layout.question_ask_dialog, null)
        val btnConfirm = view.findViewById<Button>(R.id.confirm_button_askDialog)
        val btnDismiss = view.findViewById<Button>(R.id.dismiss_button_askDialog)
        val dialog = Dialog(activity)
        dialog.setContentView(view)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (activity.resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        btnDismiss.setOnClickListener {
            dialog.dismiss()
        }
        btnConfirm.setOnClickListener {
            questionViewModel!!.response(lang,2)
            dialog.dismiss()
        }
        return dialog
    }
}