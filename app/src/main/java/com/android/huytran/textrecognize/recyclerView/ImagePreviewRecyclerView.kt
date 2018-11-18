package com.android.huytran.textrecognize.recyclerView

import android.content.Context
import android.content.res.TypedArray
import android.os.SystemClock
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.AbsListView
import android.widget.FrameLayout
import com.android.huytran.textrecognize.adapter.ImagePreviewAdapter
import com.ecloud.pulltozoomview.PullToZoomBase


class ImagePreviewRecyclerView : PullToZoomBase<RecyclerView>, AbsListView.OnScrollListener {

    var mHeaderContainer: FrameLayout? = null
    private var mHeaderHeight = 0
    private var mScalingRunnable: ScalingRunnable

    companion object {
        val sInterpolator = Interpolator { paramAnonymousFloat ->
            val f = paramAnonymousFloat - 1.0f
            1.0f + f * (f * (f * (f * f)))
        }
    }

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mRootView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (mZoomView != null && !isHideHeader && isPullToZoomEnabled) {
                    val f = mHeaderHeight - mHeaderContainer!!.bottom
                    if (isParallax) {
                        if (f > 0.0f && f < mHeaderHeight) {
                            val i = (0.65 * f).toInt()
                            mHeaderContainer?.scrollTo(0, -i)
                        } else if (mHeaderContainer?.scrollY != 0) {
                            mHeaderContainer?.scrollTo(0, 0)
                        }
                    }
                }
            }
        })

        mScalingRunnable = ScalingRunnable()
    }

    override fun setHeaderView(headerView: View?) {
        headerView?.let {
            this.mHeaderView = headerView
            updateHeaderView()
        }
    }

    private fun updateHeaderView() {
        if (mHeaderContainer != null) {

            if (mRootView != null && mRootView.adapter != null) {

                val mAdapter = mRootView.adapter as ImagePreviewAdapter<RecyclerView.ViewHolder>

                if (mAdapter.getHeader() != null)
                    mAdapter.removeHeaderView()

                mHeaderContainer?.removeAllViews()

                if (mZoomView != null) {
                    mHeaderContainer?.addView(mZoomView)
                }

                if (mHeaderView != null) {
                    mHeaderContainer?.addView(mHeaderView)
                }

                mHeaderHeight = mHeaderContainer!!.height

                val mExtraItem = ImagePreviewAdapter.ExtraItem(object : RecyclerView.ViewHolder(mHeaderContainer!!) {}, ImagePreviewAdapter.EXTRA_ITEM_TYPE)

                mAdapter.addHeaderView(mExtraItem as ImagePreviewAdapter.ExtraItem<RecyclerView.ViewHolder>)
            }
        }
    }

    private fun isFirstItemVisible(): Boolean {
        if (mRootView != null && mRootView.adapter != null) {
            val adapter = mRootView.adapter
            val mLayoutmanager = mRootView.layoutManager as LinearLayoutManager


            if (null == adapter || adapter.itemCount == 0) {
                return true
            } else {

                val into = intArrayOf(0, 0)
                into[0] = mLayoutmanager.findFirstVisibleItemPosition()
                if (into.isNotEmpty() && into[0] <= 1) {
                    val firstVisibleChild = mRootView.getChildAt(0)
                    if (firstVisibleChild != null) {
                        return firstVisibleChild.top >= mRootView.top
                    }
                }
            }
        }

        return false
    }

    fun setAdapterAndLayoutManager(adapter: RecyclerView.Adapter<*>, mLayoutManager: LinearLayoutManager) {
        mRootView.layoutManager = mLayoutManager
        mRootView.adapter = adapter
        updateHeaderView()
    }

    fun setHeaderLayoutParams(layoutParams: AbsListView.LayoutParams) {
        if (mHeaderContainer != null) {
            mHeaderContainer!!.layoutParams = layoutParams
            mHeaderHeight = layoutParams.height
        }
    }

    override fun setZoomView(zoomView: View?) {
        zoomView?.let {
            this.zoomView = it
            updateHeaderView()
        }
    }

    override fun isReadyForPullStart(): Boolean {
        return isFirstItemVisible()
    }

    override fun createRootView(context: Context?, attrs: AttributeSet?): RecyclerView {
        return RecyclerView(context, attrs)
    }

    override fun smoothScrollToTop() {
        mScalingRunnable.startAnimation(200)
    }

    override fun handleStyledAttributes(a: TypedArray?) {
        if (mRootView.adapter == null) return
        mHeaderContainer = FrameLayout(context)
        if (mZoomView != null) {
            mHeaderContainer!!.addView(mZoomView)
        }
        if (mHeaderView != null) {
            mHeaderContainer!!.addView(mHeaderView)
        }
        val mAdapter = mRootView.adapter as ImagePreviewAdapter<RecyclerView.ViewHolder>

        val mExtraItem = ImagePreviewAdapter.ExtraItem(object : RecyclerView.ViewHolder(mHeaderContainer!!) {}, ImagePreviewAdapter.EXTRA_ITEM_TYPE)

        mAdapter.addHeaderView(mExtraItem as ImagePreviewAdapter.ExtraItem<RecyclerView.ViewHolder>)
    }

    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (mZoomView != null && !isHideHeader && isPullToZoomEnabled) {
            val f = mHeaderHeight - mHeaderContainer!!.bottom
            if (isParallax) {
                if (f > 0.0f && f < mHeaderHeight) {
                    val i = (0.65 * f).toInt()
                    mHeaderContainer?.scrollTo(0, -i)
                } else if (mHeaderContainer?.scrollY != 0) {
                    mHeaderContainer?.scrollTo(0, 0)
                }
            }
        }
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

    }

    override fun pullHeaderToZoom(newScrollValue: Int) {
        if (!mScalingRunnable.isFinished) {
            mScalingRunnable.abortAnimation()
        }

        val localLayoutParams = mHeaderContainer?.layoutParams
        localLayoutParams?.height = Math.abs(newScrollValue) + mHeaderHeight
        mHeaderContainer?.layoutParams = localLayoutParams
    }

    internal open inner class ScalingRunnable : Runnable {
        private var mDuration: Long = 0
        var isFinished = true
            protected set
        private var mScale: Float = 0.toFloat()
        private var mStartTime: Long = 0

        fun abortAnimation() {
            isFinished = true
        }

        override fun run() {
            if (mZoomView != null && mHeaderContainer != null) {
                val f2: Float
                val localLayoutParams: ViewGroup.LayoutParams
                if (!isFinished && mScale > 1.0) {
                    val f1 = (SystemClock.currentThreadTimeMillis().toFloat() - mStartTime.toFloat()) / mDuration.toFloat()
                    f2 = mScale - (mScale - 1.0f) * ImagePreviewRecyclerView.sInterpolator.getInterpolation(f1)
                    localLayoutParams = mHeaderContainer!!.layoutParams
                    if (f2 > 1.0f) {
                        localLayoutParams.height = (f2 * mHeaderHeight).toInt()
                        mHeaderContainer!!.layoutParams = localLayoutParams
                        post(this)
                        return
                    }
                    isFinished = true
                }
            }
        }

        fun startAnimation(paramLong: Long) {
            if (mZoomView != null) {
                mStartTime = SystemClock.currentThreadTimeMillis()
                mDuration = paramLong
                mScale = mHeaderContainer!!.bottom.toFloat() / mHeaderHeight
                isFinished = false
                post(this)
            }
        }
    }
}