package com.github.com.yournovel.android.ui.widget.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.com.yournovel.android.R
import com.github.com.yournovel.android.base.BaseDialogFragment
import com.github.com.yournovel.android.databinding.DialogPhotoViewBinding
import com.github.com.yournovel.android.help.book.BookHelp
import com.github.com.yournovel.android.help.glide.ImageLoader
import com.github.com.yournovel.android.help.glide.OkHttpModelLoader
import com.github.com.yournovel.android.model.BookCover
import com.github.com.yournovel.android.model.ImageProvider
import com.github.com.yournovel.android.model.ReadBook
import com.github.com.yournovel.android.utils.setLayout
import com.github.com.yournovel.android.utils.viewbindingdelegate.viewBinding

/**
 * 显示图片
 */
class PhotoDialog() : BaseDialogFragment(R.layout.dialog_photo_view) {

    constructor(src: String, sourceOrigin: String? = null) : this() {
        arguments = Bundle().apply {
            putString("src", src)
            putString("sourceOrigin", sourceOrigin)
        }
    }

    private val binding by viewBinding(DialogPhotoViewBinding::bind)

    override fun onStart() {
        super.onStart()
        setLayout(1f, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    @SuppressLint("CheckResult")
    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        val arguments = arguments ?: return
        val src = arguments.getString("src") ?: return
        ImageProvider.get(src)?.let {
            binding.photoView.setImageBitmap(it)
            return
        }
        val file = ReadBook.book?.let { book ->
            BookHelp.getImage(book, src)
        }
        if (file?.exists() == true) {
            ImageLoader.load(requireContext(), file)
                .error(R.drawable.image_loading_error)
                .dontTransform()
                .downsample(DownsampleStrategy.NONE)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.photoView)
        } else {
            ImageLoader.load(requireContext(), src).apply {
                arguments.getString("sourceOrigin")?.let { sourceOrigin ->
                    apply(RequestOptions().set(OkHttpModelLoader.sourceOriginOption, sourceOrigin))
                }
            }.error(BookCover.defaultDrawable)
                .dontTransform()
                .downsample(DownsampleStrategy.NONE)
                .into(binding.photoView)
        }
    }

}
