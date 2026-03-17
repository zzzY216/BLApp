package com.software.biliapp.data.remote.model

import com.google.gson.annotations.SerializedName
import com.software.biliapp.domain.model.PopularDataDomain
import com.software.biliapp.domain.model.PopularDataOwnerDomain
import com.software.biliapp.domain.model.PopularDataStatDomain
import com.software.biliapp.domain.model.PopularItemDomain
import kotlinx.serialization.Serializable

@Serializable
data class PopularData(
    val list: List<PopularItem>,
    @SerializedName("no_more") val noMore: String,
)

@Serializable
data class PopularItem(
    val aid: String,
    val videos: Int,
    val tname: String,
    val pic: String,
    val title: String,
    val owner: PopularDataOwner,
    val stat: PopularDataStat
)

@Serializable
data class PopularDataOwner(
    val mid: Long,
    val name: String,
    val face: String
)

@Serializable
data class PopularDataStat(
    val cid: Long
)

fun PopularData.toDomain(): PopularDataDomain {
    return PopularDataDomain(
        list = list.map { it.toDomain() },
        noMore = noMore
    )
}

fun PopularItem.toDomain(): PopularItemDomain {
    return PopularItemDomain(
        aid = aid,
        videos = videos,
        tname = tname,
        pic = pic,
        title = title,
        owner = owner.toDomain(),
        stat = stat.toDomain()
    )
}

fun PopularDataOwner.toDomain(): PopularDataOwnerDomain {
    return PopularDataOwnerDomain(
        mid = mid,
        name = name,
        face = face
    )
}

fun PopularDataStat.toDomain(): PopularDataStatDomain {
    return PopularDataStatDomain(
        cid = cid
    )
}