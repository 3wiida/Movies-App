package com.ewida.rickmorti.ui.home.fragments.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ewida.rickmorti.R
import com.ewida.rickmorti.databinding.TrendingLoadingStateLayoutBinding

class SearchLoadStateAdapter:LoadStateAdapter<SearchLoadStateAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val binding: TrendingLoadingStateLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(loadState: LoadState){
            binding.trendingMovieShimmer.isVisible= loadState is LoadState.Loading
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ItemViewHolder {
        val binding=TrendingLoadingStateLayoutBinding.bind(LayoutInflater.from(parent.context).inflate(
            R.layout.trending_loading_state_layout,parent,false))
        return ItemViewHolder(binding)
    }
}