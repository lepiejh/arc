package com.ved.framework.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ved.framework.utils.DpiUtils

class SpacingItemDecoration  (private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val spacingPx = DpiUtils.dip2px(parent.context,spacing)
        // 设置左、上、右、下四个方向的间距（这里统一设置为spacingPx的值）
        outRect.left = spacingPx
        outRect.right = spacingPx
        outRect.top = spacingPx
        outRect.bottom = spacingPx
    }
}