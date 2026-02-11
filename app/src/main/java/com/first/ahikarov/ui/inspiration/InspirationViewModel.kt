package com.first.ahikarov.ui.inspiration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.first.ahikarov.data.InspirationApiService
import com.first.ahikarov.data.models.ImageItem
import com.first.ahikarov.data.models.QuoteResponse
import com.first.ahikarov.data.models.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class InspirationViewModel @Inject constructor(
    @param:Named("QuotesApi") private val quotesApi: InspirationApiService,
    @param:Named("MusicApi") private val musicApi: InspirationApiService,
    @param:Named("ImagesApi") private val imagesApi: InspirationApiService
) : ViewModel() {

    private val _quotes = MutableLiveData<List<QuoteResponse>>()
    val quotes: LiveData<List<QuoteResponse>> = _quotes

    private val _images = MutableLiveData<List<ImageItem>>()
    val images: LiveData<List<ImageItem>> = _images

    private val _music = MutableLiveData<List<Track>>()
    val music: LiveData<List<Track>> = _music

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // ציטוטים (Quotes)
    fun fetchQuotes() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // שימוש ב-API שהוזרק
                val response = quotesApi.getQuotes()
                _quotes.postValue(response)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error fetching quotes: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // תמונות (Images)
    fun fetchImages() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // שימוש ב-API שהוזרק (Picsum)
                val response = imagesApi.getImages()

                // המרה למודל התצוגה
                val imageItems = response.map { picsumImage ->
                    ImageItem(picsumImage.download_url)
                }
                _images.postValue(imageItems)

            } catch (e: Exception) {
                Log.e("API_ERROR", "Error fetching images: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    //  להיטים (Top Tracks)
    fun fetchTopTracks() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // שימוש ב-API שהוזרק (Deezer)
                val response = musicApi.getArtistTopTracks()
                _music.postValue(response.data)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error fetching top tracks: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    // חיפוש שירים (Search Music)
    fun fetchMusic(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // שימוש ב-API שהוזרק (Deezer)
                val response = musicApi.searchMusic(query)
                _music.postValue(response.data)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error searching music: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}