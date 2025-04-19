package com.example.gymlog.data.repository.gym_day


import com.example.gymlog.data.local.room.dao.GymSessionDao
import com.example.gymlog.data.local.room.mapper.toDomain
import com.example.gymlog.domain.model.plan.GymSession
import com.example.gymlog.domain.repository.GymSessionRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GymSessionRepository @Inject constructor (
    private val gymSessionDao: GymSessionDao,
) : GymSessionRepositoryInterface {


    override suspend fun getGymSessionByProgramId(progId: Long): List<GymSession> {
        return gymSessionDao.getGymDaysEntities(progId).map { it.toDomain() }.toMutableList()
    }


}