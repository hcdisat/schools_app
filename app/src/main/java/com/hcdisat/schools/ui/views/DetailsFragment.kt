package com.hcdisat.schools.ui.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.hcdisat.schools.databinding.FragmentDetailsBinding
import com.hcdisat.schools.models.SchoolDetails
import com.hcdisat.schools.ui.viewmodels.SchoolResults
import com.hcdisat.schools.ui.viewmodels.SchoolsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : DialogFragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SchoolsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        binding.btnClose.setOnClickListener { dismissAllowingStateLoss() }

        viewModel.schoolDetails.observe(viewLifecycleOwner, ::handleDetails)

        return binding.root
    }

    private fun handleDetails(schoolResults: SchoolResults) {
        when (schoolResults) {
            is SchoolResults.ERROR -> errorHandler(schoolResults)

            is SchoolResults.LOADING -> shouldDisplayContent(false)
            is SchoolResults.SUCCESS<*> -> {
                val details = schoolResults.results as SchoolDetails
                with(binding) {
                    schoolName.text = details.schoolName
                    dbn.text = details.dbn
                    numOfSatTestTakers.text = details.numOfSatTestTakers
                    satMathAvgScore.text = details.satMathAvgScore
                    satCriticalReadingAvgScore.text = details.satCriticalReadingAvgScore
                    satWritingAvgScore.text = details.satWritingAvgScore
                }
                shouldDisplayContent(true)
            }
        }
    }

    private fun errorHandler(schoolResults: SchoolResults.ERROR) {
        val dialog = ErrorFragment.showError(
            "Error: ${schoolResults.throwable.localizedMessage}"
        ).show(childFragmentManager, ErrorFragment.TAG)
    }

    private fun shouldDisplayContent(yes: Boolean) {
        when {
            yes -> {
                binding.detailsContent.visibility = View.VISIBLE
                binding.loading.visibility = View.GONE
            }
            else -> {
                binding.detailsContent.visibility = View.GONE
                binding.loading.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSchoolDetails()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Material_Light_NoActionBar_Fullscreen
        )

        dialog?.let {
            it.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "DetailsFragment"

        @JvmStatic
        fun newDetailsFragment() = DetailsFragment()
    }
}