package com.jhj.amapdemo

import android.view.View
import com.jhj.map.AMapActivity
import kotlinx.android.synthetic.main.layout_top_bar.view.*

/**
 * Created by jhj on 18-10-19.
 */
class MapActivity : AMapActivity() {

    /* override val isNeedSearch: Boolean
         get() = false

     override val isShowCenterPos: Boolean
         get() = false
 */

    override val layoutRes: Int?
        get() = R.layout.layout_top_bar

    override fun initAMapTopBar(view: View) {
        super.initAMapTopBar(view)
        view.iv_topBar_back.setOnClickListener {
            finish()
        }

        view.tv_topBar_title.text = "地图"
        view.tv_topbar_confim.setOnClickListener {
            intent.putExtra("data", resultLocation)
            setResult(RESULT_OK, intent)
            finish()
        }


    }


}