package com.maiguy.dessert.activity.register.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.maiguy.dessert.dialogs.LoadingDialog
import com.maiguy.dessert.R
import com.maiguy.dessert.activity.main.view.MainActivity
import com.nipunru.nsfwdetector.NSFWDetector
import com.maiguy.dessert.services.FirstAuthentication
import com.tapadoo.alerter.Alerter
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.ByteArrayOutputStream
import java.io.IOException

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NAME_SHADOWING")
class RegisterFinishAcivity : AppCompatActivity() {
    private var email: String? = null
    private var pass: String? = null
    private var name: String? = null
    private var sex: String? = null
    private var age: Int = 18
    private var type: String? = null
    private var x: Double = 0.0
    private var y: Double = 0.0
    private var hashMapQA: Map<*, *>? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var imageView: ImageView
    private lateinit var userId: String
    private lateinit var currentUserDb: DatabaseReference
    private lateinit var dialog: Dialog
    private lateinit var toolbar: Toolbar
    private lateinit var add: ImageView
    private lateinit var skip: TextView
    private lateinit var b1: Button
    private lateinit var firstAuthentication: FirstAuthentication

    var bitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regis_target__acivity)
        firstAuthentication = FirstAuthentication()
        mAuth = FirebaseAuth.getInstance()
        toolbar = findViewById(R.id.my_tools)
        skip = findViewById(R.id.skip)
        imageView = findViewById(R.id.ImageMain)
        add = findViewById(R.id.add1)
        b1 = findViewById(R.id.button6)
        mAuth = FirebaseAuth.getInstance()
        setStatusBar()
        x = intent.getDoubleExtra("X", x)
        y = intent.getDoubleExtra("Y", y)
        type = intent.getStringExtra("Type")
        email = intent.getStringExtra("email")
        pass = intent.getStringExtra("password")
        name = intent.getStringExtra("Name")
        sex = intent.getStringExtra("Sex")
        age = intent.getIntExtra("Age", age)
        hashMapQA = intent.getSerializableExtra("MapQA") as Map<*, *>
        dialog = LoadingDialog(this).dialog()
        imageView.setOnClickListener {
            inputImage()
        }
        b1.setOnClickListener {createDataToFirebase(1) }
        skip.setOnClickListener { createDataToFirebase(2) }
    }

    private fun setStatusBar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.register)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }



    private fun inputImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3, 4)
                .start(this)
    }

    private fun createDataToFirebase(i: Int) {

        val oppositeUserSex = if(sex == "Male") "Female"
        else "Male"
        val editor = getSharedPreferences("notification_match", Context.MODE_PRIVATE).edit()
        editor.putString("noti", "1")
        editor.apply()
        userId = mAuth.currentUser!!.uid
        currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        val userInfo = hashMapOf(
                "name" to name,
                "sex" to sex,
                "Age" to age,
                "Distance" to "Untitled",
                "OppositeUserSex" to oppositeUserSex,
                "OppositeUserAgeMin" to 18,
                "OppositeUserAgeMax" to 70,
                "MaxChat" to 15,
                "MaxLike" to 30,
                "MaxAdmob" to 10,
                "MaxStar" to 3,
                "Vip" to 0,
                "birth" to intent.getLongExtra("Birth", 0)
        )
        currentUserDb.updateChildren(userInfo as Map<String, *>).addOnSuccessListener {
            firstAuthentication.check()
        }
        val location = hashMapOf(
                "X" to x,
                "Y" to y
        )
        currentUserDb.child("Questions").setValue(hashMapQA)
        currentUserDb.child("Location").updateChildren(location as Map<String, Any>)
        if (i == 1) {
            if (bitmap != null) {
                dialog.show()
                val filepath = FirebaseStorage.getInstance().reference
                        .child("profileImages")
                        .child(userId)
                        .child("profileImageUrl0")

                val baos = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 40, baos)
                val data = baos.toByteArray()
                val uploadTask = filepath.putBytes(data)
                uploadTask.apply {

                    addOnFailureListener { finish() }
                    addOnSuccessListener {

                        filepath.downloadUrl.addOnSuccessListener { uri ->
                            dialog.dismiss()
                            val userInfo = hashMapOf(
                                    "profileImageUrl0" to uri.toString()
                            )
                            currentUserDb.child("ProfileImage").updateChildren(userInfo as Map<String, Any>)

                            val intent = Intent(this@RegisterFinishAcivity, MainActivity::class.java)
                            intent.putExtra("first", "0")
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener(OnFailureListener {
                            return@OnFailureListener
                        })
                    }
                }

            } else {
                Alerter.create(this@RegisterFinishAcivity)
                        .setTitle(R.string.profile_image)
                        .setText(getString(R.string.choose_photo))
                        .setBackgroundColorRes(R.color.c3)
                        .show()
            }
        } else {
            dialog.dismiss()
            val intent = Intent(this@RegisterFinishAcivity, MainActivity::class.java)
            intent.putExtra("first", "0")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                var image: InputImage? = null
                var bitmap: Bitmap? = null
                try {

                    image = InputImage.fromFilePath(this@RegisterFinishAcivity, result.uri!!)
                    bitmap = if (Build.VERSION.SDK_INT >= 29) {
                        val source = ImageDecoder.createSource(this.contentResolver, result.uri)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        MediaStore.Images.Media.getBitmap(application.contentResolver, result.uri)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                dialog.show()
                val options = FaceDetectorOptions.Builder().build()
                val detector = FaceDetection.getClient(options)
                detector.process(image!!)
                        .addOnSuccessListener { faces ->
                            dialog.dismiss()
                            if (faces.isNotEmpty()) {

                                NSFWDetector.isNSFW(bitmap!!) { isNSFW, confidence, image ->
                                    if (isNSFW) {
                                        Snackbar.make(b1, "โรคจิต", Snackbar.LENGTH_LONG).show()
                                        dialog.dismiss()
                                    } else {
                                        Glide.with(application).load(result.uri).placeholder(R.drawable.tran).into(imageView)
                                        add.visibility = View.GONE
                                        this.bitmap = bitmap
                                    }
                                }
                            } else {
                                Snackbar.make(b1, "ไม่ใช่คน", Snackbar.LENGTH_LONG).show()
                            }
                        }
                        .addOnFailureListener {
                            // Task failed with an exception
                            // ...
                        }
            }
        }
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
}