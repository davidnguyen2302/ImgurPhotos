package davidnguyen2302.gmail.com.imgurphotos

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import com.squareup.picasso.Picasso
import davidnguyen2302.gmail.com.imgurphotos.model.Photo
import kotlinx.android.synthetic.main.activity_fullscreen.*

/**
 * Fullscreen activity that display the image selected in fullscreen
 */
class FullscreenActivity : Activity() {
    private val url: String = "https://i.imgur.com/"
    private val ext: String = ".jpg"
    private val mPhotoList: List<Photo> = MainActivity.photos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        // Set the status bar to transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }

        // Set image and title
        val position = intent.getIntExtra("img", 0)
        Picasso.with(this).load(url.plus(mPhotoList[position].id.plus(ext)))
                                 .into(imageView_fullscreen)
        textView_fullscreen_title.text = mPhotoList[position].title
        back_btn.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })
    }
}
