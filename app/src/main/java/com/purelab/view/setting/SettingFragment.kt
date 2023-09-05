package com.purelab.view.setting

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.purelab.R
import com.purelab.databinding.FragmentSettingBinding
import com.purelab.enums.user.Sex
import com.purelab.enums.user.SkinType
import com.purelab.utils.toEnumString
import com.purelab.view.BaseDataBindingFragment

class SettingFragment : BaseDataBindingFragment<FragmentSettingBinding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_setting

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = dataBinding!!

        val context = context ?: return
        setPicker(binding.userSex, Sex.values().map { it.toEnumString(context) })
        setPicker(binding.userSkinType, SkinType.values().map { it.toEnumString(context) })
        setHideKeyBoard(binding.userName)
        setHideKeyBoard(binding.userAge)
    }

    private fun setPicker(spinner: Spinner, enumList: List<String>) {
        // ArrayAdapterの作成
        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, enumList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // SpinnerにAdapterを設定
        spinner.adapter = adapter

        // Spinnerの項目が選択された時の処理
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 選択されたEnum値を取得
                // val selectedSkinType = Sex.values()[position]
            }
        }
    }

    private fun setHideKeyBoard(view: View) {
        // タッチリスナーを設定
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val currentFocusedView = activity?.currentFocus
                    currentFocusedView?.let { focusedView ->
                        requireContext().hideKeyboard(focusedView)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    v.performClick()
                }
            }
            true
        }
    }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
