package com.purelab.view.itemdetail
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purelab.R
import com.purelab.adapters.ItemListAdapter
import com.purelab.databinding.FragmentDashboard3Binding
import com.purelab.view.BaseDataBindingFragment
import com.purelab.models.mockItem

class DashboardFragment3 : BaseDataBindingFragment<FragmentDashboard3Binding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_dashboard3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = dataBinding!!

        setFragmentResultListener("request_key") { _, bundle ->
            val category = bundle.getString("category")
            binding.itemDetailCategory.text = category
        }
        binding.itemDetailBrand.text = "キュレル（Curel）"
        binding.itemDetailTitle.text = "キュレル 化粧水Ⅲ"
        binding.itemDetailText.text = "●「セラミド機能カプセル※（保湿）」配合。洗顔後、スキンケア前の乾燥対策をしていない「無防備肌」に、速やかに角層まで潤いを届け、抱え込むように保つ。\n●潤い成分（ユーカリエキス）配合。外部刺激で肌荒れしにくい、なめらかで潤いに満ちた肌に保つ。\n●消炎剤配合。肌荒れを防ぐ。\n●とてもしっとり潤う使い心地。　※カプセル状のセラミド機能成分（ヘキサデシロキシＰＧヒドロキシエチルヘキサデカナミド）【医薬部外品】"

        return binding.root
    }
}
