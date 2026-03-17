package com.software.biliapp.data.repository

import android.util.Log
import com.software.biliapp.data.remote.model.toDomain
import com.software.biliapp.data.remote.network.BiliLoginApiService
import com.software.biliapp.domain.model.QrPollDataDomain

interface BlBlPollQrCodeStatusRepository {
    suspend fun pollQrCodeStatus(qrcodeKey: String): Result<QrPollDataDomain>
}

class BlBlPollQrCodeStatusRepositoryImpl(
    private val apiService: BiliLoginApiService
) : BlBlPollQrCodeStatusRepository {
    override suspend fun pollQrCodeStatus(qrcodeKey: String): Result<QrPollDataDomain> {
        return try {
            val response = apiService.pollQrCodeStatus(qrcodeKey)
            if (response.code == 0) {
                Result.success(response.toDomain())
            } else {
                Result.failure(Exception("Code is not 200"))
            }
        } catch (e: Exception) {
            Log.d("QRCode", "QRCodeRepository二维码状态查询失败: ${e.message}")
            Result.failure(e)
        }
    }
}