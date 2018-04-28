package com.example.nghican.caculatorapp.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.example.nghican.caculatorapp.BR
import com.example.nghican.caculatorapp.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity<VM : BaseViewModel, V : ViewDataBinding> : AppCompatActivity() {

    protected abstract val layoutId: Int @LayoutRes get
    protected abstract val viewModel: VM
    protected lateinit var binding: V
    protected val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.setVariable(BR.viewModel, viewModel)
    }

    override fun onResume() {
        super.onResume()
        subscriber()
    }

    override fun onPause() {
        unsubscriber()
        super.onPause()
    }

    protected fun bindCall(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private fun unsubscriber() {
        compositeDisposable.clear()
    }

    protected fun showError(message: String) {
        AlertDialog.Builder(this).apply {
            setMessage(message)
            setPositiveButton(R.string.ok, null)
        }.create().show()

    }

    protected abstract fun subscriber()
}