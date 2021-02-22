package com.app.foodplace.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.foodplace.R
import com.app.foodplace.ui.adapter.LocationsAdapter
import com.app.foodplace.viewmodel.LocationViewModel
import com.esri.arcgisruntime.geometry.Point
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

class LocationsFragment : Fragment() {
    private val locationViewModel: LocationViewModel by lazy { getSharedViewModel() }

    private lateinit var locationList: RecyclerView

    private val adapter: LocationsAdapter by lazy { LocationsAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_locations, container, false)
        setupView(root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        locationList.layoutManager = layoutManager
        locationList.adapter = adapter

        locationViewModel.getDeviceLocation().observe(viewLifecycleOwner) {
            it?.let {
                locationViewModel.refreshPlaces(Point(it.latitude, it.longitude))
            }
        }

        locationViewModel.nearbyData.observe(viewLifecycleOwner) {
            adapter.notifyData(it)
        }
    }

    private fun setupView(root: View) {
        locationList = root.findViewById(R.id.locationsView)
    }
}