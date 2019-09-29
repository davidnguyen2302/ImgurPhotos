package davidnguyen2302.gmail.com.imgurphotos

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //Inflate tehe menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
