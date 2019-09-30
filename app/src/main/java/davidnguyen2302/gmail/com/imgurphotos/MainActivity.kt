package davidnguyen2302.gmail.com.imgurphotos

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import davidnguyen2302.gmail.com.imgurphotos.model.Photo
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*


/**
 * Home page class with search menu and view to display the images
 * @author Duc Nguyen
 */
class MainActivity : AppCompatActivity() {

    private val baseUrl: String = "https://api.imgur.com/3/gallery/search/time/?q="
    private val photos = arrayListOf<Photo>()
    private var searchTerm: String? = ""

    companion object {
        private val TAG by lazy { MainActivity::class.java.simpleName }
    }

    /**
     * Create the view of the Main page of the app
     * @param savedInstanceState is the state of the instance of the app
     * null at first run, and it will get used when the activity state changed
     * (e.g. orientation changed)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        render(photos)
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

            // Upon editing search term
            override fun onQueryTextChange(newText: String?): Boolean {
                // Hide textView when performing a search
                runOnUiThread {
                    textView.visibility = View.GONE
                }
                return false
            }

            // Upon submitting
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Make sure to always start the new search from empty list
                photos.clear()

                val url = baseUrl.plus(query)
                getRequest(url)
                searchTerm = query
                hideKeyboard()
                return true
            }
        })
    }

    /**
     * Hide keyboard from the view
     */
    private fun Activity.hideKeyboard() {
        hideKeyboard(
                if (currentFocus == null)
                    View(this)
                else
                    currentFocus
        )
    }

    /**
     * Helper method to hide the keyboard from view
     * @param view is the current view that the input is hidden from
     */
    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Create the GET request using the search input
     * @param url is the URL link that will be used to call the get request
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
                val items: JSONArray = data.getJSONArray("data")

                for (i in 0 until items.length()) {
                    var photo: Photo = object : Photo() {}
                    val item: JSONObject = items.getJSONObject(i)
                    if (item.getBoolean("is_album")) {
                        photo.id = item.getString("cover")
                    } else {
                        photo.id = item.getString("id")
                    }
                    photo.title = item.getString("title")
                    photos.add(photo)
                }
                // Only render photos if there are results
                if (photos.size > 0) {
                    runOnUiThread {
                        render(photos)
                    }
                } else {
                    runOnUiThread {
                        displayNoResult()
                    }
                }
            }
        })
    }

    /**
     * Set no result textView to visible when no result returned
     */
    private fun displayNoResult() {
        val noResult = "Your search for \"$searchTerm\" didn't return any results"
        textView.text = noResult
        textView.visibility = View.VISIBLE
    }

    /**
     * Set the adapter to the recyclerView to display the content
     * @param photos is the list of photos retrieved from the get request
     */
    private fun render(photos: List<Photo>) {
        recyclerView_photos.layoutManager = LinearLayoutManager(this)
        recyclerView_photos.adapter = MainAdapter(photos as ArrayList<Photo>, ::photoOnClick, this)
    }

    /**
     * Helper method to trigger action upon onClick on each photo
     * @param position is the location of the image in the list
     */
    private fun photoOnClick(position: Int) {
        Log.d(TAG, "CLICKED $position")

    }
}
