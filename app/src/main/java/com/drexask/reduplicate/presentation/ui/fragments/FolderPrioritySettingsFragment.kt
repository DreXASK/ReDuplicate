package com.drexask.reduplicate.presentation.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.drexask.reduplicate.DuplicatePrioritySelectorViewModel
import com.drexask.reduplicate.FoldersPrioritySettingsAdapter
import com.drexask.reduplicate.databinding.FragmentFolderPrioritySettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderPrioritySettingsFragment : Fragment() {

    private val viewModel: DuplicatePrioritySelectorViewModel by viewModels()

    private var _binding: FragmentFolderPrioritySettingsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFolderPrioritySettingsBinding.inflate(layoutInflater)

        setupRecyclerView()
        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        clickGoBack()
    }

    private fun clickGoBack() {
        binding.floatbtnGoBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        context?.let {
            val foldersPrioritySettingsAdapter =
                FoldersPrioritySettingsAdapter(it, viewModel.mainActivitySharedData.uRIsContainDuplicatesPriorityList!!)

            binding.rvFoldersPrioritySettings.apply {
                adapter = foldersPrioritySettingsAdapter
                setHasFixedSize(true)
            }

            val itemTouchHelper = object : ItemTouchHelper.Callback() {
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    return makeMovementFlags(dragFlags, 0)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition

                    viewModel.swapUriPriorities(fromPosition, toPosition)

                    recyclerView.adapter?.run {
                        notifyItemMoved(fromPosition, toPosition)
                        notifyItemChanged(toPosition)
                        notifyItemChanged(fromPosition)
                    }
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // No need to override it
                }
            }

            val itemTouchHelperCallBack = ItemTouchHelper(itemTouchHelper)
            itemTouchHelperCallBack.attachToRecyclerView(binding.rvFoldersPrioritySettings)
        }
    }
}