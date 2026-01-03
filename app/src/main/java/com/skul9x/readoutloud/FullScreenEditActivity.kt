package com.skul9x.readoutloud

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class FullScreenEditActivity : AppCompatActivity() {

    private lateinit var editText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_edit)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val saveButton = findViewById<TextView>(R.id.saveButton)
        editText = findViewById(R.id.fullScreenEditText)

        val text = intent.getStringExtra(EXTRA_TEXT) ?: ""
        editText.setText(text)

        // Move cursor to end
        editText.setSelection(editText.text.length)
        editText.requestFocus()

        toolbar.setNavigationOnClickListener {
            finish() // Cancel, do nothing
        }

        saveButton.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra(EXTRA_TEXT, editText.text.toString())
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    companion object {
        const val EXTRA_TEXT = "extra_text"
    }
}
