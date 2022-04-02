package com.example.quickcartanimation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quickcartanimation.databinding.ActivityMainBinding
import com.example.quickcartanimation.models.Product

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val products = listOf(
        Product("Banano Uraba", "$ 2.780", R.drawable.bananas),
        Product("Jumbo - Tomate chonto", "$ 2.991", R.drawable.tomatos),
        Product("Cebolla cabezona", "$ 3.832", R.drawable.cebolla),
        Product("Jumbo - Zanahoria", "$ 4.502", R.drawable.zanahoria),
        Product("Fresas", "$ 932", R.drawable.fresas),
        Product("Cilantro", "$ 1.720", R.drawable.cilantro),
        Product("Aguacate hass", "$ 5.181", R.drawable.aguacate),
        Product("Jumbo - Arandanos", "$ 3.422", R.drawable.arandanos),
        Product("Jumbo - Pimentones rojos", "$ 2.780", R.drawable.pimenton)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.setupView()
    }

    private fun ActivityMainBinding.setupView() {
        rvProducts.adapter = ProductsAdapter(products)
    }
}
