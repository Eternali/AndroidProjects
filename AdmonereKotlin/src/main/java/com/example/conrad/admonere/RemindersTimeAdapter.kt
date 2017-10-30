package com.example.conrad.admonere

// import required libraries
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

enum class PagerState (val titleId : Int, val resId : Int) {

    PAST(R.string.past, R.layout.pager_layout),
    CURRENT(R.string.current, R.layout.pager_layout),
    INPROGRESS(R.string.not_done, R.layout.pager_layout)

}

class RemindersTimeAdapter (val context : Context) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val stateEnum : PagerState = PagerState.values()[position]
        val inflater : LayoutInflater = LayoutInflater.from(context)
        val layout = inflater.inflate(position, container, false) as ViewGroup
        container?.addView(layout)
        return layout
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return PagerState.values().size
    }

    override fun isViewFromObject(p0: View?, p1: Any?): Boolean {
        return p0 == p1
    }

    override fun getPageTitle(position: Int): CharSequence {
        val stateEnum : PagerState = PagerState.values()[position]
        return context.getString(stateEnum.titleId)
    }

}