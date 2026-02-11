package com.first.ahikarov.data.models

// מודל לתצוגה ברשימה (מה שהאדפטר מכיר ומציג)
data class ImageItem(
    val imageUrl: String
)

// השרת שולח  רשימה של אובייקטים כאלה
data class PicsumImage(
    val id: String,
    val author: String,
    val download_url: String // הקישור לתמונה בגודל מלא
)

// מודל לציטוט (ZenQuotes)
data class QuoteResponse(
    val q: String, // הציטוט
    val a: String  // המחבר
)

// מודל לשיר (Deezer)
data class Track(
    val title: String,
    val artist: Artist,
    val album: Album,
    val preview: String // לינק להשמעה
)

data class Artist(val name: String)

data class Album(val cover_medium: String)

data class DeezerResponse(
    val data: List<Track>
)