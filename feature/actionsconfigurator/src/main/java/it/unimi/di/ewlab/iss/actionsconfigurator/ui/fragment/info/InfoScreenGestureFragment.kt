package it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FragmentInfoScreenGestureActionBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment.ActionsListFragment
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.InfoScreenGestureViewModel
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.Action
import it.unimi.di.ewlab.iss.common.model.actions.ScreenGestureAction
import it.unimi.di.ewlab.iss.common.model.actions.ScreenGestureAction.GestureId

class InfoScreenGestureFragment : Fragment() {

    private lateinit var binding: FragmentInfoScreenGestureActionBinding

    val viewModel: InfoScreenGestureViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentInfoScreenGestureActionBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this

        binding.viewmodel = viewModel

        val idAction = arguments?.getInt(ActionsListFragment.ARG_ID_ACTION)
        idAction?.let {
            val action = MainModel.getInstance().getActionById(it)
            setUpListeners(action)
        }

        return binding.root
    }

    private fun setUpListeners(action: Action?) {
        (action as? ScreenGestureAction)?.let {
            binding.nomeGesture.text = it.name
            binding.addAction.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), GESTURES_DRAWABLES[it.s_g_a_BtnId]!!)
            )
        }
    }

    companion object {
        private val GESTURES_DRAWABLES = mapOf(
            GestureId.HR_UP to R.drawable.sg_horiz_screen_up,
            GestureId.HR_DOWN to R.drawable.sg_horiz_screen_down,
            GestureId.VR_LEFT to R.drawable.sg_vert_screen_left,
            GestureId.VR_RIGHT to R.drawable.sg_vert_screen_right,
            GestureId.FULL_SCREEN to R.drawable.sg_screen,
            GestureId.X_UP to R.drawable.sg_xscreen_up,
            GestureId.X_DOWN to R.drawable.sg_xscreen_down,
            GestureId.X_LEFT to R.drawable.sg_xscreen_left,
            GestureId.X_RIGHT to R.drawable.sg_xscreen_right,
            GestureId.SWIPE_UP to R.drawable.sg_swipe_screen_up,
            GestureId.SWIPE_DOWN to R.drawable.sg_swipe_screen_down,
            GestureId.SWIPE_LEFT to R.drawable.sg_swipe_screen_left,
            GestureId.SWIPE_RIGHT to R.drawable.sg_swipe_screen_right
        )
    }
}