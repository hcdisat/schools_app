package com.hcdisat.schools.ui.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.hcdisat.schools.databinding.FragmentDetailsBinding
import com.hcdisat.schools.models.SchoolDetails
import com.hcdisat.schools.ui.state.RequestState
import com.hcdisat.schools.ui.state.RequestState.*
import com.hcdisat.schools.ui.schoollist.SchoolsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : DialogFragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SchoolsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        binding.btnClose.setOnClickListener { dismissAllowingStateLoss() }
        viewModel.schoolDetails.observe(viewLifecycleOwner, ::handleDetails)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            bundle.getString(DBN_KEY)?.let {
                viewModel.getSchoolDetails(it)
            }
        }
    }

    private fun handleDetails(schoolResults: RequestState) {
        when (schoolResults) {
            is ERROR -> errorHandler(schoolResults)

            is INITIAL -> shouldDisplayContent(false)
            is SUCCESS<*> -> {
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

    private fun errorHandler(schoolResults: ERROR) {
        val dialog = ErrorFragment.showError(
            "Error: ${schoolResults.throwable.localizedMessage}"
        )
        dialog.show(childFragmentManager, ErrorFragment.TAG)
        dialog.onClosing = {
            dismissAllowingStateLoss()
        }
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
        private const val DBN_KEY = "DBN_KEY"

        @JvmStatic
        fun newDetailsFragment(dbn: String) = DetailsFragment().apply {
            arguments = Bundle().apply {
                putString(DBN_KEY, dbn)
            }
        }
    }
}