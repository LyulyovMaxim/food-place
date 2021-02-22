package com.app.foodplace.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.foodplace.BuildConfig
import com.app.foodplace.R
import com.app.foodplace.viewmodel.LocationViewModel
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel


class MapFragment : Fragment() {

    private val TAG = "{" + MapFragment::class.java.simpleName + "}"
    private val locationViewModel: LocationViewModel by lazy { getSharedViewModel() }
    private val SCALE = 5000.0

    private lateinit var mapView: MapView

    private val locationDisplay: LocationDisplay by lazy { mapView.locationDisplay }
    private val simpleMarkerSymbol by lazy {
        SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, -0xa8cd, 10f)
    }
    private val graphicsOverlay by lazy { GraphicsOverlay() }
    private val blueOutlineSymbol by lazy {
        SimpleLineSymbol(
            SimpleLineSymbol.Style.SOLID,
            -0xff9c01,
            2f
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = layoutInflater.inflate(R.layout.fragment_map, container, false)
        setupMap(root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationViewModel.getDeviceLocation().observe(viewLifecycleOwner) {
            Log.d(TAG, "Current location $it")
            it?.let {
                mapView.setViewpoint(Viewpoint(it.latitude, it.longitude, SCALE))
            }
        }

        locationViewModel.nearbyData.observe(viewLifecycleOwner) {
            Log.d(TAG, "Nearby places $it")
            clearPoints()
            it.forEach { place ->
                addPoint(place.location)
            }
        }
    }

    override fun onPause() {
        mapView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onDestroy() {
        mapView.dispose()
        super.onDestroy()
    }

    private fun setupMap(root: View) {
        mapView = root.findViewById(R.id.mapView)
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.API_KEY)

        mapView.map = ArcGISMap(BasemapStyle.ARCGIS_IMAGERY)
        locationDisplay.addDataSourceStatusChangedListener {
            Log.d(TAG, "it.isStarted=[${it.isStarted}], it.error=[${it.error}]")
            if (!it.isStarted && it.error != null) {
                requestPermissions(it)
            }
        }
        Log.d(TAG, "locationDisplay.isStarted=[${locationDisplay.isStarted}]")
        if (!locationDisplay.isStarted) locationDisplay.startAsync()

        mapView.graphicsOverlays.add(graphicsOverlay)
        simpleMarkerSymbol.outline = blueOutlineSymbol
    }

    private fun addPoint(point: Point) {
        val pointGraphic = Graphic(point, simpleMarkerSymbol)
        graphicsOverlay.graphics.add(pointGraphic)
    }

    private fun clearPoints() {
        graphicsOverlay.graphics.clear()
    }

    private fun requestPermissions(dataSourceStatusChangedEvent: LocationDisplay.DataSourceStatusChangedEvent) {
        val requestCode = 2
        val reqPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val permissionCheckFineLocation =
            ContextCompat.checkSelfPermission(requireContext(), reqPermissions[0]) ==
                    PackageManager.PERMISSION_GRANTED
        val permissionCheckCoarseLocation =
            ContextCompat.checkSelfPermission(requireContext(), reqPermissions[1]) ==
                    PackageManager.PERMISSION_GRANTED
        if (!(permissionCheckFineLocation && permissionCheckCoarseLocation)) { // if permissions are not already granted, request permission from the user
            ActivityCompat.requestPermissions(requireActivity(), reqPermissions, requestCode)
        } else {
            val message = String.format(
                "Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                    .source.locationDataSource.error.message
            )
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationDisplay.startAsync()
        } else {
            Toast.makeText(
                context,
                resources.getString(R.string.location_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}