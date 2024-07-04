package it.unimi.di.ewlab.iss.actionsconfigurator.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import it.unimi.di.ewlab.iss.actionsconfigurator.R
import it.unimi.di.ewlab.iss.actionsconfigurator.databinding.FragmentActionsListBinding
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.adapter.ActionItemAdapter
import it.unimi.di.ewlab.iss.actionsconfigurator.ui.adapter.itemaction.ActionItem
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.ActionManagerEvent
import it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel.ActionsListViewModel
import it.unimi.di.ewlab.iss.common.configuratorsmodels.BaseDialog
import it.unimi.di.ewlab.iss.common.configuratorsmodels.EventObserver
import it.unimi.di.ewlab.iss.common.model.MainModel
import it.unimi.di.ewlab.iss.common.model.actions.Action

class ActionsListFragment : Fragment() {

    companion object {
        const val TAG = "ActionsListFragment"
        const val ARG_ID_ACTION = "ActionsListFragment_ARG_ID_ACTION"
    }

    private lateinit var binding: FragmentActionsListBinding
    val viewModel: ActionsListViewModel by viewModels()
    private var actionsAdapter: ActionItemAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentActionsListBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this

        binding.viewmodel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        val actionTypeStr = requireArguments().getString(DirectoriesFragment.ACTION_TYPE_KEY)
        if (actionTypeStr == null) {
            Log.e(TAG, "Action type argument ${DirectoriesFragment.ACTION_TYPE_KEY} not defined")
            throw IllegalStateException("Action type argument ${DirectoriesFragment.ACTION_TYPE_KEY} not defined")
        }

        val actionType = Action.ActionType.valueOf(actionTypeStr)
        Log.d(TAG, "Action type: $actionType")

        val actions = when (actionType) {
            Action.ActionType.SCREEN_GESTURE -> MainModel.getInstance().screenGestureActions
            Action.ActionType.BUTTON -> MainModel.getInstance().buttonActions
            Action.ActionType.FACIAL_EXPRESSION -> MainModel.getInstance().facialExpressionActions
            else -> {
                Log.e(TAG, "Action type not defined: $actionType")
                listOf()
            }
        }

        setUpObserver(actions)
    }

    private fun setUpObserver(actions: List<Action>) {
        viewModel.initActionRecycler(actions)

        viewModel.actionList.observe(viewLifecycleOwner) {
            actionsAdapter = ActionItemAdapter(it)
            binding.actionsList.adapter = actionsAdapter
        }

        viewModel.eventhandler.observe(viewLifecycleOwner, EventObserver {
            if (it is ActionManagerEvent.ActionManagerEventClick)
                navigateToInfoFragment(it.item)
            else if (it is ActionManagerEvent.ActionManagerDelete) {
                showDeleteDialog(it.item.id)
            }
        })

    }

    private fun navigateToInfoFragment(actionItem: ActionItem) {
        when (actionItem.actionType) {
            Action.ActionType.SCREEN_GESTURE -> navigateToInfoScreenGestureFragment(actionItem.id)
            Action.ActionType.BUTTON -> navigateToInfoButtonActionFragment(actionItem.id)
            Action.ActionType.FACIAL_EXPRESSION -> navigateToInfoFacialExpressionActionFragment(actionItem.id)
            else -> {}
        }
    }

    private fun navigateToInfoScreenGestureFragment(actionId: Int) {
        navigateToInfoFragmentFromAction(
            R.id.action_listaGestureSchermoFragment_to_infoScreenGestureFragment,
            actionId
        )
    }

    private fun navigateToInfoFacialExpressionActionFragment(actionId: Int) {
        navigateToInfoFragmentFromAction(
            R.id.action_actionsListFragment_to_infoFacialExpressionActionFragment,
            actionId
        )
    }

    private fun navigateToInfoButtonActionFragment(actionId: Int) {
        navigateToInfoFragmentFromAction(
            R.id.action_actionsListFragment_to_infoButtonActionFragment,
            actionId
        )
    }

    private fun navigateToInfoFragmentFromAction(action: Int, actionId: Int) {
        Navigation.findNavController(requireView())
            .navigate(
                action,
                bundleOf(ARG_ID_ACTION to actionId)
            )
    }

    private fun showDeleteDialog(actionId: Int) {
        Log.d(TAG, "showDeleteDialog $actionId")

        val action = MainModel.getInstance().getActionById(actionId) ?: return

        val dialog = BaseDialog(requireContext()).apply {
            setTitleString(action.name)
            setSubTitleString(getString(R.string.actionmanager_delete_action_dialog_msg, action.name))
            textPrimaryButton = R.string.delete
            textSecondaryButton = R.string.cancel
            setColorsByActionType(action.actionType)
        }

        dialog.listener = object : BaseDialog.BaseDialogListener {
            override fun onPrimaryButtonClicked() {
                MainModel.getInstance().removeAction(actionId)
                MainModel.getInstance().writeActionsJson()

                actionsAdapter!!.notifyDataSetChanged()

                val actions = when (action.actionType) {
                    Action.ActionType.SCREEN_GESTURE -> MainModel.getInstance().screenGestureActions
                    Action.ActionType.BUTTON -> MainModel.getInstance().buttonActions
                    Action.ActionType.FACIAL_EXPRESSION -> MainModel.getInstance().facialExpressionActions
                    else -> listOf()
                }

                if (dialog.isShowing)
                    dialog.dismiss()

                if (actions.isEmpty())
                    Navigation.findNavController(requireView()).popBackStack()

            }

            override fun onSecondaryButtonClicked() {
                if (dialog.isShowing)
                    dialog.dismiss()
            }

            override fun onCheckClicked(checked: Boolean) {}
        }

        dialog.show()
    }
}