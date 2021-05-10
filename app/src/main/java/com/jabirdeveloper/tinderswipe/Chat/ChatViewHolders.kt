package com.jabirdeveloper.tinderswipe.Chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jabirdeveloper.tinderswipe.ImageChat.ItemImageActivity
import com.jabirdeveloper.tinderswipe.R
import com.ldoublem.loadingviewlib.LVCircularCD
import kotlinx.android.synthetic.main.item_chat.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Suppress("NAME_SHADOWING")
class ChatViewHolders(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
    private var mMessage: TextView = itemView.chatmessage
    private var timeSend: TextView = itemView.time_chat_user
    private var mMatchId: TextView = itemView.match_id
    private var mMatchIdReal: TextView = itemView.match_id_real_image
    private var audioUrl: TextView = itemView.audio_url
    var beginAudio: TextView = itemView.begin_audio
    //var mContainer: LinearLayout = itemView.container
    var buttonAudio: Button = itemView.play_audio
    var mchk: RelativeLayout = itemView.lilili
    var mchk2: RelativeLayout = itemView.lili
    private var mchk3: RelativeLayout = itemView.li
    private var imageOpposite: ImageView = itemView.image_holder
    private var mImageSent: ImageView = itemView.img_sent
    //var stop_Animate: ImageView? = null
    private var progressBarAudio: ProgressBar = itemView.progressBar_playAudio
    private var play:Boolean = false
    //var mRecycler: RecyclerView?
    private var mChk: TextView = itemView.chk_image
    private var mChk2: TextView = itemView.chk_image_2
    //private val loading: AVLoadingIndicatorView? = null
    private lateinit var myClipboard: ClipboardManager
    private lateinit var myClip: ClipData
    private lateinit var mediaPlayer: MediaPlayer
    var cd: LVCircularCD = itemView.findViewById(R.id.play_pause_animate)
    private var length = 0
    private var totalLength = 0
    private lateinit var countDownTimer: CountDownTimer
    //private var card: CardView = itemView.findViewById(R.id.card)
    private var check = true
    private lateinit var alertDialog: AlertDialog
    
    @SuppressLint("SetTextI18n")
    fun start(chatList: ChatObject) {

        mMatchIdReal.text = chatList.createByUser
        mMatchId.text = chatList.Match_id
        mChk2.text = "" + chatList.chk
        mChk.text = "" + chatList.chk
        timeSend.text = chatList.time
        if (chatList.currentUser!!) {
            when {
                chatList.url !== "default" -> {
                    a()
                    mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_1_photo)!!
                    Glide.with(context).load(chatList.url).thumbnail(0.1f).into(mImageSent)
                    mchk2.visibility = View.GONE
                }
                chatList.audio_Url != "null" -> {
                    buttonAudio.visibility = View.VISIBLE
                    cd.visibility = View.VISIBLE
                    beginAudio.visibility = View.VISIBLE
                    if (Integer.valueOf(chatList.audio_length!!) < 60) {
                        audioUrl.text = chatList.audio_Url
                        val second = String.format("%02d", Integer.valueOf(chatList.audio_length!!))
                        beginAudio.text = "00:00"
                        mMessage.text = "00:$second"
                    } else {
                        val minute = String.format("%02d", Integer.valueOf(chatList.audio_length!!) / 60)
                        val second = String.format("%02d", Integer.valueOf(chatList.audio_length!!) % 60)
                        beginAudio.text = "00:00"
                        mMessage.text = "$minute:$second"
                    }
                    beginAudio.setTextColor(Color.parseColor("#FFFFFF"))
                    mMessage.setTextColor(Color.parseColor("#FFFFFF"))
                    a1()
                }
                else -> {
                    mMessage.text = chatList.message
                    buttonAudio.visibility = View.GONE
                    cd.visibility = View.GONE
                    beginAudio.visibility = View.GONE
                    beginAudio.setTextColor(Color.parseColor("#FFFFFF"))
                    mMessage.setTextColor(Color.parseColor("#FFFFFF"))
                    a1()
                }
            }
        } else {
            when {
                chatList.url !== "default" -> {
                    b()
                    imageOpposite.visibility = View.VISIBLE
                    Glide.with(context).load(chatList.profileImageUrl).thumbnail(0.1f).into(imageOpposite)
                    Glide.with(context).load(chatList.url).thumbnail(0.1f).into(mImageSent)
                    mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_2_photo)
                    mchk2.visibility = View.GONE
                }
                chatList.audio_Url != "null" -> {
                    buttonAudio.visibility = View.VISIBLE
                    cd.visibility = View.VISIBLE
                    beginAudio.visibility = View.VISIBLE
                    if (Integer.valueOf(chatList.audio_length!!) < 60) {
                        audioUrl.text = chatList.audio_Url
                        val second = String.format("%02d", Integer.valueOf(chatList.audio_length!!))
                        beginAudio.text = "00:00"
                        mMessage.text = "00:$second"
                    } else {
                        val minute = String.format("%02d", Integer.valueOf(chatList.audio_length!!) / 60)
                        val second = String.format("%02d", Integer.valueOf(chatList.audio_length!!) % 60)
                        beginAudio.text = "00:00"
                        mMessage.text = "$minute:$second"
                    }
                    beginAudio.setTextColor(Color.parseColor("#292929"))
                    mMessage.setTextColor(Color.parseColor("#292929"))
                   b1()
                }
                else -> {
                    mMessage.text = chatList.message
                    buttonAudio.visibility = View.GONE
                    cd.visibility = View.GONE
                    beginAudio.visibility = View.GONE
                    mMessage.setTextColor(Color.parseColor("#292929"))
                    Glide.with(context).load(chatList.profileImageUrl).into(imageOpposite)
                    mchk3.visibility = View.GONE
                    mchk.visibility = View.VISIBLE
                    mchk2.visibility = View.VISIBLE
                    b1()
                }
            }
        }
        cd.setViewColor(Color.parseColor("#FFF064"))
        val item = context.resources.getStringArray(R.array.chat_item)
        val itemImage = context.resources.getStringArray(R.array.chat_item_image)
        val builder = AlertDialog.Builder(context)
        myClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        buttonAudio.setOnClickListener{
            if(!play){
                play =true
                cd.startAnim()
                if (check) {
                    val minute = Integer.valueOf(mMessage.text.toString().substring(0, 2))
                    val second = Integer.valueOf(mMessage.text.toString().substring(3, 5))
                    totalLength = second + minute * 60
                    check = false
                }
                buttonAudio.visibility = View.GONE
                progressBarAudio.visibility = View.VISIBLE
                val minute = Integer.valueOf(mMessage.text.toString().substring(0, 2))
                val second = Integer.valueOf(mMessage.text.toString().substring(3, 5))
                val minuteSub = Integer.valueOf(beginAudio.text.toString().substring(0, 2))
                val secondSub = Integer.valueOf(beginAudio.text.toString().substring(3, 5))
                val counterSub = secondSub + minuteSub * 60
                val counter = second + minute * 60 - counterSub
                mediaPlayer = MediaPlayer()
                try {
                    mediaPlayer.setDataSource(audioUrl.text.toString())
                    mediaPlayer.prepare()
                } catch (e: IOException) { e.printStackTrace() }
                if (length == 0) {
                    mediaPlayer.start()
                } else {
                    mediaPlayer.seekTo(length)
                    mediaPlayer.start()
                }
                if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1)!!.constantState) {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1_selected)
                } else {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2_selected)
                }
                progressBarAudio.visibility = View.GONE
                buttonAudio.visibility = View.VISIBLE
                buttonAudio.background = ContextCompat.getDrawable(context, R.drawable.ic_pause_circle_outline_black_24dp)
                countDownTimer = object : CountDownTimer(((counter + 1) * 1000).toLong(), 1000) {
                    var total = counter
                    override fun onTick(millisUntilFinished: Long) {
                        val aaa = millisUntilFinished.toInt()
                        val allSecond = total - aaa / 1000
                        if (allSecond < 60) {
                            val second = String.format("%02d", allSecond + secondSub)
                            beginAudio.text = ("00:$second")
                        } else {
                            val checkMinute = allSecond / 60
                            val checkSecond = allSecond % 60
                            val secondS = String.format("%02d", checkSecond + minuteSub)
                            val minute = String.format("%02d", checkMinute + secondSub)
                            beginAudio.text = ("$minute:$secondS")
                        }
                    }

                    override fun onFinish() {
                        length = 0
                        cd.stopAnim()
                        buttonAudio.background = ContextCompat.getDrawable(context, R.drawable.ic_play_circle_outline_black_24dp)
                        beginAudio.text = ("00:00")
                        if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1_selected)!!.constantState) {
                            mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1)
                        } else {
                            mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2)
                        }
                        play = false
                    }
                }.start()

            }else{
                play =false
                Log.d("audioBackground","$play")
                cd.stopAnim()
                countDownTimer.cancel()
                buttonAudio.background = ContextCompat.getDrawable(context, R.drawable.ic_play_circle_outline_black_24dp)
                mediaPlayer.stop()
                length = mediaPlayer.currentPosition
                Log.d("audioBackground","$length")
                if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1_selected)!!.constantState) {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1)
                } else {
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2)
                }
            }
        }
        mchk2.setOnLongClickListener {
            if (mchk2.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_2)!!.constantState) {
                mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2_selected)
                builder.setItems(item) { _, which ->
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2)
                    if (item[which] == "คัดลอก") {
                        val text: String = mMessage.text.toString()
                        myClip = ClipData.newPlainText("text", text)
                        myClipboard.setPrimaryClip(myClip)
                    }
                }
                builder.setOnDismissListener { mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2) }
                alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window!!.setLayout(800, 400)
            } else {
                mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1_selected)
                builder.setItems(item) { _, which ->
                    mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1)
                    if (item[which] == "คัดลอก") {
                        val text: String = mMessage.text.toString()
                        myClip = ClipData.newPlainText("text", text)
                        myClipboard.setPrimaryClip(myClip)
                    }
                }
                builder.setOnDismissListener { mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1) }
                alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window!!.setLayout(800, 390)
            }
            true
        }
        mImageSent.setOnClickListener{
            val intent = Intent(context, ItemImageActivity::class.java)
            intent.putExtra("matchIdReal", mMatchIdReal.text.toString())
            intent.putExtra("matchId", mMatchId.text.toString())
            intent.putExtra("ChkImage", mChk.text.toString())
            intent.putExtra("ChkImage2", mChk2.text.toString())
            context.startActivity(intent)
        }
        mImageSent.setOnLongClickListener {
            if (mchk3.background.constantState === ContextCompat.getDrawable(context, R.drawable.chat_1_photo)!!.constantState) {
                mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_1_photo_selected)
                builder.setItems(itemImage) { _, which ->
                    if (itemImage[which] == "ดาวน์โหลดภาพ") {
                        ActivityCompat.requestPermissions((context as Activity?)!!, arrayOf<String?>(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                        mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_1_photo)
                        val bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.maicar)
                        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "test.png")
                        val out: FileOutputStream
                        try {
                            out = FileOutputStream(file)
                            Toast.makeText(context, "chk", Toast.LENGTH_SHORT).show()
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                            out.flush()
                            out.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                builder.setOnDismissListener { mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_1_photo) }
                alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window!!.setLayout(800, 245)
            } else {
                mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_2_photo_selected)
                builder.setItems(itemImage) { _, _ -> mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_2_photo) }
                builder.setOnDismissListener { mchk3.background = ContextCompat.getDrawable(context, R.drawable.chat_2_photo) }
                alertDialog = builder.create()
                alertDialog.show()
                alertDialog.window!!.setLayout(800, 245)
            }
            true
        }
    }
    private fun a(){
        mchk3.visibility = View.VISIBLE
        mchk.visibility = View.VISIBLE
        mchk2.visibility = View.GONE
        val params3 = mchk.layoutParams as RelativeLayout.LayoutParams
        params3.apply {
            addRule(RelativeLayout.END_OF, 0)
            addRule(RelativeLayout.START_OF, mchk3.id)
            addRule(RelativeLayout.ALIGN_BOTTOM, mchk3.id)
        }
        val params = mchk3.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
    }
    private fun a1(){
        mchk3.visibility = View.GONE
        mchk.visibility = View.VISIBLE
        mchk2.visibility = View.VISIBLE
        val params2 = mchk2.layoutParams as RelativeLayout.LayoutParams
        params2.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
        val params3 = mchk.layoutParams as RelativeLayout.LayoutParams
        params2.setMargins(0,0,0,0)
        params3.apply {
            addRule(RelativeLayout.END_OF, 0)
            addRule(RelativeLayout.START_OF, mchk2.id)
            addRule(RelativeLayout.ALIGN_BOTTOM, mchk2.id)
        }
        imageOpposite.visibility = View.GONE
        mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_1)
    }
    private fun b(){
        mchk3.visibility = View.VISIBLE
        mchk.visibility = View.VISIBLE
        mchk2.visibility = View.GONE
        val params2 = mchk3.layoutParams as RelativeLayout.LayoutParams
        params2.addRule(RelativeLayout.ALIGN_PARENT_END, 0)
        val params3 = mchk.layoutParams as RelativeLayout.LayoutParams
        params3.apply {
            addRule(RelativeLayout.START_OF, 0)
            addRule(RelativeLayout.END_OF, mchk3.id)
            addRule(RelativeLayout.ALIGN_BOTTOM, mchk3.id)
        }
        imageOpposite.visibility = View.VISIBLE
        mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2)
    }
    private fun b1(){
        mchk3.visibility = View.GONE
        mchk.visibility = View.VISIBLE
        mchk2.visibility = View.VISIBLE
        val params2 = mchk2.layoutParams as RelativeLayout.LayoutParams
        params2.setMargins(0,0,0,0)
        params2.addRule(RelativeLayout.ALIGN_PARENT_END, 0)
        val params3 = mchk.layoutParams as RelativeLayout.LayoutParams
        params3.apply {
            addRule(RelativeLayout.START_OF, 0)
            addRule(RelativeLayout.END_OF, mchk2.id)
            addRule(RelativeLayout.ALIGN_BOTTOM, mchk2.id)
        }
        imageOpposite.visibility = View.VISIBLE
        mchk2.background = ContextCompat.getDrawable(context, R.drawable.chat_2)
    }
}