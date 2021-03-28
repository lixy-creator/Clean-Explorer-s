package com.cinnamon.cleanexplorers.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.api.model.Mission


class MissionAdapter(private val listMissions: List<Mission>) :
    RecyclerView.Adapter<MissionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mission = listMissions[position]
        holder.titleMission.text = mission.name
        holder.textPoint.text = "${mission.point}px"
    }

    override fun getItemCount() = listMissions.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val titleMission: TextView = view.findViewById(R.id.title_task)
        val textPoint: TextView = view.findViewById(R.id.text_point)

    }
}