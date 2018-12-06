package com.aboutobjects.curriculum.readinglist.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class DataboundViewHolder<BINDING : ViewDataBinding> : RecyclerView.ViewHolder {
    val binding: BINDING
    fun context(): Context = binding.root.context

    constructor(binding: BINDING) : super(binding.root) {
        this.binding = binding
    }

    constructor(@LayoutRes layoutId: Int, @NonNull parent: ViewGroup)
            : this(
        DataBindingUtil.inflate<BINDING>(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false
        )
    )
}