package com.jhj.amapdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.amap.api.location.AMapLocation
import com.amap.api.maps2d.model.LatLng
import com.jhj.location.LocationCallback
import com.jhj.map.AMapActivity
import com.jhj.map.AMapPositionBean
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var latLng: LatLng? = null

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
            val intent = Intent(this@MainActivity, aa::class.java)
            intent.putExtra(AMapActivity.LAT_LON, latLng)
            startActivityForResult(intent, 100)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val position = data?.getSerializableExtra("data") as AMapPositionBean?
            position?.let {
                latLng = LatLng(position.latitude, position.longitude)
            }

            Toast.makeText(this, position?.address, Toast.LENGTH_SHORT).show()
        }

    }
}
