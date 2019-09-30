package davidnguyen2302.gmail.com.imgurphotos

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import davidnguyen2302.gmail.com.imgurphotos.model.Photo
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.widget.AbsListView

/**
 * Home page class with search menu and view to display the images
 * @author Duc Nguyen
 */
class MainActivity : AppCompatActivity() {

    private val baseUrl: String = "https://api.imgur.com/3/gallery/search/time/"
    private var pageNum: Int = 1 // Default to 1
    private val queryUrl: String = "?q="
    private var searchTerm: String? = ""
    private var maxSize: Boolean = false
    private var isScrolling: Boolean = false
    private var currentItems: Int = 0
    private var totalItems: Int = 0
    private var scrolledOutItems: Int = 0

    companion object {
        private val TAG by lazy { MainActivity::class.java.simpleName }
        val photos = arrayListOf<Photo>()
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

    override fun onResume() {
        super.onResume()
        layout_root.requestFocus()
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
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            // Upon editing search term
            override fun onQueryTextChange(newText: String?): Boolean {
                pageNum = 1 // Reset page number
                // Hide textView when performing a search
                runOnUiThread {
                    textView.visibility = View.GONE
                    recyclerView_photos.visibility = View.GONE
                    recyclerView_photos.adapter.notifyDataSetChanged()
                }
                // Clear the list
                photos.clear()
                return false
            }

            // Upon submitting
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Make sure to always start the new search from empty list
                photos.clear()
                // Assign query -> searchTerm var in case of result not found or request next page
                searchTerm = query
                getRequest(updateURL(searchTerm))
                hideKeyboard()
                return true
            }
        })
    }

    /**
     * Update the URL with page number and search term
     * @param query is the search term
     * @return the updated URL
     */
    private fun updateURL(query: String?) : String {
        // Concatenate the base URL + page number + query URL + search term
        return baseUrl.plus(pageNum.toString()).plus(queryUrl).plus(query)
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
        setView(progress, recyclerView_photos, textView)
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
                // Clear out old data
                photos.clear()
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
                // Set flag if max size of the page
                maxSize = (photos.size == 60)

                // Only render photos if there are results
                if (photos.size > 0) {
                    Log.d(TAG, "##HAS RESULTS")
                    runOnUiThread {
                        render(photos)
                        recyclerView_photos.adapter.notifyDataSetChanged()
                        setView(recyclerView_photos, textView, progress)
                    }
                } else {
                    Log.d(TAG, "##NO RESULTS")
                    runOnUiThread {
                        displayNoResult()
                    }
                }
            }
        })
    }

    /**
     * Display one view at a time and hide the others
     * @param display the view to be seen
     * @param hide the view to be hid
     * @param hide2 the view to be hid
     */
    private fun setView(display: View, hide: View, hide2: View) {
        hide.visibility = View.GONE
        hide2.visibility = View.GONE
        display.visibility = View.VISIBLE
    }

    /**
     * Set no result textView to visible when no result returned
     */
    private fun displayNoResult() {
        val noResult = "Your search for \"$searchTerm\" didn't return any results"
        textView.text = noResult
        setView(textView, recyclerView_photos, progress)
    }

    /**
     * Set the adapter to the recyclerView to display the content
     * @param photos is the list of photos retrieved from the get request
     */
    private fun render(photos: List<Photo>) {
        val manager = LinearLayoutManager(this)
        recyclerView_photos.layoutManager = manager
        recyclerView_photos.adapter = MainAdapter(photos as ArrayList<Photo>, ::photoOnClick, this)
        recyclerView_photos.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentItems = manager.childCount
                scrolledOutItems = manager.findFirstVisibleItemPosition()
                totalItems = manager.itemCount
                // If user scrolled to the bottom and there's more page -> get the new data
                if (maxSize && isScrolling && (currentItems + scrolledOutItems == totalItems)) {
                    isScrolling = false
                    pageNum++
                    getRequest(updateURL(searchTerm))
                }
            }
        })
    }

    /**
     * Helper method to launch image in fullscreen upon onClick on a photo
     * @param position is the location of the image in the list
     */
    private fun photoOnClick(position: Int) {
        val intent = Intent(this@MainActivity,
                FullscreenActivity::class.java)
        intent.putExtra("img", position)
        startActivity(intent)
        Log.d(TAG, "##CLICKED $position")
    }
}
