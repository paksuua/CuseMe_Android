package com.tistory.comfy91.excuseme_android.api


import com.tistory.comfy91.excuseme_android.data.answer.*
import com.tistory.comfy91.excuseme_android.data.request.*
import com.tistory.comfy91.excuseme_android.data.server.BodyChangeVisibility
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import com.tistory.comfy91.excuseme_android.data.request.BodyChangePw as BodyChangePw1

interface Service{

    // region 카드
    /**
     * 전체 카드 조회
     * @param token : 토큰
     */
    @GET("/cards/")
    fun getAllCards(
        @Header("token") token: String
    ): Call<ResCards>


    /**
     * 카드 상세 조회
     * @param token: 토큰
     * @param cardIdx: 카드 인덱스 번호
     */
    @GET("/cards/{cardIdx}")
    fun getCardDetail(
        @Header("token") token: String,
        @Path("cardIdx") cardIdx: String
    ): Call<ResCardDetail>


    /**
     * 발달 장애인이 보는 카드 전체 조회
     *  @param uuid : UUID
     */
    @POST("/cards/visible")
    fun getDisabledCards(
        @Body uuid: BodyGetDisabledCard
    ):Call<ResCards>


    /**
     * 카드 추가
     * @param token
     * @param cardBean = 추가할 카드
     */
    @Multipart
    @POST("/cards")
    fun addCard(
        @Header("token") token: String,
        @Part image: MultipartBody.Part,
        @Part record: MultipartBody.Part?,
        @Part("title") title: RequestBody,
        @Part("content") desc : RequestBody,
        @Part("visible") visibility: RequestBody
    ): Call<ResAddCard>

    /**
     * 카드 상세 수정
     * @param token
     * @param cardBean = 수정할 카드
     */
    @Multipart
    @PUT("/cards/{cardIdx}")
    fun editCardDetail(
        @Header("token") token: String,
        @Path("cardIdx") cardIdx: String,
        @Part("title") title: RequestBody,
        @Part("content") desc : RequestBody,
        @Part("visible") visibility: Boolean,
        @Part image: MultipartBody.Part?,
        @Part record: MultipartBody.Part?
    ): Call<ResDownCard>

    @DELETE("/cards/{cardIdx}")
    fun deleteCard(
        @Header("token") token: String,
        @Path ("cardIdx") cardIdx: String
    ): Call<ResCards>


    /**
     * 카드 다운받기
     */
    @POST("/cards/{serialNum}")
    fun downCard(
        @Header("token") token: String,
        @Path("serialNum") serialNum: String
    ): Call<ResDownCard>

    /**
     * 카드 클릭 횟수 증가
     */
    @PUT("/cards/{cardIdx}/count")
    fun incCardCount(
        @Header("token") token: String,
        @Path("cardIdx") cardIdx: String
    ): Call<ResCards>
    // endregion 카드

    // region 유저(User)
    /**
     * 앱 시작
     * @param bodyStartApp.uuid
     * = uuid로 기존 사용자인지 새로운 사용자 인지 체크,
     * 새로운 사용자일 때 DB에 저장됨
     */
    @POST("/auth/start")
    fun startApp(
        @Body bodyStartApp: BodyStartApp
    ): Call<ResUser>

    /**
     * @param uuid: 보호자 모드로 넘어갈 때 uuid를 받아서 토큰을 생성
     * @param password: 보호자(사용자) 비밀번호
     */
    @POST("/auth/signin")
    fun helperSignIn(
        @Body bodyHelperSingIn: BodyHelperSignIn
    ): Call<ResponseLogin>



    /**
     * 비밀번호 변경
     */
    @PUT("/auth")
    fun changePw(
        @Header("token") token: String,
        @Body bodyChangePw: BodyChangePw1
    ):Call<ResUser>



    /**
     * 보호자 전화변경
     * @param token
     * @param bodyChangePhoneNum.phoneNum = 변경할 전화번호
     */
    @PUT("/auth/phone")
    fun changePhoneNum(
        @Header("token") token: String,
        @Body bodyChangePhoneNum: BodyChangePhoneNum
    ): Call<ResUser>

    /**
     * 카드 전체 수정
     */
    @PUT("/cards/")
    fun changeAllCards(
        @Header("token") token: String,
        @Body changeAllCards: BodyChangeAllCards
    ): Call<ResCards>

    /**
     * TODO: 카드 개별 숨김
     */
    @PUT("/cards/{cardIdx}/hide")
    fun changeVisibility(
        @Header("token") token: String,
        @Body changeVisibility: BodyChangeVisibility,
        @Path ("cardIdx") cardIdx: String
    ): Call<ResCards>
    // endregion
}