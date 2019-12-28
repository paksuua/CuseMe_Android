package com.tistory.comfy91.excuseme_android

import android.os.SystemClock
import android.view.View

/*abstract class OnSingleClickListener : View.OnClickListener {

    private var mLastClickTime: Long = 0

    override fun onClick(v: View) {
        val lastClickTime = mLastClickTime
        val now = System.currentTimeMillis()
        mLastClickTime = now
        if (now - lastClickTime < MIN_DELAY_MS) {
        } else {
            onSingleClick(v)
        }
    }

    abstract fun onSingleClick(v: View)

    companion object {
        private val TAG = OnSingleClickListener::class.java.simpleName

        //중복 클릭 방지 시간 설정(이 시간 이후 다시 클릭 가능)
        //3초
        private const val MIN_DELAY_MS: Long = 3000

        fun wrap(onClickListener: View.OnClickListener): View.OnClickListener {
            return object : OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    onClickListener.onClick(v)
                }
            }
        }
    }
}
*/


abstract class OneOffOnClickListener : View.OnClickListener {

    private val mDelayInMilliseconds: Long
    private var mLastClickTime: Long = 0

    constructor(customDelayInMilliseconds: Long) {
        mDelayInMilliseconds = customDelayInMilliseconds
    }

    constructor() {
        mDelayInMilliseconds = 1000
    }

    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < mDelayInMilliseconds) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        onOneClick(v)
    }

    abstract fun onOneClick(v: View)
}

