package com.mashood.thesaurus.search.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mashood.thesaurus.search.domain.model.SearchResponse
import com.mashood.thesaurus.search.ui.meaning.ResultMeaningsFragment

class MeaningViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val meaningsList: List<SearchResponse.MeaningModel>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return meaningsList.size
    }

    override fun createFragment(position: Int): Fragment {
        return ResultMeaningsFragment(meaningsList[position])
    }
}
