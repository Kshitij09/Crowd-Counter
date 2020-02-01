package com.kshitijpatil.crowdcounter.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.kshitijpatil.crowdcounter.databinding.DetailsFragmentBinding
import com.kshitijpatil.crowdcounter.utils.executeAfter

class DetailsFragment : Fragment() {
    /** AndroidX navigation arguments */
    private val args: DetailsFragmentArgs by navArgs()

    companion object {
        fun newInstance() =
            DetailsFragment()
    }

    private lateinit var viewModel: DetailsViewModel
    private lateinit var binding: DetailsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailsFragmentBinding.inflate(
            inflater, container, false
        ).apply {
            dummy.text = args.imagePath
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DetailsViewModel::class.java)
        viewModel.sendRequest(args.imagePath)
        binding.executeAfter {
            viewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
            viewModel.sendRequest(args.imagePath)
        }
    }

}
