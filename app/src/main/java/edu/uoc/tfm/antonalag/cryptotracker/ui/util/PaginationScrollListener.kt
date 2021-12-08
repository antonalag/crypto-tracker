package edu.uoc.tfm.antonalag.cryptotracker.ui.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Abstract class that allows to customize the onScroll method of a recycler view
 *  so that it has a behavior according to the needs of the place where its implemented
 */
abstract class PaginationScrollListener constructor() : RecyclerView.OnScrollListener() {

    private lateinit var layoutManager: RecyclerView.LayoutManager

    // Specific constructor for LinearLayoutManager
    constructor(layoutManager: LinearLayoutManager) : this() {
        this.layoutManager = layoutManager
    }

    // Specific constructor for StaggeredGridLayoutManager
    constructor(layoutManager: StaggeredGridLayoutManager) : this() {
        this.layoutManager = layoutManager
    }

    // Method get callback when user scroll the list
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        var firstVisibleItemPosition = 0

        when (layoutManager) {
            is StaggeredGridLayoutManager -> {
                val firstVisibleItemPositions =
                    (layoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(null)
                // Get maximum elements within the list
                firstVisibleItemPosition = firstVisibleItemPositions[0]
            }
            is LinearLayoutManager -> {
                firstVisibleItemPosition =
                    (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            }
        }

        if(!isLoading && !isLastPage) {
            if( visibleItemCount + firstVisibleItemPosition >= totalItemCount
                && firstVisibleItemPosition >= 0 ){
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()

    abstract val isLastPage: Boolean
    abstract val isLoading: Boolean


}