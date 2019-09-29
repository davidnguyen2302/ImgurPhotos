package davidnguyen2302.gmail.com.imgurphotos

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG by lazy { MainActivity::class.java.simpleName }
    }
    private val baseUrl: String = "https://api.imgur.com/3/gallery/search/time/"
    private val tempUrl: String = "https://api.imgur.com/3/gallery/search/time/1?q=cats"
    private var results: String? = ""

    /**
     * Create the view of the Main page of the app
     * @param savedInstanceState is the state of the instance of the app
     * null at first run, and it will get used when the activity state changed
     * (e.g. orientation changed)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




//        val client = OkHttpClient()
//
//        val request = Request.Builder()
//                .url(tempUrl)
//                .get()
//                .addHeader("authorization", "Client-ID 126701cd8332f32")
//                .build()
//
//        val response = client.newCall(request).enqueue(object: Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.stackTrace
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                result = response.body?.string()
//                runOnUiThread {
//                    textView.text = result
//                }
//            }
//        })





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
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                runOnUiThread {
                    textView.text = query
                }
                return true
            }
        })
    }

}
