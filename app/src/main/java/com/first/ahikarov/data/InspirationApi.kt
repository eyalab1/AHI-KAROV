package com.first.ahikarov.data

import com.first.ahikarov.data.models.DeezerResponse
import com.first.ahikarov.data.models.PicsumImage
import com.first.ahikarov.data.models.QuoteResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// תפריט
interface InspirationApiService {

    // ציטוטים
    @GET("quotes")
    suspend fun getQuotes(): List<QuoteResponse>

    // חיפוש שירים
    @GET("search")
    suspend fun searchMusic(@Query("q") query: String): DeezerResponse

    // מצעד שירים (בקשה שונה)
    @GET("artist/13/top")
    suspend fun getArtistTopTracks(): DeezerResponse

    //  תמונות
    @GET("v2/list?limit=30")
    suspend fun getImages(): List<PicsumImage>
}

// החיבורים
object RetrofitClient {

    // חיבור לציטוטים
    private val retrofitQuotes = Retrofit.Builder()
        .baseUrl("https://zenquotes.io/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val quotesApi: InspirationApiService = retrofitQuotes.create(InspirationApiService::class.java)

    // חיבור למוסיקה
    private val retrofitMusic = Retrofit.Builder()
        .baseUrl("https://api.deezer.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val musicApi: InspirationApiService = retrofitMusic.create(InspirationApiService::class.java)

    //  חיבור  לתמונות
    private val retrofitImages = Retrofit.Builder()
        .baseUrl("https://picsum.photos/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val imagesApi: InspirationApiService = retrofitImages.create(InspirationApiService::class.java)
}