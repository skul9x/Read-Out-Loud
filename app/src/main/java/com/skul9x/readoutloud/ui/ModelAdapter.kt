package com.skul9x.readoutloud.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skul9x.readoutloud.data.ApiKeyManager
import com.skul9x.readoutloud.data.ModelItem
import com.skul9x.readoutloud.data.ModelQuotaManager
import com.skul9x.readoutloud.databinding.ItemModelBinding
import com.skul9x.readoutloud.utils.SecurityUtils

class ModelAdapter(
    private var models: List<ModelItem>,
    private val quotaManager: ModelQuotaManager,
    private val apiKeyManager: ApiKeyManager,
    private val onToggle: (Int) -> Unit,
    private val onMoveUp: (Int) -> Unit,
    private val onMoveDown: (Int) -> Unit
) : RecyclerView.Adapter<ModelAdapter.ModelViewHolder>() {

    fun updateModels(newModels: List<ModelItem>) {
        models = newModels
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val binding = ItemModelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bind(models[position], position)
    }

    override fun getItemCount(): Int = models.size

    inner class ModelViewHolder(private val binding: ItemModelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ModelItem, position: Int) {
            binding.modelNameText.text = item.name.substringAfter("/")
            binding.modelCheckBox.isChecked = item.isEnabled
            
            // Determine status
            val status = getModelStatus(item.name)
            binding.modelStatusText.text = status
            
            binding.modelCheckBox.setOnClickListener { onToggle(position) }
            binding.moveUpButton.setOnClickListener { onMoveUp(position) }
            binding.moveDownButton.setOnClickListener { onMoveDown(position) }
            
            // Disable buttons at boundaries
            binding.moveUpButton.isEnabled = position > 0
            binding.moveUpButton.alpha = if (position > 0) 1.0f else 0.3f
            
            binding.moveDownButton.isEnabled = position < models.size - 1
            binding.moveDownButton.alpha = if (position < models.size - 1) 1.0f else 0.3f
        }

        private fun getModelStatus(modelName: String): String {
            val keys = apiKeyManager.getApiKeys()
            if (keys.isEmpty()) return "No API Keys"
            
            var availableCount = 0
            var cooldownCount = 0
            var exhaustedCount = 0
            
            keys.forEach { key ->
                val pairHash = SecurityUtils.getPairHash(modelName, key)
                // We don't have a direct way to check cooldown vs exhausted in ModelQuotaManager 
                // without adding methods, but isAvailable checks both.
                // For UI, let's just show a summary.
                if (quotaManager.isAvailable(pairHash)) {
                    availableCount++
                } else {
                    // Logic to distinguish would be nice, but let's keep it simple for now
                    // based on what's available in ModelQuotaManager.
                    exhaustedCount++ 
                }
            }
            
            return when {
                availableCount == keys.size -> "Available"
                availableCount > 0 -> "Partial ($availableCount/${keys.size})"
                else -> "Unavailable / Cooldown"
            }
        }
    }
}
