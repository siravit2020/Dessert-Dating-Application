package com.jabirdeveloper.tinderswipe.QAStore

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.jabirdeveloper.tinderswipe.R


class DialogFragment : AppCompatDialogFragment() {
    var listener: ExampleClassListener? = null
    /*var radio1: RadioButton? = null
    var radio2: RadioButton? = null
    var questionText: TextView? = null
    var confirmText: TextView? = null
    var dismissText: TextView? = null
    var radioGroup1: RadioGroup? = null
    var radioGroupWeight: RadioGroup? = null
    var question: String = ""*/
    private var choice: ArrayList<QAObject> = ArrayList()
    //private lateinit var functions: FirebaseFunctions

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = Dialog(activity!!)
        val lay = activity!!.layoutInflater
        val view: View = lay.inflate(R.layout.viewpager_questions, null)
        val viewpager: ViewPager2 = view.findViewById(R.id.pagerTest)
        val adapter = QAPagerAdapter(activity!!, choice, builder, viewpager)
        viewpager.adapter = adapter
        viewpager.isUserInputEnabled = false
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.setContentView(view)
        val width = Resources.getSystem().displayMetrics.widthPixels
        builder.window!!.setLayout(width-60, ViewGroup.LayoutParams.WRAP_CONTENT)
        builder.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.show()
        return builder
    }

    fun setData(choice: ArrayList<QAObject>) {
        this.choice = choice
    }

    override fun onAttach(context: Context) {
        try {
            listener = context as ExampleClassListener;
        } catch (e: Exception) {
            Log.d("error", e.toString())
        }
        super.onAttach(context)
    }

    interface ExampleClassListener {
        fun applyTexts(choice: Int)
    }
}