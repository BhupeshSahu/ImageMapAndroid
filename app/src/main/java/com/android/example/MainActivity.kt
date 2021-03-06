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

class MainActivity : AppCompatActivity() {

    private val url = ""
    private val urlMap = ""
    private var target = object : Target {
        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            e?.printStackTrace()
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
        val stringRequest = object : StringRequest(Method.GET, urlMap,
                Response.Listener {
                    if (it != null && it.trim().isNotEmpty()) {
                        val mapResource = MapResource(ResourceType.RAW_STRING, it)
                        map.setMap(mapResource)
                    }
                }, Response.ErrorListener { Log.e("", "Something went wrong", it.cause) }) {
        }
        val reqQueue = Volley.newRequestQueue(this)
        reqQueue.add(stringRequest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        map.setImageMapListener {
            map.showArea(it)
            Toast.makeText(this, "Area clicked $it", Toast.LENGTH_SHORT).show()
        }
//        Picasso.get().load(url).into(target)
        map.setImageResource(R.drawable.floormap)
        map.setMap(MapResource(R.xml.floor))
    }

    override fun onBackPressed() {
        if (!map.resetToOverviewMode())
            super.onBackPressed()
    }
}
