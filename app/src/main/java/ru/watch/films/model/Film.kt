package ru.watch.films.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.time.Year

class Film(
    var id:Int=0,
    var title:String = "",
    var yearOut: String = "",
    var genre: String = "",
    var rating: Double = 0.0,
    var poster:ByteArray? = null,
) {
    val image: Bitmap?
        get() = if (poster != null) BitmapFactory.decodeByteArray(
            poster,
            0,
            poster!!.size
        ) else null
}