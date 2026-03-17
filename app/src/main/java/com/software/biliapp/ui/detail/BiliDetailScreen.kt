package com.software.biliapp.ui.detail

import android.transition.CircularPropagation
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.software.biliapp.R
import com.software.biliapp.domain.model.BiliReplyDomain
import com.software.biliapp.domain.model.VideoDetailDomain
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

@Composable
fun BiliDetailScreen(
    avid: String,
    cid: String,
    qn: Int,
    type: String,
    platform: String,
    viewModel: BiliDetailViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.onEvent(BiliDetailEvent.LoadData(avid, cid, qn, type, platform))
        viewModel.onEvent(BiliDetailEvent.GetVideoDetail(avid, cid))
        viewModel.onEvent(BiliDetailEvent.GetReplyList(avid.toLong(), 1, 0, 1, 20))
    }
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is BiliDetailUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 12.dp)
    ) {
        when (uiState.value.state) {
            is State.Error -> {
                Column() {
                    Text(text = "Error")
                }
            }

            State.Idle -> {
                Column() {
                    Text(text = "Idle")
                }
            }

            State.Loading -> {
                BiliLoading()
            }

            State.Success -> {
                BiliDetailSuccessContent(
                    player = viewModel.player,
                    selectedTab = selectedTab,
                    onTabClick = {
                        selectedTab = it
                    },
                    onClick = {
                        viewModel.onEvent(BiliDetailEvent.GetVideoDetail(avid, cid))
                    },
                    videoDetail = uiState.value.videoDetail,
                    uiState = uiState.value
                )
            }
        }
    }
}


