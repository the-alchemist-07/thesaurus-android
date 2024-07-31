package com.mashood.thesaurus.shortcuts.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.mashood.thesaurus.BuildConfig
import com.mashood.thesaurus.R
import com.mashood.thesaurus.databinding.ActivityUninstallBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UninstallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUninstallBinding
    private val viewModel: UninstallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUninstallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
        observeState()
    }

    private fun setListeners() {
        with(binding) {
            toolbar.setNavigationOnClickListener {
                finish()
            }

            btnUninstall.setOnClickListener {
                viewModel.validateData(
                    comment = binding.etComments.text.toString(),
                    selection = SELECTION_UNINSTALL
                )
            }

            btnStay.setOnClickListener {
                viewModel.validateData(
                    comment = binding.etComments.text.toString(),
                    selection = SELECTION_STAY
                )
            }

            btnSubmit.setOnClickListener {
                viewModel.validateData(
                    comment = binding.etComments.text.toString(),
                    selection = SELECTION_STAY
                )
            }

            toggleGroupOptions.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    when (checkedId) {
                        R.id.btnOption1 -> {
                            viewModel.selectedReason = btnOption1.text.toString()
                            lytComments.isVisible = false
                        }

                        R.id.btnOption2 -> {
                            viewModel.selectedReason = btnOption2.text.toString()
                            lytComments.isVisible = false
                        }

                        R.id.btnOption3 -> {
                            viewModel.selectedReason = btnOption3.text.toString()
                            lytComments.isVisible = false
                        }

                        R.id.btnOption4 -> {
                            viewModel.selectedReason = btnOption4.text.toString()
                            lytComments.isVisible = false
                        }

                        R.id.btnOption5 -> {
                            viewModel.selectedReason = btnOption5.text.toString()
                            lytComments.isVisible = true
                        }
                    }
                } else {
                    viewModel.selectedReason = null
                    lytComments.isVisible = false
                }
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    when (state) {
                        is UninstallState.ShowMessage -> Snackbar.make(
                            binding.root, state.message, Snackbar.LENGTH_LONG
                        ).show()

                        is UninstallState.ShowSubmitUi -> {
                            binding.btnUninstall.visibility = View.INVISIBLE
                            binding.btnStay.visibility = View.INVISIBLE
                            binding.btnSubmit.isVisible = true
                        }

                        is UninstallState.NavigateToSettings -> {
                            val intent = Intent()
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = if (BuildConfig.DEBUG)
                                Uri.fromParts(SCHEME, PKG_DEBUG, null)
                            else Uri.fromParts(SCHEME, PKG_RELEASE, null)
                            intent.setData(uri)
                            startActivity(intent)
                        }

                        is UninstallState.CloseActivity -> finish()
                    }
                }
            }
        }
    }

    companion object {
        const val SCHEME = "package"
        const val PKG_DEBUG = "com.mashood.thesaurus.debug"
        const val PKG_RELEASE = "com.mashood.thesaurus"
        const val SELECTION_UNINSTALL = "uninstall"
        const val SELECTION_STAY = "stay"
    }
}