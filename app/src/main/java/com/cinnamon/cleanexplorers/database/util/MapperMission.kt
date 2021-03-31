package com.cinnamon.cleanexplorers.database.util

import com.cinnamon.cleanexplorers.api.model.Mission
import com.cinnamon.cleanexplorers.database.entity.MissionEntity

class MapperMission {
    fun mapperList(listMission: List<Mission>, userId: String): List<MissionEntity> {
        return listMission.map {
            it.toEntity(userId)
        }.toList()
    }
}

fun Mission.toEntity(userId: String): MissionEntity {
    return MissionEntity(
        missionId = this.id.toLong(),
        userId = userId,
        name = this.name,
        nbrTime = this.nbrTime,
        point = this.point
    )
}