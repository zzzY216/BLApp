package com.software.biliapp.ui.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.software.biliapp.R
import com.software.biliapp.domain.model.PopularItemDomain
import com.software.biliapp.domain.model.RecommendItemDomain
import com.software.biliapp.ui.components.cards.BiliVideoUiCard
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun BiliHomeUiScreen(
    viewModel: BiliHomeUiViewModel = hiltViewModel(),
    onNavigateToDetail: (String, String, Int, String, String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()
    val popularListPagingFlow = viewModel.popularListPagingFlow.collectAsLazyPagingItems()
    val recommendListPagingFlow = viewModel.recommendListPagingFlow.collectAsLazyPagingItems()
    BiliHomeUiContent(
        uiState = uiState.value,
        popularListPagingFlow = popularListPagingFlow,
        recommendListPagingFlow = recommendListPagingFlow,
        onNavigateToDetail = onNavigateToDetail
    )
}

@Composable
fun BiliHomeUiContent(
    uiState: BiliHomeUiState,
    popularListPagingFlow: LazyPagingItems<PopularItemDomain>,
    recommendListPagingFlow: LazyPagingItems<RecommendItemDomain>,
    onNavigateToDetail: (String, String, Int, String, String) -> Unit
) {
    val tabs = listOf("直播", "推荐", "热门", "动画", "影视", "运动", "科技", "美食")
    val pagerState = rememberPagerState(initialPage = 1) { tabs.size }
    val coroutineScope = rememberCoroutineScope()

    // 用于处理联动滚动的位移量（根据你的 Header 高度来定）
    val headerHeightPx = with(LocalDensity.current) { 52.dp.roundToPx().toFloat() }
    val headerOffsetHeightPx = remember { mutableStateOf(0f) }

    // 嵌套滚动连接器
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = headerOffsetHeightPx.value + delta
                // 限制 offset 在 [-headerHeight, 0] 之间
                headerOffsetHeightPx.value = newOffset.coerceIn(-headerHeightPx, 0f)
                return Offset.Zero
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .nestedScroll(nestedScrollConnection) // 绑定嵌套滚动
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Pager 随 Header 的滚动而上移，但要留出 Tab 的高度
                    translationY = headerHeightPx + headerOffsetHeightPx.value
                }
        ) {
            BiliHomeSnackbar(
                selectedTabIndex = pagerState.currentPage,
                onClickToChangeIndex = { index ->
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                // 每个页面可以有完全不同的布局
                when (pageIndex) {
                    1 -> {
                        BiliHomeRecommendList(
                            pagingItems = recommendListPagingFlow,
                            onNavigateToDetail = onNavigateToDetail
                        )
                    }

                    2 -> {
                        BiliHomePopularList(
                            popularListPagingFlow = popularListPagingFlow,
                            onNavigateToDetail = onNavigateToDetail
                        )
                        Spacer(modifier = Modifier.size(0.dp))
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Page $pageIndex")
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(0, headerOffsetHeightPx.value.roundToInt()) }
        ) {
            val alpha = 1f + headerOffsetHeightPx.value / headerHeightPx
            BiliHomeTab(avatar = uiState.userInfo?.face ?: "", modifier = Modifier.graphicsLayer {
                this.alpha = alpha
            })
        }
    }
}

@Composable
fun BiliHomeRecommendList(
    pagingItems: LazyPagingItems<RecommendItemDomain>,
    onNavigateToDetail: (String, String, Int, String, String) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(
            count = pagingItems.itemCount
        ) { index ->
            val video = pagingItems[index] ?: return@items
            Log.d("BiliHomeUiScreen", "video: $video")
            BiliVideoUiCard(
                modifier = Modifier,
                imageUrl = video.cover,
                title = video.title,
                label1 = video.coverLeft1ContentDescription,
                label2 = video.coverLeft2ContentDescription,
                imageHeaders = mapOf("Referer" to "https://www.bilibili.com"),
                onClick = {
                    // 在这里处理具体的业务逻辑
                    onNavigateToDetail(
                        video.playerArgs?.aid.toString(),
                        video.playerArgs?.cid.toString(),
                        64, "mp4", "html5"
                    )
                },
                aspectRatio = 16 / 10f
            )
        }
    }
}

@Composable
fun BiliHomePopularList(
    popularListPagingFlow: LazyPagingItems<PopularItemDomain>,
    onNavigateToDetail: (String, String, Int, String, String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (popularListPagingFlow.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Magenta // 给个明显的颜色确认它在动
            )
        } else if (popularListPagingFlow.loadState.refresh is LoadState.Error) {
            Text(
                text = "加载失败，点击重试",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp
        ) {
            items(popularListPagingFlow.itemCount) { index ->
                val video = popularListPagingFlow[index]
                if (video != null) {
                    BiliVideoUiCard(
                        imageUrl = video.pic,
                        title = video.title,
                        label1 = "tname: ${video.tname}",
                        label2 = "up: ${video.owner.name}",
                        imageHeaders = mapOf("Referer" to "https://www.bilibili.com"),
                        onClick = {
                            onNavigateToDetail(
                                video.aid.toString(),
                                video.cid.toString(),
                                64,
                                "mp4",
                                "html5"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BiliHomeSnackbar(
    selectedTabIndex: Int = 0,
    onClickToChangeIndex: (Int) -> Unit
) {
    val tabs = listOf("直播", "推荐", "热门", "动画", "影视", "运动", "科技", "美食")
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .widthIn(min = 10.dp),
                    selected = selectedTabIndex == index,
                    onClick = { onClickToChangeIndex(index) },
                    text = { Text(text = title) }
                )
            }
        }
    }
}

@Composable
fun BiliHomeTab(
    avatar: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp)
            .height(52.dp)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatar,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(38.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.Transparent,
            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray.copy(alpha = 0.5f),
                )
                Text(text = "Home Home", color = Color.Gray.copy(alpha = 0.5f))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_bili_game),
            contentDescription = "Game",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_bili_email),
            contentDescription = "Mail",
            modifier = Modifier.size(24.dp)
        )
    }
}