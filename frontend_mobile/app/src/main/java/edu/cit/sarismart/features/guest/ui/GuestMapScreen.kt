package edu.cit.sarismart.features.guest.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import edu.cit.sarismart.features.user.tabs.maps.ui.map.StoreProduct
import edu.cit.sarismart.features.user.tabs.stores.data.models.Store
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.random.Random
import androidx.core.graphics.createBitmap

@Composable
fun GuestMapScreen(
    onNavigateToLogin: () -> Unit = {},
    viewModel: GuestMapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var searchText by remember { mutableStateOf("") }
    val nearbyStores by viewModel.filteredStores.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedStore by remember { mutableStateOf<Store?>(null) }

    val mockProducts = remember {
        listOf(
            StoreProduct(1, "Rice", 50.0, 100),
            StoreProduct(2, "Canned Goods", 25.0, 50),
            StoreProduct(3, "Soap", 15.0, 30),
            StoreProduct(4, "Shampoo", 35.0, 20),
            StoreProduct(5, "Toothpaste", 20.0, 40)
        )
    }

    val defaultLocation = LatLng(14.5995, 120.9842)

    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val storeColors = remember { mutableMapOf<Long, Color>() }

    LaunchedEffect(nearbyStores) {
        nearbyStores.forEach { store ->
            if (!storeColors.containsKey(store.id)) {
                val hue = Random.nextFloat() * 360f
                storeColors[store.id] = androidx.compose.ui.graphics.Color.hsv(hue, 0.8f, 0.9f)
            }
        }
    }

    val hasLocationPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getUserLocation(fusedLocationClient) { location ->
                userLocation = location
                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)

                viewModel.fetchNearbyStores(location)
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(key1 = true) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getUserLocation(fusedLocationClient) { location ->
                userLocation = location
                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)

                viewModel.fetchNearbyStores(location)
            }
        }
    }

    LaunchedEffect(searchText) {
        delay(300)
        viewModel.filterStores(searchText)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = true
            ),
            onMapLoaded = {
                if (nearbyStores.isNotEmpty() && userLocation != null) {
                    scope.launch {
                        fitMapToShowAllMarkers(nearbyStores, userLocation!!, cameraPositionState)
                    }
                }
            }
        ) {
            nearbyStores.forEach { store ->
                store.latitude?.let { lat ->
                    store.longitude?.let { lng ->
                        val position = LatLng(lat, lng)
                        val storeColor = storeColors[store.id] ?: MaterialTheme.colorScheme.primary

                        val customMarker = createCustomMarkerFromVector(
                            context = context,
                            vectorResId = edu.cit.sarismart.R.drawable.custom_marker_icon,
                            color = storeColor,
                            label = store.storeName.take(1).uppercase()
                        )

                        Marker(
                            state = MarkerState(position = position),
                            title = store.storeName,
                            snippet = store.location ?: "No address",
                            icon = customMarker,
                            onClick = {
                                selectedStore = store
                                true
                            }
                        )
                    }
                }
            }

            userLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Your Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                    flat = true,
                    rotation = 0f,
                    zIndex = 2f
                )

                Circle(
                    center = location,
                    radius = 1000.0,
                    strokeWidth = 2f,
                    strokeColor = Color(0xFF2196F3),
                    fillColor = Color(0x302196F3)
                )
            }
        }

        // Search bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 64.dp)
                .align(Alignment.TopCenter),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 8.dp)
                )

                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Find nearby stores") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    ),
                )

                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { searchText = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Location and refresh buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 24.dp, vertical = 80.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Refresh button
            FloatingActionButton(
                onClick = {
                    userLocation?.let {
                        viewModel.fetchNearbyStores(it)
                    }
                },
                modifier = Modifier.size(48.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // My location button
            FloatingActionButton(
                onClick = {
                    userLocation?.let {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
                    }
                },
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "My Location",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Login button
        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .align(Alignment.BottomCenter),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Login,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "Login to Access More Features",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp),
            )
        }

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        // Store details card with products
        AnimatedVisibility(
            visible = selectedStore != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp, start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            selectedStore?.let { store ->
                StoreDetailsWithProductsCard(
                    store = store,
                    products = mockProducts,
                    onClose = { selectedStore = null },
                    onLogin = onNavigateToLogin
                )
            }
        }

        // Snackbar for errors
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp, start = 16.dp, end = 16.dp)
        ) { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun StoreDetailsWithProductsCard(
    store: Store,
    products: List<StoreProduct>,
    onClose: () -> Unit,
    onLogin: () -> Unit
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }
    var selectedTabIndex by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Store icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Store name and location
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = store.storeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    store.location?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Close button
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs for Info and Products
            TabRow(selectedTabIndex = selectedTabIndex) {
                listOf("Info", "Products").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTabIndex) {
                0 -> {
                    // Store Info Tab
                    Column {
                        InfoRow(title = "Owner", value = store.owner?.fullName ?: "Unknown")
                        InfoRow(title = "Email", value = store.owner?.email ?: "No email")
                        InfoRow(title = "Phone", value = store.owner?.phone ?: "No phone")

                        Spacer(modifier = Modifier.height(16.dp))

                        // Login button
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onLogin),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "Login to View More Details",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                1 -> {
                    // Products Tab
                    if (products.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Inventory,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "No products available",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(products) { product ->
                                ProductItem(
                                    product = product,
                                    currencyFormatter = currencyFormatter
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProductItem(
    product: StoreProduct,
    currencyFormatter: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currencyFormatter.format(product.price),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Stock: ${product.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (product.quantity < 10)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Progress bar for stock level
            Spacer(modifier = Modifier.height(4.dp))

            val stockPercentage = (product.quantity.toFloat() / 100f).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(stockPercentage)
                        .height(4.dp)
                        .background(
                            when {
                                product.quantity < 10 -> MaterialTheme.colorScheme.error
                                product.quantity < 30 -> Color(0xFFFFA000) // Amber color for warnings
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                )
            }
        }
    }
}

// Helper function to get user location
private fun getUserLocation(
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocationReceived: (LatLng) -> Unit
) {
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                onLocationReceived(LatLng(it.latitude, it.longitude))
            }
        }
    } catch (e: SecurityException) {
        Log.e("GuestMapScreen", "Error getting location: ${e.message}")
    }
}

// Helper function to fit map to show all markers
private suspend fun fitMapToShowAllMarkers(
    stores: List<Store>,
    userLocation: LatLng,
    cameraPositionState: com.google.maps.android.compose.CameraPositionState
) {
    val builder = LatLngBounds.Builder()

    // Add user location
    builder.include(userLocation)

    // Add all store locations
    var hasValidStores = false
    stores.forEach { store ->
        store.latitude?.let { lat ->
            store.longitude?.let { lng ->
                builder.include(LatLng(lat, lng))
                hasValidStores = true
            }
        }
    }

    if (!hasValidStores) {
        // If no valid stores, just center on user location
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(userLocation, 15f),
            durationMs = 500
        )
        return
    }

    try {
        val bounds = builder.build()
        val padding = 100 // Padding in pixels

        // Update camera position to show all markers
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(bounds, padding),
            durationMs = 500
        )
    } catch (e: Exception) {
        Log.e("GuestMapScreen", "Error fitting map to markers: ${e.message}")
        // Fallback to centering on user location
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(userLocation, 15f),
            durationMs = 500
        )
    }
}

// Helper function to create custom marker with color and label
private fun createCustomMarkerFromVector(
    context: android.content.Context,
    vectorResId: Int,
    color: Color,
    label: String
): BitmapDescriptor {
    val drawable = ContextCompat.getDrawable(context, vectorResId)
    drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

    // Apply color tint
    DrawableCompat.setTint(DrawableCompat.wrap(drawable!!), color.toArgb())

    val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)

    val canvas = Canvas(bitmap)
    drawable.draw(canvas)

    // Add text label
    val paint = android.graphics.Paint().apply {
        this.color = android.graphics.Color.WHITE
        this.textSize = 24f
        this.textAlign = android.graphics.Paint.Align.CENTER
        this.isFakeBoldText = true
    }

    // Draw text in the center
    canvas.drawText(
        label,
        bitmap.width / 2f,
        bitmap.height / 2f + paint.textSize / 3, // Adjust for vertical centering
        paint
    )

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}