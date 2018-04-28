package com.example.nghican.caculatorapp.ui

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.example.nghican.caculatorapp.R
import com.example.nghican.caculatorapp.base.BaseActivity
import com.example.nghican.caculatorapp.databinding.ActivityMainBinding

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_main
    override val viewModel: MainViewModel
        get() = ViewModelProviders.of(this).get(MainViewModel::class.java)

    override fun subscriber() {
        bindCall(viewModel.inputErrorSubject.subscribe { showError(getString(R.string.error_invalid_input)) })
    }
}
