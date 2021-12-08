package edu.uoc.tfm.antonalag.cryptotracker.ui.util

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewStub
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import edu.uoc.tfm.antonalag.cryptotracker.R
import kotlinx.android.synthetic.main.activity_base.*

/**
 * Class that configures the general elements of each activity
 */
open class BaseActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    /**
     * Set the specific toolbar for each activity
     */
    fun initSetToolbar(toolbarLayout: Int, toolbarId: Int) {
        // Clear and set toolbar view
        val toolbarContent = findViewById<ViewStub>(R.id.toolbar_content)
        toolbarContent.layoutResource = (toolbarLayout)
        toolbarContent.inflate()
        toolbar = findViewById(toolbarId)
        setSupportActionBar(toolbar)
    }

    /**
     * Set the specific layout for each activity
     */
    fun initSetLayout(activityLayout: Int) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(activityLayout, null)
        main_content_below.addView(view)
    }

    /**
     * Show/Hide specific view
     */
    fun showView(view: View, show: Boolean) {
        view.visibility = if(show) View.VISIBLE else View.GONE
    }
}