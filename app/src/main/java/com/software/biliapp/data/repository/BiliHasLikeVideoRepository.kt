package com.software.biliapp.data.repository

import com.software.biliapp.data.remote.model.toDomain
import com.software.biliapp.data.remote.network.BiliApiService
import com.software.biliapp.domain.model.HasLikeVideoDataDomain
import com.software.biliapp.domain.model.LikeVideoDataDomain
import javax.inject.Inject

interface BiliHasLikeVideoRepository {
    suspend fun hasLikeVideo(
        aid: String? = null,
        bvid: String? = null,
    ): Result<HasLikeVideoDataDomain>
}

class BiliHasLikeVideoRepositoryImpl @Inject constructor(
    private val apiService: BiliApiService
) : BiliHasLikeVideoRepository {
    override suspend fun hasLikeVideo(
        aid: String?,
        bvid: String?,
    ): Result<HasLikeVideoDataDomain> {
        return try {
            val response = apiService.hasLikeVideo(
                aid = aid,
                bvid = bvid,
            )
            if (response.code == 0) {
                Result.success(response.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}