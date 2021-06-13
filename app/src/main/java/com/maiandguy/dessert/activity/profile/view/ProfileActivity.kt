package com.maiandguy.dessert.activity.profile.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.maiandguy.dessert.activity.filter_setting.view.FilterSettingActivity
import com.maiandguy.dessert.constants.Id
import com.maiandguy.dessert.utils.DialogSlide
import com.maiandguy.dessert.utils.GlobalVariable
import com.maiandguy.dessert.activity.like_you.view.LikeYouActivity
import com.maiandguy.dessert.R
import com.maiandguy.dessert.activity.send_problem.view.SendProblemActivity
import com.maiandguy.dessert.activity.edit_profile.view.EditProfileActivity
import com.maiandguy.dessert.activity.profile_information.view.ProfileInformationsActivity
import java.io.IOException
import java.util.*

class ProfileActivity : Fragment(), BillingProcessor.IBillingHandler {
    private lateinit var setting: TextView
    private lateinit var name: TextView
    private lateinit var age: TextView
    private lateinit var mcity: TextView
    private lateinit var count: TextView
    private lateinit var see: TextView
    private lateinit var vip: TextView

    private lateinit var setting2: LinearLayout
    private lateinit var edit: LinearLayout
    private lateinit var likeYou: LinearLayout
    private lateinit var seeProfileYou: LinearLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var imageView: ImageView
    private lateinit var mUserDatabase: DatabaseReference
    private lateinit var p1: ProgressBar
    private lateinit var dialog: Dialog
    private lateinit var dialog2: Dialog
    private lateinit var bp: BillingProcessor

    private var statusDialog = false
    private lateinit var setimage: ImageView
    private var gotoProfile = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_profile, container, false)
        super.onCreate(savedInstanceState)
        bp = BillingProcessor(requireContext(), Id.Id, this)
        bp.initialize()
        dialog = Dialog(requireContext())
        val view2 = inflater.inflate(R.layout.progress_dialog, null)
        dialog2 = Dialog(requireContext())
        dialog2.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog2.setContentView(view2)
        val width = (resources.displayMetrics.widthPixels * 0.80).toInt()
        dialog2.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        imageView = view.findViewById(R.id.pre_Image_porfile)
        name = view.findViewById(R.id.pre_name_profile)
        age = view.findViewById(R.id.pre_age_profile)
        count = view.findViewById(R.id.count_like)
        see = view.findViewById(R.id.see_porfile)
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser!!.uid
        mcity = view.findViewById(R.id.aa)
        p1 = view.findViewById(R.id.progress_bar_pre_pro)
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        setting = view.findViewById(R.id.b1)
        setting2 = view.findViewById(R.id.linearLayout20)
        edit = view.findViewById(R.id.linearLayout21)
        likeYou = view.findViewById(R.id.like_you)
        seeProfileYou = view.findViewById(R.id.see_porfile_you)
        vip = view.findViewById(R.id.vip)

        setimage = view.findViewById(R.id.goto_set_image)

        vip.setOnClickListener{
            openDialog()
        }

        likeYou.setOnClickListener{
            val intent = Intent(context, LikeYouActivity::class.java)
            intent.putExtra("Like", count.text.toString().toInt())
            startActivity(intent)
        }
        seeProfileYou.setOnClickListener{
            
            val intent = Intent(context, LikeYouActivity::class.java)
            intent.putExtra("See", see.text.toString().toInt())
            startActivity(intent)
        }
        setting2.setOnClickListener{
            val intent = Intent(context, FilterSettingActivity::class.java)
            startActivityForResult(intent, 15)
        }
        edit.setOnClickListener{
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivityForResult(intent, 14)
        }
        imageView.setOnClickListener{
            if (gotoProfile) {
                val intent = Intent(context, ProfileInformationsActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(context, EditProfileActivity::class.java)
                startActivity(intent)
            }
        }
        setimage.setOnClickListener{
            val intent = Intent(context, EditProfileActivity::class.java)
            intent.putExtra("setImage", "1")
            startActivity(intent)
        }
        view.findViewById<LinearLayout>(R.id.linearLayout22).setOnClickListener {
            val intent = Intent(context, SendProblemActivity::class.java)
            startActivityForResult(intent,14)
        }
        view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh).setOnRefreshListener {
            requireActivity().finish()
            requireActivity().overridePendingTransition(0, 0)
            requireActivity().startActivity(requireActivity().intent)
            requireActivity().overridePendingTransition(0, 0)
        }

        return view
    }

    @SuppressLint("InflateParams")
    fun openDialog() {

        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.vip_dialog, null)
        val b1 = view.findViewById<Button>(R.id.buy)
        val b2 = view.findViewById<Button>(R.id.admob)
        val text = view.findViewById<TextView>(R.id.test_de)
        if (!statusDialog) {
            b1.setOnClickListener {
                bp.subscribe(requireActivity(), "YOUR SUBSCRIPTION ID FROM GOOGLE PLAY CONSOLE HERE")
                mUserDatabase.child("Vip").setValue(1)
                GlobalVariable.vip = true
                getData()
                dialog.dismiss()
            }
        } else {
            b1.setText(R.string.back)
            b1.setOnClickListener { dialog.dismiss() }
        }
        b2.visibility = View.GONE
        text.text = "สมัคร Desert VIP เพื่อรับสิทธิพิเศษต่างๆ"
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(view)
        DialogSlide(requireContext(), dialog, view).start()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window!!.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.show()

    }


    @SuppressLint("SetTextI18n")
    private fun getData() {

        val gender = if (GlobalVariable.image == "Male") {
            R.drawable.ic_man
        } else R.drawable.ic_woman
        if (GlobalVariable.image.isNotEmpty()) {
            Glide.with(requireContext()).load(GlobalVariable.image)
                    .placeholder(R.color.background_gray).listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            p1.visibility = View.GONE
                            return false
                        }
                    })
                    .apply(RequestOptions().override(300, 300)).into(imageView)
        } else {
            gotoProfile = false
            Glide.with(requireContext()).load(gender).placeholder(R.color.background_gray)
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            p1.visibility = View.GONE
                            return false
                        }
                    })
                    .apply(RequestOptions().override(300, 300)).into(imageView)
        }
        if (GlobalVariable.vip) {
            vip.setText(R.string.You_are_vip)
            statusDialog = true
        }
        count.text = GlobalVariable.likeYou.toString()
        see.text = GlobalVariable.seeYou.toString()
        name.text = GlobalVariable.name
        age.text = ", " + GlobalVariable.age.toString()
        val latDouble = GlobalVariable.x.toDouble()
        val lonDouble = GlobalVariable.y.toDouble()
        val preferences2 = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = preferences2.getString("My_Lang", "")
        val geoCoder: Geocoder = if (language == "th") {
            Geocoder(context)
        } else {
            Geocoder(context, Locale.UK)
        }
        var addresses: MutableList<Address?>? = null
        try {
            addresses = geoCoder.getFromLocation(latDouble, lonDouble, 1)
            val city = addresses[0]!!.adminArea
            mcity.text = city
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    override fun onResume() {
        super.onResume()
        gotoProfile = true
        getData()
    }

    override fun onStop() {
        super.onStop()
        dialog2.dismiss()
    }

    override fun onDestroy() {
        bp.release()
        super.onDestroy()
    }

    override fun onBillingInitialized() {

    }

    override fun onPurchaseHistoryRestored() {

    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {

    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 14) {
                getData()
                onAttach(requireContext())
            }
        }
        if(requestCode == 14){
            if(resultCode == 14){
                Snackbar.make(likeYou,"ขอบคุณ",Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}