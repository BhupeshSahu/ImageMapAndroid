package com.android.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.android.imagemap.MapResource
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        map.setImageMapListener {
            map.showArea(it)
            Toast.makeText(this, "Area clicked $it", Toast.LENGTH_SHORT).show()
        }
        map.setImageResource(R.drawable.floormap)
        map.setMap(MapResource(R.xml.floor))
    }

    override fun onBackPressed() {
        if (!map.resetToOverviewMode())
            super.onBackPressed()
    }
}
