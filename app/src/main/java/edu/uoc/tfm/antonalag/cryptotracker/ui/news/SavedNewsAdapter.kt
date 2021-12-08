package edu.uoc.tfm.antonalag.cryptotracker.ui.news

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.LocalNews
import kotlinx.android.synthetic.main.saved_news_list_item_swipe_view.view.*
import java.lang.ClassCastException

class SavedNewsAdapter(private val context: Context): ListAdapter<LocalNews, SavedNewsAdapter.SavedNewsViewHolder>(
    SavedNewsDiffCallback
) {

    private lateinit var listener: SavedNewsApadterClickListener
    private val viewBinderHelper = ViewBinderHelper()

    /**
     * The activity that creates an instance of this Adapter must
     * implement this interface in order to receive event callbacks. Each
     * method passes the id of the element selected
     */
    interface SavedNewsApadterClickListener{
        fun onDeleteClickListener(localNewsId: Long)
        fun onClickListener(localNewsUrl: String, localNewsTitle: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedNewsViewHolder {
        return SavedNewsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.saved_news_list_item_swipe_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SavedNewsViewHolder, position: Int) {
        // Set swipe properties
        viewBinderHelper.setOpenOnlyOne(true)
        viewBinderHelper.bind(holder.itemView.saved_news_list_item_swipe, getItem(position).id.toString())
        viewBinderHelper.closeLayout(getItem(position).id.toString())
        holder.bindTo(getItem(position))
    }

    inner class SavedNewsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindTo(localNews: LocalNews) {
            // Set news title
            itemView.saved_news_title.text = localNews.title
            // Set on click listeners
            itemView.delete_saved_news.setOnClickListener {
                listener.onDeleteClickListener(localNews.id)
            }
            itemView.saved_news_detail.setOnClickListener {
                listener.onClickListener(localNews.url, localNews.title)
            }

        }
    }

    /**
     * Override onAattach method to instantiate DeleteCLickListener
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        // Verify that the host acitivity implements the callback interface
        try {
            // Instantiate the DeleteCLickListener to be able to send events to the host
            listener = context as SavedNewsApadterClickListener
        } catch( e: ClassCastException ) {
            throw ClassCastException("$context must implement DeleteCLickListener")
        }
    }

    companion object {
        private val SavedNewsDiffCallback =
            object: DiffUtil.ItemCallback<LocalNews>() {
                override fun areItemsTheSame(
                    oldItem: LocalNews,
                    newItem: LocalNews
                ): Boolean {
                    return oldItem.title == newItem.title
                }

                override fun areContentsTheSame(
                    oldItem: LocalNews,
                    newItem: LocalNews
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

}