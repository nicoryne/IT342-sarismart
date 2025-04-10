package edu.cit.sarismart.ui.guest

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun GuestMapScreen(
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // search text
    var searchText by remember { mutableStateOf("") }

    // default (Manila, Philippines)
    val defaultLocation = LatLng(14.5995, 120.9842)

    // camera pos
    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    // current location (user)
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    // check if have location perms
    val hasLocationPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // perms launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(it.latitude, it.longitude), 15f
                        )
                    }
                }
            } catch (e: SecurityException) {
                /* TODO: handle error */
            }
        }
    }

    // request perms if not granted
    LaunchedEffect(key1 = true) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(it.latitude, it.longitude), 15f
                        )
                    }
                }
            } catch (e: SecurityException) {
                /* TODO: handle error */
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        ) {
            // sample markers for other stores
            val storeLocations = listOf(
                LatLng(14.6010, 120.9876) to "Store 1",
                LatLng(14.5950, 120.9800) to "Store 2",
                LatLng(14.6050, 120.9750) to "Store 3"
            )

            storeLocations.forEach { (position, title) ->
                Marker(
                    state = MarkerState(position = position),
                    title = title,
                    snippet = "Tap to view details"
                )
            }

            // show marker on user
            userLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Your Location"
                )
            }
        }

        // location button
        FloatingActionButton(
            onClick = {
                userLocation?.let {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "My Location",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        // search bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 64.dp)
                .align(Alignment.TopCenter),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Find nearby stores") },
                modifier = Modifier.padding(4.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
            )
        }

        // back to login button
        Button(
            onClick = { onNavigateToLogin() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 100.dp)
                .align(Alignment.BottomStart),

            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                "Back to Login",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp),
            )
        }
    }
}