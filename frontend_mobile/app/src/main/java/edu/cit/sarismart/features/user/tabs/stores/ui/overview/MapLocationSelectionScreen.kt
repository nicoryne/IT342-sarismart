package edu.cit.sarismart.features.user.tabs.stores.ui.overview

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapLocationSelectionScreen(
    onLocationSelected: (String, Double, Double) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var selectedLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var selectedLocationName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState()

    val scope = rememberCoroutineScope()
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }

    LaunchedEffect(key1 = true) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            showPermissionDialog = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                } else {
                    showPermissionDialog = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLocation = it
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    scope.launch {
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    }
                    selectedLatLng = currentLatLng
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    if (addresses?.isNotEmpty() == true) {
                        selectedLocationName = addresses[0].getAddressLine(0)
                    }
                }
            }
        }
    }

    val onSearch = {
        if (searchQuery.isNotBlank()) {
            scope.launch {
                try {
                    val addresses = geocoder.getFromLocationName(searchQuery, 1)
                    if (addresses?.isNotEmpty() == true) {
                        val address = addresses[0]
                        val searchedLatLng = LatLng(address.latitude, address.longitude)
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(searchedLatLng, 15f))
                        selectedLatLng = searchedLatLng
                        selectedLocationName = address.getAddressLine(0) ?: ""
                    }
                } catch (e: Exception) {
                    Log.e("MapLocationSelectionScreen", "Error searching location: ${e.message}")
                }
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(text = "Location Permission Required") },
            text = { Text("This app needs location permission to show your current location and select a point on the map.") },
            confirmButton = {
                Button(onClick = {
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                    showPermissionDialog = false
                }) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showPermissionDialog = false
                    onCancel()
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Location") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { onSearch() }
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onCancel) {
                    Icon(Icons.Filled.Cancel, contentDescription = "Cancel")
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedLocationName,
                    modifier = Modifier.width(170.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall,

                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onCancel) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        onLocationSelected(selectedLocationName, selectedLatLng.latitude, selectedLatLng.longitude)
                    },
                    enabled = selectedLocationName.isNotBlank()
                ) {
                    Text("Confirm")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedLatLng = latLng
                    scope.launch {
                        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                        selectedLocationName = if (addresses?.isNotEmpty() == true) {
                            addresses[0].getAddressLine(0) ?: ""
                        } else {
                            "(${latLng.latitude}, ${latLng.longitude})"
                        }
                    }
                }
            ) {
                if (currentLocation != null && locationPermissionGranted) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                currentLocation!!.latitude,
                                currentLocation!!.longitude
                            )
                        ),
                        title = "Current Location",
                        icon = (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)),
                    )
                }
                Marker(
                    state = MarkerState(position = selectedLatLng),
                    title = if (selectedLocationName.isNotBlank()) "Selected Location" else "Tap to Select",
                )
            }
        }
    }
}