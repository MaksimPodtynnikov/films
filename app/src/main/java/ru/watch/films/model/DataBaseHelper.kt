package ru.watch.films.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream

class DataBaseHelper(var context: Context) : SQLiteOpenHelper(
    context, DB_NAME, null, SCHEMA
) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE " + TABLE_FILM + " (" + COLUMN_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TITLE
                    + " CHAR(100), " + COLUMN_GENRE + " CHAR(100), " + COLUMN_RATING
                    + " INTEGER, " + COLUMN_YEAR + " CHAR(4), "+ COLUMN_POSTER+" BLOB NULL)"
        )
    }

    private fun convertToBlob(bitmap: Bitmap?): ByteArray?
    {
        return if (bitmap != null) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            bitmap.recycle()
            squeeze(byteArray)
        } else null
    }
    private fun squeeze(image: ByteArray?): ByteArray? {
        var img = image
        return if (img != null) {
            while (img!!.size > 500000) {
                val bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)
                val resized = Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * 0.8).toInt(),
                    (bitmap.height * 0.8).toInt(),
                    true
                )
                val stream = ByteArrayOutputStream()
                resized.compress(Bitmap.CompressFormat.PNG, 100, stream)
                img = stream.toByteArray()
            }
            img
        } else null
    }
    private fun insertFilms(db: SQLiteDatabase, film: Film) {
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, film.title)
        cv.put(COLUMN_RATING, film.rating)
        cv.put(COLUMN_POSTER, film.poster)
        cv.put(COLUMN_GENRE, film.genre)
        cv.put(COLUMN_YEAR, film.yearOut)
        db.insert(TABLE_FILM, null, cv)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FILM")
        onCreate(db)
    }

    companion object {
        private const val DB_NAME = "tours.db"
        private const val SCHEMA = 1 // версия базы данных
        const val TABLE_FILM = "film" // название таблицы в бд
        // названия столбцов
        const val COLUMN_ID = "id"
        const val COLUMN_RATING = "rating"
        const val COLUMN_TITLE = "title"
        const val COLUMN_GENRE = "genre"
        const val COLUMN_YEAR = "year"
        const val COLUMN_POSTER = "poster"
    }
}