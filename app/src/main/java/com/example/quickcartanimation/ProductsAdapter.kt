package com.example.quickcartanimation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quickcartanimation.ProductsAdapter.ProductsViewHolder
import com.example.quickcartanimation.databinding.ProductsItemBinding
import com.example.quickcartanimation.models.Product

class ProductsAdapter(
    private val products: List<Product>
) : RecyclerView.Adapter<ProductsViewHolder>() {

    inner class ProductsViewHolder(
        parent: ViewGroup,
        private val binding: ProductsItemBinding = ProductsItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindProduct(product: Product) {
            with(binding) {
                tvTitle.text = product.title
                tvDescription.text = product.description
                imgIcon.setImageDrawable(ContextCompat.getDrawable(binding.root.context, product.img))
                quickCounter.setOnFocusChangeListener { view, hasFocus ->
                    if (!hasFocus) quickCounter.collapseView()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.bindProduct(products[position])
    }

    override fun getItemCount() = products.size
}
