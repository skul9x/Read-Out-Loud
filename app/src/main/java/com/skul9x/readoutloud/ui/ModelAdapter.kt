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
    private val onMoveDown: (Int) -> Unit,
    private val onDelete: (Int) -> Unit,
    private val onEdit: (Int) -> Unit
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
            binding.priorityBadgeText.text = "#${position + 1}"
            binding.modelNameText.text = item.name.substringAfter("/")
            binding.modelCheckBox.isChecked = item.isEnabled
            binding.modelItemCardView.alpha = if (item.isEnabled) 1.0f else 0.55f
            
            binding.modelCheckBox.setOnClickListener { onToggle(position) }
            binding.moveUpButton.setOnClickListener { onMoveUp(position) }
            binding.moveDownButton.setOnClickListener { onMoveDown(position) }
            binding.deleteButton.setOnClickListener { onDelete(position) }
            
            binding.modelNameText.setOnClickListener { onEdit(position) }
            binding.root.setOnLongClickListener {
                onEdit(position)
                true
            }
            
            // Disable buttons at boundaries
            binding.moveUpButton.isEnabled = position > 0
            binding.moveUpButton.alpha = if (position > 0) 1.0f else 0.3f
            
            binding.moveDownButton.isEnabled = position < models.size - 1
            binding.moveDownButton.alpha = if (position < models.size - 1) 1.0f else 0.3f
        }
    }
}
