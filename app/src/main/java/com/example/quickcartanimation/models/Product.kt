package com.example.quickcartanimation.models

import androidx.annotation.DrawableRes

data class Product(
    val title: String,
    val description: String,
    @DrawableRes val img: Int
)
