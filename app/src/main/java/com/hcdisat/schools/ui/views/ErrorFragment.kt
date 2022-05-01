package com.hcdisat.schools.ui.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hcdisat.schools.databinding.FragmentErrorBinding

class ErrorFragment : DialogFragment() {

    var onClosing: () -> Unit = {}

    private var _binding: FragmentErrorBinding? = null
    private val binding: FragmentErrorBinding get() = _binding!!

    private var errorMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NO_FRAME,
            android.R.style.Theme_Material_DialogWhenLarge
        )
        arguments?.let {
            errorMessage = it.getString(ERROR_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentErrorBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener {
            dismissAllowingStateLoss()
            onClosing()
        }
        binding.errorMessage.text = errorMessage
    }

    companion object {

        const val TAG = "ErrorFragment"
        private const val ERROR_KEY = "ERROR_KEY"

        @JvmStatic
        fun showError(errorMessage: String) = ErrorFragment().apply {
            arguments = Bundle().apply {
                putString(ERROR_KEY, errorMessage)
            }
        }
    }
}