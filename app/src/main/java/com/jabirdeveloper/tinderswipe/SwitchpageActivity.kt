package com.jabirdeveloper.tinderswipe

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.jabirdeveloper.tinderswipe.Functions.ChangLanguage
import com.jabirdeveloper.tinderswipe.Functions.GlobalVariable
import com.jabirdeveloper.tinderswipe.Functions.WarningDialog
import com.jabirdeveloper.tinderswipe.Listcard.ListCardActivity
import com.jabirdeveloper.tinderswipe.Matches.MatchesActivity

import kotlinx.coroutines.*

import kotlin.collections.HashMap

@Suppress("NAME_SHADOWING")
class SwitchpageActivity : AppCompatActivity() ,LocationListener {
    private lateinit var mLocationManager: LocationManager
    private var id = R.id.item2
    private var first: String = ""

    private var uid = FirebaseAuth.getInstance().currentUser!!.uid
    private val page1 = SettingMainActivity()
    private val page2 = MainActivity()
    private val page3 = ListCardActivity()
    private val page4 = MatchesActivity()
    private var functions = Firebase.functions
    private var activeFragment: Fragment = MainActivity()
    private val language:ChangLanguage = ChangLanguage(this)
    private val j1 = CoroutineScope(Job())
    lateinit var load:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        language.setLanguage()
        permissionCheck()
        setContentView(R.layout.activity_switch_page)
        load = findViewById(R.id.candyCane)
        j1.launch(Dispatchers.Unconfined) { // launch a new coroutine in background and continue

                getMyUser()
                getUnreadFunction()

        }
        //questionCalculate()
        bar = findViewById(R.id.bar2)
        if (intent.hasExtra("warning")) {
            val choice = this.resources.getStringArray(R.array.report_item)
            var nameAndValue = ""
            val name = intent.getStringArrayListExtra("warning")
            val value = intent.getIntegerArrayListExtra("warning_value")
            for (i in name!!.indices) {
                nameAndValue += "${i + 1}.${choice[Integer.valueOf(name[i])]}${getString(R.string.count_report)}	${value!![i]} ${getString(R.string.times)}"
            }
            WarningDialog(this,nameAndValue).show()

        }
        if (intent.hasExtra("first")) {
            first = intent.getStringExtra("first")!!
            if (first != "0") {
                bar!!.showBadge(R.id.item4, Integer.valueOf(first))
            }
            id = R.id.item2
            intent.removeExtra("first")
        }
        if (intent.hasExtra("accept")) {
            id = R.id.item4
            intent.removeExtra("accept")
        }
        if (intent.hasExtra("back")) {
            id = R.id.item1
            intent.removeExtra("back")
        }

