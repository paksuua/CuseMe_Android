package com.tistory.comfy91.excuseme_android.feature.disabledimport android.content.Intentimport android.opengl.Visibilityimport android.os.Bundleimport android.speech.tts.TextToSpeechimport android.speech.tts.UtteranceProgressListenerimport android.text.method.TextKeyListener.clearimport android.util.Logimport android.view.MotionEventimport android.view.ScaleGestureDetectorimport android.view.Viewimport android.widget.Toastimport androidx.appcompat.app.AppCompatActivityimport androidx.core.view.isVisibleimport androidx.recyclerview.widget.GridLayoutManagerimport androidx.recyclerview.widget.RecyclerViewimport com.google.android.material.card.MaterialCardViewimport com.tistory.comfy91.excuseme_android.*import com.tistory.comfy91.excuseme_android.api.ServerServiceimport com.tistory.comfy91.excuseme_android.data.CardBeanimport com.tistory.comfy91.excuseme_android.data.ResCardDetailimport com.tistory.comfy91.excuseme_android.data.ResCardsimport com.tistory.comfy91.excuseme_android.data.SingletoneTokenimport com.tistory.comfy91.excuseme_android.data.repository.DummyCardDataRepositoryimport com.tistory.comfy91.excuseme_android.data.repository.ServerCardDataRepositoryimport com.tistory.comfy91.excuseme_android.data.server.BodyGetDisabledCardimport com.tistory.comfy91.excuseme_android.feature.login.Loginimport com.tistory.comfy91.excuseme_android.feature.unlock.UnlockActivityimport kotlinx.android.synthetic.main.activity_disabled.*import retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseimport java.util.*import java.util.Collections.addAllimport kotlin.collections.ArrayListclass DisabledActivity : AppCompatActivity() {    lateinit var tts: TextToSpeech    lateinit var gridManager1: GridLayoutManager    lateinit var gridManager2: GridLayoutManager    lateinit var gridManager3: GridLayoutManager    lateinit var mCurrentLayoutManager: RecyclerView.LayoutManager    lateinit var rvDisabledaAapter: CardAdapter    private var cardData: ArrayList<CardBean> = arrayListOf()    lateinit var cardView: MaterialCardView    var position = 0    var count = 0    private var cardDataRepository = ServerCardDataRepository()    private lateinit var uuid: String    private val token = SingletoneToken.getInstance().token    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_disabled)        //data        initUi()        initData()    }    private fun initUi() {        btnDisabledUnlock.setOnSingleClickListener {            this.newStartActivity(UnlockActivity::class.java)        }        gridManager1 = GridLayoutManager(this, 1)        gridManager2 = GridLayoutManager(this, 2)        gridManager3 = GridLayoutManager(this, 3)        mCurrentLayoutManager = gridManager2        rvDisabledaAapter = CardAdapter(object : CardAdapter.ItemClickListener {            override fun onItemClicked(desc: String) {                // 아이템 선택 되었을때 desc 표시                tvDisabledShowCardText.setText(desc)                img_home_quote_right.visibility = View.VISIBLE                img_home_quote_left.visibility = View.VISIBLE            }            override fun onItemClickTimeFnished() {                // 아이템 선택 해제되었을때 desc 지움                tvDisabledShowCardText.setText("")                img_home_quote_right.visibility = View.INVISIBLE                img_home_quote_left.visibility = View.INVISIBLE            }        })        rvDisabledCard.layoutManager = gridManager2        rvDisabledCard.adapter = rvDisabledaAapter        val mScaleGestureDetector = ScaleGestureDetector(            this,            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {                override fun onScale(detector: ScaleGestureDetector): Boolean {                    Log.v("Excuse", "줌2")                    if (detector.currentSpan > 200 && detector.timeDelta > 200) {                        // 2에서 3됐을 때                        if (detector.currentSpan - detector.previousSpan < -1) {                            if (mCurrentLayoutManager == gridManager2) {                                mCurrentLayoutManager = gridManager3                                rvDisabledCard.layoutManager = mCurrentLayoutManager                                rvDisabledCard.scrollToPosition(position)                                return true                                //1에서 2됐을 때                            } else if (detector.currentSpan - detector.previousSpan < -1) {                                if (mCurrentLayoutManager == gridManager1) {                                    mCurrentLayoutManager = gridManager2                                    rvDisabledCard.layoutManager = mCurrentLayoutManager                                    rvDisabledCard.scrollToPosition(position)                                    return true                                }                            }                        }                        //3에서 2됐을 떄                    } else if (detector.currentSpan - detector.previousSpan > 1) {                        if (mCurrentLayoutManager == gridManager3) {                            mCurrentLayoutManager = gridManager2                            rvDisabledCard.layoutManager = mCurrentLayoutManager                            rvDisabledCard.scrollToPosition(position)                            return true                            //2에서 1됐을 때                        } else if (detector.currentSpan - detector.previousSpan > 1) {                            if (mCurrentLayoutManager == gridManager2) {                                mCurrentLayoutManager = gridManager1                                rvDisabledCard.layoutManager = mCurrentLayoutManager                                rvDisabledCard.scrollToPosition(position)                                return true                            }                        }                    }                    return false                }            })        rvDisabledCard.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {            }            //ViewGroup 표면에서 터치 이벤트가 감지될 때 항상 호출            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {                //findChildViewUnder = 지정 point 위의 view를 찾아줌                val child = rv.findChildViewUnder(e.x, e.y)                //recyclerView의 빈 곳을 터치할 때                if (child == null) {                    //true를 반환하면 이전에 터치 이벤트를 처리하던 하위 뷰가 ACTION_CANCEL을 수신하고                    // 이 시점 이후로는 이벤트가 상위 뷰의 onTouchEvent() 메서드로 전송되어 평소와 같은 처리                    return true                } else {                    //getChildAdapterPosition = adapter에서 지정된 view에 해당하는 위치 반환.                    position = rv.getChildAdapterPosition(child!!)                    mScaleGestureDetector.onTouchEvent(e)                    Log.v("ExcuseP", position.toString())                    return false                }            }            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {            }        })    }    private fun initData() {        uuid = Login.getUUID(this)        getCard()        //TTS        tts = TextToSpeech(this,            TextToSpeech.OnInitListener { status ->                if (status == TextToSpeech.SUCCESS) {                    tts.setLanguage(Locale.KOREA).let {                        if (it == TextToSpeech.LANG_MISSING_DATA                            || it == TextToSpeech.LANG_NOT_SUPPORTED                        ) {                            Toast.makeText(this, "지금 지원되지 않습니다.", Toast.LENGTH_LONG).show()                        }                    }                }            })    }    private fun getCard() {        requestCard()    }    private fun setAdapterData(cardData: ArrayList<CardBean>) {        rvDisabledaAapter.data = cardData        rvDisabledaAapter.notifyDataSetChanged()    }    private fun requestCard() {        "Request Disabled Card".logDebug(this@DisabledActivity)        cardDataRepository.getDisabledCards(BodyGetDisabledCard(uuid))            .enqueue(object : Callback<ResCards> {                override fun onFailure(call: Call<ResCards>, t: Throwable) {                    "${t.message}".logDebug(this@DisabledActivity)                }                override fun onResponse(call: Call<ResCards>, response: Response<ResCards>) {                    "responser code : ${response.code()}, response message:  ${response.message()}".logDebug(                        this@DisabledActivity                    )                    if (response.isSuccessful) {                        response.body().let { body ->                            "status: ${body!!.status} data : ${body!!.data}".logDebug(this@DisabledActivity)                            if (body!!.success) {                                cardData.clear()                                // for dummy                                cardData.addAll(getCarDummy())//                              cardData.addAll(body!!.data as ArrayList<CardBean>)                                rvDisabledaAapter.data.clear()                                rvDisabledaAapter.data.addAll(cardData)                                rvDisabledaAapter.notifyDataSetChanged()                                if(cardData.isEmpty()){                                    rvDisabledCard.isVisible = false                                    imgDisabledCardEmpty.isVisible = true                                    tvDisabledCardEmpty.isVisible = true                                }                                else{                                    rvDisabledCard.isVisible = true                                    imgDisabledCardEmpty.isVisible = false                                    tvDisabledCardEmpty.isVisible = false                                }                            } else {                                "Resonse is not Success : body is empty".logDebug(this@DisabledActivity)                            }                        }                    } else {                        "response.is not success".logDebug(DisabledActivity::class.java)                    }                }            })    }    private fun getCarDummy(): ArrayList<CardBean> {        var dummyList = arrayListOf(            CardBean(                0,                "first card",                "화장실 가고 싶어요.",                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",                "",                0,                false,                "serialNum",                0,                ""            ),            CardBean(                0,                "second card",                "김강희씨!!!",                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",                "",                0,                false,                "serialNum",                0,                ""            ),            CardBean(                0,                "third card",                "배고파요.",                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",                "",                0,                false,                "serialNum",                0,                ""            ),            CardBean(                0,                "fifth card",                "피엠님 할 말 있어요.",                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",                "",                0,                false,                "serialNum",                0,                ""            ),            CardBean(                0,                "sixth card",                "집 가고싶어요.",                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",                "",                0,                false,                "serialNum",                0,                ""            ),            CardBean(                0,                "seventh card",                "네?",                "https://t18.pimg.jp/055/208/688/1/55208688.jpg",                "",                0,                false,                "serialNum",                0,                ""            )        )        return dummyList    }    override fun onStop() {        super.onStop()        rvDisabledaAapter.stopPlaying()    }}