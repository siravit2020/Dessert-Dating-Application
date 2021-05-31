package com.jabirdeveloper.tinderswipe.ui.register.view_model

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.jabirdeveloper.tinderswipe.MainActivity
import com.jabirdeveloper.tinderswipe.R
import com.tapadoo.alerter.Alerter
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream

class RegisterViewModel(private var intent: Intent, application: Application) : AndroidViewModel(application) {

    private var email: String? = null
    private var pass: String? = null
    private var name: String? = null
    private var sex: String? = null
    private var Age: Int = 18
    private var type: String? = null
    private var x: Double = 0.0
    private var y: Double = 0.0
    private var hashMapQA: Map<*, *>? = null
    private lateinit var UserId: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUserDb: DatabaseReference
    private var app = application

    init {

        x = intent.getDoubleExtra("X", x)
        y = intent.getDoubleExtra("Y", y)
        type = intent.getStringExtra("Type")
        email = intent.getStringExtra("email")
        pass = intent.getStringExtra("password")
        name = intent.getStringExtra("Name")
        sex = intent.getStringExtra("Sex")
        Age = intent.getIntExtra("Age", Age)
    }



    private fun createId(i: Int) {
        Log.d("showItemList", "$type , email")
        if (type == "email") {
            mAuth.createUserWithEmailAndPassword(email!!, pass!!).addOnCompleteListener(ContextCompat.getMainExecutor(app)) { task ->
//                if (!task.isSuccessful) Snackbar.make(b1, R.string.try_again, Snackbar.LENGTH_LONG).show()
//                else createDataToFirebase(i)
            }
        } else {
            Log.d("showItemList", "else")
//            createDataToFirebase(i)
        }
    }

//    private fun createDataToFirebase(i: Int) {
//        val editor = activity.getSharedPreferences("notification_match", Context.MODE_PRIVATE).edit()
//        editor.putString("noti", "1")
//        editor.apply()
//        UserId = mAuth.currentUser!!.uid
//        currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(UserId)
//        val userInfo = hashMapOf(
//                "name" to name,
//                "sex" to sex,
//                "Age" to Age,
//                "Distance" to "Untitled",
//                "OppositeUserSex" to "All",
//                "OppositeUserAgeMin" to 18,
//                "OppositeUserAgeMax" to 70,
//                "MaxChat" to 20,
//                "MaxLike" to 40,
//                "MaxAdmob" to 10,
//                "MaxStar" to 3,
//                "Vip" to 0,
//                "birth" to intent.getLongExtra("Birth", 0)
//        )
//        currentUserDb.updateChildren(userInfo as Map<String, Any>)
//        val location = hashMapOf(
//                "X" to x,
//                "Y" to y
//        )
//        currentUserDb.child("Questions").setValue(hashMapQA)
//        currentUserDb.child("Location").updateChildren(location as Map<String, Any>)
//        if (i == 1) {
//            if (bitmap != null) {
//                dialog.show()
//                val filepath = FirebaseStorage.getInstance().reference
//                        .child("profileImages")
//                        .child(UserId)
//                        .child("profileImageUrl0")
//
//                val baos = ByteArrayOutputStream()
//                bitmap?.compress(Bitmap.CompressFormat.JPEG, 40, baos)
//                val data = baos.toByteArray()
//                val uploadTask = filepath.putBytes(data)
//                uploadTask.apply {
//
//                    addOnFailureListener { finish() }
//                    addOnSuccessListener {
//
//                        Log.d("TAG", "สำเร็จ")
//                        filepath.downloadUrl.addOnSuccessListener { uri ->
//                            dialog.dismiss()
//                            val userInfo = hashMapOf(
//                                    "profileImageUrl0" to uri.toString()
//                            )
//                            currentUserDb.child("ProfileImage").updateChildren(userInfo as Map<String, Any>)
//
//                            val intent = Intent(this@RegisterFinishAcivity, MainActivity::class.java)
//                            intent.putExtra("first", "0")
//                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                            startActivity(intent)
//                            finish()
//                        }.addOnFailureListener(OnFailureListener {
//                            Log.d("TAG", "สวยยยยย")
//                            return@OnFailureListener
//                        })
//                    }
//                }
//
//            } else {
//                Alerter.create(this@RegisterFinishAcivity)
//                        .setTitle(R.string.profile_image)
//                        .setText(getString(R.string.choose_photo))
//                        .setBackgroundColorRes(R.color.c3)
//                        .show()
//            }
//        } else {
//            dialog.dismiss()
//            val intent = Intent(this@RegisterFinishAcivity, MainActivity::class.java)
//            intent.putExtra("first", "0")
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//            finish()
//        }
//    }

}