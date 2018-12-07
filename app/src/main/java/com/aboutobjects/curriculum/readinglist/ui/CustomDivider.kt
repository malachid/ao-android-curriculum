package com.aboutobjects.curriculum.readinglist.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aboutobjects.curriculum.readinglist.ReadingListApp

class CustomDivider(context: Context): RecyclerView.ItemDecoration() {
    private val overPaint = Paint()
    private val underPaint = Paint()
    private val thickness = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        5.5f,
        context.resources.displayMetrics
    )
    private val center = thickness / 2

    init {
        overPaint.color = Color.WHITE
        overPaint.strokeWidth = thickness / 3

        underPaint.color = Color.BLUE
        underPaint.strokeWidth = thickness
    }

    private fun drawLines(c: Canvas, parent: RecyclerView, paint: Paint) {
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            c.drawLine(view.left.toFloat(),
                view.bottom.toFloat() + center,
                view.right.toFloat(),
                view.bottom.toFloat() + center,
                paint)
        }
    }

    /**
     * Draw any appropriate decorations into the Canvas supplied to the RecyclerView.
     * Any content drawn by this method will be drawn after the item views are drawn
     * and will thus appear over the views.
     *
     * @param c Canvas to draw into
     * @param parent RecyclerView this ItemDecoration is drawing into
     * @param state The current state of RecyclerView.
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawLines(c, parent, overPaint)
    }

    /**
     * Draw any appropriate decorations into the Canvas supplied to the RecyclerView.
     * Any content drawn by this method will be drawn before the item views are drawn,
     * and will thus appear underneath the views.
     *
     * @param c Canvas to draw into
     * @param parent RecyclerView this ItemDecoration is drawing into
     * @param state The current state of RecyclerView
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawLines(c, parent, underPaint)
    }

    /**
     * Retrieve any offsets for the given item. Each field of `outRect` specifies
     * the number of pixels that the item view should be inset by, similar to padding or margin.
     * The default implementation sets the bounds of outRect to 0 and returns.
     *
     *
     *
     * If this ItemDecoration does not affect the positioning of item views, it should set
     * all four fields of `outRect` (left, top, right, bottom) to zero
     * before returning.
     *
     *
     *
     * If you need to access Adapter for additional data, you can call
     * [RecyclerView.getChildAdapterPosition] to get the adapter position of the
     * View.
     *
     * @param outRect Rect to receive the output.
     * @param view    The child view to decorate
     * @param parent  RecyclerView this ItemDecoration is decorating
     * @param state   The current state of RecyclerView.
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(0, 0, 0, thickness.toInt())
    }
}