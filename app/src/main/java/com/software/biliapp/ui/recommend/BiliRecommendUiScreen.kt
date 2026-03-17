package com.software.biliapp.ui.recommend

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.software.biliapp.domain.model.RecommendItemDomain

@Composable
fun BiliRecommendUiScreen(
    viewModel: BiliRecommendUiViewModel = hiltViewModel(),
    onNavigateToDetail: (String, String, Int, String, String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()
    val videoPagingFlow = viewModel.videoPagingFlow.collectAsLazyPagingItems()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BiliHomeUiVideoList(
            pagingItems = videoPagingFlow,
            onNavigateToDetail = onNavigateToDetail
        )
    }
}

@Composable
fun BiliHomeUiContent() {
}

@Composable
fun BiliHomeUiVideoList(
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
            BiliHomeUiVideoItem(video = video, onNavigateToDetail = onNavigateToDetail)
        }
    }
}

@Composable
fun BiliHomeUiVideoItem(
    video: RecommendItemDomain,
    onNavigateToDetail: (String, String, Int, String, String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.clickable {
                onNavigateToDetail(
                    video.playerArgs?.aid.toString(),
                    video.playerArgs?.cid.toString(),
                    64, "mp4", "html5"
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 10f)
            ) {
                Log.d("BiliHomeUiScreen", "cover: ${video.cover}")
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(video.cover) // 图片 URL
                        .setHeader("Referer", "https://www.bilibili.com") // 必须加这个
                        .crossfade(true)
                        .listener(
                            onStart = { Log.d("Coil", "开始加载: ${video.cover}") },
                            onError = { _, result ->
                                Log.e("Coil", "加载失败: ${result.throwable.message}")
                            },
                            onSuccess = { _, _ -> Log.d("Coil", "加载成功") }
                        )
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(0.6f)
                                )
                            )
                        )
                        .padding(4.dp)
                ) {
                    Text(
                        text = video.coverLeft1ContentDescription ?: "",
                        color = Color.White,
                        fontSize = 10.sp
                    )
                    Text(
                        text = video.coverLeft2ContentDescription ?: "",
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = video.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun BiliHomeUiPicture(
    modifier: Modifier
) {
    val pagerState = rememberPagerState {
        3
    }
    HorizontalPager(
        state = pagerState
    ) {

    }
}

@Composable
fun BiliHomeUiTopTab() {

}
