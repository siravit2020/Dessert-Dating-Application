package com.maiandguy.dessert.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.maiandguy.dessert.Functions.ChangLanguage
import com.maiandguy.dessert.QAStore.QAActivityAdapter
import com.maiandguy.dessert.R
import com.maiandguy.dessert.ViewModel.QuestionViewModel
import com.maiandguy.dessert.ui.register.view.RegisterFinishAcivity

class QuestionActivity : AppCompatActivity() {
    private var x: Double = 0.0
    private var y: Double = 0.0
    private lateinit var pager: ViewPager2
    private var email: String? = null
    private var pass: String? = null
    private var name: String? = null
    private lateinit var progressBar: ProgressBar
    private var sex: String? = null
    private var age: Int = 18
    private lateinit var toolbar: Toolbar
    private lateinit var intent1: Intent
    private var language:ChangLanguage = ChangLanguage(this)
    private lateinit var questionViewModel:QuestionViewModel
    private val localizationDelegate = LocalizationActivityDelegate(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        toolbar = findViewById(R.id.my_tools)
        progressBar = findViewById(R.id.progressQuestion)
        setSupportActionBar(toolbar)
        questionViewModel = ViewModelProvider(this,object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return QuestionViewModel(this@QuestionActivity) as T
            }
        }).get(QuestionViewModel::class.java)
        supportActionBar!!.setTitle(R.string.register)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        pager = findViewById(R.id.viewPagerQuestion)
        language.setLanguage()
        intent.apply {
            x = getDoubleExtra("X", x)
            y = getDoubleExtra("Y", y)
            type = getStringExtra("Type")
            email = getStringExtra("email")
            pass = getStringExtra("password")
            name = getStringExtra("Name")
            sex = getStringExtra("Sex")
            age = getIntExtra("Age", age)
        }
        intent1 = Intent(this@QuestionActivity, RegisterFinishAcivity::class.java)
        intent1.apply {
            putExtra("Sex", intent.getStringExtra("Sex"))
            putExtra("Type", intent.getStringExtra("Type"))
            putExtra("X", intent.getDoubleExtra("X", 0.0))
            putExtra("Y", intent.getDoubleExtra("Y", 0.0))
            putExtra("Name", intent.getStringExtra("Name"))
            putExtra("Age", intent.getStringExtra("Age"))
            putExtra("email", intent.getStringExtra("email"))
            putExtra("password", intent.getStringExtra("password"))
            putExtra("Birth", intent.getLongExtra("Birth", 0))
        }
        questionViewModel.fetchRegisterQA.observe(this,{
            val pagerAdapter = QAActivityAdapter(this,it,pager,intent1)
            pager.offscreenPageLimit = it.size
            pager.isUserInputEnabled = false
            pager.adapter = pagerAdapter
            progressBar.visibility = View.GONE
        })
        questionViewModel.responseRegisterQA(localizationDelegate.getLanguage(this).toLanguageTag())
        questionViewModel.response(localizationDelegate.getLanguage(this).toLanguageTag())

    }
}