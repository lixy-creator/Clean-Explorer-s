package com.cinnamon.cleanexplorers.view.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.api.model.CleanStop
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MapInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    private val window =
        (context as Activity).layoutInflater.inflate(R.layout.clean_stop_info_layout, null)


    private fun setView(marker: Marker?, view: View) {
        marker ?: return
        val cleanStop = marker.tag as? CleanStop ?: return
        val title = view.findViewById<TextView>(R.id.title_clean_stop)
        title.text = cleanStop.name


    }


    override fun getInfoWindow(marker: Marker?): View {
        setView(marker, window)
        return window
    }

    override fun getInfoContents(marker: Marker?): View {
        setView(marker, window)
        return window
    }
}