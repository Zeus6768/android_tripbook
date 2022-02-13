package com.olympos.tripbook.src.tripcourse

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior.getTag
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.kakao.sdk.common.util.Utility
import com.olympos.tripbook.R
import com.olympos.tripbook.config.BaseActivity
import com.olympos.tripbook.config.BaseDialog
import com.olympos.tripbook.databinding.ActivityTripcourseBinding
import com.olympos.tripbook.src.home.MainActivity
import com.olympos.tripbook.src.trip.model.Trip
import com.olympos.tripbook.src.tripcourse.model.Card
import com.olympos.tripbook.src.tripcourse.model.CardService
import com.olympos.tripbook.src.tripcourse.model.CardsView
import com.olympos.tripbook.src.tripcourse.model.ServerView
import com.olympos.tripbook.utils.getTripIdx

class TripcourseActivity : BaseActivity(), CardsView, ServerView {

    lateinit var binding : ActivityTripcourseBinding
    private var gson : Gson = Gson()

    private lateinit var cardRVAdapter : RVCardAdapter

    private var cardIdx = 1
    private var tripIdx : Int = 0
    val cardService = CardService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripcourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cardService.setCardsView(this)

        initView()
        initRecyclerView()
        addDefaultCard()


    }

    private fun addDefaultCard() {
        //        val setTestCard1 : Card = Card(tripIdx, cardIdx, TRUE,"https://post-phinf.pstatic.net/MjAxOTEyMjRfODgg/MDAxNTc3MTY0NzE3ODI0.td40390rDg76HqexxOaLbmw4FMvAE5-taBjKL0QqGw4g.O1S4JTJnFfVcGPgHiCn09gNG2VtFZDO6umEH6e6fqygg.JPEG/%EC%A0%9C%EC%A3%BC%EB%8F%84_%EB%9A%9C%EB%B2%85%EC%9D%B4_%EC%97%AC%ED%96%89.jpg?type=w1200", "2000-00-00", 2, "이름있는제목 1","바뀐 내용 11111", "", "") //cardIdx =1
        //        cardRVAdapter.addCard(setTestCard1)
        //        cardIdx++
        //
        //        val setTestCard2 : Card = Card(tripIdx, cardIdx, TRUE, "https://korean.nlcsjeju.co.kr/userfiles/nlcsjejukrmvc/images/body/IMG_9153.jpg", "2000-11-11", 3, "어떻게든 지어본 이름 2", "바뀌어버린 내용", "", "") //cardIdx =2
        //        cardRVAdapter.addCard(setTestCard2)
        //        cardIdx++

        val defaultCard1: Card = Card(tripIdx, cardIdx) //cardIdx =1
        cardRVAdapter.addCard(defaultCard1)
        postCard(defaultCard1)
        cardIdx++

        val defaultCard2: Card = Card(tripIdx, cardIdx) //cardIdx =2
        cardRVAdapter.addCard(defaultCard2)
        postCard(defaultCard2)
        cardIdx++

        val defaultCard3: Card = Card(tripIdx, cardIdx) //cardIdx =3
        cardRVAdapter.addCard(defaultCard3)
        postCard(defaultCard3)
        cardIdx++
    }

    private fun startTripcourseRecordActivity(card: Card) {
        val intent = Intent(this@TripcourseActivity, TripcourseRecordActivity::class.java)

        if (card.hasData == TRUE) { //데이터가 있는 경우
            val cardData = gson.toJson(card)
            intent.putExtra("card", cardData)
        }

        intent.putExtra("cardIdx", card.idx)
        intent.putExtra("tripIdx", card.tripIdx)

        startActivity(intent)
    }

    override fun onRestart() {
        super.onRestart()
        initRecyclerView()
        getTrip(tripIdx)
    }

    //여행 삭제하기 context menu
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo? ) {
        val inflater = menuInflater
        inflater.inflate(R.menu.context_menu_tripcourse_delete_trip, menu)
//        super.onCreateContextMenu(menu, v, menuInfo)
        showDialog("", "", "")
    }
    //여행 삭제하기 context menu
    override fun onContextItemSelected(item: MenuItem): Boolean {
        //다이어로그 뜨기
        return super.onContextItemSelected(item)
    }

    override fun onOKClicked() {
        super.onOKClicked()
        //todo 현재 여행 삭제
        finish()
    }

    private fun initView() {
        //topbar layout view randering
        binding.tripcourseTopbarLayout.topbarTitleTv.setText(R.string.tripcourse_title)
        binding.tripcourseTopbarLayout.topbarSubbuttonIb.setImageResource(R.drawable.btn_base_check_black)

        if(intent.hasExtra("tripData")) {
            Log.d("들어오는지 확인,,", "들어오긴 하냐")
            val json = intent.getStringExtra("tripData")
            val tripData = gson.fromJson(json, Trip::class.java)
            Log.d("__tripData__ tripcourse", tripData.toString())
            val d_date = tripData.departureDate.split("-")
            val d_year = d_date[0].substring(2,4)
            val d_month = d_date[1]
            val d_day = d_date[2]
            val a_date = tripData.arrivalDate.split("-")
            val a_year = a_date[0].substring(2,4)
            val a_month = a_date[1]
            val a_day = a_date[2]
            val period = d_year + "년 " + d_month + "월 " + d_day + "일 ~ " + a_year + "년 " + a_month + "월 " + a_day + "일의 추억"

            //임시 데이터
            userIdx = tripData.userIdx
            tripIdx = getTripIdx(this)

            binding.tripcourseTitlebarPeriodTv.text = period
            binding.tripcourseTitlebarTitleTv.text = tripData.tripTitle
        }

        binding.tripcourseTopbarLayout.topbarBackIb.setOnClickListener(this)
        binding.tripcourseTopbarLayout.topbarSubbuttonIb.setOnClickListener(this)
        binding.tripcourseAddCardBtn.setOnClickListener(this)

        //타이틀바 길게 클릭 - 여행 삭제하기
        registerForContextMenu(binding.tripcourseTitlebarLayout)
    }

    //종료된 액티비티에서 정보 받아오기 : TripcourseRecord -> card Data
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    private fun initRecyclerView() {
        cardRVAdapter = RVCardAdapter(this)
        binding.lookerAlbumlistRecyclerview.adapter = cardRVAdapter

        binding.lookerAlbumlistRecyclerview.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        binding.lookerAlbumlistRecyclerview.addItemDecoration(RVCardAdapterDecoration())

        cardRVAdapter.setItemClickListener(object : RVCardAdapter.CardClickListener {
            override fun onItemClick(card: Card) {
                startTripcourseRecordActivity(card)
            }
        })
    }

    private fun getTrip(tripIdx : Int){
        Log.d("Check tripIdx", tripIdx.toString())
        cardService.getTrip(tripIdx)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(v: View?) {
        super.onClick(v)
        when(v!!.id) {
            R.id.topbar_back_ib -> //상단바 - 뒤로가기 버튼 - 현재 액티비티 종료
                showDialog("발자국 작성 취소", "발자국 작성을 취소하시겠습니까?\n"
                        + "작성중인 정보는 저장되지 않습니다.", "확인")
            R.id.topbar_subbutton_ib -> { //상단바 - 체크 버튼 - 저장
                //서버에 카드는 다 올라갔으므로 그냥 종료
                finish()
            }
            R.id.tripcourse_add_card_btn -> {
                addCard()
            }
        }
    }

    private fun addCard() {
        val card: Card = Card(tripIdx, cardIdx)
        cardIdx++

        postCard(card)

        cardRVAdapter.addCard(card)
        cardRVAdapter.notifyItemInserted(cardRVAdapter.itemCount - 1)

        Log.d("Check num of cardDatas", cardRVAdapter.itemCount.toString())
    }

    private fun postCard(card : Card) {
        Log.d("Check card Data", card.toString())
        cardService.postCard(card)
    }

    //서버에서 카드들 가져오는 View
    override fun onGetCardsLoading() {
        binding.tripcourseLoadingPb.visibility = View.VISIBLE
    }

    override fun onGetCardsSuccess(cards: ArrayList<Card>) {
        binding.tripcourseLoadingPb.visibility = View.GONE
        cardRVAdapter.setCards(cards)
    }

    override fun onGetCardsFailure(code: Int, message: String) { //통신 실패 View
        binding.tripcourseLoadingPb.visibility = View.GONE
        Toast.makeText(this, "$code : $message", Toast.LENGTH_LONG).show()
    }

    //서버에 카드 보내는 중 View
    override fun onServerLoading() {
        binding.tripcourseLoadingPb.visibility = View.VISIBLE
    }

    override fun onServerSuccess() {
        binding.tripcourseLoadingPb.visibility = View.GONE
    }

    override fun onServerFailure(code: Int, message: String) {
        binding.tripcourseLoadingPb.visibility = View.GONE
        Toast.makeText(this, "$code : $message", Toast.LENGTH_LONG).show()
    }
}