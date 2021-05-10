package com.jabirdeveloper.tinderswipe.Chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.jabirdeveloper.tinderswipe.*
import com.jabirdeveloper.tinderswipe.Functions.DateTime
import com.jabirdeveloper.tinderswipe.Functions.LoadingDialog
import com.jabirdeveloper.tinderswipe.Functions.ReportUser
import com.jabirdeveloper.tinderswipe.R
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mChatAdapter: RecyclerView.Adapter<*>
    //private lateinit var mChatLayoutManager: RecyclerView.LayoutManager
    private lateinit var linearLayoutOvalSend: LinearLayout
    private lateinit var menu: LinearLayout
    private val plus: SwitchpageActivity? = SwitchpageActivity()
    private lateinit var linearRecord: LinearLayout
    private lateinit var imgSend: ImageView
    private lateinit var mSendImage: ImageView
    private lateinit var mCameraOpen: ImageView
    private lateinit var menuBar: ImageView
    private lateinit var profile: ImageView
    private lateinit var back: ImageView
    private lateinit var mRecord: ImageView
    private lateinit var mRecordReal: ImageView
    private lateinit var mNameChat: TextView
    private lateinit var mRecordStatus: TextView
    private lateinit var mSendButton: TextView
    private lateinit var openMenu: ImageView
    private var chk = 0
    private var chk2 = 0
    private var timeCount = 0
    private lateinit var currentUserId: String
    private lateinit var matchId:String
    private lateinit var chatId: String
    private lateinit var urlImage: String
    private lateinit var nameChat: String
    private lateinit var fileName: String
    private lateinit var uriCamera:Uri
    private lateinit var mSendEditText: CustomEdittext
    private var checkBack = 0
    private lateinit var pro: ProgressBar
    private lateinit var proAudio: ProgressBar
    private lateinit var recorder: MediaRecorder
    private var active = true
    private lateinit var timer:Timer
    private lateinit var dialog: Dialog
    private lateinit var mDatabaseUser: DatabaseReference
    private lateinit var mDatabaseChat: DatabaseReference
    private lateinit var mDatabaseImage: DatabaseReference
    private lateinit var userDatabase: DatabaseReference
    private lateinit var usersDb: DatabaseReference
    private val myPermissionRequestReadMedia = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf<String?>(Manifest.permission.WRITE_EXTERNAL_STORAGE), myPermissionRequestReadMedia)
        }
        mRecord = findViewById(R.id.record_audio)
        profile = findViewById(R.id.pre_Image_porfile)
        back = findViewById(R.id.imageView5)
        imgSend = findViewById(R.id.img_send)
        mCameraOpen = findViewById(R.id.camera_open)
        proAudio = findViewById(R.id.progressBar_audio)
        pro = findViewById(R.id.progressBar_Chat)
        matchId = intent.extras!!.getString("matchId")!!
        var unreadCount = intent!!.extras!!.getString("unread")
        if (unreadCount == "-1") {
            val myUnread = getSharedPreferences("NotificationMessage", Context.MODE_PRIVATE)
            val dd2 = myUnread.getInt(matchId, 0)
            val removeNotification = getSharedPreferences("NotificationActive", Context.MODE_PRIVATE)
            val editorRead = removeNotification.edit()
            editorRead.putString("ID", matchId)
            editorRead.apply()
            unreadCount = dd2.toString()
        }
        dialog = LoadingDialog(this).dialog()
        val mySharedPreferences = getSharedPreferences("SentRead", Context.MODE_PRIVATE)
        val editor = mySharedPreferences.edit()
        editor.putInt("Read", Integer.valueOf(unreadCount!!.toInt()))
        editor.apply()
        nameChat = intent!!.extras!!.getString("nameMatch")!!
        mRecordReal = findViewById(R.id.record_real)
        openMenu = findViewById(R.id.menu_button)
        mSendEditText = findViewById(R.id.message)
        menu = findViewById(R.id.menu_app)
        mRecordStatus = findViewById(R.id.record_status)
        linearRecord = findViewById(R.id.Linear_record)
        menuBar = findViewById(R.id.menubar)
        linearLayoutOvalSend = findViewById(R.id.oval_send)
        mNameChat = findViewById(R.id.name_chat)
        mSendImage = findViewById(R.id.send_image)
        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        usersDb = FirebaseDatabase.getInstance().reference.child("Users")
        mDatabaseUser = if (intent.hasExtra("Hi")) {
            FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId).child("connection").child("chatna").child(matchId)
        } else {
            FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId).child("connection").child("matches").child(matchId).child("ChatId")
        }
        mDatabaseImage = FirebaseDatabase.getInstance().reference.child("Users").child(matchId).child("ProfileImage").child("profileImageUrl0")
        mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat")
        mRecord.setOnClickListener{
            if (ActivityCompat.checkSelfPermission(this@ChatActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@ChatActivity, arrayOf<String?>(
                        Manifest.permission.RECORD_AUDIO), 72)
            } else {
                if (linearRecord.visibility == View.GONE) {
                    proAudio.visibility = View.GONE
                    linearRecord.visibility = View.VISIBLE
                    mRecordReal.visibility = View.VISIBLE
                    mRecordStatus.text = resources.getString(R.string.record)
                } else {
                    linearRecord.visibility = View.GONE
                }
            }
        }

        mRecordReal.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                startRecording()
                mRecordStatus.text = ("00:00")
                timer = Timer()
                timer.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            ++timeCount
                            val minute = timeCount / 60
                            val second = timeCount % 60
                            mRecordStatus.text = (String.format("%02d", minute) + ":" + String.format("%02d", second))
                        }
                    }
                }, 1000, 1000)
            } else if (event.action == MotionEvent.ACTION_UP) {
                timer.cancel()
                stopRecording()
                mRecordReal.visibility = (View.GONE)
                proAudio.visibility = (View.VISIBLE)
                mRecordStatus.text = resources.getString(R.string.uploading)
            }
            true
        }
        back.setOnClickListener { onBackPressed() }
        menuBar.setOnClickListener { v ->
            val dd = PopupMenu(this@ChatActivity, v)
            dd.menuInflater.inflate(R.menu.menu_bar, dd.menu)
            dd.gravity = Gravity.END
            dd.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_unmatch -> {
                        Alerter.create(this@ChatActivity)
                                .setTitle(getString(R.string.cancel_match2))
                                .setText(getString(R.string.cancel_match_confirm))
                                .setIconColorFilter(Color.parseColor("#FFFFFF"))
                                .setBackgroundColorInt(Color.parseColor("#FF5050"))
                                .setIcon(ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_warning_black_24dp)!!)
                                .addButton(getString(R.string.cancle), R.style.AlertButton) { Alerter.hide() }
                                .addButton(getString(R.string.ok), R.style.AlertButton) {
                                    Alerter.hide()
                                    deleteChild()
                                }
                                .show()
                    }
                    R.id.menu_delete -> {
                        val mDialog = ReportUser(this@ChatActivity, matchId).reportDialog()
                        mDialog.show()
                    }
                    R.id.delete_chat -> {
                        Alerter.create(this@ChatActivity)
                                .setTitle(getString(R.string.delete_message_all))
                                .setText(getString(R.string.delete_message_all_confirm))
                                .setIconColorFilter(Color.parseColor("#FFFFFF"))
                                .setBackgroundColorInt(Color.parseColor("#FF5050"))
                                .setIcon(ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_warning_black_24dp)!!)
                                .addButton(getString(R.string.cancle), R.style.AlertButton) { Alerter.hide() }
                                .addButton(getString(R.string.ok), R.style.AlertButton) {
                                    Alerter.hide()
                                    val getStart = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId).child("connection").child("matches").child(matchId).child("Start")
                                    getStart.setValue(fetchId!![fetchId.size - 1])
                                    val prefs1 = getSharedPreferences(chatId, Context.MODE_PRIVATE)
                                    val allPrefs = prefs1.all
                                    val set = allPrefs.keys
                                    for (s in set) {
                                        Log.d("Id1", s)
                                        getSharedPreferences(s, Context.MODE_PRIVATE).edit().clear().apply()
                                    }
                                    getSharedPreferences(chatId, Context.MODE_PRIVATE).edit().clear().apply()
                                    fetchId.clear()
                                    start = "null"
                                    sizePre = 0
                                    resultChat.clear()
                                    mChatAdapter.notifyDataSetChanged()
                                    val removeNotification = getSharedPreferences("DeleteChatActive", Context.MODE_PRIVATE)
                                    val editorRead = removeNotification.edit()
                                    editorRead.putString("ID", matchId)
                                    editorRead.apply()
                                }
                                .show()
                    }
                    else -> {
                        Toast.makeText(this@ChatActivity, "" + item, Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
            dd.setOnDismissListener { }
            dd.show()
        }
        profile.setOnClickListener {
            val intent = Intent(applicationContext, ProfileUserOppositeActivity2::class.java)
            intent.putExtra("madoo", "1")
            intent.putExtra("User_opposite", matchId)
            startActivity(intent)
        }
        openMenu.setOnClickListener {
            if (menu.visibility == View.GONE) {
                openMenu.visibility = View.GONE
                menu.visibility = View.VISIBLE
            }
        }
        getImageProfile()
        mNameChat.text = nameChat
        mRecyclerView = findViewById<View?>(R.id.recyclerView_2) as RecyclerView
        val mChatLayoutManager = LinearLayoutManager(this@ChatActivity)
        mRecyclerView.layoutManager = mChatLayoutManager
        mChatAdapter = ChatAdapter(getDataSetChat(), this@ChatActivity)
        mSendButton = findViewById(R.id.send)
        mSendImage.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "เลือกรูปภาพ"), 23)
        }
        mSendEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    mSendButton.background = ContextCompat.getDrawable(this@ChatActivity, R.drawable.chat_after)
                    mSendButton.rotation = 0f
                } else {
                    mSendButton.background = ContextCompat.getDrawable(this@ChatActivity, R.drawable.chat_before)
                    mSendButton.rotation = 0f
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        mSendEditText.setOnEditTextImeBackListener(object : EditTextImeBackListener {
            override fun onImeBack(ctrl: CustomEdittext?, text: String?) {
                menu.visibility = View.VISIBLE
                mSendEditText.clearFocus()
                openMenu.visibility = View.GONE
            }
        })
        mSendEditText.setOnFocusChangeListener { _, b ->
            if (b) {
                linearRecord.visibility = View.GONE
                menu.visibility = View.GONE
                openMenu.visibility = View.VISIBLE
            } else {
                menu.visibility = View.VISIBLE
                openMenu.visibility = View.GONE
            }
        }
        mSendButton.setOnClickListener { sendMessage() }
        linearLayoutOvalSend.setOnClickListener{ sendMessage() }
        mCameraOpen.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this@ChatActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@ChatActivity, arrayOf<String?>(
                        Manifest.permission.CAMERA), 2)
            } else {
                val values = ContentValues()
                uriCamera = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCamera)
                startActivityForResult(intent, 33)
            }
        }
    }

    private fun getImageProfile() {
        mDatabaseImage.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val logoMoveAnimation: Animation = AnimationUtils.loadAnimation(this@ChatActivity, R.anim.fade_in2)
                if (dataSnapshot.exists()) {
                    urlImage = dataSnapshot.value.toString()
                    Glide.with(applicationContext).load(urlImage).apply(RequestOptions().override(100, 100)).placeholder(R.color.background_gray).listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            profile.startAnimation(logoMoveAnimation)
                            return false
                        }
                    }).into(profile)
                } else {
                    if (intent.getStringExtra("gender") == "Female") Glide.with(applicationContext).load(R.drawable.ic_woman).apply(RequestOptions().override(100, 100)).into(profile) else Glide.with(applicationContext).load(R.drawable.ic_man).apply(RequestOptions().override(100, 100)).into(profile)
                }
                getChatId()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendMessage() {
        val sendMessageText = mSendEditText.text.toString()
        if (sendMessageText.isNotEmpty()) {
            val d = DateTime
            val newMessageDb = mDatabaseChat.push()
            val newMessage = hashMapOf(
                    "createByUser" to currentUserId,
                    "text" to sendMessageText,
                    "time" to d.time(),
                    "date" to d.date(),
                    "read" to "Unread")
            newMessageDb.setValue(newMessage)
        }
        mSendEditText.text = (null)
    }

    private fun getChatId() {
        mDatabaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.value.toString()
                    mDatabaseChat = mDatabaseChat.child(chatId)
                    userDatabase = FirebaseDatabase.getInstance().reference.child("Chat").child(chatId)
                    fetchSharedPreference()
                    //getCount()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun chatCheckRead() {
        val dd = mDatabaseChat.orderByChild("read").equalTo("Unread")
        dd.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (loop in dataSnapshot.children) {
                    readAlready(loop.key)
                    val myUnread = getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
                    val dd2 = myUnread.getInt("total", 0)
                    val count = dd2 - 1
                    Log.d("chatNotificationTest", "$dd2-1")
                    plus!!.setCurrentIndex(count)
                    val myUnread2 = getSharedPreferences("TotalMessage", Context.MODE_PRIVATE)
                    val editorRead = myUnread2.edit()
                    editorRead.putInt("total", count)
                    editorRead.apply()
                    readAlready(loop.key)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun readAlready(key: String?) {
        mDatabaseChat.child(key.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("createByUser").value.toString() == matchId) {
                    mDatabaseChat.child(key.toString()).child("read").setValue("Read")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var countNodeD = 0
    /*private fun getCount() {
        val dd = FirebaseDatabase.getInstance().reference.child("Chat")
        dd.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChild(chatId.toString())) {
                    pro!!.visibility = View.INVISIBLE
                }
                fetchSharedPreference()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }*/

    private var c = 0
    private var firstConnect = true
    private var start: String? = "null"
    private fun getFirstNode() {
        val getStart = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId).child("connection").child("matches").child(matchId).child("Start")
        getStart.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (firstConnect) {
                    firstConnect = false
                    if (dataSnapshot.exists()) {
                        start = dataSnapshot.value.toString()
                        getChatMessages()
                    } else {
                        getChatMessages()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var sizePre = 0
    private val fetchId: MutableList<String?>? = ArrayList()
    private fun fetchSharedPreference() {
        val prefs = getSharedPreferences(chatId, Context.MODE_PRIVATE)
        val allPrefs = prefs.all
        val set = allPrefs.keys
        for (s in set) {
            fetchId!!.add(s)
        }
        for (s in set) {
            Log.d("Id2", "" + prefs.getInt(s, 0))
            fetchId!![prefs.getInt(s, 0) - 1] = s
        }
        sizePre = fetchId!!.size
        setMessage()
    }

    private fun closeProgress(){
        val logoMoveAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out2)
        logoMoveAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                progress.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })
        progress.startAnimation(logoMoveAnimation)
    }

    private fun setMessage() {
        for (i in (fetchId!!.indices)) {
            c++
            var message: String
            var createdByUser: String
            var time: String
            var urlSend = "default"
            var audio: String
           // var read: String
            var audioLength: String
            val myInNode = getSharedPreferences(fetchId.elementAt(i), Context.MODE_PRIVATE)
            message = myInNode.getString("text", "null")!!
            //read = myInNode.getString("read", "null")!!
            createdByUser = myInNode.getString("createByUser", "null")!!
            time = myInNode.getString("time", "null")!!
            val check = myInNode.getString("image", "null")
            Log.d("text_chat", message)
            if (check != "default" && check != "null") {
                ++chk2
                urlSend = myInNode.getString("image", "null")!!
            }
            audio = myInNode.getString("audio", "null")!!
            audioLength = myInNode.getString("audio_length", "null")!!
            var currentUserBoolean = false
            if (createdByUser == currentUserId) {
                currentUserBoolean = true
            } else if (c == fetchId.size) {
                if (createdByUser != currentUserId)
                    chatCheckRead()
            }
            val newMessage = ChatObject(message, currentUserBoolean, urlImage, time, chatId, urlSend, chk2, matchId, audio, audioLength, currentUserId)
            resultChat.add(newMessage)
            ++chk
                if (fetchId.size == chk) {
                    mChatAdapter.notifyDataSetChanged()
                    mRecyclerView.adapter = mChatAdapter
                    mRecyclerView.scrollToPosition(resultChat.size - 1)
                    pro.visibility = View.INVISIBLE
                    countNodeD = fetchId.size
                }

        }
        getFirstNode()
    }

    private fun getChatMessages() {
        var chatDatabase: Query? = mDatabaseChat
        closeProgress()
        if (fetchId!!.size > 0) {
            Toast.makeText(this@ChatActivity, "Size > 1 :" + fetchId.elementAt(fetchId.size - 1), Toast.LENGTH_SHORT).show()
            chatDatabase = mDatabaseChat.orderByKey().startAt(fetchId.elementAt(fetchId.size - 1))

        } else if (start != "null" && fetchId.size == 0) {
            Toast.makeText(this@ChatActivity, "Size == 0 :$start", Toast.LENGTH_SHORT).show()
            chatDatabase = mDatabaseChat.orderByKey().startAt(start)

        }
        chatDatabase!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.exists()) {
                    if (fetchId.size > 0) {
                        if (dataSnapshot.key != fetchId.elementAt(fetchId.size - 1)) {
                            c++
                            var message: String? = null
                            var createdByUser: String? = null
                            var time: String? = null
                            var urlSend = "default"
                            var audio = "null"
                            var audioLength = "null"
                            var read = "null"
                            if (dataSnapshot.child("read").value != null) {
                                read = dataSnapshot.child("read").value.toString()
                            }
                            if (dataSnapshot.child("text").value != null) {
                                message = dataSnapshot.child("text").value.toString()
                            }
                            if (dataSnapshot.child("createByUser").value != null) {
                                createdByUser = dataSnapshot.child("createByUser").value.toString()
                            }
                            if (dataSnapshot.child("time").value != null) {
                                time = dataSnapshot.child("time").value.toString()
                            }
                            if (dataSnapshot.child("image").value != null) {
                                urlSend = dataSnapshot.child("image").value.toString()
                                ++chk2
                            }
                            if (dataSnapshot.child("audio").value != null) {
                                audio = dataSnapshot.child("audio").value.toString()
                                audioLength = dataSnapshot.child("audio_length").value.toString()
                            }
                            val chatMessageStored = getSharedPreferences(chatId, Context.MODE_PRIVATE)
                            val editorRead = chatMessageStored.edit()
                            editorRead.putInt(dataSnapshot.key, ++sizePre)
                            //Toast.makeText(this@ChatActivity,"UnSave"+(sizePre),Toast.LENGTH_SHORT).show();
                            fetchId.add(dataSnapshot.key)
                            editorRead.apply()
                            val nodeChatMessageStored = getSharedPreferences(dataSnapshot.key, Context.MODE_PRIVATE)
                            val nodeEditorRead = nodeChatMessageStored.edit()
                            nodeEditorRead.putString("text", message)
                            nodeEditorRead.putString("time", time)
                            nodeEditorRead.putString("createByUser", createdByUser)
                            nodeEditorRead.putString("image", urlSend)
                            nodeEditorRead.putString("audio", audio)
                            nodeEditorRead.putString("audio_length", audioLength)
                            nodeEditorRead.putString("read", read)
                            nodeEditorRead.apply()
                            if (createdByUser != null && time != null) {
                                var currentUserBoolean = false
                                if (createdByUser == currentUserId) {
                                    currentUserBoolean = true
                                } else {
                                    if (active) {
                                        if (dataSnapshot.child("read").value.toString() == "Unread") {
                                            Toast.makeText(this@ChatActivity, dataSnapshot.child("read").value.toString(), Toast.LENGTH_LONG).show()
                                            chatCheckRead()
                                        }
                                    }
                                }
                                val newMessage = ChatObject(message, currentUserBoolean, urlImage, time, chatId, urlSend, chk2, matchId, audio, audioLength, currentUserId)
                                resultChat.add(newMessage)
                                mChatAdapter.notifyDataSetChanged()
                                ++chk
                                if (fetchId.size == 1) {
                                    mChatAdapter.notifyDataSetChanged()
                                    mRecyclerView.adapter = mChatAdapter
                                    mRecyclerView.scrollToPosition(resultChat.size - 1)
                                    pro.visibility = View.INVISIBLE


                                } else if (countNodeD < chk) {
                                    mRecyclerView.smoothScrollToPosition(mRecyclerView.adapter!!.itemCount - 1)

                                }
                            }
                        }
                    } else if (dataSnapshot.key != start) {
                        c++
                        var message: String? = null
                        var createdByUser: String? = null
                        var time: String? = null
                        var urlSend = "default"
                        var audio = "null"
                        var audioLength = "null"
                        var read = "null"
                        Log.d("unsave", dataSnapshot.key.toString())
                        if (dataSnapshot.child("read").value != null) {
                            read = dataSnapshot.child("read").value.toString()
                        }
                        if (dataSnapshot.child("text").value != null) {
                            message = dataSnapshot.child("text").value.toString()
                        }
                        if (dataSnapshot.child("createByUser").value != null) {
                            createdByUser = dataSnapshot.child("createByUser").value.toString()
                        }
                        if (dataSnapshot.child("time").value != null) {
                            time = dataSnapshot.child("time").value.toString()
                        }
                        if (dataSnapshot.child("image").value != null) {
                            urlSend = dataSnapshot.child("image").value.toString()
                            ++chk2
                        }
                        if (dataSnapshot.child("audio").value != null) {
                            audio = dataSnapshot.child("audio").value.toString()
                            audioLength = dataSnapshot.child("audio_length").value.toString()
                        }
                        val chatMessageStored = getSharedPreferences(chatId, Context.MODE_PRIVATE)
                        val editorRead = chatMessageStored.edit()
                        editorRead.putInt(dataSnapshot.key, ++sizePre)
                        fetchId.add(dataSnapshot.key)
                        editorRead.apply()
                        val nodeChatMessageStored = getSharedPreferences(dataSnapshot.key, Context.MODE_PRIVATE)
                        val nodeEditorRead = nodeChatMessageStored.edit()
                        nodeEditorRead.putString("text", message)
                        nodeEditorRead.putString("time", time)
                        nodeEditorRead.putString("createByUser", createdByUser)
                        nodeEditorRead.putString("image", urlSend)
                        nodeEditorRead.putString("audio", audio)
                        nodeEditorRead.putString("audio_length", audioLength)
                        nodeEditorRead.putString("read", read)
                        nodeEditorRead.apply()
                        if (createdByUser != null && time != null) {
                            var currentUserBoolean = false
                            if (createdByUser == currentUserId) {
                                currentUserBoolean = true
                            } else {
                                if (active) {
                                    if (dataSnapshot.child("read").value.toString() == "Unread") {

                                    }
                                }
                            }
                            val newMessage = ChatObject(message, currentUserBoolean, urlImage, time, chatId, urlSend, chk2, matchId, audio, audioLength, currentUserId)
                            resultChat.add(newMessage)
                            mChatAdapter.notifyDataSetChanged()
                            ++chk
                            if (fetchId.size == 1) {
                                mRecyclerView.adapter = mChatAdapter
                                mRecyclerView.scrollToPosition(resultChat.size - 1)
                                pro.visibility = View.INVISIBLE


                            } else if (countNodeD < chk) {
                                mRecyclerView.smoothScrollToPosition(mRecyclerView.adapter!!.itemCount - 1)

                            }
                        }
                    }
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 33 && resultCode == Activity.RESULT_OK) {
            dialog.show()
            val name = System.currentTimeMillis().toString()
            val filepath = FirebaseStorage.getInstance().reference.child("SendImage").child(currentUserId).child(matchId).child("image$name")
            try {
                val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, uriCamera))
                } else {
                    MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            uriCamera
                    )
                }
                val byteOutput = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, byteOutput)
                val dataUrl = byteOutput.toByteArray()
                val uploadTask = filepath.putBytes(dataUrl)
                uploadTask.addOnFailureListener {
                    Toast.makeText(this@ChatActivity, "Fail Upload", Toast.LENGTH_LONG).show()
                    finish()
                }
                uploadTask.addOnSuccessListener {
                    val filePath = FirebaseStorage.getInstance().reference.child("SendImage").child(currentUserId).child(matchId).child("image$name")
                    filePath.downloadUrl.addOnSuccessListener { uri ->
                        val newMessageDb = mDatabaseChat.push()
                        val d = DateTime
                        val newMessage = hashMapOf(
                                "createByUser" to currentUserId,
                                "time" to d.time(),
                                "date" to d.date(),
                                "text" to "photo$currentUserId",
                                "read" to "Unread",
                                "image" to uri.toString())
                        newMessageDb.setValue(newMessage)
                        dialog.dismiss()
                    }.addOnFailureListener { }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == 23 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            dialog.show()
            val name = System.currentTimeMillis().toString()
            val fileUri = data.data
            val filepath = FirebaseStorage.getInstance().reference.child("SendImage").child(currentUserId).child(matchId).child("image$name")
            try {
                val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, fileUri!!))
                } else {
                   MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                           fileUri
                    )
                }
                val byteOutput = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, byteOutput)
                val dataURL = byteOutput.toByteArray()
                val uploadTask = filepath.putBytes(dataURL)
                uploadTask.addOnFailureListener {
                    Toast.makeText(this@ChatActivity, "Fail Upload", Toast.LENGTH_LONG).show()
                    finish()
                }
                uploadTask.addOnSuccessListener {
                    val filePath = FirebaseStorage.getInstance().reference.child("SendImage").child(currentUserId).child(matchId).child("image$name")
                    filePath.downloadUrl.addOnSuccessListener { uri ->
                        val newMessageDb = mDatabaseChat.push()
                        val d = DateTime
                        val newMessage = hashMapOf(
                                "createByUser" to currentUserId,
                                "time" to d.time(),
                                "date" to d.date(),
                                "text" to "photo$currentUserId",
                                "read" to "Unread",
                                "image" to uri.toString())
                        newMessageDb.setValue(newMessage)
                        dialog.dismiss()
                    }.addOnFailureListener { }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun startRecording() {
        fileName = externalCacheDir!!.absolutePath
        fileName += "/recorded_audio.3gp"
        recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.setOutputFile(fileName)
        try {
            recorder.prepare()
        } catch (e: IOException) {
            Toast.makeText(this@ChatActivity, "Fail to recorded", Toast.LENGTH_SHORT).show()
        }
        recorder.start()
    }

    private fun stopRecording() {
        recorder.stop()
        recorder.release()
        upLoadAudio()
    }

    private fun upLoadAudio() {
        val name = System.currentTimeMillis().toString()
        val ss2 = FirebaseStorage.getInstance().reference.child("Audio").child(currentUserId).child(matchId).child("audio$name.3gp")
        val uri = Uri.fromFile(File(fileName))
        ss2.putFile(uri).addOnSuccessListener {
            ss2.downloadUrl.addOnSuccessListener { uri ->
                Toast.makeText(this@ChatActivity, "Success", Toast.LENGTH_SHORT).show()
                linearRecord.visibility = View.GONE
                val downloadUrl = uri
                val newMessageDb = mDatabaseChat.push()
                val calendar = Calendar.getInstance()
                val currentTime = SimpleDateFormat("HH:mm", Locale.UK)
                val timeUser = currentTime.format(calendar.time)
                val currentDate = SimpleDateFormat("dd/MM/yyyy")
                val dateUser = currentDate.format(calendar.time)
                val newMessage = hashMapOf(
                        "createByUser" to currentUserId,
                        "time" to timeUser,
                        "date" to dateUser,
                        "audio_length" to timeCount.toString(),
                        "audio" to downloadUrl.toString(),
                        "text" to "audio$currentUserId",
                        "read" to "Unread")
                newMessageDb.setValue(newMessage)
                timeCount = 0
            }
        }
    }

    override fun onBackPressed() {
        mSendEditText.clearFocus()
        if (linearRecord.visibility == View.VISIBLE) {
            linearRecord.visibility = View.GONE
        } else {
            super.onBackPressed()
            if (intent.hasExtra("chat_na")) {
                if (c > 1) {
                    usersDb.apply {
                        usersDb.child(matchId).child("connection")
                                .child("yep")
                                .child(currentUserId).setValue(true)
                        usersDb.child(currentUserId).child("connection")
                                .child("yep")
                                .child(matchId).setValue(true)
                        usersDb.child(currentUserId).child("connection")
                                .child("chatna")
                                .child(matchId).setValue(null)
                    }
                }
            }
            if (checkBack == 0) {
                finish()
                return
            }
            val intent = Intent(this@ChatActivity, SwitchpageActivity::class.java)
            intent.putExtra("first", 1)
            startActivity(intent)
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        active = false

    }

    private fun deleteChild() {
        val dataDelete = FirebaseDatabase.getInstance().reference.child("Users")
         FirebaseDatabase.getInstance().reference
                 .addListenerForSingleValueEvent(object : ValueEventListener {
                     override fun onDataChange(dataSnapshot: DataSnapshot) {
                         val chatId = dataSnapshot.child("Users")
                                 .child(currentUserId)
                                 .child("connection")
                                 .child("matches")
                                 .child(matchId)
                                 .child("ChatId")
                                 .value.toString()
                         if (dataSnapshot.child("Chat").hasChild(chatId)) {
                             FirebaseDatabase.getInstance().reference
                                     .child("Chat")
                                     .child(chatId).removeValue()
                         }
                         dataDelete.child(currentUserId).child("connection")
                                 .child("matches")
                                 .child(matchId).removeValue()
                         dataDelete.child(currentUserId).child("connection")
                                 .child("yep")
                                 .child(matchId).removeValue()
                         dataDelete.child(matchId).child("connection")
                                 .child("matches")
                                 .child(currentUserId).removeValue()
                         dataDelete.child(matchId).child("connection")
                                 .child("yep")
                                 .child(currentUserId).removeValue()
                     }

                     override fun onCancelled(databaseError: DatabaseError) {}
                 })
    }

    private val resultChat: ArrayList<ChatObject> = ArrayList()
    private fun getDataSetChat(): ArrayList<ChatObject> {
        return resultChat
    }
}