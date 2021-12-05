package edu.uoc.tfm.antonalag.cryptotracker.ui.news

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.uoc.tfm.antonalag.cryptotracker.R
import edu.uoc.tfm.antonalag.cryptotracker.features.cryptocurrency.model.CryptocurrencyListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsListViewDto
import edu.uoc.tfm.antonalag.cryptotracker.features.news.model.NewsListViewResultDto
import edu.uoc.tfm.antonalag.cryptotracker.ui.cryptocurrency.CryptocurrencyStatisticsView
import edu.uoc.tfm.antonalag.cryptotracker.ui.register.RegisterView
import kotlinx.android.synthetic.main.news_list_item_view.view.*


class NewsAdapter() : ListAdapter<NewsListViewResultDto, NewsAdapter.NewsViewHolder>(
    NewsListViewResultDtoDiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.news_list_item_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    fun clearData() {
        currentList.clear()
    }

    inner class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindTo(newsListViewResultDto: NewsListViewResultDto) {
            itemView.news_title.text = newsListViewResultDto.title
            itemView.setOnClickListener {
                val intent = Intent(it.context, NewsDetailView::class.java).apply {
                    putExtra("url", newsListViewResultDto.url)
                    putExtra("title", newsListViewResultDto.title)
                }
                it.context.startActivity(intent)
            }
        }
    }

    companion object {
        private val NewsListViewResultDtoDiffCallback =
            object : DiffUtil.ItemCallback<NewsListViewResultDto>() {
                override fun areItemsTheSame(
                    oldItem: NewsListViewResultDto,
                    newItem: NewsListViewResultDto
                ): Boolean {
                    return oldItem.title == newItem.title
                }

                override fun areContentsTheSame(
                    oldItem: NewsListViewResultDto,
                    newItem: NewsListViewResultDto
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

}