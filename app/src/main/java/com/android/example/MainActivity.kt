package com.android.example

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.android.imagemap.MapResource
import com.android.imagemap.ResourceType
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val url = "http://www.theitbooth.com/floormap.png"
    private val urlMap = "http://www.theitbooth.com/floor.xml"
    private var target = object : Target {
        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            Log.d("TAG", "Resolution : ${bitmap?.width}x${bitmap?.height}")
            val bmpDrawable = BitmapDrawable(resources, bitmap)
            map.setImageFile(url, bmpDrawable)
            getAndSetMap()
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

        }

    }

    private fun getAndSetMap() {
        val stringRequest = object : StringRequest(com.android.volley.Request.Method.GET, url,
                Response.Listener {
                    if (it != null && it.trim().isNotEmpty()) {
                        val mapResource = MapResource(ResourceType.RAW_STRING, it)
                        map.setMap(mapResource)
                    }
                }, null) {
        }
        var reqQueue = Volley.newRequestQueue(this)
        reqQueue.add(stringRequest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        map.setImageMapListener {
            map.showArea(it)
            Toast.makeText(this, "Area clicked $it", Toast.LENGTH_SHORT).show()
        }
        Picasso.get().load(url).into(target)
    }

    override fun onBackPressed() {
        if (!map.resetToOverviewMode())
            super.onBackPressed()
    }
}
