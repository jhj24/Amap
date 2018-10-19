package com.jhj.amapdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.amap.api.location.AMapLocation
import com.jhj.location.LocationCallback
import com.jhj.map.Amap
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val location = (application as MyApplication).location

        btn_location.setOnClickListener {
            location?.setLocationListener(object : LocationCallback {
                override fun onLocation(location: AMapLocation) {
                    val address = location.address
                    Toast.makeText(this@MainActivity, address, Toast.LENGTH_SHORT).show()
                }
            })
            location.start()
        }

        btn_map.setOnClickListener {
            val intent = Intent(this@MainActivity, Amap::class.java)
            startActivity(intent)
        }

    }
}
