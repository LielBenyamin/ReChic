package com.example.rechic.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rechic.databinding.ItemProductBinding
import com.bumptech.glide.Glide
import com.example.rechic.database.local.entities.ProductEntity
import com.example.rechic.model.ProductWithUserProfile
import com.example.rechic.utils.ImageUtils

class ProductAdapter(
    private val onEditClickedListener: ((ProductWithUserProfile) -> Unit)? = null,
    private val onDeleteClickedListener: ((ProductWithUserProfile) -> Unit)? = null,
    private val onCardClickedListener: ((ProductWithUserProfile) -> Unit)? = null,
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    // DiffUtil callback
    private val diffUtil = object : DiffUtil.ItemCallback<ProductWithUserProfile>() {
        override fun areItemsTheSame(
            oldItem: ProductWithUserProfile,
            newItem: ProductWithUserProfile,
        ): Boolean {
            return oldItem.product.productDocumentId == newItem.product.productDocumentId
        }

        override fun areContentsTheSame(
            oldItem: ProductWithUserProfile,
            newItem: ProductWithUserProfile,
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)

    // Function to submit data
    fun submitData(products: List<ProductWithUserProfile>) {
        asyncListDiffer.submitList(products)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productWithUserProfile = asyncListDiffer.currentList[position]
        holder.binding.apply {
            textViewUserName.text = productWithUserProfile.userProfile.userName
            textViewName.text = productWithUserProfile.product.name
            textViewPrice.text = "${productWithUserProfile.product.price}$"
            Glide.with(imageView.context)
                .load(productWithUserProfile.product.imgUrl)
                .centerCrop()
                .placeholder(ImageUtils.createShimmerDrawable())
                .into(imageView)
            imageViewEdit.apply {
                visibility = if (onEditClickedListener != null) View.VISIBLE else View.GONE
                setOnClickListener {
                    onEditClickedListener?.invoke(productWithUserProfile)
                }
            }

            // Show or hide delete button based on the listener
            imageViewDelete.apply {
                visibility = if (onDeleteClickedListener != null) View.VISIBLE else View.GONE
                setOnClickListener {
                    onDeleteClickedListener?.invoke(productWithUserProfile)
                }
            }

            // Handle card click
            root.setOnClickListener {
                onCardClickedListener?.invoke(productWithUserProfile)
            }
        }
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }
}
