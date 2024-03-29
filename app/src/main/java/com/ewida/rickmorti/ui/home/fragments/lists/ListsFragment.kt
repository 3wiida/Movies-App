package com.ewida.rickmorti.ui.home.fragments.lists

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.ewida.rickmorti.base.BaseFragment
import com.ewida.rickmorti.custom_view.dialogs.CreateListBottomSheet
import com.ewida.rickmorti.custom_view.dialogs.LoadingDialog
import com.ewida.rickmorti.databinding.FragmentListsBinding
import com.ewida.rickmorti.model.create_list_response.CreateListResponse
import com.ewida.rickmorti.model.created_lists_response.CreatedLists
import com.ewida.rickmorti.model.created_lists_response.ListsResponse
import com.ewida.rickmorti.ui.home.fragments.lists.adapters.ListsAdapter
import com.ewida.rickmorti.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListsFragment : BaseFragment<FragmentListsBinding, ListsViewModel>() {

    override val viewModel: ListsViewModel by viewModels()
    private var listsObserver: RecyclerView.AdapterDataObserver?=null
    private val listsAdapter: ListsAdapter = ListsAdapter()
    private var lastDeletedItem: CreatedLists? = null
    private var lastCreatedItem: CreatedLists? = null

    //TODO this bottom sheet and dialog will cause memory leak --> fix later
    val createListBottomSheet: CreateListBottomSheet by lazy { CreateListBottomSheet() }
    val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun getViewBinding() = FragmentListsBinding.inflate(layoutInflater)

    override fun setUpViews() {
        binding.viewModel = viewModel
        binding.rvLists.adapter = listsAdapter
        listsObserver = object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.lottieAnimationView.isVisible = false
                binding.rvLists.isVisible = true
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                binding.lottieAnimationView.isVisible = false
                binding.rvLists.isVisible = true
            }
        }
        listsAdapter.registerAdapterDataObserver(listsObserver!!)
    }

    override fun initClicks() {

        binding.addListBtn.setOnClickListener {
            createListBottomSheet.show(requireActivity().supportFragmentManager, null)
        }

        binding.emptyState.onBtnClick = {
            createListBottomSheet.show(requireActivity().supportFragmentManager, null)
        }

        createListBottomSheet.onCreateBtnClick = { name, description ->
            viewModel.createList(requireContext(), name, description)
            lastCreatedItem =
                CreatedLists(description = description, name = name, id = 0, item_count = 0)
        }

        listsAdapter.onItemDeleted = { deletedList ->
            lastDeletedItem = deletedList
            viewModel.deleteList(requireContext(), deletedList.id.toString())
        }

        binding.listSearchView.watchText(
            action = { query ->
                listsAdapter.submitList(viewModel.searchList(listsAdapter.currentList, query))
                binding.searchEmptyState.isVisible = listsAdapter.currentList.isEmpty()
            },
            emptyAction = {
                viewModel.getLists(requireContext())
            },
            loading = {
                binding.lottieAnimationView.isVisible = true
                binding.rvLists.isVisible = false
            },
            duration = 2000L
        )

    }

    override fun sendCalls() {
        if (!viewModel.isGuest()) viewModel.getLists(requireContext())
    }

    override fun collectResults() {
        collectState<CreateListResponse>(
            stateFlow = viewModel.createListState,
            failure = { msg, _ -> CreateListCollector().failure(msg) },
            success = { data -> CreateListCollector().success(data) }
        )

        collectState<ListsResponse>(
            stateFlow = viewModel.getListsState,
            failure = { msg, _ -> GetListsCollector().failure(msg) },
            success = { data -> GetListsCollector().success(data) }
        )

        collectState<Any>(
            stateFlow = viewModel.deleteListState,
            loading = { DeleteListCollector().loading() },
            failure = { _, _ -> DeleteListCollector().failure() },
            success = { DeleteListCollector().success() }
        )

    }


    /** Collectors **/
    private inner class CreateListCollector {
        fun failure(msg: String) {
            toast(msg)
        }

        fun success(data: CreateListResponse) {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) createListBottomSheet.dismiss()
            lastCreatedItem?.id = data.list_id
            lastCreatedItem?.let {
                listsAdapter.submitList(
                    viewModel.generateListAfterCreate(
                        listsAdapter.currentList,
                        it
                    )
                )
            }
        }
    }

    private inner class GetListsCollector {
        fun failure(msg: String) {
            toast(msg)
        }

        fun success(data: ListsResponse) {
            listsAdapter.submitList(data.results)
        }
    }

    private inner class DeleteListCollector {
        fun loading() {
            loadingDialog.show()
        }

        fun failure() {
            loadingDialog.dismiss()
            /**
             * I put this code here because the API call returns 500 ( Internal Server Error )
             * but the list is deleted anyway
             * remove this when the backend fix this problem
             * */
            lastDeletedItem?.let {
                listsAdapter.submitList(
                    viewModel.generateListAfterDelete(
                        oldList = listsAdapter.currentList,
                        deletedItem = it
                    )
                )
                if (listsAdapter.currentList.isEmpty()) viewModel.isEmptyState.set(true) else viewModel.isEmptyState.set(
                    false
                )
            }
        }

        fun success() {
            loadingDialog.dismiss()
            lastDeletedItem?.let {
                listsAdapter.submitList(
                    viewModel.generateListAfterDelete(
                        oldList = listsAdapter.currentList,
                        deletedItem = it
                    )
                )
            }
            if (listsAdapter.currentList.isEmpty()) viewModel.isEmptyState.set(true) else viewModel.isEmptyState.set(
                false
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listsAdapter.unregisterAdapterDataObserver(listsObserver!!)
        listsObserver=null
    }

}