package com.skul9x.readoutloud.utils

import android.content.Context
import com.skul9x.readoutloud.R

object PromptTemplateHelper {
    private const val PLACEHOLDER = "[INFORMATION/NEWS/TOPIC I WANT TO RESEARCH]"

    fun loadTemplate(context: Context): String {
        val resId = context.resources.getIdentifier("prompt_template", "raw", context.packageName)
        val stream = if (resId != 0) {
            context.resources.openRawResource(resId)
        } else {
            context.resources.openRawResource(R.raw.prompt_template)
        }
        return stream.bufferedReader().use { it.readText() }
    }

    fun buildPrompt(template: String, topic: String): String {
        return template.replace(PLACEHOLDER, topic)
    }
}
