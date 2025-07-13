package com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.ViewModelProvider
import com.github.aivanovski.testswithme.android.R
import com.github.aivanovski.testswithme.android.databinding.ComposeViewBinding
import com.github.aivanovski.testswithme.android.di.GlobalInjector.inject
import com.github.aivanovski.testswithme.android.extensions.getParcelableCompat
import com.github.aivanovski.testswithme.android.extensions.requireArgument
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.ThemeProvider
import com.github.aivanovski.testswithme.android.presentation.core.ViewModelFactory
import com.github.aivanovski.testswithme.android.presentation.core.compose.rememberCallback
import com.github.aivanovski.testswithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testswithme.android.presentation.screens.bottomSheetMenu.model.BottomSheetUiEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetMenuFragment : BottomSheetDialogFragment() {

    private val themeProvider: ThemeProvider by inject()

    private val viewModel: BottomSheetMenuViewModel by lazy {
        ViewModelProvider(
            owner = this,
            factory = ViewModelFactory(getArgs())
        )[BottomSheetMenuViewModel::class]
    }

    private var onClickListener: ((index: Int) -> Unit)? = null

    override fun getTheme(): Int = R.style.BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ComposeViewBinding.inflate(inflater, container, false)

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val eventCollector = rememberCallback { event: BottomSheetUiEvent ->
                    handleEvent(event)
                }

                AppTheme(theme = themeProvider.theme) {
                    BottomSheetMenuScreen(
                        viewModel = viewModel,
                        eventCollector = eventCollector
                    )
                }
            }
        }

        return binding.root
    }

    private fun getArgs(): BottomSheetMenu {
        return arguments?.getParcelableCompat<BottomSheetMenu>(MENU)
            ?: requireArgument(MENU)
    }

    private fun handleEvent(event: BottomSheetUiEvent) {
        when (event) {
            is BottomSheetUiEvent.OnClick -> {
                onClickListener?.invoke(event.index)
                dismiss()
            }
        }
    }

    companion object {

        val TAG: String = BottomSheetMenuFragment::class.java.simpleName

        private const val MENU = "menu"

        fun newInstance(
            menu: BottomSheetMenu,
            onClick: (index: Int) -> Unit
        ): BottomSheetMenuFragment {
            val bundle = Bundle()
                .apply {
                    putParcelable(MENU, menu)
                }

            return BottomSheetMenuFragment()
                .apply {
                    arguments = bundle
                    onClickListener = onClick
                }
        }
    }
}