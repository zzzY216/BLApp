package com.software.biliapp.data.repository

import com.software.biliapp.data.remote.model.toDomain
import com.software.biliapp.data.remote.network.BiliApiService
import com.software.biliapp.domain.model.PlayUrlDataDomain
import javax.inject.Inject

interface BiliGetVideoPlayUrlRepository {
    suspend fun getVideoPlayUrl(
        avid: String,
        cid: String,
        qn: Int,
        type: String,
        platform: String
    ): Result<PlayUrlDataDomain>
}

class BiliGetVideoPlayUrlRepositoryImpl @Inject constructor(
    private val apiService: BiliApiService
) : BiliGetVideoPlayUrlRepository {
    override suspend fun getVideoPlayUrl(
        avid: String,
        cid: String,
        qn: Int,
        type: String,
        platform: String
    ): Result<PlayUrlDataDomain> {
        return try {
            val response = apiService.getVideoPlayUrl(avid, cid, qn, type, platform)
            if (response.code == 0) {
                if (response.data != null) {
                    Result.success(response.data.toDomain())
                } else {
                    Result.failure(Exception("Data is null"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code} - ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
