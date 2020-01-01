package com.tistory.comfy91.excuseme_android.feature.unlockimport android.os.Bundleimport android.view.Viewimport android.view.WindowManagerimport androidx.appcompat.app.AppCompatActivityimport com.tistory.comfy91.excuseme_android.*import com.tistory.comfy91.excuseme_android.api.Serviceimport com.tistory.comfy91.excuseme_android.data.repository.DummyUserDataRepositoryimport com.tistory.comfy91.excuseme_android.data.ResponseLoginimport com.tistory.comfy91.excuseme_android.data.SingletoneTokenimport com.tistory.comfy91.excuseme_android.data.server.BodyHelperSignInimport com.tistory.comfy91.excuseme_android.feature.helper.HelperActivityimport com.tistory.comfy91.excuseme_android.feature.login.Loginimport androidx.core.content.ContextCompatimport androidx.core.widget.doOnTextChangedimport com.tistory.comfy91.excuseme_android.Rimport kotlinx.android.synthetic.main.activity_unlock.*import retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseclass UnlockActivity : AppCompatActivity() {    private val userDataRepository =DummyUserDataRepository()    private val singletoneToken = SingletoneToken.getInstance()    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_unlock)        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)        initUi()        edt_unlockpw.doOnTextChanged { text1, start, count, after ->            if (!text1.isNullOrBlank()) {                btnUnlockGoHelper.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_pink)            } else {                btnUnlockGoHelper.background = ContextCompat.getDrawable(this, R.drawable.bg_btn_gray)            }        }    }    private fun initUi() {        if(Login.getPwFlag(this)){            edt_unlockpw.hint = "${edt_unlockpw.hint} (초기 비밀번호 : 0000)"        }        btnUnlockBack.setOnSingleClickListener {            finish()        }        btnUnlockGoHelper.setOnSingleClickListener {            login()        }    }    private fun login() {        edt_unlockpw.text.toString()            ?.let {                when (it) {                    "" -> "아이디나 패스워드를 다시 확인해주세요".toast(this)                    else -> requestLogin(it)                }            }    }    private fun requestLogin(pw: String) {        val uuid = Login.getUUID(this)        run {            userDataRepository.helperSignIn(                BodyHelperSignIn(uuid, pw)            ).enqueue(                object : Callback<ResponseLogin> {                    override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {                        "아이디나 패스워드를 다시 확인해주세요".toast(this@UnlockActivity)                        "(failure message : ${t.message})"                    }                    override fun onResponse(                        call: Call<ResponseLogin>,                        response: Response<ResponseLogin>                    ) {                        if (response.isSuccessful) {                            response.body()?.let {                                singletoneToken.token = it.data.token                                this@UnlockActivity.newStartActivity(HelperActivity::class.java)                            }                        } else {                            //todo("구현해야 ")                        }                    }//                    override fun onFailure(call: Call<Token>, t: Throwable) {//                    }////                    override fun onResponse(call: Call<Token>, response: Response<Token>) {////                    }                }            )        }    }}