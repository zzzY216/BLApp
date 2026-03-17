package com.software.biliapp.domain.model

data class PopularDataDomain(
    val list: List<PopularItemDomain>,
    val noMore: String
)


data class PopularItemDomain(
    val aid: String,
    val videos: Int,
    val tname: String,
    val pic: String,
    val title: String,
    val owner: PopularDataOwnerDomain,
    val stat: PopularDataStatDomain
)


data class PopularDataOwnerDomain(
    val mid: Long,
    val name: String,
    val face: String
)

data class PopularDataStatDomain(
    val cid: Long
)
