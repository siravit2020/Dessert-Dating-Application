package com.maiguy.dessert.activity.profile.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate

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
import com.maiandguy.dessert.utils.ErrorDialog
import com.maiguy.dessert.QAStore.DialogFragment
import com.maiguy.dessert.QAStore.data.QAObject
import com.maiguy.dessert.activity.filter_setting.view.FilterSettingActivity
import com.maiguy.dessert.utils.DialogSlide
import com.maiguy.dessert.utils.GlobalVariable
import com.maiguy.dessert.activity.like_you.view.LikeYouActivity
import com.maiguy.dessert.R
import com.maiguy.dessert.ViewModel.QuestionViewModel
import com.maiguy.dessert.ViewModel.ViewModelFactory
import com.maiguy.dessert.activity.send_problem.view.SendProblemActivity
import com.maiguy.dessert.activity.edit_profile.view.EditProfileActivity
import com.maiguy.dessert.activity.main.view.MainActivity
import com.maiguy.dessert.activity.profile_information.view.ProfileInformationsActivity
import com.maiguy.dessert.dialogs.DialogAskQuestion
import com.maiguy.dessert.dialogs.FeedbackDialog
import com.maiguy.dessert.services.BillingService
import com.maiguy.dessert.services.RemoteConfig
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ProfileActivity : Fragment() {
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
    private lateinit var resultQa:LinearLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var imageView: ImageView
    private lateinit var mUserDatabase: DatabaseReference
    private lateinit var p1: ProgressBar
    private lateinit var dialog: Dialog
    private lateinit var dialog2: Dialog
    private lateinit var resultFeedback:TextView
    private lateinit var localizationDelegate: LocalizationActivityDelegate
    private lateinit var accurateQuestionBtn:TextView
    private var statusDialog = false
    private lateinit var setimage: ImageView
    private var gotoProfile = true
    private lateinit var questionViewModel:QuestionViewModel
    private lateinit var errorDialog: ErrorDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_profile, container, false)
        super.onCreate(savedInstanceState)
        RemoteConfig(requireActivity()).remote()
        localizationDelegate = LocalizationActivityDelegate(requireActivity())

        questionViewModel = ViewModelProvider(this, ViewModelFactory(requireContext())).get(QuestionViewModel::class.java)
        
        accurateQuestionBtn = view.findViewById(R.id.accurate_question_btn)

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
        resultFeedback = view.findViewById(R.id.result_feedback)
        resultQa = view.findViewById(R.id.result_qa)

        setimage = view.findViewById(R.id.goto_set_image)

        if(GlobalVariable.feedbackOn && GlobalVariable.countMatch >= 1 && GlobalVariable.feedbackResult){
            resultFeedback.visibility = View.VISIBLE
        }

        vip.setOnClickListener {
            if (GlobalVariable.vip) {
                vip.setText(R.string.you_are_vip)
                statusDialog = true
            }
            openDialog()
        }
        questionViewModel.outOfQuestion.observe(requireActivity(),{
            if(it){
                GlobalVariable.outOfQuestion = true
                resultQa.visibility = View.GONE
            }
        })
        questionViewModel.responseOutOfQuestion()
        questionViewModel.fetchQA.observe(requireActivity(), {
            if (it.size > 0) {
                val dialogFragment = DialogFragment()
                dialogFragment.setData(it, "Question")
                dialogFragment.show(requireActivity().supportFragmentManager, "Question dialog")
            } else {
                GlobalVariable.outOfQuestion = true
                resultQa.visibility = View.GONE
                errorDialog = ErrorDialog(requireContext())
                errorDialog.outOfQuestionDialog().show()
            }

        })

        likeYou.setOnClickListener {
            val intent = Intent(context, LikeYouActivity::class.java)
            intent.putExtra("Like", count.text.toString().toInt())
            startActivity(intent)
        }
        seeProfileYou.setOnClickListener {

            val intent = Intent(context, LikeYouActivity::class.java)
            intent.putExtra("See", see.text.toString().toInt())
            startActivity(intent)
        }
        setting2.setOnClickListener {
            val intent = Intent(context, FilterSettingActivity::class.java)
            startActivityForResult(intent, 15)
        }
        edit.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivityForResult(intent, 14)
        }
        imageView.setOnClickListener {
            if (gotoProfile) {
                val intent = Intent(context, ProfileInformationsActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(context, EditProfileActivity::class.java)
                startActivity(intent)
            }
        }
        setimage.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            intent.putExtra("setImage", "1")
            startActivity(intent)
        }
        view.findViewById<LinearLayout>(R.id.linearLayout22).setOnClickListener {
            val intent = Intent(context, SendProblemActivity::class.java)
            intent.putExtra("fromProfile",true)
            startActivityForResult(intent, 14)
        }
        view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh).setOnRefreshListener {
            val intent = Intent(context, MainActivity::class.java)
            requireActivity().finish()
            requireActivity().overridePendingTransition(0, 0)
            requireActivity().startActivity(intent)
            requireActivity().overridePendingTransition(0, 0)
        }
        resultFeedback.setOnClickListener {
            FeedbackDialog(requireActivity()) { getFeedbackQuestion() }.show()
        }
        if(GlobalVariable.outOfQuestion){
            resultQa.visibility = View.GONE
        }
        accurateQuestionBtn.setOnClickListener {
            DialogAskQuestion(requireContext()).questionAskDialog(localizationDelegate.getLanguage(requireContext()).toLanguageTag(),questionViewModel,10).show()
        }
        val dialogFragment = DialogFragment()
        questionViewModel.fetchQAFeedback.observe(requireActivity(),{
            dialogFragment.setData(it,"Feedback")
            dialogFragment.show(requireActivity().supportFragmentManager,"Feedback dialog")
        })

        return view
    }

    @SuppressLint("InflateParams")
    fun openDialog() {

        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.vip_dialog, null)
        val b1 = view.findViewById<Button>(R.id.buy)
        val b2 = view.findViewById<Button>(R.id.admob)
        val text = view.findViewById<TextView>(R.id.test_de)
        view.findViewById<TextView>(R.id.question_call).visibility = View.GONE
        if (!statusDialog) {
            b1.setOnClickListener {

                BillingService(requireActivity()).billing()
                dialog.dismiss()
            }
        } else {
            b1.setText(R.string.back)
            b1.setOnClickListener { dialog.dismiss() }
        }
        b2.visibility = View.GONE
        text.setText(R.string.apply_vip_dessert)
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
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        p1.visibility = View.GONE
                        return false
                    }
                })
                .apply(RequestOptions().override(300, 300)).into(imageView)
        } else {
            gotoProfile = false
            Glide.with(requireContext()).load(gender).placeholder(R.color.background_gray)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        p1.visibility = View.GONE
                        return false
                    }
                })
                .apply(RequestOptions().override(300, 300)).into(imageView)
        }
        BillingService(requireActivity())
        if (GlobalVariable.vip) {
            vip.setText(R.string.you_are_vip)
            statusDialog = true
        }
        count.text = GlobalVariable.likeYou.toString()
        see.text = GlobalVariable.seeYou.toString()
        name.text = GlobalVariable.name
        age.text = ", " + GlobalVariable.age.toString()
        val latDouble = GlobalVariable.x.toDouble()
        val lonDouble = GlobalVariable.y.toDouble()
        val language = LocalizationActivityDelegate(requireActivity()).getLanguage(requireContext()).toString()
        val geoCoder: Geocoder = if (language == "th") {
            Geocoder(context)
        } else {
            Geocoder(context, Locale.UK)
        }
        val addresses: MutableList<Address?>?
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
    private var arrQuestion:ArrayList<QAObject> = ArrayList()
    private fun getFeedbackQuestion() {
        Log.d("QUESTION_TAG","Work")
        questionViewModel.responseFeedbackQA(localizationDelegate.getLanguage(requireContext()).toLanguageTag())
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
//            super.onActivityResult(requestCode, resultCode, data)
//            if (requestCode == 14) {
//                getData()
//                onAttach(requireContext())
//            }
//        }

        if (requestCode == 14) {
            if (resultCode == 14) {
                Snackbar.make(likeYou, getString(R.string.thank), Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}