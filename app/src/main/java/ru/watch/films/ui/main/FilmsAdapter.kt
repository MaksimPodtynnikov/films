package ru.watch.films.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import ru.watch.films.R
import ru.watch.films.model.DataBaseAdapter
import ru.watch.films.model.Film

class FilmsAdapter (context: Context?, private val layout: Int, val films: ArrayList<Film>, adapter: DataBaseAdapter,mode: Boolean) :
    ArrayAdapter<Film?>(context!!, layout, films as List<Film?>) {
    private val inflater: LayoutInflater
    private val adapterDB: DataBaseAdapter = adapter
    private val modeWork = mode
    init {
        inflater = LayoutInflater.from(context)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        @SuppressLint("ViewHolder") val view = inflater.inflate(layout, parent, false)
        val titleView = view.findViewById<TextView>(R.id.titleView)
        val yearView = view.findViewById<TextView>(R.id.dateOutView)
        val genreView = view.findViewById<TextView>(R.id.genreView)
        val ratingView = view.findViewById<TextView>(R.id.ratingView)
        val ratingStarView = view.findViewById<ImageView>(R.id.ratingStarView)
        val posterView = view.findViewById<ImageView>(R.id.posterView)
        val addButton = view.findViewById<Button>(R.id.addButton)
        if(!modeWork)
            addButton.text = "Удалить"
        val film = films[position]
        addButton.setOnClickListener {
            adapterDB.open()
            if(modeWork)
                adapterDB.insertFilm(film)
            else {
                adapterDB.deleteFilm(film)
                films.remove(film)
                this.notifyDataSetChanged()
            }
            adapterDB.close()
        }
        titleView.text = film.title
        yearView.text = film.yearOut
        genreView.text = film.genre
        ratingView.text = film.rating.toString()
        posterView.setImageBitmap(film.image)
        if(film.rating<4) ratingStarView.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(0xFFFF0000.toInt(), BlendModeCompat.SRC_ATOP)
        else if(film.rating<7) ratingStarView.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(0xFFFFFF00.toInt(), BlendModeCompat.SRC_ATOP)
        else ratingStarView.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(0xFF00FF00.toInt(), BlendModeCompat.SRC_ATOP)
        return view
    }
}