package com.macreai.storyapp.view.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.macreai.storyapp.R

class EmailEditText: AppCompatEditText, View.OnTouchListener {
    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = resources.getString(R.string.email)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init(){
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()
                val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()

                if (!email.isNullOrEmpty() && !isValidEmail){
                    error = resources.getString(R.string.required_field)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean =
        false

}