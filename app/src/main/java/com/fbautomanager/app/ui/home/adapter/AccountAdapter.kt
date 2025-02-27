package com.fbautomanager.app.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fbautomanager.app.data.entity.Account
import com.fbautomanager.app.databinding.ItemAccountBinding

class AccountAdapter(
    private val onActiveStatusChanged: (Account, Boolean) -> Unit,
    private val onAccountSelected: (Account) -> Unit,
    private val onDeleteAccount: (Account) -> Unit
) : ListAdapter<Account, AccountAdapter.AccountViewHolder>(AccountDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemAccountBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = getItem(position)
        holder.bind(account)
    }

    inner class AccountViewHolder(private val binding: ItemAccountBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAccountSelected(getItem(position))
                }
            }

            binding.switchActive.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onActiveStatusChanged(getItem(position), isChecked)
                }
            }

            binding.btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteAccount(getItem(position))
                }
            }
        }

        fun bind(account: Account) {
            binding.tvAccountName.text = account.name
            binding.tvPackageName.text = account.packageName
            binding.switchActive.isChecked = account.isActive
            binding.tvStatus.text = if (account.isActive) "Đang chạy" else "Tạm dừng"
        }
    }

    class AccountDiffCallback : DiffUtil.ItemCallback<Account>() {
        override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem == newItem
        }
    }
} 