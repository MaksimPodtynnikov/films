package ru.watch.films.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase


class DataBaseAdapter(context: Context) {
    private val dbHelper: DataBaseHelper
    private var database: SQLiteDatabase? = null

    init {
        dbHelper = DataBaseHelper(context.applicationContext)
    }

    fun open(): DataBaseAdapter {
        database = dbHelper.writableDatabase
        return this
    }

    fun close() {
        dbHelper.close()
    }

    private val allEntriesFilm: Cursor
        get() {
            val columns = arrayOf(
                DataBaseHelper.COLUMN_ID,DataBaseHelper.COLUMN_TITLE,DataBaseHelper.COLUMN_GENRE,DataBaseHelper.COLUMN_RATING,
                DataBaseHelper.COLUMN_YEAR, DataBaseHelper.COLUMN_POSTER
            )
            return database!!.query(
                DataBaseHelper.TABLE_FILM,
                columns,
                null,
                null,
                null,
                null,
                DataBaseHelper.COLUMN_ID + " ASC"
            )
        }

    val films: ArrayList<Film>
        @SuppressLint("SimpleDateFormat")
        get() {
            val films = ArrayList<Film>()
            val cursor = allEntriesFilm
            while (cursor.moveToNext()) {
                @SuppressLint("Range") val id =
                    cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COLUMN_ID))
                @SuppressLint("Range") val title =
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_TITLE))
                @SuppressLint("Range") val genre =
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_GENRE))
                @SuppressLint("Range") val poster =
                    cursor.getBlob(cursor.getColumnIndex(DataBaseHelper.COLUMN_POSTER))
                @SuppressLint("Range") val rating =
                    cursor.getDouble(cursor.getColumnIndex(DataBaseHelper.COLUMN_RATING))
                @SuppressLint("Range") val year =
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_YEAR))
                    films.add(Film(id,title,year,genre,rating,poster))
            }
            cursor.close()
            return films
        }
    @SuppressLint("SimpleDateFormat")
    fun getFilm(id:Int):Film?{
        var film: Film? = null
        val query = String.format(
            "SELECT * FROM %s WHERE %s=?",
            DataBaseHelper.TABLE_FILM,
            DataBaseHelper.COLUMN_ID
        )
        val cursor = database!!.rawQuery(query, arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") val title =
                cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_TITLE))
            @SuppressLint("Range") val poster =
                cursor.getBlob(cursor.getColumnIndex(DataBaseHelper.COLUMN_POSTER))
            @SuppressLint("Range") val rating =
                cursor.getDouble(cursor.getColumnIndex(DataBaseHelper.COLUMN_RATING))
            @SuppressLint("Range") val genre =
                cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_GENRE))
            @SuppressLint("Range") val year =
                cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_YEAR))
            film = Film(id,title,year,genre,rating,poster)
        }
        cursor.close()
        return film
    }
    fun insertFilm(film: Film): Long {
        val cv = ContentValues()
        cv.put(DataBaseHelper.COLUMN_TITLE, film.title)
        cv.put(DataBaseHelper.COLUMN_GENRE, film.genre)
        cv.put(DataBaseHelper.COLUMN_RATING, film.rating)
        cv.put(DataBaseHelper.COLUMN_YEAR, film.yearOut)
        cv.put(DataBaseHelper.COLUMN_POSTER, film.poster)
        return database!!.insert(DataBaseHelper.TABLE_FILM, null, cv)
    }
    fun search(text: String): ArrayList<Film> {
        val films: ArrayList<Film> = ArrayList()
        val cursor = database?.rawQuery(
            "select * from " + DataBaseHelper.TABLE_FILM + " where " +
                    DataBaseHelper.COLUMN_TITLE + " LIKE ? OR " + DataBaseHelper.COLUMN_GENRE +
                    " LIKE ? OR " + DataBaseHelper.COLUMN_YEAR+ " LIKE ? ",
            arrayOf(
                "%$text%", "%$text%", "%$text%"
            )
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") val id =
                    cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COLUMN_ID))
                @SuppressLint("Range") val title =
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_TITLE))
                @SuppressLint("Range") val genre =
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_GENRE))
                @SuppressLint("Range") val poster =
                    cursor.getBlob(cursor.getColumnIndex(DataBaseHelper.COLUMN_POSTER))
                @SuppressLint("Range") val rating =
                    cursor.getDouble(cursor.getColumnIndex(DataBaseHelper.COLUMN_RATING))
                @SuppressLint("Range") val year =
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_YEAR))
                films.add(Film(id,title,year,genre,rating,poster))
            }
        }
        cursor?.close()
        return films
    }
    fun deleteFilm(film: Film):Long{
        val whereClause = "id = ?"
        val whereArgs = arrayOf(film.id.toString())
        return database!!.delete(DataBaseHelper.TABLE_FILM, whereClause, whereArgs).toLong()
    }
}