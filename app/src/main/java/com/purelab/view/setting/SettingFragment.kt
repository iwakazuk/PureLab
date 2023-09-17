package com.purelab.view.setting

import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.purelab.R
import com.purelab.databinding.FragmentSettingBinding
import com.purelab.enums.user.Sex
import com.purelab.enums.user.SkinType
import com.purelab.models.User
import com.purelab.utils.getEnumIndex
import com.purelab.utils.hideKeyboard
import com.purelab.utils.toEnumString
import com.purelab.view.BaseDataBindingFragment

class SettingFragment : BaseDataBindingFragment<FragmentSettingBinding>() {
    override fun getLayoutRes(): Int = R.layout.fragment_setting
    private val vm: SettingViewModel by viewModels()
    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        val context = context ?: return binding.root

        // LiveDataの監視
        vm.userLiveData.observe(viewLifecycleOwner) { user ->
            // ユーザーデータをビューにセットする
            binding.userName.setText(user?.userName)
            binding.userAge.setText(user?.age)

            val sexIndex = getEnumIndex(user?.sex, Sex.values(), context)
            binding.userSex.setSelection(sexIndex)

            val skinTypeIndex = getEnumIndex(user?.skinType, SkinType.values(), context)
            binding.userSkinType.setSelection(skinTypeIndex)
        }

        // ここでデータをロードする
        vm.loadUser("userSetting") // desiredUserIdには取得したいユーザーのIDを指定

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context ?: return
        setPicker(binding.userSex, Sex.values().map { it.toEnumString(context) })
        setPicker(binding.userSkinType, SkinType.values().map { it.toEnumString(context) })
        setHideKeyBoard(binding.root)

        binding.saveButton.setOnClickListener { v ->
            val user = User(
                userId = "userSetting",
                userName = binding.userName.text.toString(),
                age = binding.userAge.text.toString(),
                sex = binding.userSex.selectedItem.toString(),
                skinType = binding.userSkinType.selectedItem.toString(),
            )
            vm.saveUser(user)

            // 保存完了のsnackbarを表示
            val snackbar = Snackbar.make(view, "ユーザー情報を保存しました", Snackbar.LENGTH_SHORT)
            val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
            params.bottomMargin = 200
            snackbar.view.layoutParams = params
            snackbar.show()
        }
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
            }
        }
    }

    private fun setHideKeyBoard(view: View) {
        // タッチリスナーを設定
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!isKeyboardShown(view)) {
                        requireContext().hideKeyboard(view)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    v.performClick()
                }
            }
            true
        }
    }

    private fun isKeyboardShown(rootView: View): Boolean {
        val softKeyboardHeight = 100
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val screenHeight = rootView.height
        val keypadHeight = screenHeight - r.bottom
        return keypadHeight > softKeyboardHeight
    }
}
