package com.spake.brony.openai.utils

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.spake.brony.openai.R
import io.noties.markwon.Markwon

object EditUtils {


    fun userText(context: Context): TextView {
        val viewLayoutParams by lazy {
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val textView = TextView(context)
        textView.setBackgroundResource(R.drawable.dp_user_layout)
        textView.setPadding(DpToPxUtils.dip2px(context,8f),
            DpToPxUtils.dip2px(context,8f),
            DpToPxUtils.dip2px(context,8f)
            ,DpToPxUtils.dip2px(context,8f))
        textView.setTextColor(Color.parseColor("#000000"))
        textView.textSize = DpToPxUtils.dip2px(context,5f).toFloat()
        viewLayoutParams.run {
            marginStart = DpToPxUtils.dip2px(context,8f)
            marginEnd = DpToPxUtils.dip2px(context,8f)
            bottomMargin = DpToPxUtils.dip2px(context,8f)
            topMargin = DpToPxUtils.dip2px(context,8f)
            gravity = (Gravity.RIGHT)
        }
        textView.layoutParams = viewLayoutParams
        textView.setTextIsSelectable(true)
        return textView
    }

    fun openAiText(context: Context): TextView {
        val viewLayoutParams by lazy {
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val textView = TextView(context)
        viewLayoutParams.run {
            marginStart = DpToPxUtils.dip2px(context,8f)
            marginEnd = DpToPxUtils.dip2px(context,8f)
            bottomMargin = DpToPxUtils.dip2px(context,8f)
            topMargin = DpToPxUtils.dip2px(context,8f)
        }
        textView.layoutParams = viewLayoutParams
        textView.textSize = DpToPxUtils.dip2px(context,5f).toFloat()
        textView.setBackgroundResource(R.drawable.dp_open_ai_layout)
        textView.setTextColor(Color.parseColor("#000000"))
        textView.setPadding(DpToPxUtils.dip2px(context,8f),
            DpToPxUtils.dip2px(context,8f),
            DpToPxUtils.dip2px(context,8f)
            ,DpToPxUtils.dip2px(context,8f))
        textView.setTextIsSelectable(true)

        return textView
    }
}