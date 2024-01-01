package com.drexask.reduplicate.presentation.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.drexask.reduplicate.DuplicatePrioritySelectorViewModel
import com.drexask.reduplicate.FoldersPrioritySettingsAdapter
import com.drexask.reduplicate.MainNavGraphViewModel
import com.drexask.reduplicate.R
import com.drexask.reduplicate.databinding.FragmentFolderPrioritySettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Collections

@AndroidEntryPoint
class FolderPrioritySettingsFragment : Fragment() {

    //private val viewModel by hiltNavGraphViewModels<MainNavGraphViewModel>(R.id.main_graph)
    private val viewModel: DuplicatePrioritySelectorViewModel by activityViewModels()

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
                FoldersPrioritySettingsAdapter(it, viewModel.uRIsContainDuplicatesPriorityList)

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