@Composable
fun BiliDetailSuccessContent(
    uiState: BiliDetailState,
    player: ExoPlayer,
    selectedTab: Int = 0,
    onTabClick: (Int) -> Unit = {},
    onClick: () -> Unit,
    videoDetail: VideoDetailDomain?,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                BiliPlayer(player = player, modifier = Modifier.height(300.dp))
            }
            stickyHeader {
                Surface(modifier = Modifier.fillMaxWidth()) {
                    BiliVideoTabHeader(
                        uiState = uiState,
                        selectedTab = selectedTab,
                        onTabClick = onTabClick
                    )
                }
            }
            if (selectedTab == 0) {
                item { BiliPlayerItem(videoDetail = videoDetail) }
                item { BiliPlayerStat(videoDetail = videoDetail) }
            } else {
                val replies = uiState.replyData?.replies
                if (replies == null) {
                    item {
                        Text(text = "暂无数据")
                    }
                } else {
                    items(replies) { reply ->
                        BiliReplyItem(
                            biliReply = reply,
                            isUp = reply.mid == videoDetail?.owner?.mid,
                            content = {
                                reply.replies?.take(2)?.forEach { subReply ->
                                    BiliReplyChildItem(
                                        subReply.member.uname,
                                        subReply.content.message
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BiliDetailSuccessItem(
    uiState: BiliDetailState,
    videoDetail: VideoDetailDomain?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BiliPlayerItem(videoDetail = videoDetail)
        BiliPlayerStat(videoDetail = videoDetail)
        BiliReplyItem(uiState.replyData?.replies?.get(0), content = {
            BiliReplyChildItem(
                uiState.replyData?.replies?.get(0)?.replies?.get(0)?.member?.uname ?: "获取失败",
                uiState.replyData?.replies?.get(0)?.replies?.get(0)?.content?.message ?: "获取失败"
            )
        }, isUp = true)
    }
}


@Composable
fun BiliReplyItem(
    biliReply: BiliReplyDomain?,
    content: @Composable () -> Unit,
    isUp: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = biliReply?.member?.avatar,
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = biliReply?.member?.uname ?: "",
                    fontWeight = FontWeight.Medium,
                    color = if (isUp) Color(0xFFEB5B5B) else Color.Gray,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Column() {
                Text(text = biliReply?.content?.message ?: "")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(biliReply?.replyControl?.location ?: "", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(12.dp))
                Text("回复", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Outlined.ThumbUp,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Text(text = biliReply?.like.toString(), fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    painterResource(id = R.drawable.ic_bili_like), // 临时占位，实际可用自定义踩图标
                    contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}


@Composable
fun BiliReplyChildItem(
    name: String,
    content: String
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        color = Color(0xFFF6F7F9)
    ) {
        Row() {
            Text(text = name)
            Text(text = content)
        }
    }
}

@Composable
fun BiliPlayerStat(
    videoDetail: VideoDetailDomain?,
) {
    Row(
        modifier = Modifier
            .height(70.dp)
            .fillMaxWidth()
    ) {
        BiliPlayerStatItem(
            text = videoDetail?.stat?.like.toString(),
            R.drawable.ic_bili_like,
            onClick = {},
            modifier = Modifier.weight(1f)
        )
        BiliPlayerStatItem(
            text = videoDetail?.stat?.coin.toString(),
            R.drawable.ic_bili_coin,
            onClick = {},
            modifier = Modifier.weight(1f)
        )
        BiliPlayerStatItem(
            text = videoDetail?.stat?.favorite.toString(),
            R.drawable.ic_bili_favorite,
            onClick = {},
            modifier = Modifier.weight(1f)
        )
        BiliPlayerStatItem(
            text = "版本受限",
            R.drawable.ic_bili_download,
            onClick = {},
            modifier = Modifier.weight(1f)
        )
        BiliPlayerStatItem(
            text = videoDetail?.stat?.share.toString(),
            R.drawable.ic_bili_share,
            onClick = {},
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BiliPlayerStatItem(
    text: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(text = text, fontSize = 8.sp)
    }
}

@Composable
fun BiliPlayerItem(
    videoDetail: VideoDetailDomain?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
//            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = videoDetail?.owner?.face,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = videoDetail?.owner?.name ?: "获取失败",
                fontSize = 12.sp,
                color = Color(255, 102, 152)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .width(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(255, 102, 152))
                .padding(4.dp),
            contentAlignment = Alignment.Center,

            ) {
            Text(text = "+关注", fontSize = 12.sp)
        }
    }
}

@Composable
fun BiliVideoTabHeader(
    uiState: BiliDetailState,
    modifier: Modifier = Modifier,
    selectedTab: Int = 0,
    onTabClick: (Int) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1.5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            TabItem(
                text = "简介",
                isSelected = selectedTab == 0,
                onClick = {
                    onTabClick(0)
                },
                extend = {
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            TabItem(
                text = "评论",
                isSelected = selectedTab == 1,
                onClick = {
                    onTabClick(1)
                },
                extend = {
                    Text(
                        text = uiState.replyData?.page?.count.toString(),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "游园会",
                color = Color(0xFF2196F3),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
        }
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFFF4F4F4))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "点我发弹幕",
                color = Color(0xFF999999),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            // 这里用自带的图标模拟图片中的弹幕图标
            Icon(
                imageVector = Icons.Default.AddTask,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    extend: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = if (isSelected) Color(0xFFFF6699) else Color.Gray,
                fontSize = 15.sp
            )
            extend()
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(20.dp)
                    .height(2.dp)
                    .background(Color(0xFFFF6699), CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
fun BiliLoading(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "加载中...")
        Spacer(modifier = Modifier.height(12.dp))
        CircularPropagation()
    }
}

@OptIn(UnstableApi::class)
@Composable
fun BiliPlayer(
    player: ExoPlayer,
    modifier: Modifier = Modifier
) {
    var isShow by remember { mutableStateOf(false) }
    var isLongPressing by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    val context = LocalContext.current
    DisposableEffect(Unit) {
        onDispose {
            player.pause()
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
            .clickable {
                isShow = !isShow
            }
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            modifier = Modifier
                .fillMaxSize()
//                .pointerInput(playbackSpeed) {
//                    awaitPointerEventScope {
//                        while(true) {
//                            // 等待按下事件
//                            val down = awaitFirstDown()
//                            // 启动一个计时器或者等待长按触发
//                            val longPressJob: Job?= null
//                        }
//                    }
//                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            isShow = !isShow
                        },
                        onLongPress = {
                            isLongPressing = true
                            player.setPlaybackSpeed(2.0f)
                            Toast.makeText(context, "倍速播放已开启", Toast.LENGTH_SHORT).show()
                        },
                        onPress = {
                            isLongPressing = false
                            player.setPlaybackSpeed(playbackSpeed)
                            Toast.makeText(context, "倍速播放已关闭", Toast.LENGTH_SHORT).show()
                        },

                    )
                }

        )
        if (isShow) {
            CustomPlayerControls(
                player = player,
                onPlayPauseClick = {
                    if (player.isPlaying) {
                        player.pause()
                    } else {
                        player.play()
                    }
                }
            )
        }
    }
}

@Composable
fun CustomPlayerControls(
    player: ExoPlayer,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLongPressing: Boolean = false
) {
    var isPlaying by remember { mutableStateOf(player.isPlaying) }
    var currentPosition by remember { mutableStateOf(player.currentPosition) }
    var duration by remember { mutableStateOf(player.duration.coerceAtLeast(0L)) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                duration = player.duration.coerceAtLeast(0L)
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
        }
    }
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = player.currentPosition
            delay(1000)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = {
                onPlayPauseClick()
            },
            modifier = Modifier
                .align(Alignment.Center)
                .size(32.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
            )
        }
        if (isLongPressing) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = null,
                    tint = Color.White,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "倍速播放"
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            LinearProgressIndicator(
                progress = { currentPosition.toFloat() / duration.coerceAtLeast(0L).toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(currentPosition), color = Color.White)
                Text(formatTime(duration), color = Color.White)
            }
        }
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}