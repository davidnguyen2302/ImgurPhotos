package davidnguyen2302.gmail.com.imgurphotos

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_photo.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * Home page class with search menu and view to display the images
 * @author Duc Nguyen
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG by lazy { MainActivity::class.java.simpleName }
    }

    private val baseUrl: String = "https://api.imgur.com/3/gallery/search/time/1?q="
    private var results: String? = ""
    private var gallery: String = "" //TODO: Remove after testing

    /**
     * Create the view of the Main page of the app
     * @param savedInstanceState is the state of the instance of the app
     * null at first run, and it will get used when the activity state changed
     * (e.g. orientation changed)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    /**
     * Create searchView and inflate the menu
     * @param menu is the menu item
     * @return return true to add this menu always to MainActivity
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)

        createSearchView(menu)

        return true
    }

    /**
     * Helper method to create the Search field in the menu bar
     * @param menu is the searchView menu item
     */
    private fun createSearchView(menu: Menu) {
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val url = baseUrl.plus(query)
                getRequest(url)
                return true
            }
        })
    }

    /**
     * Create the GET request using the search input
     */
    private fun getRequest(url: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("authorization", "Client-ID 126701cd8332f32")
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "An error has occurred ${e.stackTrace}")
            }

            override fun onResponse(call: Call, response: Response) {
                val data = JSONObject(response.body?.string())
                if (data != null) {
                    val items: JSONArray = data.getJSONArray("data")
                    val photos = arrayListOf<Photo>()

                    for (i in 0 until items.length()) {
                        var photo: Photo = object : Photo() {}
                        val item: JSONObject = items.getJSONObject(i)
                        photo.id = item.getString("id")
                        photo.title = item.getString("title")

                        photos.add(photo)
                    }
                    Log.d(TAG, "IMAGES SIZE: ${photos.size}")
                    for (i in 0 until photos.size) {
                        println("IMAGES $i: ${photos[i].id}, ${photos[i].title}")
                    }
                } else {
                    Log.d(TAG, "$data is null")
                }
            }
        })

    }
}
