package ru.watch.films.ui.main

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import okhttp3.*
import org.json.JSONObject
import ru.watch.films.R
import ru.watch.films.databinding.FragmentMainBinding
import ru.watch.films.model.DataBaseAdapter
import ru.watch.films.model.Film
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL


/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment(){

    private var _binding: FragmentMainBinding? = null
    var client = OkHttpClient()
    var films: ArrayList<Film> = ArrayList()
    private var adapter: DataBaseAdapter? =null
    private var filmAdapter: FilmsAdapter? = null
    private var mode:Boolean = true
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        arguments?.let {
            mode = it.getBoolean("mode")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = this.context?.let { DataBaseAdapter(it) }
        val searchText = view.findViewById<EditText>(R.id.searchView)
        val refreshView =view.findViewById<SwipeRefreshLayout>(R.id.refresh)
        refreshView.setOnRefreshListener {
            if (!mode) {
                films.clear()
                adapter?.open()
                adapter?.let { films.addAll(it.films) }
                adapter?.close()
                refreshView.isRefreshing = false
            }
        }
        val filmList = view.findViewById<ListView>(R.id.filmlist)
        if(!mode) {
            adapter?.open()
            adapter?.let { films.addAll(it.films) }
            adapter?.close()
        }
        filmAdapter = adapter?.let {
            FilmsAdapter(this.context,R.layout.list_element_film,films,
                it,mode
            )
        }
        filmList.adapter = filmAdapter
        view.findViewById<Button>(R.id.buttonSearch)?.setOnClickListener {
            val keyWord = searchText?.text?.toString()
            if(mode)
                run(keyWord)
            else
            {
                adapter?.open()
                films.clear()
                if(keyWord == null || keyWord.isEmpty()) adapter?.let { it1 -> films.addAll(it1.films) }
                else adapter?.let { it1 -> films.addAll(it1.search(keyWord)) }
                adapter?.close()
                filmAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun run(keyWord: String?) {
        val httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("kinopoiskapiunofficial.tech")
            .addPathSegment("api")
            .addPathSegment("v2.1")
            .addPathSegment("films")
            .addPathSegment("search-by-keyword")
            .addQueryParameter(
                "keyword",
                keyWord
            )
            .addQueryParameter(
                "page",
                "1"
            )
            .build()
        val request = Request.Builder()
                .get()
                .url(httpUrl)
                .addHeader("X-API-KEY", "c78b222f-7247-45ad-9e0f-eb68f9906234")
                .build()

        if (request != null) {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    val json=response.body()?.string()?.let { JSONObject(it) }
                    val jsonArray = json?.getJSONArray("films")
                    if (jsonArray != null) {
                        films.clear()
                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            var genre=""
                            var nameRu=""
                            if(item.has("nameRu"))
                                nameRu=item.get("nameRu").toString()
                            else if(item.has("nameEn"))
                                nameRu=item.get("nameEn").toString()
                            var yearOut=""
                            if(item.get("year").toString()!="")
                                yearOut=item.get("year").toString()
                            if(item.getJSONArray("genres").length()>0)
                                genre =  item.getJSONArray("genres").getJSONObject(0).get("genre").toString()
                            val stream = ByteArrayOutputStream()
                            var img :ByteArray?=null
                            if(item.get("posterUrlPreview").toString()!="null") {
                                BitmapFactory.decodeStream(
                                    URL(
                                        item.get("posterUrlPreview").toString()
                                    ).openStream()
                                ).compress(Bitmap.CompressFormat.PNG, 90, stream)
                                img= stream.toByteArray()
                            }

                                 films.add(Film(i,nameRu, yearOut ,
                                    genre,
                                     existNullDouble(item.get("rating").toString()),
                                     img
                                ))
                            activity?.runOnUiThread { filmAdapter?.notifyDataSetChanged() }

                        }
                    }
                }
            })
        }
    }
    fun existNullDouble(stroke:String):Double{
        return if(stroke == "null")
            0.0
        else stroke.toDouble()
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val MODE = "mode"

        @JvmStatic
        fun newInstance(sectionNumber: Int,mode:Boolean): PlaceholderFragment {

            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                    putBoolean(MODE, mode)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}