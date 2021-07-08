package com.maiguy.dessert.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.maiguy.dessert.activity.first.view.FirstActivity
import com.maiguy.dessert.activity.main.view.MainActivity
import com.maiguy.dessert.activity.show_gps_open.view.ShowGpsOpen
import com.maiguy.dessert.utils.GlobalVariable
import kotlinx.coroutines.*

class BillingService(private var activity: Activity) {
    private var billingClient: BillingClient

    private var vipDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("Vip")

    init {
        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        FirebaseDatabase.getInstance().reference
                            .child("Users")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child("Vip")
                            .setValue(1).addOnSuccessListener {
                                val intent = Intent(activity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                activity.overridePendingTransition(0, 0)
                                activity.startActivity(intent)
                                activity.overridePendingTransition(0, 0)
                            }

                    }
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                } else {
                    // Handle any other error codes.
                }
            }
        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()


    }

    fun billing() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("billing", "200ok")
                    GlobalScope.launch {
                        querySkuDetails()
                    }
                }

            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })


    }

    suspend fun checkStatusBilling() {
        withContext(Dispatchers.IO){

            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { billingResult, mutableList ->
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                if (mutableList.isNotEmpty()) {
                                    Log.d("billing_status", mutableList[0].originalJson)
                                } else {
                                    vipDatabase
                                        .get()
                                        .addOnSuccessListener {
                                            if (it.exists()) {
                                                if (it.value.toString() == "1") {

                                                    vipDatabase.setValue(0)
                                                    GlobalVariable.vip = false
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }

                }

                override fun onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            })
        }



    }


    suspend fun querySkuDetails() {
        val skuList = ArrayList<String>()

        skuList.add("dessert_vip")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        // leverage querySkuDetails Kotlin extension function
        val skuDetailsResult = withContext(Dispatchers.IO) {
            billingClient.querySkuDetails(params.build())
        }
        Log.d("billing", skuDetailsResult.skuDetailsList!!.size.toString())
        skuDetailsResult.skuDetailsList!!.forEach {
            Log.d("billing", it.title)

            // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(it)
                .build()
            val responseCode = billingClient.launchBillingFlow(activity, flowParams).responseCode

        }

    }


}