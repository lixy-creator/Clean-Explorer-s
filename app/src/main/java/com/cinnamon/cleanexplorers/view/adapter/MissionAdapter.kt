package com.cinnamon.cleanexplorers.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.database.entity.MissionEntity


class MissionAdapter(private val context: Context, private val listMissions: List<MissionEntity>) :
    RecyclerView.Adapter<MissionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mission = listMissions[position]
        holder.textPoint.text = "${mission.point}px"

        if (mission.valid) {
            holder.titleMission.text = "C'est Clean!"
            holder.titleMission.setTextColor(
                ContextCompat.getColor(context, R.color.white)
            )
            holder.textPoint.setTextColor(
                ContextCompat.getColor(context, R.color.white)
            )
            holder.contentLayout.background =
                ContextCompat.getDrawable(context, R.drawable.item_task_valid_shape)
            holder.pointLayout.background =
                ContextCompat.getDrawable(context, R.drawable.point_valid_content_shape)
        } else {
            holder.titleMission.text = mission.name
            holder.titleMission.setTextColor(
                ContextCompat.getColor(context, R.color.grey)
            )
            holder.textPoint.setTextColor(
                ContextCompat.getColor(context, R.color.grey)
            )
            holder.contentLayout.background =
                ContextCompat.getDrawable(context, R.drawable.item_task_shape)
            holder.pointLayout.background =
                ContextCompat.getDrawable(context, R.drawable.point_content_shape)
        }
    }

    override fun getItemCount() = listMissions.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val titleMission: TextView = view.findViewById(R.id.title_task)
        val textPoint: TextView = view.findViewById(R.id.text_point)
        val contentLayout: ConstraintLayout = view.findViewById(R.id.content_task_layout)
        val pointLayout: ConstraintLayout = view.findViewById(R.id.point_layout)

    }
}