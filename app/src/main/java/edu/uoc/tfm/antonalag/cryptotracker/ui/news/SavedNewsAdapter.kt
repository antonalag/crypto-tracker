package edu.uoc.tfm.antonalag.cryptotracker.ui.news

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.LocalNews
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsListViewResultDto
import kotlinx.android.synthetic.main.saved_news_list_item_swipe_view.view.*
import java.lang.ClassCastException

class SavedNewsAdapter(private val context: Context): ListAdapter<LocalNews, SavedNewsAdapter.SavedNewsViewHolder>(
    SavedNewsDiffCallback
) {

    private lateinit var listener: DeleteClickListener
    private val viewBinderHelper = ViewBinderHelper()

    /**
     * The activity that creates an instance of this Adapter must
     * implement this interface in order to receive event callbacks. Each
     * method passes the id of the element selected
     */
    interface DeleteClickListener{
        fun onDeleteClickListener(localNewsId: Long)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedNewsViewHolder {
        return SavedNewsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.saved_news_list_item_swipe_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SavedNewsViewHolder, position: Int) {
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
            itemView.setOnClickListener {
                val intent = Intent(it.context, NewsDetailView::class.java).apply {
                    putExtra("url", localNews.url)
                    putExtra("title", localNews.title)
                }
                it.context.startActivity(intent)
            }

        }
    }

    // Override onAattach method to instantiate DeleteCLickListener
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        // Verify that the host acitivity implements the callback interface
        try {
            // Instantiate the DeleteCLickListener to be able to send events to the host
            listener = context as DeleteClickListener
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