package com.rodolfonavalon.canadatransit.view.adapter.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rodolfonavalon.canadatransit.databinding.ItemOperatorBinding
import com.rodolfonavalon.canadatransit.model.database.transit.Operator
import com.rodolfonavalon.canadatransit.viewmodel.MainViewModel

class OperatorViewHolder(val binding: ItemOperatorBinding) : RecyclerView.ViewHolder(binding.root)

class OperatorAdapter(private val viewModel: MainViewModel) : RecyclerView.Adapter<OperatorViewHolder>() {
    val operators = mutableListOf<Operator>()

    fun addAll(operators: List<Operator>) {
        this.operators.clear()
        this.operators.addAll(operators)
        notifyDataSetChanged()
    }

    fun clear() {
        this.operators.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperatorViewHolder {
        val binding = ItemOperatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OperatorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OperatorViewHolder, position: Int) {
        val context = holder.binding.root.context
        val operator = operators[position]
        holder.binding.textviewOperatorTitle.text = operator.name
        holder.binding.textviewOperatorWebsite.text = operator.website
        val operatorIconPath = operator.website?.split("/")?.subList(2, 3)?.joinToString("/")
        Glide.with(context)
                .load("https://icons.duckduckgo.com/ip3/$operatorIconPath.ico")
                .into(holder.binding.imageviewOperatorIcon)
    }

    override fun getItemCount(): Int {
        return operators.count()
    }
}
