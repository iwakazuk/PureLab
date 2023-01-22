package com.purelab.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.purelab.R
import com.purelab.app.ListViewAdapter
import com.purelab.app.Data
import com.purelab.app.ListData
//import kotlinx.android.synthetic.main.fragment_itemlist.*
//import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // リストデータの作成
        val dataList = arrayListOf<ListData>()
        for (i in 0..25){
            dataList.add(ListData().apply {
                title = "${i}番目のタイトル"
            })
        }
        // アダプターをセット
//        val adapter = context?.let { ListViewAdapter(it, dataList) }
//        List_view.adapter = adapter
        return  inflater.inflate(R.layout.fragment_search, container, false)
    }
}


//        val button = findViewById(R.id.ListView) // ②クリックしたいviewを取得
//        button.setOnClickListener(this) // ③ viewにsetOnClickListenerを設置
//
////        val listView : ListView = findViewById(R.id.ListView)
//////        listView.adapter = arrayAdapter
//////
//////        // 項目をタップしたときの処理
//////        listView.setOnItemClickListener {parent, view, position, id ->
//////
//////            // 項目の TextView を取得
//////            val itemTextView : TextView = view.findViewById(android.R.id.text1)
//////
//////        }
//
//            fun ListOnClick(view: View){ // ①クリック時の処理を追加
////                Toast.makeText(this, "ボタンが押されました", Toast.LENGTH_LONG).show()
//            }
//
//
//
//        return  inflater.inflate(R.layout.fragment_search, container, false)
//    }
//
//    override fun onClick(view: View){ // ④ onClickメソッドとクリック時の処理を追加




