package com.drexask.reduplicate

import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.drexask.reduplicate.databinding.FragmentDuplicateFinderBinding
import com.drexask.reduplicate.databinding.FragmentFolderPickerBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DuplicateFinderFragment : Fragment() {

    private val viewModel: DuplicateFinderFragmentViewModel by viewModels()

    private var _binding: FragmentDuplicateFinderBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDuplicateFinderBinding.inflate(layoutInflater)

        when {
            SDK_INT >= 33 -> arguments?.getParcelable(TREE_URI, Uri::class.java)
            else -> @Suppress("DEPRECATION") arguments?.getParcelable(TREE_URI) as? Uri
        }?.let {
            viewModel.treeUri.value = it
            viewModel.folderFileDoc.value = context?.let { context -> DocumentFile.fromTreeUri(context, it) }
        }

        println(viewModel.itemsQuantityInSelectedFolder)

        viewModel.treeUri.observe(viewLifecycleOwner, treeUriObserver)
        binding.btnBackToFolderPicker.setOnClickListener {
            findNavController().popBackStack()
        }


        return binding.root
    }

    private val treeUriObserver = Observer<Uri> {
        binding.currentFolderName.text = it.lastPathSegment?.replaceFirst("primary:", "")
    }


}