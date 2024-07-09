package com.example.madcamp2_frontend.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.madcamp2_frontend.databinding.FragmentTotalRankingBinding
import com.example.madcamp2_frontend.model.network.UserInfo
import com.example.madcamp2_frontend.view.adapter.RankingAdapter
import com.example.madcamp2_frontend.viewmodel.RankingViewModel

class TotalRankingFragment : Fragment() {

    private var _binding: FragmentTotalRankingBinding? = null
    private val binding get() = _binding!!

    private val rankingViewModel: RankingViewModel by viewModels()
    private lateinit var adapter: RankingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTotalRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        rankingViewModel.fetchUserTotalRankings()
    }

    private fun setupRecyclerView() {
        adapter = RankingAdapter(isTotalRanking = true)
        binding.rankingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.rankingRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        rankingViewModel.userTotalRankings.observe(viewLifecycleOwner, Observer { totalRankings ->
            if (totalRankings != null) {
                Log.d("TotalRankingFragment", "Received user total rankings: $totalRankings")
                adapter.submitList(totalRankings)
            } else {
                Log.e("TotalRankingFragment", "User total rankings are null")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
