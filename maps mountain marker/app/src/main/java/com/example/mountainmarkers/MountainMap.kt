// Copyright 2024 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.mountainmarkers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mountainmarkers.data.utils.DMS
import com.example.mountainmarkers.data.utils.Direction.WEST
import com.example.mountainmarkers.data.utils.toDecimalDegrees
import com.example.mountainmarkers.presentation.AdvancedMarkersMapContent
import com.example.mountainmarkers.presentation.BasicMarkersMapContent
import com.example.mountainmarkers.presentation.ClusteringMarkersMapContent
import com.example.mountainmarkers.presentation.MountainsScreenEvent
import com.example.mountainmarkers.presentation.MountainsScreenViewState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.widgets.ScaleBar
import com.google.maps.android.data.kml.KmlLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Shows a [GoogleMap] with collection of markers
 */
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MountainMap(
    paddingValues: PaddingValues,
    viewState: MountainsScreenViewState.MountainList,
    eventFlow: Flow<MountainsScreenEvent>,
    selectedMarkerType: MarkerType,
) {
    var isMapLoaded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mapId = stringResource(id = R.string.map_id)

    // Inisialisasi posisi awal (bisa menyebabkan isu jika boundingBox besar)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(viewState.boundingBox.center, 5f)
    }

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json)
            )
        )
    }

    // --- Perbaikan 1: Mencegah zoomAll sebelum peta dimuat (untuk event dari ViewModel) ---
    LaunchedEffect(isMapLoaded) {
        if (isMapLoaded) {
            eventFlow.collect { event ->
                when (event) {
                    MountainsScreenEvent.OnZoomAll -> {
                        zoomAll(scope, cameraPositionState, viewState.boundingBox)
                    }
                }
            }
        }
    }

    // --- Perbaikan 2: Mencegah zoomAll sebelum peta dimuat (untuk boundingBox pertama) ---
    LaunchedEffect(key1 = viewState.boundingBox, key2 = isMapLoaded) {
        if (isMapLoaded) {
            zoomAll(scope, cameraPositionState, viewState.boundingBox)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            // Perbaikan: isMapLoaded diubah menjadi true saat peta siap
            onMapLoaded = { isMapLoaded = true },
            googleMapOptionsFactory = {
                GoogleMapOptions().mapId(mapId)
            }
        ) {
            ColoradoPolygon()

            when (selectedMarkerType) {
                MarkerType.Basic -> {
                    BasicMarkersMapContent(
                        mountains = viewState.mountains,
                    )
                }

                MarkerType.Advanced -> {
                    AdvancedMarkersMapContent(
                        mountains = viewState.mountains,
                    )
                }

                MarkerType.Clustered -> {
                    ClusteringMarkersMapContent(
                        mountains = viewState.mountains,
                        onClusterClick = { cluster ->
                            val newZoom = cameraPositionState.position.zoom + 1
                            scope.launch {
                                // Pemanggilan CameraUpdateFactory di sini relatif aman
                                // karena sudah ada di dalam Composable GoogleMap yang dimuat.
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(
                                        cluster.position, newZoom
                                    ),
                                    durationMs = 500,
                                )
                            }
                            false
                        },
                    )
                }
            }

            // MapEffect tidak perlu dicek isMapLoaded karena akan dieksekusi setelah peta siap
            MapEffect(key1 = true) { map ->
                val layer = KmlLayer(map, R.raw.mountain_ranges, context)
                layer.addLayerToMap()
            }
        }

        ScaleBar(
            modifier = Modifier
                .padding(top = 5.dp, end = 15.dp)
                .align(Alignment.TopEnd),
            cameraPositionState = cameraPositionState
        )

        // Indikator loading hanya ditampilkan jika peta BELUM dimuat
        if (!isMapLoaded) {
            AnimatedVisibility(
                modifier = Modifier.matchParentSize(),
                visible = !isMapLoaded,
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .wrapContentSize(Alignment.Center) // Perbaikan: Pastikan berada di tengah
                )
            }
        }
    }
}

@Composable
@GoogleMapComposable
fun ColoradoPolygon() {
    // There are obvious advantages to drawing Colorado in this way...
    val north = 41.0
    val south = 37.0
    val east = DMS(WEST, 102.0, 3.0).toDecimalDegrees()
    val west = DMS(WEST, 109.0, 3.0).toDecimalDegrees()

    val locations = listOf(
        LatLng(north, east),
        LatLng(south, east),
        LatLng(south, west),
        LatLng(north, west),
    )

    Polygon(
        points = locations,
        strokeColor = MaterialTheme.colorScheme.tertiary,
        strokeWidth = 3F,
        fillColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
    )
}

/**
 * Fungsi pembantu yang memindahkan kamera untuk menampilkan semua gunung.
 *
 * @param scope CoroutineScope untuk menjalankan animasi.
 * @param cameraPositionState State posisi kamera.
 * @param boundingBox LatLngBounds yang mencakup semua marker.
 */
fun zoomAll(
    scope: CoroutineScope,
    cameraPositionState: CameraPositionState,
    boundingBox: LatLngBounds
) {
    scope.launch {
        // Angka 64 adalah padding dalam piksel yang diterapkan di sekitar bounding box.
        // Pemanggilan ini sekarang lebih aman karena dipastikan isMapLoaded=true
        // sebelum dipanggil dalam LaunchedEffect.
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngBounds(boundingBox, 64),
            durationMs = 1000
        )
    }
}