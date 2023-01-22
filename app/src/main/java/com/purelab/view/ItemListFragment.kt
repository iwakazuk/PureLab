package com.purelab.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.purelab.R
import com.purelab.app.CustomAdapter
import com.purelab.app.Data
//import kotlinx.android.synthetic.main.fragment_itemlist.*

class ItemListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // リストデータの作成
        val dataList = arrayListOf<Data>()
        for (i in 0..100){
            dataList.add(Data().apply {
                title = "${i}番目のタイトル"
                text =  "${i}番目のテキスト"
            })
        }
        // アダプターをセット
        val adapter = context?.let { CustomAdapter(it, dataList) }
//        custom_list_view.adapter = adapter
        return  inflater.inflate(R.layout.fragment_itemlist, container, false)
    }
}

