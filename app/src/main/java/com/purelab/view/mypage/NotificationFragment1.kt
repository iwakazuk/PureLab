package com.purelab.view.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.adapters.ItemListAdapter
import com.purelab.databinding.FragmentNotification1Binding
import com.purelab.models.Item
import com.purelab.view.BaseDataBindingFragment
import com.purelab.models.mockItem
import com.purelab.utils.Category
import com.purelab.utils.toEnumString
import com.purelab.view.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NotificationFragment1 : BaseDataBindingFragment<FragmentNotification1Binding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_notification1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = dataBinding!!

        binding.buttonFavorite.setOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment1_to_notificationFragment2)
        }

        binding.buttonSetting.setOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment1_to_setting)
        }

        return binding.root
    }
}
