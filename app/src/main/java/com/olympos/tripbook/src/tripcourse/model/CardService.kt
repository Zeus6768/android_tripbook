package com.olympos.tripbook.src.tripcourse.model

import android.util.Log
import com.olympos.tripbook.utils.ApplicationClass.Companion.retrofit
import com.olympos.tripbook.utils.getUserIdx
import retrofit2.*

class CardService{

    private lateinit var cardsView : CardsView

    fun setCardsView(cardsView : CardsView) {
        this.cardsView = cardsView
    }

    private lateinit var serverView: ServerView
    fun setServerView(serverView: ServerView) {
        this.serverView = serverView
    }

    private lateinit var postCardView : PostCardView
    fun setPostCardView(postCardView : PostCardView) {
        this.postCardView = postCardView
    }

    //카드 서버로 전송
    fun postCard(card : Card){
        Log.d("CheckPoint : ", "CardService-postCard Activated")
        postCardView.onPostCardLoading()

        val userIdx = getUserIdx()

        val cardRetrofitService = retrofit.create(CardRetrofitInterface::class.java)
        cardRetrofitService.postCard(userIdx, card).enqueue(object : Callback<PostCardResponse> {
            override fun onResponse(call: Call<PostCardResponse>, response: Response<PostCardResponse>){
                if (response.isSuccessful) {
                    val res = response.body()!!
                    Log.d("__res", response.body()!!.toString())
                    when (res.code) {
                        1000 -> { //성공
                            Log.d("CardService-postCard", res.code.toString() + " : " + res.message+ "courseIdx : "+ res.courseIdx.toString())
                            postCardView.onPostCardSuccess(res.courseIdx)
                        }
                        else -> { //의도된 실패
                            Log.d("CardService-postCard", res.code.toString() + " : " + res.message)
                            postCardView.onPostCardFailure(res.code, res.message)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<PostCardResponse>, t: Throwable) {
                serverView.onServerFailure(400, t.message.toString())
                Log.d("CardService-postCard", t.toString()) //네트워크 실패
            }
        })
    }

    //여행 가져오기
    fun getTripcourses(tripIdx : String){
        val cardRetrofitService = retrofit.create(CardRetrofitInterface::class.java)

        val userIdx = getUserIdx()
        cardsView.onGetCardsLoading()

        cardRetrofitService.getTripcourses(userIdx, tripIdx).enqueue(object : Callback<GetTripcourseResponse> {
            override fun onResponse(call: Call<GetTripcourseResponse>, response: Response<GetTripcourseResponse>) {
                Log.d("들어오는지 확인", "CardService-getTrip-onResponse")
                if (response.isSuccessful) {
                    val res = response.body()!!
                    Log.d("__res", response.body()!!.toString())
                    when (res.code) {
                        1000 -> {
                            Log.d("REST API TEST 성공", res.toString())
                            cardsView.onGetCardsSuccess(res.result)
                        }

                        else -> {
                            Log.d("통신 실패 : ", res.toString())
                            cardsView.onGetCardsFailure(res.code, res.message)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<GetTripcourseResponse>, t: Throwable) {
                Log.d("들어오는지 확인", "CardService-getTrip-onFailure")
                cardsView.onGetCardsFailure(400, t.message.toString())
            }
        })
    }

    //작성 완료한 카드 서버로 전송
    fun patchTripImg(tripIdx: Int, tripImg: String) {
        Log.d("CheckPoint : ", "CardService-patchTripImg Activated")
        serverView.onServerLoading()
        val params: HashMap<String, Any> = HashMap()
        params["tripImg"] = tripImg

        val userIdx = getUserIdx()

        val cardRetrofitService = retrofit.create(CardRetrofitInterface::class.java)
        cardRetrofitService.patchTripImg(userIdx, tripIdx, params).enqueue(object : Callback<ServerDefaultResponse> {
            override fun onResponse(call: Call<ServerDefaultResponse>, response: Response<ServerDefaultResponse>) {
                if (response.isSuccessful) {
                    val res = response.body()!!
                    Log.d("__res", response.body()!!.toString())
                    when (res.code) {
                        1000 -> { //성공
                            Log.d("CardServicepatchTripImg", res.code.toString() + " : " + res.message)
                            serverView.onServerSuccess()
                        }
                        else -> { //의도된 실패
                            Log.d("CardServicepatchTripImg", res.code.toString() + " : " + res.message)
                            serverView.onServerFailure(res.code, res.message)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                serverView.onServerFailure(400, t.message.toString())
                Log.d("CardService-patchTitle", t.toString()) //네트워크 실패
            }
        })
    }

    fun deleteTrip(tripIdx: Int) {
        Log.d("CheckPoint : ", "CardService-deleteTrip Activated")

        val userIdx = getUserIdx()

        serverView.onServerLoading()

        val cardRetrofitService = retrofit.create(CardRetrofitInterface::class.java)
        cardRetrofitService.deleteTrip(userIdx, tripIdx).enqueue(object : Callback<ServerDefaultResponse> {
            override fun onResponse(call: Call<ServerDefaultResponse>, response: Response<ServerDefaultResponse>) {
                if (response.isSuccessful) {
                    val res = response.body()!!
                    Log.d("__res", response.body()!!.toString())
                    when (res.code) {
                        1000 -> { //성공
                            Log.d("CardService-deleteTrip", res.code.toString() + " : " + res.message)
                            serverView.onServerSuccess()
                        }
                        else -> { //의도된 실패
                            Log.d("CardService-deleteTrip", res.code.toString() + " : " + res.message)
                            serverView.onServerFailure(res.code, res.message)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                serverView.onServerFailure(400, t.message.toString())
                Log.d("CardService-deleteTrip", t.toString()) //네트워크 실패
            }
        })
    }


    fun deleteCard(courseIdx : String) {
        Log.d("CheckPoint : ", "CardService-deleteCard Activated")

        val userIdx = getUserIdx()

        serverView.onServerLoading()

        val cardRetrofitService = retrofit.create(CardRetrofitInterface::class.java)
        cardRetrofitService.deleteCard(userIdx, courseIdx).enqueue(object : Callback<ServerDefaultResponse> {
            override fun onResponse(call: Call<ServerDefaultResponse>, response: Response<ServerDefaultResponse>) {
                if (response.isSuccessful) {
                    val res = response.body()!!
                    Log.d("__res", response.body()!!.toString())
                    when (res.code) {
                        1000 -> { //성공
                            Log.d("CardService-deleteCard", res.code.toString() + " : " + res.message)
                            serverView.onServerSuccess()
                        }
                        else -> { //의도된 실패
                            Log.d("CardService-deleteCard", res.code.toString() + " : " + res.message)
                            serverView.onServerFailure(res.code, res.message)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                serverView.onServerFailure(400, t.message.toString())
                Log.d("CardService-patchTitle", t.toString()) //네트워크 실패
            }
        })
    }


    //카드 세부내용 올리기
    //작성 완료한 카드 서버로 전송
    fun patchTitle(userIdx : String, courseIdx : String, title : String) {
        Log.d("CheckPoint : ", "CardService-patchTitle Activated")
        serverView.onServerLoading()
        val params: HashMap<String, Any> = HashMap()
        params["courseTitle"] = title

        val cardRetrofitService = retrofit.create(CardRetrofitInterface::class.java)
        cardRetrofitService.patchTitle(userIdx, courseIdx, params).enqueue(object : Callback<ServerDefaultResponse> {
            override fun onResponse(call: Call<ServerDefaultResponse>, response: Response<ServerDefaultResponse>) {
                if (response.isSuccessful) {
                    val res = response.body()!!
                    Log.d("__res", response.body()!!.toString())
                    when (res.code) {
                        1000 -> { //성공
                            Log.d("CardService-patchTitle", res.code.toString() + " : " + res.message)
                            serverView.onServerSuccess()
                        }
                        else -> { //의도된 실패
                            Log.d("CardService-patchTitle", res.code.toString() + " : " + res.message)
                            serverView.onServerFailure(res.code, res.message)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                serverView.onServerFailure(400, t.message.toString())
                Log.d("CardService-patchTitle", t.toString()) //네트워크 실패
            }
        })
    }

    fun patchBody(userIdx : String, courseIdx : String, body : String) {
        Log.d("CheckPoint : ", "CardService-patchBody Activated")
        serverView.onServerLoading()

        val params: HashMap<String, Any> = HashMap()
        params["courseComment"] = body

        val cardRetrofitService = retrofit.create(CardRetrofitInterface::class.java)
        cardRetrofitService.patchBody(userIdx, courseIdx, params).enqueue(object : Callback<ServerDefaultResponse> {
            override fun onResponse(call: Call<ServerDefaultResponse>, response: Response<ServerDefaultResponse>) {
                if (response.isSuccessful) {
                    val res = response.body()!!
                    Log.d("__res", response.body()!!.toString())
                    when (res.code) {
                        1000 -> { //성공
                            Log.d("CardService-patchBody", res.code.toString() + " : " + res.message)
                            serverView.onServerSuccess()
                        }
                        else -> { //의도된 실패
                            Log.d("CardService-patchBody", res.code.toString() + " : " + res.message)
                            serverView.onServerFailure(res.code, res.message)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                serverView.onServerFailure(400, t.message.toString())
                Log.d("CardService-patchBody", t.toString()) //네트워크 실패
            }
        })
    }

    fun patchImg(userIdx : String, courseIdx : String, img : String) {
        Log.d("CheckPoint : ", "CardService-patchImg Activated")
        serverView.onServerLoading()

        val params: HashMap<String, Any> = HashMap()
        params["courseImg"] = img

        val cardRetrofitService = retrofit.create(CardRetrofitInterface::class.java)
        cardRetrofitService.patchImg(userIdx, courseIdx, params).enqueue(object : Callback<ServerDefaultResponse> {
            override fun onResponse(call: Call<ServerDefaultResponse>, response: Response<ServerDefaultResponse>) {
                if (response.isSuccessful) {
                    val res = response.body()!!
                    Log.d("__res", response.body()!!.toString())
                    when (res.code) {
                        1000 -> { //성공
                            Log.d("CardService-patchImg", res.code.toString() + " : " + res.message)
                            serverView.onServerSuccess()
                        }
                        else -> { //의도된 실패
                            Log.d("CardService-patchImg", res.code.toString() + " : " + res.message)
                            serverView.onServerFailure(res.code, res.message)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<ServerDefaultResponse>, t: Throwable) {
                serverView.onServerFailure(400, t.message.toString())
                Log.d("CardService-patchImg", t.toString()) //네트워크 실패
            }
        })
    }
}
