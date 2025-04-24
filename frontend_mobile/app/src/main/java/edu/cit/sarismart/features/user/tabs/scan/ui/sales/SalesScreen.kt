package edu.cit.sarismart.features.user.tabs.scan.ui.sales

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    onNavigateBack: () -> Unit,
    storeId: Long,
    viewModel: SalesViewModel = hiltViewModel()
) {
    val sales by viewModel.sales.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val totalSales by viewModel.totalSales.collectAsState()
    val salesByDay by viewModel.salesByDay.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedTabIndex by remember { mutableStateOf(0) }
    var selectedTimeRange by remember { mutableStateOf("This Week") }
    var showTimeRangeDropdown by remember { mutableStateOf(false) }

    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearError()
            }
        }
    }

    // Load sales data when the screen is first displayed
    LaunchedEffect(storeId, selectedTimeRange) {
        viewModel.loadSales(storeId, selectedTimeRange)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales Reports") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs for different sales views
            TabRow(selectedTabIndex = selectedTabIndex) {
                listOf("Overview", "Transactions", "Products").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Time range selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Time Range:")

                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .clickable { showTimeRangeDropdown = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedTimeRange,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Select",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showTimeRangeDropdown,
                        onDismissRequest = { showTimeRangeDropdown = false }
                    ) {
                        listOf("Today", "This Week", "This Month", "This Year").forEach { range ->
                            DropdownMenuItem(
                                text = { Text(range) },
                                onClick = {
                                    selectedTimeRange = range
                                    showTimeRangeDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                // Show loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                when (selectedTabIndex) {
                    0 -> SalesOverviewTab(totalSales, salesByDay)
                    1 -> SalesTransactionsTab(sales)
                    2 -> SalesProductsTab(viewModel)
                }
            }
        }
    }
}

@Composable
fun SalesOverviewTab(totalSales: Double, salesByDay: Map<String, Double>) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Total sales card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Sales",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currencyFormatter.format(totalSales),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sales chart
        Text(
            text = "Sales Trend",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (salesByDay.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No sales data available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            SalesChart(
                salesData = salesByDay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sales summary
        Text(
            text = "Summary",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SummaryCard(
                title = "Transactions",
                value = "${salesByDay.size}",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            SummaryCard(
                title = "Avg. Sale",
                value = if (salesByDay.isNotEmpty()) {
                    currencyFormatter.format(totalSales / salesByDay.size)
                } else {
                    currencyFormatter.format(0)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SalesChart(salesData: Map<String, Double>, modifier: Modifier = Modifier) {
    if (salesData.isEmpty()) return

    // Remember the color outside the Canvas scope
    val primaryColor = MaterialTheme.colorScheme.primary
    val values = salesData.values.toList()
    val maxValue = values.maxOrNull() ?: 0.0
    val minValue = values.minOrNull() ?: 0.0

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 16.dp.toPx()

        // Draw axes
        drawLine(
            color = Color.Gray,
            start = Offset(padding, height - padding),
            end = Offset(width - padding, height - padding),
            strokeWidth = 2f
        )

        drawLine(
            color = Color.Gray,
            start = Offset(padding, padding),
            end = Offset(padding, height - padding),
            strokeWidth = 2f
        )

        if (salesData.size > 1) {
            val path = Path()
            val points = mutableListOf<Offset>()

            // Calculate points
            salesData.entries.forEachIndexed { index, entry ->
                val x = padding + (index.toFloat() / (salesData.size - 1)) * (width - 2 * padding)
                val normalizedValue = if (maxValue > minValue) {
                    ((entry.value - minValue) / (maxValue - minValue)).toFloat()
                } else {
                    0.5f
                }
                val y = height - padding - normalizedValue * (height - 2 * padding)
                points.add(Offset(x, y))
            }

            // Draw line chart
            path.moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                path.lineTo(points[i].x, points[i].y)
            }

            // Use the remembered color here
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 3f)
            )

            // Draw points
            points.forEach { point ->
                // Use the remembered color here
                drawCircle(
                    color = primaryColor,
                    radius = 4.dp.toPx(),
                    center = point
                )
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SalesTransactionsTab(sales: List<SaleTransaction>) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    if (sales.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Receipt,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No transactions yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sales) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    dateFormatter = dateFormatter,
                    timeFormatter = timeFormatter,
                    currencyFormatter = currencyFormatter
                )
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: SaleTransaction,
    dateFormatter: SimpleDateFormat,
    timeFormatter: SimpleDateFormat,
    currencyFormatter: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Transaction #${transaction.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = currencyFormatter.format(transaction.total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateFormatter.format(transaction.date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Text(
                    text = timeFormatter.format(transaction.date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${transaction.items.size} items",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            transaction.items.take(3).forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.quantity}x ${item.productName}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = currencyFormatter.format(item.price * item.quantity),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (transaction.items.size > 3) {
                Text(
                    text = "... and ${transaction.items.size - 3} more items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun SalesProductsTab(viewModel: SalesViewModel) {
    val topProducts by viewModel.topProducts.collectAsState()
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Top Selling Products",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (topProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No product sales data available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(topProducts) { product ->
                    TopProductItem(
                        product = product,
                        currencyFormatter = currencyFormatter,
                        totalSales = topProducts.sumOf { it.totalSales }
                    )
                }
            }
        }
    }
}

@Composable
fun TopProductItem(
    product: ProductSales,
    currencyFormatter: NumberFormat,
    totalSales: Double
) {
    val percentage = if (totalSales > 0) {
        ((product.totalSales / totalSales) * 100).roundToInt()
    } else {
        0
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = currencyFormatter.format(product.totalSales),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${product.quantitySold} units sold",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Text(
                    text = "$percentage% of sales",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar showing percentage of total sales
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percentage / 100f)
                        .height(8.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

// Data classes for sales
data class SaleTransaction(
    val id: Long,
    val date: Date,
    val total: Double,
    val items: List<SaleItem>
)

data class SaleItem(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: Double
)

data class ProductSales(
    val productId: Long,
    val productName: String,
    val quantitySold: Int,
    val totalSales: Double
)