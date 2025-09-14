package com.skul9x.readoutloud

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.skul9x.readoutloud.TtsService.State

class QuickSettingsTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        when (TtsService.currentState) {
            State.IDLE -> readFromClipboard()
            State.PLAYING -> sendAction(TtsService.ACTION_PAUSE)
            State.PAUSED -> sendAction(TtsService.ACTION_RESUME)
        }
    }

    private fun readFromClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val textToRead = clipboard.primaryClip?.getItemAt(0)?.text?.toString()

        if (textToRead.isNullOrBlank()) {
            Toast.makeText(this, "Clipboard rỗng", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, TtsService::class.java).apply {
            action = TtsService.ACTION_READ_CLIPBOARD
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun sendAction(action: String) {
        val intent = Intent(this, TtsService::class.java).apply { this.action = action }
        startService(intent)
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        when (TtsService.currentState) {
            State.IDLE -> {
                tile.state = Tile.STATE_INACTIVE
                tile.label = getString(R.string.qs_tile_label_read)
                tile.icon = Icon.createWithResource(this, R.drawable.ic_qs_read_clipboard)
            }
            State.PLAYING -> {
                tile.state = Tile.STATE_ACTIVE
                tile.label = getString(R.string.qs_tile_label_pause)
                tile.icon = Icon.createWithResource(this, R.drawable.ic_notification_pause)
            }
            State.PAUSED -> {
                tile.state = Tile.STATE_ACTIVE
                tile.label = getString(R.string.qs_tile_label_resume)
                tile.icon = Icon.createWithResource(this, R.drawable.ic_notification_resume)
            }
        }
        tile.updateTile()
    }
}

