package edu.uoc.tfm.antonalag.cryptotracker.ui.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewStub
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import edu.uoc.tfm.antonalag.cryptotracker.R
import kotlinx.android.synthetic.main.activity_base.*

open class BaseActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun initSetToolbar(toolbarLayout: Int, toolbarId: Int) {
        //val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //val view = inflater.inflate(toolbarLayout, null)
        // Clear and set toolbar view
        val toolbarContent = findViewById<ViewStub>(R.id.toolbar_content)
        toolbarContent.layoutResource = (toolbarLayout)
        toolbarContent.inflate()
        //tc.removeAllViews()
        //tc.addView(view)
        toolbar = findViewById(toolbarId)
        setSupportActionBar(toolbar)
    }

    fun initSetLayout(activityLayout: Int) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(activityLayout, null)
        main_content_below.addView(view)
    }
}