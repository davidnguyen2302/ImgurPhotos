package davidnguyen2302.gmail.com.imgurphotos

import android.content.Context

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import davidnguyen2302.gmail.com.imgurphotos.model.Photo

/**
 * MainAdapter class to setup RecyclerView
 * @author Duc Nguyen
 */
class MainAdapter(photoList : ArrayList<Photo>, private val mPos: (Int) -> Unit, private val context: Context)
    : RecyclerView.Adapter<MainAdapter.PhotoViewHolder>() {

    private val url: String = "https://i.imgur.com/"
    private val ext: String = ".jpg"

    private val mPhotoList = photoList

    /**
     * Return the size of the gallery requested
     */
    override fun getItemCount(): Int {
        return mPhotoList.size
    }

    /**
     * Inflate the layout and create ViewHolder with photo and text view attached
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewHolder
                = PhotoViewHolder(layoutInflater.inflate(R.layout.item_photo, parent, false))
        viewHolder.photo = viewHolder.view.findViewById(R.id.imageView_photo) as ImageView
        viewHolder.title = viewHolder.view.findViewById(R.id.textView_title) as TextView
        return viewHolder
    }

    /**
     * Load images from onto each ViewHolder based on position
     * @param holder is the item_photo layout for each image
     * @param position is the position of the populated image in the list
     */
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        Picasso.with(context).load(url.plus(mPhotoList[position].id.plus(ext))).into(holder.photo)
        holder.title.text = mPhotoList[position].title
        holder.onBind(position)
    }

    /**
     * PhotoViewHolder class with
     */
    inner class PhotoViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var photo: ImageView
        lateinit var title: TextView

        fun onBind(position: Int) {
            view.setOnClickListener{ mPos(position) }
        }
    }
}