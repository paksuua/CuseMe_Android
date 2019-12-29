package com.tistory.comfy91.excuseme_android.feature.unlock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tistory.comfy91.excuseme_android.R
import com.tistory.comfy91.excuseme_android.setOnSingleClickListener
import kotlinx.android.synthetic.main.activity_unlock.*

class UnlockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlock)
        
        initUi()
    }

    private fun initUi(){
        btnUnlockBack.setOnSingleClickListener {
            finish()
        }

        btnUnlockGoHelper.setOnSingleClickListener{
            //todo
            // 다음으로 갈 액티비티 지정
            //this.newStartActivity()
        }
    }
}
