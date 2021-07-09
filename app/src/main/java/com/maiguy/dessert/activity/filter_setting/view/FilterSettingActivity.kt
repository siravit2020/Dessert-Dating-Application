package com.maiguy.dessert.activity.filter_setting.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.maiguy.dessert.activity.close_account.view.CloseAccount
import com.maiguy.dessert.R
import com.maiguy.dessert.activity.main.view.MainActivity
import com.maiguy.dessert.utils.ChangLanguage
import com.maiguy.dessert.utils.GlobalVariable
import com.maiguy.dessert.activity.sign_in.view.SignInActivity
import com.maiguy.dessert.activity.web_view.WebViewActivity
import hearsilent.discreteslider.DiscreteSlider
import hearsilent.discreteslider.DiscreteSlider.OnValueChangedListener
import kotlinx.android.synthetic.main.activity_filter_setting.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap


class  FilterSettingActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var radioGroup: RadioGroup
    private lateinit var toolbar: Toolbar
    private lateinit var delete: LinearLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var textSeek2: TextView
    private lateinit var textSeekAge: TextView
    private lateinit var change: TextView
    private lateinit var seekBar2: DiscreteSlider
    private lateinit var distanceUser: String
    private lateinit var currentUid: String
    private lateinit var ageMin: String
    private lateinit var ageMax: String
    private lateinit var gender: String
    private var maxV = 0
    private var minV = 0
    private lateinit var logout: TextView
    private lateinit var mSlider: DiscreteSlider
    private var selectedPosition = -1
    private var checkItem = -1
    private lateinit var noti1: SwitchMaterial
    private lateinit var online: SwitchMaterial
    private lateinit var onOffCard: SwitchMaterial
    private lateinit var onOffList: SwitchMaterial
    private var notificationMatch: String? = null
    private var onOff: String? = null
    private val localizationDelegate = LocalizationActivityDelegate(this)
    private val order: Array<String?> = arrayOf("ภาษาไทย", "English")
    private lateinit var map: HashMap<String, Any>
    private val language: ChangLanguage = ChangLanguage(this)
    private var valCh = false
    private lateinit var job:Job
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = CoroutineScope(Job()).launch(Dispatchers.IO) {
            getdisAge()
        }
        setContentView(R.layout.activity_filter_setting)
        change = findViewById(R.id.change_Language)
        loadLocal()
        mAuth = FirebaseAuth.getInstance()
        radioGroup = findViewById(R.id.radioGroup)
        textSeek2 = findViewById(R.id.text_seek2)
        seekBar2 = findViewById(R.id.seekBar_2)
        toolbar = findViewById(R.id.my_tools)
        delete = findViewById(R.id.delete)
        logout = findViewById(R.id.bl)
        mSlider = findViewById(R.id.discreteSlider)
        noti1 = findViewById(R.id.switch1)
        online = findViewById(R.id.switch2)
        onOffCard = findViewById(R.id.on_off_card)
        onOffList = findViewById(R.id.on_off_list)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.setting)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        currentUid = mAuth.uid.toString()
        textSeekAge = findViewById(R.id.seek_age_text)
        change.setOnClickListener(this)
        mSlider.setValueChangedImmediately(true)
        mSlider.setOnValueChangedListener(object : OnValueChangedListener() {


            @SuppressLint("SetTextI18n")
            override fun onValueChanged(minProgress: Int, maxProgress: Int, fromUser: Boolean) {
                minV = minProgress + 18
                maxV = maxProgress + 18

                textSeekAge.text = "$minV - $maxV"
                if (maxV == 70) textSeekAge.text = "$minV - $maxV+" else textSeekAge.text = "$minV - $maxV"
            }
        })
        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButton -> gender = "Male"
                R.id.radioButton2 -> gender = "Female"
                R.id.radioButton3 -> gender = "All"
            }

        })
        seekBar2.setValueChangedImmediately(true)
        seekBar2.setOnValueChangedListener(object : OnValueChangedListener() {
            @SuppressLint("SetTextI18n")
            override fun onValueChanged(progress: Int, fromUser: Boolean) {
                super.onValueChanged(progress, fromUser)
                val value = progress.toString()

                if (progress == 190) textSeek2.text = "$value km+" else textSeek2.text = "$value km"
            }

            override fun onValueChanged(minProgress: Int, maxProgress: Int, fromUser: Boolean) {}
        })
        findViewById<RelativeLayout>(R.id.policy).setOnClickListener {
            startActivity(Intent(this@FilterSettingActivity, WebViewActivity::class.java))
        }
        logout.setOnClickListener(this)
        delete.setOnClickListener(this)
        onOffCard.setOnClickListener(this)
        onOffList.setOnClickListener(this)
    }

    private fun getdisAge() {

        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                map = HashMap<String, Any>()
                if (dataSnapshot.hasChild("Distance")) {
                    when {
                        dataSnapshot.child("Distance").value.toString() == "true" -> {
                            seekBar2.progress = 1
                        }
                        dataSnapshot.child("Distance").value.toString() == "Untitled" -> {
                            seekBar2.progress = 190
                        }
                        else -> {
                            distanceUser = dataSnapshot.child("Distance").value.toString()
                            val distance1 = distanceUser.toInt()
                            seekBar2.progress = distance1 - 1
                            textSeek2.text = "$distance1 km"
                        }
                    }
                    map["Distance"] = seekBar2.progress
                }
                if (dataSnapshot.hasChild("OppositeUserAgeMin")) {
                    ageMin = dataSnapshot.child("OppositeUserAgeMin").value.toString()
                    ageMax = dataSnapshot.child("OppositeUserAgeMax").value.toString()
                    minV = ageMin.toInt()
                    maxV = ageMax.toInt()
                    map["OppositeUserAgeMin"] = minV
                    map["OppositeUserAgeMax"] = maxV
                    mSlider.minProgress = minV - 18
                    mSlider.maxProgress = maxV - 18
//                    if (maxV == 70) textSeekAge.text = "$minV - $maxV+" else textSeekAge.text = "$ageMin - $ageMax"
                    textSeekAge.text = "$ageMin - $ageMax"
                }
                if (dataSnapshot.hasChild("OppositeUserSex")) {
                    when (dataSnapshot.child("OppositeUserSex").value.toString()) {
                        "Male" -> {
                            radioGroup.check(R.id.radioButton);map["gender"] = "Male"
                        }
                        "Female" -> {
                            radioGroup.check(R.id.radioButton2);map["gender"] = "Female"
                        }
                        "All" -> {
                            radioGroup.check(R.id.radioButton3);map["gender"] = "All"
                        }
                    }

                }
                if (!dataSnapshot.hasChild("off_status")) {
                    map["on_off"] = true
                    onOff = "1"
                    online.isChecked = true
                } else {
                    map["on_off"] = false
                    onOff = null
                    online.isChecked = false
                }
                if (!dataSnapshot.hasChild("off_card")) {
                    map["off_card"] = true
                    onOffCard.isChecked = true
                }
                if (!dataSnapshot.hasChild("off_list")) {
                    map["off_list"] = true
                    onOffList.isChecked = true
                }
                val preferences2 = getSharedPreferences("notification_match", Context.MODE_PRIVATE)
                notificationMatch = preferences2.getString("noti", "1")
                noti1.isChecked = notificationMatch == "1"
                map["noti"] = noti1.isChecked
                val logoMoveAnimation: Animation = AnimationUtils.loadAnimation(this@FilterSettingActivity, R.anim.fade_in2)
                settingPage.visibility = View.VISIBLE
                settingPage.startAnimation(logoMoveAnimation)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    private fun loadLocal() {
        if (localizationDelegate.getLanguage(this).toString() == "th") {
            checkItem = 0
            change.text = order[checkItem]
        } else {
            checkItem = 1
            change.text = order[checkItem]
        }
        language.setLanguage()
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

    fun save() {
        var value = ""
        val progress = seekBar2.progress + 1
        if (progress in 0..189) {
            value = progress.toString()
        } else if (progress == 190) {
            value = "Untitled"
        }
        notificationMatch = if (noti1.isChecked) {
            "1"
        } else "0"
        onOff = if (!online.isChecked) {
            "1"
        } else null
        val card: String? = if (!onOffCard.isChecked) {
            "1"
        } else null

        val list: String? = if (!onOffList.isChecked) {
            "1"
        } else null
        val currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(currentUid)

        val userInfo = hashMapOf(
                "Distance" to value,
                "off_status" to onOff,
                "OppositeUserSex" to gender,
                "OppositeUserAgeMin" to minV,
                "OppositeUserAgeMax" to maxV,
                "off_card" to card,
                "off_list" to list
        )
        GlobalVariable.apply {
            oppositeUserAgeMin = minV
            oppositeUserAgeMax = maxV
            oppositeUserSex = gender
            distance = value
        }
        currentUserDb.updateChildren(userInfo as Map<String, *>)
        val editor = getSharedPreferences("notification_match", Context.MODE_PRIVATE).edit()
        editor.putString("noti", notificationMatch)
        editor.apply()
        Handler().postDelayed({
            finish()
            overridePendingTransition(0, 0)
            startActivity(Intent(this@FilterSettingActivity, MainActivity::class.java))
            overridePendingTransition(0, 0)
        }, 100)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(this::map.isInitialized){
            val user = FirebaseAuth.getInstance().currentUser
            if (map["Distance"] != seekBar2.progress) valCh = true
            if (map["noti"] != noti1.isChecked) valCh = true
            if (map["on_off"] != online.isChecked) valCh = true
            if (map["off_card"] != onOffCard.isChecked) valCh = true
            if (map["off_list"] != onOffList.isChecked) valCh = true
            if (minV != map["OppositeUserAgeMin"]) valCh = true
            if (maxV != map["OppositeUserAgeMax"]) valCh = true
            if (gender != map["gender"]) valCh = true
            if (user != null && valCh) {
                save()
            }
        }



    }

    override fun onClick(v: View?) {
        if(v == change){
            val mBuilder = AlertDialog.Builder(this@FilterSettingActivity)
            mBuilder.setTitle(R.string.language)
            mBuilder.setSingleChoiceItems(order, checkItem) { _, which -> //item2 = order[which];
                selectedPosition = which
            }
            mBuilder.setCancelable(true)
            mBuilder.setPositiveButton(R.string.ok) { _, _ ->
                if (selectedPosition == 0) localizationDelegate.setLanguage(this,"th")
                else
                    localizationDelegate.setLanguage(this,"en")
                language.setLanguage()
                save()
                finish()
                overridePendingTransition(0, 0)
                startActivity(Intent(this@FilterSettingActivity, MainActivity::class.java))
                overridePendingTransition(0, 0)

            }
            mBuilder.setNegativeButton(R.string.cancle) { dialog, _ -> dialog.dismiss() }
            val mDialog = mBuilder.create()
            mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@FilterSettingActivity, R.drawable.myrect2))
            mDialog.show()
        }
        if(v == logout){
            val userDb = Firebase.database.reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
            val status_up2 = HashMap<String?, Any?>()
            status_up2["date"] = ServerValue.TIMESTAMP
            status_up2["status"] = 0
            userDb.updateChildren(status_up2)
            mAuth.signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(application.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            googleSignInClient = GoogleSignIn.getClient(application, gso)
            googleSignInClient.signOut()
            val intent = Intent(applicationContext, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        if(v == delete){

            val intent = Intent(applicationContext, CloseAccount::class.java)
            startActivity(intent)
        }
        if(v == onOffCard){
            if (!onOffCard.isChecked) {
                val mBuilder = AlertDialog.Builder(this@FilterSettingActivity)
                mBuilder.setTitle(R.string.vision_closed)
                mBuilder.setMessage(R.string.vision_closed_match)
                mBuilder.setCancelable(true)
                mBuilder.setOnCancelListener { onOffCard.isChecked = true }
                mBuilder.setPositiveButton(R.string.ok) { _, _ -> onOffCard.isChecked = false; }
                mBuilder.setNegativeButton(R.string.cancel) { _, _ -> onOffCard.isChecked = true; }
                val mDialog = mBuilder.create()
                mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@FilterSettingActivity, R.drawable.myrect2))
                mDialog.show()
            }
        }
        if(v == onOffList){
            if (!onOffList.isChecked) {
                val mBuilder = AlertDialog.Builder(this@FilterSettingActivity)
                mBuilder.setTitle(R.string.vision_closed)
                mBuilder.setMessage(R.string.vision_closed_nearby)
                mBuilder.setCancelable(true)
                mBuilder.setOnCancelListener { onOffList.isChecked = true }
                mBuilder.setPositiveButton(R.string.ok) { _, _ -> onOffList.isChecked = false; }
                mBuilder.setNegativeButton(R.string.cancle) { _, _ -> onOffList.isChecked = true; }
                val mDialog = mBuilder.create()
                mDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@FilterSettingActivity, R.drawable.myrect2))
                mDialog.show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        job.cancel()
    }
}