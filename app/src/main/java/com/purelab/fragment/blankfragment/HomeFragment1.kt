package com.purelab.fragment.blankfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.purelab.R
import com.purelab.databinding.FragmentHome1Binding

class HomeFragment1 : BaseDataBindingFragment<FragmentHome1Binding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_home1
    private var currentPosition = 0
    private val itemList = listOf(
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val homeView: View =
            inflater.inflate(R.layout.fragment_home, container, false) ?: return null
        setupMotion(homeView)
        return homeView
    }

    private fun setupMotion(homeView: View) {
        val motionLayout: MotionLayout = homeView.findViewById(R.id.motionLayout)
        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }

            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startedId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (motionLayout?.progress == 0.toFloat()) {
                    return
                }
                when (currentId) {
                    R.id.move_left_to_right -> {
                        if (currentPosition > 0) {
                            currentPosition--
                        } else {
                            currentPosition = itemList.lastIndex
                        }
                        motionLayout?.progress = 0F
                        updateView(homeView)
                    }

                    R.id.move_right_to_left -> {
                        if (currentPosition < itemList.lastIndex) {
                            currentPosition++
                        } else {
                            currentPosition = 0
                        }
                        motionLayout?.progress = 0F
                        updateView(homeView)
                    }
                }
            }
        })
        updateView(homeView)
    }

    private fun updateView(homeView: View) {
        val centerTextView: TextView = homeView.findViewById(R.id.centerTextView)
        val rightTextView: TextView = homeView.findViewById(R.id.rightTextView)
        val leftTextView: TextView = homeView.findViewById(R.id.leftTextView)

        centerTextView.text = "Item\n${itemList[currentPosition]}"

        rightTextView.text = if (currentPosition == itemList.lastIndex) {
            "Item\n${itemList.first()}"
        } else {
            "Item\n${itemList[currentPosition + 1]}"
        }

        leftTextView.text = if (currentPosition == 0) {
            "Item\n${itemList.last()}"
        } else {
            "Item\n${itemList[currentPosition - 1]}"
        }

    }
//    private var count = 0

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val binding = dataBinding!!
//
//        binding.tvCount.text = "Count: $count"
//
//        binding.btnIncrease.setOnClickListener {
//            binding.tvCount.text = "Count: ${++count}"
//        }
//
//        binding.btnNextPage.setOnClickListener {
//            val bundle = bundleOf("count" to count)
//            findNavController().navigate(R.id.action_homeFragment1_to_homeFragment2, bundle)
//        }
//
//        binding.btnGallery.setOnClickListener {
//            val bundle = bundleOf("count" to count)
//
//            findNavController().navigate(
//                R.id.nav_graph_gallery,
//                bundle
//            )
//        }
//
//         🔥 Listen savedStateHandle liveData, any type can be put in a Bundle is supported
//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("count")
//            ?.observe(
//                viewLifecycleOwner, Observer { result: Int ->
//                    // Do something with the result.
//                    count = result
//                    binding.tvCount.text = "Count: $count"
//
//                })
//
//
//        // TODO Not Working
//        setFragmentResultListener("count") { key, bundle ->
//            count = bundle.getInt("count")
//        }
//
//    }

}
