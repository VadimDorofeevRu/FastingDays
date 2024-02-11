package com.vadimdorofeev.orthodoxlentendays

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.navdraw_misc_about)
    }

    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun linkClick(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.vadimdorofeev.ru/"))
        startActivity(intent)
    }
}