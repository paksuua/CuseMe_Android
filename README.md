![71553942-423e6f80-2a5b-11ea-972f-364c5b896b60](https://user-images.githubusercontent.com/57262833/71586878-f96fdf00-2b5e-11ea-95f7-0add8389e15b.png)

# CuseMe_Android :loudspeaker:

## 1. 프로젝트 사용 라이브러리
Retrofit
    
```       
    implementation 'com.squareup.retrofit2:retrofit:2.6.2'
    implementation 'com.google.code.gson:gson:2.8.6'
``` 

    
Bottom Navigation Bar
```
    implementation 'androidx.lifecycle:lifecycle-extensions:2.1.0'
    implementation 'com.google.android.material:material:1.0.0'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.1.0'
    implementation 'androidx.navigation:navigation-ui-ktx:2.1.0'
    implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.1.1'
```

CardView
```
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'com.google.android.material:material:1.0.0'
```

Fragment 
```
    implementation "androidx.navigation:navigation-fragment-ktx:2.1.0-alpha05"
    implementation "androidx.navigation:navigation-ui-ktx:2.1.0-alpha05"
```
    
RecyclerView
```
    implementation 'androidx.recyclerview:recyclerview:1.1.0'
```
    
Glide
```
    implementation 'com.github.bumptech.glide:glide:4.9.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0'
```
2. 프로젝트 구조


3. 핵심 기능 구현


1)Zoom In / Zoom Out

```
val mScaleGestureDetector = ScaleGestureDetector(
    this,
    object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            Log.v("Excuse", "줌2")

            if (detector.currentSpan > 200 && detector.timeDelta > 200) {

                // 2에서 3
                if (detector.currentSpan - detector.previousSpan < -1) {
                    if (mCurrentLayoutManager == gridManager2) {
                        mCurrentLayoutManager = gridManager3
                        rvDisabledCard.layoutManager = mCurrentLayoutManager
                        rvDisabledCard.scrollToPosition(position)
                        return true

                        //1에서 2
                    } else if (detector.currentSpan - detector.previousSpan < -1) {
                        if (mCurrentLayoutManager == gridManager1) {
                            mCurrentLayoutManager = gridManager2
                            rvDisabledCard.layoutManager = mCurrentLayoutManager
                            rvDisabledCard.scrollToPosition(position)
                            return true
                        }
                    }
                }
                //3에서 2
            } else if (detector.currentSpan - detector.previousSpan > 1) {
                if (mCurrentLayoutManager == gridManager3) {
                    mCurrentLayoutManager = gridManager2
                    rvDisabledCard.layoutManager = mCurrentLayoutManager
                    rvDisabledCard.scrollToPosition(position)
                    return true

                    //2에서 1
                } else if (detector.currentSpan - detector.previousSpan > 1) {
                    if (mCurrentLayoutManager == gridManager2) {
                        mCurrentLayoutManager = gridManager1
                        rvDisabledCard.layoutManager = mCurrentLayoutManager
                        rvDisabledCard.scrollToPosition(position)
                        return true
                    }
                }
            }

            return false
        }
    })
    
```


2)Long Click / Drag&Drop

```
class DragManageAdapter (adapter: RvHelperSortAdapter, context: Context, dragDirs: Int, swipeDirs: Int)
    : ItemTouchHelper.Callback(){

    var cardAdapter = adapter

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        cardAdapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
        return true
    } // end onMove()

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 사용안함
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags: Int = ((ItemTouchHelper.UP.or(ItemTouchHelper.DOWN)).or(ItemTouchHelper.LEFT)).or(ItemTouchHelper.RIGHT)
        val swipeFlags = 0
        return makeMovementFlags(dragFlags,swipeFlags)
    }
}

```


3)중복 터치 방지

```
btnDisabledUnlock.setOnClickListener(object : View.OnClickListener {

    private var mLastClickTime: Long = 0

    override fun onClick(v: View) {

        if (SystemClock.elapsedRealtime() - mLastClickTime > 3000) {
            Log.v("Excuse", "터치")
            mLastClickTime = SystemClock.elapsedRealtime()
            count++
            tv2.setText("" + count)

        } else return
        Log.v("Excuse", "연속 터치")

    }
})

```


4)TTS

```
class TTSActivity : AppCompatActivity() {
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tts)

        tts = TextToSpeech(applicationContext,
            TextToSpeech.OnInitListener { status ->
                if(status == TextToSpeech.SUCCESS){
                    tts.setLanguage(Locale.KOREA).let {
                        if(it == TextToSpeech.LANG_MISSING_DATA
                            || it == TextToSpeech.LANG_NOT_SUPPORTED){
                            Toast.makeText(this@TTSActivity, "지금 지원되지 않습니다.", Toast.LENGTH_LONG).show()
                        } else{
                            btnTTS.isEnabled = true
                        }
                    }
                }
            })
            
```
            
            
5)애니메이션

```
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <scale
        android:fromXScale="0.8"
        android:fromYScale="0.8"
        android:toXScale="0.0"
        android:toYScale="0.0"
        android:pivotX="50%"
        android:pivotY="50%"
        android:duration="300"
        android:interpolator="@android:anim/linear_interpolator"/>

    <alpha
        android:fromAlpha="1.0"
        android:toAlpha="0.0"
        android:duration="300"
        android:interpolator="@android:anim/accelerate_interpolator"/>
</set>

```


6)Sort(보이는 순, 빈도순, 이름순)

```
private fun dataSort(sortStandard: Int) {
    when (sortStandard) {
        SORT_BY_VISIBILITY -> dummyData.sortByDescending { it.visibility }
        SORT_BY_COUNT -> dummyData.sortByDescending { it.count }
        SORT_BY_TITLE -> dummyData.sortBy { it.title }
        else -> "Wrong Standard Flag".logDebug(activity!!.baseContext)
    }
    selectSortAdapter.notifyDataSetChanged()
}

```

7)Searching

```
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    searchedList = data
                } else {
                    val filteredList = ArrayList<DataHelperSortCard>()
                    //이부분에서 원하는 데이터를 검색
                    for (row in data) {

                        Log.d("search1","search data :  " + row.title.toLowerCase())
                        Log.d("search2","input data " + charString.toLowerCase())
                        if (row.title.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    searchedList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = searchedList
                return filterResults
            }
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                Log.d("search3","publishResults")
                searchedList = filterResults.values as ArrayList<DataHelperSortCard>
                notifyDataSetChanged()
            }
        }
    }
}

```




