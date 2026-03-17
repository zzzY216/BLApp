package com.software.biliapp.di

import com.software.biliapp.data.remote.network.BiliApiService
import com.software.biliapp.data.remote.network.BiliLoginApiService
import com.software.biliapp.data.repository.BiliGetPopularListRepository
import com.software.biliapp.data.repository.BiliGetPopularListRepositoryImpl
import com.software.biliapp.data.repository.BiliGetReplyListRepository
import com.software.biliapp.data.repository.BiliGetReplyListRepositoryImpl
import com.software.biliapp.data.repository.BiliGetUserInfoRepository
import com.software.biliapp.data.repository.BiliGetUserInfoRepositoryImpl
import com.software.biliapp.data.repository.BiliGetVideoDetailRepository
import com.software.biliapp.data.repository.BiliGetVideoDetailRepositoryImpl
import com.software.biliapp.data.repository.BiliGetVideoPlayUrlRepository
import com.software.biliapp.data.repository.BiliGetVideoPlayUrlRepositoryImpl
import com.software.biliapp.data.repository.BiliRecommendVideoRepository
import com.software.biliapp.data.repository.BiliRecommendVideoRepositoryImpl
import com.software.biliapp.data.repository.BlBlPollQrCodeStatusRepository
import com.software.biliapp.data.repository.BlBlPollQrCodeStatusRepositoryImpl
import com.software.biliapp.data.repository.BlBlQrCodeDataRepository
import com.software.biliapp.data.repository.BlBlQrCodeDataRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BiliRepositoryModule {
    @Provides
    @Singleton
    fun provideBiliGetVideoPlayUrlRepository(
        @BiliApiNetwork apiService: BiliApiService
    ): BiliGetVideoPlayUrlRepository {
        return BiliGetVideoPlayUrlRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBlBlQrCodeDataRepository(
        @BiliLoginNetwork apiService: BiliLoginApiService
    ): BlBlQrCodeDataRepository {
        return BlBlQrCodeDataRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBlBlpollQrCodeStatusRepository(
        @BiliLoginNetwork apiService: BiliLoginApiService
    ): BlBlPollQrCodeStatusRepository {
        return BlBlPollQrCodeStatusRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBiliRecommendVideoRepository(
        @BiliAppNetwork apiService: BiliApiService
    ): BiliRecommendVideoRepository {
        return BiliRecommendVideoRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBiliGetVideoDetailRepository(
        @BiliApiNetwork apiService: BiliApiService
    ): BiliGetVideoDetailRepository {
        return BiliGetVideoDetailRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBiliGetUserInfoRepository(
        @BiliApiNetwork apiService: BiliApiService
    ): BiliGetUserInfoRepository {
        return BiliGetUserInfoRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBiliGetReplyListRepository(
        @BiliApiNetwork apiService: BiliApiService
    ): BiliGetReplyListRepository {
        return BiliGetReplyListRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideBiliGetPopularListRepository(
        @BiliApiNetwork apiService: BiliApiService
    ): BiliGetPopularListRepository {
        return BiliGetPopularListRepositoryImpl(apiService)
    }
}