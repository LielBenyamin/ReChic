package com.example.rechic.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.example.rechic.R
import com.example.rechic.model.Country

class CountrySpinnerAdapter(context: Context, private val countries: List<Country?>) :
    ArrayAdapter<Country?>(context, R.layout.item_spinner, countries) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent, true)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent, false)
    }

    private fun createView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
        isMainView: Boolean,
    ): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_spinner, parent, false)
        val imageView = view.findViewById<ImageView>(R.id.flagImage)
        val textView = view.findViewById<TextView>(R.id.prefixText)

        val country = getItem(position)

        if (country == null) {
            textView.text = "Select Country"
            imageView.visibility = View.GONE
        } else {
            textView.text = country.getPrefix()
            imageView.visibility = View.VISIBLE
            Glide.with(context).load(country.flags.png).into(imageView)
        }

        if (isMainView) {
            imageView.visibility = View.GONE
        }

        return view
    }
}
