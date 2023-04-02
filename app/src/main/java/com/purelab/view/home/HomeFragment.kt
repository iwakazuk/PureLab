package com.purelab.view.home

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.constraintlayout.motion.widget.MotionLayout
import com.purelab.R
import androidx.appcompat.app.AppCompatActivity

class HomeFragment : Fragment() {

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
}
