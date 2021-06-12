package com.siravit.dessert.activity.register.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.siravit.dessert.R
import com.siravit.dessert.activity.register.QuestionActivity
import com.tapadoo.alerter.Alerter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset
import java.util.*


class RegisterAgeActivity : AppCompatActivity() {

    private var age = 0
    private var y = 0
    private var m = 0
    private var d = 0
    private lateinit var button: Button
    private lateinit var toolbar: Toolbar
    private lateinit var calendar: Calendar
    private lateinit var datePicker: DatePicker
    private lateinit var date: LocalDate
    private lateinit var intent1: Intent
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocal()
        setContentView(R.layout.activity_regis_age)
        button = findViewById(R.id.button_name)
        toolbar = findViewById(R.id.my_tools)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.register)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        datePicker = findViewById<DatePicker>(R.id.datePicker)
        intent1 = Intent(this@RegisterAgeActivity, QuestionActivity::class.java)
        calendar = Calendar.getInstance()
        datePicker.maxDate = calendar.timeInMillis
        calendar.timeInMillis = System.currentTimeMillis()
        y = calendar.get(Calendar.YEAR)
        m = calendar.get(Calendar.MONTH)
        d = calendar.get(Calendar.DAY_OF_MONTH)
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { _, year, month, dayOfMonth ->
            y = year
            m = month + 1
            d = dayOfMonth
        }
        button.setOnClickListener(View.OnClickListener {
            age = getAge(y, m, d)
            if (age >= 18) {
                Alerter.hide()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    date = LocalDate.of(y, m, d)
                    date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                    intent1.putExtra("Birth", date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
                } else {
                    val strDate = "$d-$m-$y"
                    val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy")
                    val date = formatter.parse(strDate) as Date
                    intent1.putExtra("Birth", date.time)
                }
                intent1.putExtra("Sex", intent.getStringExtra("Sex"))
                intent1.putExtra("X", intent.getDoubleExtra("X", 0.0))
                intent1.putExtra("Y", intent.getDoubleExtra("Y", 0.0))
                intent1.putExtra("Name", intent.getStringExtra("Name"))
                intent1.putExtra("Age", age)
                intent1.putExtra("email", intent.getStringExtra("email"))
                intent1.putExtra("password", intent.getStringExtra("password"))
                startActivity(intent1)
                return@OnClickListener
            } else {
                Alerter.create(this@RegisterAgeActivity)
                        .setTitle(getString(R.string.Noti))
                        .setText(getString(R.string.up18))
                        .setBackgroundColorRes(R.color.c3)
                        .show()
            }
        })
    }

    private fun setLocal(lang: String) {

        val locale = Locale(lang)
        Locale.setDefault(locale)
//        val configuration = Configuration()
//        configuration.setLocale(locale)
//        baseContext.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
        val overrideConfiguration = baseContext.resources.configuration
        overrideConfiguration.setLocale(locale)
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", lang)
        editor.apply()
        Log.d("My", lang)


    }

    private fun loadLocal() {
        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = preferences.getString("My_Lang", "")!!
        Log.d("My2", language)
        setLocal(language)
    }

    private fun getAge(year: Int, month: Int, dayOfMonth: Int): Int {
        return Period.between(
                LocalDate.of(year, month, dayOfMonth),
                LocalDate.now()
        ).years;
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