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
import com.software.biliapp.ui.components.cards.BiliVideoUiCard

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
