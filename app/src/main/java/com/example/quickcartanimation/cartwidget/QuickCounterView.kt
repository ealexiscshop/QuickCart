package com.example.quickcartanimation.cartwidget

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.example.quickcartanimation.R
import com.google.android.material.button.MaterialButton
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class QuickCounterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val displayDensity = resources.displayMetrics.density
    private var counterValue = DEFAULT_COUNTER_VALUE
    private val textView: TextView
    private val btnDecreaseCounter: MaterialButton
    private val btnIncreaseCounter: MaterialButton
    private val btnGhost: MaterialButton
    private var expandedWidth = 0
    private var collapsedWidth = 0
    private var counterPadding = DEFAULT_COUNTER_PADDING.toDp()
    private var isExpanded = false
    private var isInitialWidthRead = false

    init {
        setBackgroundResource(R.drawable.background_rounded)
        isClickable = true
        isFocusable = true
        isFocusableInTouchMode = true
        btnGhost = getGhostButton()
        textView = getTextView()
        btnDecreaseCounter = getDecreaseButton()
        btnIncreaseCounter = getIncreaseButton()
        attrs?.let(::readAttrs)
        setupViews()
        addViews()
        setupConstraints()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!isInitialWidthRead) {
            isInitialWidthRead = true
            if (isExpanded) expand(false) else collapse(false)
        }
    }

    private fun addViews() {
        addView(btnGhost)
        addView(textView)
        addView(btnDecreaseCounter)
        addView(btnIncreaseCounter)
    }

    private fun setupViews() {
        setPadding(counterPadding, counterPadding, counterPadding, counterPadding)
        updateTextView()
        textView.setOnClickListener { onTextViewClicked() }
        btnDecreaseCounter.setOnClickListener { onDecreaseButtonClicked() }
        btnIncreaseCounter.setOnClickListener { onIncreaseButtonClicked() }
    }

    private fun updateTextView() {
        textView.text = counterValue.toString()
    }

    private fun setupConstraints() {
        ConstraintSet().run {
            clone(this@QuickCounterView)
            connect(btnGhost.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(btnGhost.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            connect(btnGhost.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

            connect(textView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(textView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(textView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            connect(textView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

            connect(btnDecreaseCounter.id, ConstraintSet.TOP, btnGhost.id, ConstraintSet.TOP)
            connect(btnDecreaseCounter.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(btnDecreaseCounter.id, ConstraintSet.BOTTOM, btnGhost.id, ConstraintSet.BOTTOM)

            connect(btnIncreaseCounter.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(btnIncreaseCounter.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            connect(btnIncreaseCounter.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            applyTo(this@QuickCounterView)
        }
    }

    private fun getTextView(): TextView {
        return TextView(context).apply {
            id = View.generateViewId()
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            setPadding(30.toDp(), 0, 30.toDp(), 0)
            minWidth = 24.toDp()    // Ver como obtener dinamicamente este valor
            minHeight = 24.toDp()    // Ver como obtener dinamicamente este valor
            gravity = Gravity.CENTER
            maxLines = 1
        }
    }

    private fun getGhostButton(): MaterialButton {
        return MaterialButton(getThemeForMaterialButtons()).apply {
            setupDefaultParameters()
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_remove_24)
            visibility = INVISIBLE
            isEnabled = false
        }
    }

    private fun getDecreaseButton(): MaterialButton {
        return MaterialButton(getThemeForMaterialButtons()).apply {
            setupDefaultParameters()
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_remove_24)
        }
    }

    private fun getIncreaseButton(): MaterialButton {
        return MaterialButton(getThemeForMaterialButtons()).apply {
            setupDefaultParameters()
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_add_24)
        }
    }

    private fun MaterialButton.setupDefaultParameters() {
        id = View.generateViewId()
        insetTop = 0; insetBottom = 0; minWidth = 0; minHeight = 0; iconPadding = 0; strokeWidth = 0
        iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        setPadding(0, 0, 0, 0)
        textSize = 0F
    }

    private fun readAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.QuickCounterView, NO_ID, NO_ID)
        counterValue = typedArray.getInteger(R.styleable.QuickCounterView_counterValue, DEFAULT_COUNTER_VALUE)
        counterPadding = typedArray.getDimensionPixelOffset(R.styleable.QuickCounterView_counterPadding, DEFAULT_COUNTER_PADDING.toDp())
        isExpanded = typedArray.getBoolean(R.styleable.QuickCounterView_expanded, false)
        typedArray.recycle()
    }

    private fun onTextViewClicked() {
        requestFocus()
        expandView()
    }

    private fun onDecreaseButtonClicked() {
        requestFocus()
        counterValue = counterValue.dec()
        if (counterValue <= 0) {
            counterValue = 0
            updateTextView()
            refreshLayoutIfTextViewWidthDecrease()
            collapseView()
        } else {
            updateTextView()
            refreshLayoutIfTextViewWidthDecrease()
        }
    }

    private fun onIncreaseButtonClicked() {
        requestFocus()
        counterValue = counterValue.inc()
        updateTextView()
        refreshLayoutIfTextViewWidthIncrease()
        expandView()
    }

    private fun refreshLayoutIfTextViewWidthIncrease() {
        measure(getCurrentMinWidth(), measuredHeight)
        layoutParams.width = measuredWidth
        expandedWidth = max(expandedWidth, measuredWidth)
        requestLayout()
    }

    private fun refreshLayoutIfTextViewWidthDecrease() {
        measure(getCurrentMinWidth(), measuredHeight)
        layoutParams.width = measuredWidth
        expandedWidth = min(expandedWidth, measuredWidth)
        requestLayout()
    }

    fun expandView(animate: Boolean = true) {
        if (!isExpanded) {
            expand(animate)
            isExpanded = !isExpanded
        }
    }

    private fun expand(animate: Boolean) {
        expandedWidth = max(expandedWidth, measuredWidth)
        val duration = if (animate) DEFAULT_COUNTER_ANIMATION_DURATION else 0
        val animationSet = AnimatorSet().apply { interpolator = AccelerateDecelerateInterpolator() }


        btnDecreaseCounter.visibility = VISIBLE
        btnIncreaseCounter.visibility = VISIBLE
        textView.visibility = VISIBLE

        animationSet.play(showView(btnDecreaseCounter, duration))
        animationSet.play(showView(btnIncreaseCounter, duration))
        animationSet.play(showView(textView, duration))
        animationSet.play(animatePadding(textView, duration))
        animationSet.play(animateWidth(collapsedWidth, expandedWidth, duration))

        animationSet.start()
    }

    fun collapseView(animate: Boolean = true) {
        if (isExpanded) {
            collapse(animate)
            isExpanded = !isExpanded
        }
    }

    private fun collapse(animate: Boolean) {
        expandedWidth = max(expandedWidth, measuredWidth)
        val duration = if (animate) DEFAULT_COUNTER_ANIMATION_DURATION else 0
        val animationSet = AnimatorSet().apply { interpolator = AccelerateDecelerateInterpolator() }


        animationSet.play(hideView(btnDecreaseCounter, duration))
        if (counterValue > 0) {
            animationSet.play(hideView(btnIncreaseCounter, duration))
        } else {
            animationSet.play(hideView(textView, duration))
        }
        textView.setPadding(0,0,0,0)
        measure(getCurrentMinWidth(), measuredHeight)
        collapsedWidth = measuredWidth
        animationSet.play(animateWidth(expandedWidth, collapsedWidth, duration))

        animationSet.start()
    }

    private fun getCurrentMinWidth() : Int {
        return max(textView.measuredWidth, btnGhost.measuredWidth)
    }

    private fun animateWidth(from: Int, to: Int, duration: Long): ValueAnimator {
        val widthAnimator = ValueAnimator.ofInt(from, to).setDuration(duration)

        return widthAnimator.apply {
            addUpdateListener { animation1: ValueAnimator ->
                val value = animation1.animatedValue as Int
                layoutParams.width = value
                requestLayout()
            }
        }
    }

    private fun animatePadding(view: View, duration: Long): ValueAnimator {
        val widthAnimator = ValueAnimator.ofInt(0, 30.toDp()).setDuration(duration)

        return widthAnimator.apply {
            addUpdateListener { animation1: ValueAnimator ->
                val value = animation1.animatedValue as Int
                view.setPadding(value,0,value,0)
                view.requestLayout()
            }
        }
    }

    private fun hideView(view: View, duration: Long):ValueAnimator{
        return animateVisibility(view, view.alpha, 0F, duration)
    }

    private fun showView(view: View, duration: Long):ValueAnimator{
        return animateVisibility(view, view.alpha, 1F, duration)
    }

    private fun animateVisibility(view: View, from: Float, to: Float, duration: Long): ValueAnimator {
        val internalDuration = if (duration != 0L) duration/2 else 0
        val widthAnimator = ValueAnimator.ofFloat(from, to).setDuration(internalDuration)

        return widthAnimator.apply {
            addUpdateListener { animation1: ValueAnimator ->
                val value = animation1.animatedValue as Float
                view.alpha = value
                view.requestLayout()
            }
            doOnEnd {
                if (from > to) view.visibility = GONE
            }
        }.apply {
            startDelay = if (from <= to) internalDuration else 0
        }
    }


    private fun getThemeForMaterialButtons(): Context {
        return ContextThemeWrapper(context, R.style.Theme_QuickCartAnimation_MaterialButton)
    }

    private fun Int.toDp() = (this * displayDensity).roundToInt()
}

private const val DEFAULT_COUNTER_VALUE = 0
private const val DEFAULT_COUNTER_PADDING = 2
private const val DEFAULT_COUNTER_ANIMATION_DURATION = 300L
