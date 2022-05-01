package com.hcdisat.schools.ui.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hcdisat.schools.databinding.FragmentHomeBinding
import com.hcdisat.schools.models.School
import com.hcdisat.schools.ui.adapters.SchoolsAdapter
import com.hcdisat.schools.ui.state.RequestState
import com.hcdisat.schools.ui.state.RequestState.*
import com.hcdisat.schools.ui.viewmodels.SchoolsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: SchoolsViewModel by activityViewModels()

    private val schoolAdapter by lazy {
        SchoolsAdapter() {
            DetailsFragment.newDetailsFragment(it)
                .show(childFragmentManager, DetailsFragment.TAG)
        }
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.schoolList.apply { adapter = schoolAdapter }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.schoolData.observe(viewLifecycleOwner, ::handleResults)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleResults(schoolResults: RequestState) {
        when (schoolResults) {
            is LOADING -> shouldDisplayData(false)
            is ERROR -> {
                ErrorFragment.showError(
                    "Error ${schoolResults.throwable.localizedMessage}"
                ).show(childFragmentManager, ErrorFragment.TAG)
                binding.btnRetry.visibility = View.VISIBLE
            }
            is SUCCESS<*> ->
                schoolAdapter.setSchools(
                    (schoolResults.results as List<*>).filterIsInstance<School>()
                ).also {
                    shouldDisplayData(true)
                }
        }
    }

    private fun shouldDisplayData(flag: Boolean) {
        if (flag) {
            binding.schoolList.visibility = View.VISIBLE
            binding.loading.visibility = View.GONE
            binding.btnRetry.visibility = View.GONE
            return
        }

        binding.schoolList.visibility = View.GONE
        binding.btnRetry.visibility = View.GONE
        binding.loading.visibility = View.VISIBLE
    }
}
