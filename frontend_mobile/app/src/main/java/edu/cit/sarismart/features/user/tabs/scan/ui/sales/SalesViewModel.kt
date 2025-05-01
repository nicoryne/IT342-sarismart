package edu.cit.sarismart.features.user.tabs.scan.ui.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SalesViewModel @Inject constructor() : ViewModel() {

    private val _sales = MutableStateFlow<List<SaleTransaction>>(emptyList())
    val sales: StateFlow<List<SaleTransaction>> = _sales

    private val _totalSales = MutableStateFlow(0.0)
    val totalSales: StateFlow<Double> = _totalSales

    private val _salesByDay = MutableStateFlow<Map<String, Double>>(emptyMap())
    val salesByDay: StateFlow<Map<String, Double>> = _salesByDay

    private val _topProducts = MutableStateFlow<List<ProductSales>>(emptyList())
    val topProducts: StateFlow<List<ProductSales>> = _topProducts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Mock product data
    private val mockProducts = listOf(
        Pair("Rice", 50.0),
        Pair("Canned Goods", 25.0),
        Pair("Soap", 15.0),
        Pair("Shampoo", 35.0),
        Pair("Toothpaste", 20.0),
        Pair("Cooking Oil", 45.0),
        Pair("Sugar", 30.0),
        Pair("Coffee", 40.0)
    )

    fun loadSales(storeId: Long, timeRange: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Simulate network delay
                delay(1000)

                // In a real app, you would call your API here
                // val salesData = salesRepository.getSalesForStore(storeId, timeRange)

                // Generate mock sales data based on time range
                val mockSales = generateMockSales(timeRange)
                _sales.value = mockSales

                // Calculate total sales
                val total = mockSales.sumOf { it.total }
                _totalSales.value = total

                // Calculate sales by day
                val salesByDay = calculateSalesByDay(mockSales)
                _salesByDay.value = salesByDay

                // Calculate top products
                val topProducts = calculateTopProducts(mockSales)
                _topProducts.value = topProducts

            } catch (e: Exception) {
                _errorMessage.value = "Error loading sales data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateMockSales(timeRange: String): List<SaleTransaction> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time

        // Set start date based on time range
        when (timeRange) {
            "Today" -> calendar.add(Calendar.DAY_OF_MONTH, -1)
            "This Week" -> calendar.add(Calendar.DAY_OF_MONTH, -7)
            "This Month" -> calendar.add(Calendar.MONTH, -1)
            "This Year" -> calendar.add(Calendar.YEAR, -1)
            else -> calendar.add(Calendar.DAY_OF_MONTH, -7) // Default to week
        }

        val startDate = calendar.time
        val random = Random(System.currentTimeMillis())

        // Generate random number of transactions
        val transactionCount = when (timeRange) {
            "Today" -> random.nextInt(1, 10)
            "This Week" -> random.nextInt(5, 30)
            "This Month" -> random.nextInt(20, 100)
            "This Year" -> random.nextInt(50, 200)
            else -> random.nextInt(5, 30)
        }

        val transactions = mutableListOf<SaleTransaction>()

        for (i in 1..transactionCount) {
            // Generate random date between start and end
            val transactionDate = randomDateBetween(startDate, endDate)

            // Generate random items for this transaction
            val itemCount = random.nextInt(1, 6)
            val items = mutableListOf<SaleItem>()

            for (j in 1..itemCount) {
                val product = mockProducts.random()
                val quantity = random.nextInt(1, 5)

                items.add(
                    SaleItem(
                        productId = j.toLong(),
                        productName = product.first,
                        quantity = quantity,
                        price = product.second
                    )
                )
            }

            val total = items.sumOf { it.price * it.quantity }

            transactions.add(
                SaleTransaction(
                    id = i.toLong(),
                    date = transactionDate,
                    total = total,
                    items = items
                )
            )
        }

        // Sort by date, newest first
        return transactions.sortedByDescending { it.date }
    }

    private fun randomDateBetween(start: Date, end: Date): Date {
        val startMillis = start.time
        val endMillis = end.time
        val randomMillis = startMillis + Random.nextLong(endMillis - startMillis)
        return Date(randomMillis)
    }

    private fun calculateSalesByDay(sales: List<SaleTransaction>): Map<String, Double> {
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        return sales
            .groupBy { dateFormat.format(it.date) }
            .mapValues { entry -> entry.value.sumOf { it.total } }
            .toSortedMap()
    }

    private fun calculateTopProducts(sales: List<SaleTransaction>): List<ProductSales> {
        val productSalesMap = mutableMapOf<String, ProductSales>()

        sales.forEach { transaction ->
            transaction.items.forEach { item ->
                val existing = productSalesMap[item.productName]
                if (existing != null) {
                    productSalesMap[item.productName] = existing.copy(
                        quantitySold = existing.quantitySold + item.quantity,
                        totalSales = existing.totalSales + (item.price * item.quantity)
                    )
                } else {
                    productSalesMap[item.productName] = ProductSales(
                        productId = item.productId,
                        productName = item.productName,
                        quantitySold = item.quantity,
                        totalSales = item.price * item.quantity
                    )
                }
            }
        }

        return productSalesMap.values.sortedByDescending { it.totalSales }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}