        bar!!.setOnItemSelectedListener(object : ChipNavigationBar.OnItemSelectedListener {
            override fun onItemSelected(id: Int) {
                Log.d("num", id.toString())
                if (isOnline(applicationContext)) {
                    when (id) {
                        R.id.item1 -> {

                            supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left).hide(activeFragment).show(page1).commit()
                            activeFragment = page1
                            this@SwitchpageActivity.id = R.id.item1
                        }
                        R.id.item2 -> {
                            if (R.id.item2 < this@SwitchpageActivity.id)
                                supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left).hide(activeFragment).show(page2).commit()
                            else
                                supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).hide(activeFragment).show(page2).commit()
                            activeFragment = page2
                            this@SwitchpageActivity.id = R.id.item2

                        }
                        R.id.item3 -> {
                            if (R.id.item3 < this@SwitchpageActivity.id)
                                supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left).hide(activeFragment).show(page3).commit()
                            else
                                supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).hide(activeFragment).show(page3).commit()
                            activeFragment = page3
                            this@SwitchpageActivity.id = R.id.item3
                        }
                        R.id.item4 -> {
                            supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).hide(activeFragment).show(page4).commit()
                            activeFragment = page4
                            this@SwitchpageActivity.id = R.id.item4

                        }
                    }
                } else {
                    val builder = AlertDialog.Builder(this@SwitchpageActivity)
                    builder.setTitle("Internet ของคุณปิดอยุ่")
                    builder.setMessage("กรุณาเปิด Internet บนอุปกรณ์ของคุณเพื่อใช้งานแอปพลิเคชัน")
                    builder.setPositiveButton("เปิด internet") { _, _ ->
                        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(intent)
                    }
                            .setNegativeButton("ปิด app") { _, _ ->
                                val intent = Intent(this@SwitchpageActivity, ShowGpsOpen::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                finish()
                                startActivity(intent)
                            }
                    val mGPSDialog: Dialog = builder.create()
                    mGPSDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this@SwitchpageActivity, R.drawable.myrect2))
                    mGPSDialog.show()
                }

            }
        })
    }

    private fun permissionCheck(){
        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, arrayOf<String?>(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            ), 1
            )
        } else {
            val location=  mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(location != null) {
                j1.launch(Dispatchers.Unconfined) {
                    lastLocation(location)
                }
            }else {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, this)
            }
        }
    }
    private fun getUnreadFunction(): Task<HttpsCallableResult> {
        val data = hashMapOf(
                "uid" to "test"
        )
        return functions
                .getHttpsCallable("getUnreadChat")
                .call(data)
                .addOnSuccessListener { task ->
                    val data = task.data as Map<*, *>
                    Log.d("testGetUnreadFunction", data.toString())
                    val count = data["resultSum"].toString()
                    val myUnread2 = getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
                    val editorRead = myUnread2.edit()
                    editorRead.putInt("total", count.toInt())
                    editorRead.apply()
                    setCurrentIndex(count.toInt())
                }
                .addOnFailureListener {
                    Log.d("testGetUnreadFunction", "error")
                }
    }
    private fun questionCalculate(): Task<HttpsCallableResult> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
                "uid" to uid
        )
        return functions
                .getHttpsCallable("getPercentageMatching")
                .call(data)
                .addOnSuccessListener { task ->
                    val data = task.data as Map<*, *>
                    Log.d("testDatatatat", data.toString())
                    val ff = data.get("dictionary") as Map<*, *>
                }
                .addOnFailureListener {
                    Log.d("testDatatatat", "error")
                }
    }

    private fun getMyUser() {

        val userDb = Firebase.database.reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
        val connectedRef: DatabaseReference = FirebaseDatabase.getInstance().getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                if (connected) {
                    val statusUp = HashMap<String?, Any?>()
                    statusUp["status"] = 1
                    userDb.updateChildren(statusUp)
                } else {
                    Log.d("TAG112", "not connected")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG112", "Listener was cancelled")
            }
        })
        userDb.onDisconnect().let {
            val statusUp2 = HashMap<String?, Any?>()
            statusUp2["date"] = ServerValue.TIMESTAMP
            statusUp2["status"] = 0
            it.updateChildren(statusUp2)
        }

        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("Vip").value.toString().toInt() == 1) {
                    Log.d("vvv", "1")
                    GlobalVariable.vip = true

                } else {

                    GlobalVariable.vip = false
                }
                if (dataSnapshot.child("connection").hasChild("yep")) {

                    GlobalVariable.c = dataSnapshot.child("connection").child("yep").childrenCount.toInt()
                } else {

                    GlobalVariable.c = 0

                }
                if (dataSnapshot.hasChild("see_profile")) {

                    GlobalVariable.s = dataSnapshot.child("see_profile").childrenCount.toInt()
                } else {

                    GlobalVariable.s = 0
                }
                GlobalVariable.buyLike = dataSnapshot.hasChild("buy_like")
                GlobalVariable.name = dataSnapshot.child("name").value.toString()
                GlobalVariable.age = dataSnapshot.child("Age").value.toString().toInt()
                GlobalVariable.maxLike = dataSnapshot.child("MaxLike").value.toString().toInt()
                GlobalVariable.maxChat = dataSnapshot.child("MaxChat").value.toString().toInt()
                GlobalVariable.maxAdmob = dataSnapshot.child("MaxAdmob").value.toString().toInt()
                GlobalVariable.maxStar = dataSnapshot.child("MaxStar").value.toString().toInt()
                GlobalVariable.oppositeUserAgeMin = dataSnapshot.child("OppositeUserAgeMin").value.toString().toInt()
                GlobalVariable.oppositeUserAgeMax = dataSnapshot.child("OppositeUserAgeMax").value.toString().toInt()
                GlobalVariable.oppositeUserSex = dataSnapshot.child("OppositeUserSex").value.toString()
                GlobalVariable.distance = dataSnapshot.child("Distance").value.toString()

                if (dataSnapshot.hasChild("Location")) {

                    GlobalVariable.x = dataSnapshot.child("Location").child("X").value.toString()
                    GlobalVariable.y = dataSnapshot.child("Location").child("Y").value.toString()

                }
                if (dataSnapshot.child("ProfileImage").hasChild("profileImageUrl0")) {
                    GlobalVariable.image = dataSnapshot.child("ProfileImage").child("profileImageUrl0").value.toString()


                } else {

                    GlobalVariable.image = ""
                }


                    supportFragmentManager.beginTransaction().apply {
                        add(R.id.fragment_container2, page1).hide(page1)
                        add(R.id.fragment_container2, page2).hide(page2)
                        add(R.id.fragment_container2, page3).hide(page3)
                        add(R.id.fragment_container2, page4).hide(page4)
                    }.commit()

                bar!!.setItemSelected(id, true)


            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("tagh", "3")
            }

        })
    }
    fun setCurrentIndex(newValueFormCurrentIndex: Int) {
        if (newValueFormCurrentIndex > 0) {
            bar!!.showBadge(R.id.item4, newValueFormCurrentIndex)
        } else {
            bar!!.dismissBadge(R.id.item4)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 8) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@SwitchpageActivity, "fail GPS", Toast.LENGTH_SHORT).show()
            }
        }
        if(requestCode == 1){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                permissionCheck()
            }else{
                showGPSDisabledDialog()
            }
        }
    }
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            return true
        }
        return false
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 1000)
    }
    companion object {
        var bar: ChipNavigationBar? = null
        fun hide() {
            bar!!.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        j1.cancel()
    }
     private fun lastLocation(location: Location){
         val lon = location.longitude
         val lat = location.latitude
         val locationData = FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("Location")
         val location = hashMapOf<String, Any>()
         location["X"] = lat
         location["Y"] = lon
         locationData.updateChildren(location)
    }
    override fun onLocationChanged(p0: Location) {
        val locationData = FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("Location")
        val location = hashMapOf<String, Any>()
        location["X"] = p0.latitude
        location["Y"] = p0.longitude
        locationData.updateChildren(location)
    }
    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
    override fun onProviderEnabled(provider: String) {permissionCheck()}
    override fun onProviderDisabled(provider: String) {
        if (provider == LocationManager.GPS_PROVIDER) {
            showGPSDisabledDialog()
        }
    }
    private fun showGPSDisabledDialog() {
        val mGPSDialog:Dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.GPS_Disabled)
        builder.setMessage(R.string.GPS_open)
        builder.setPositiveButton(R.string.open_gps) { _, _ -> startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0) }.setNegativeButton(R.string.report_close) { _, _ ->
            val intent = Intent(this, ShowGpsOpen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            this.finish()
            startActivity(intent)
        }
        mGPSDialog = builder.create()
        mGPSDialog.window!!.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.myrect2))
        mGPSDialog.show()
    }
}