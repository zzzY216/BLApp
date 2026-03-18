package com.software.biliapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage

@Composable
fun BiliProfileScreen(
    viewModel: BiliProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var isDarkMode by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val customDrawerWidth = screenWidth * 0.7f
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(customDrawerWidth)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "我的", fontSize = 16.sp)
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "是否启用深色模式")
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = {
                                isDarkMode = it
                            }
                        )
                    }
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = {
                            Text(text = "账号资料")
                        },
                        selected = false,
                        onClick = {},
                        badge = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null
                            )
                        }
                    )
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = {
                            Text(text = "安全隐私")
                        },
                        selected = false,
                        onClick = {},
                        badge = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null
                            )
                        }
                    )
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = {
                            Text(text = "收货信息")
                        },
                        selected = false,
                        onClick = {},
                        badge = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        BiliProfileContent(
            uiState = uiState.value
        )
    }
}

@Composable
fun BiliProfileContent(
    uiState: BiliProfileState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        item {
            TopActionBar()
        }
        item {
            BiliProfileAvatar(
                uiState = uiState
            )
        }
        item {
            BiliProfileSocialStatusRow()
        }
        item {
            BiliProfileVIPRow()
        }
        item { SectionTitle("推荐服务") }
        item {
            ServiceGrid(
                services = listOf(
                    ServiceItem("我的课程", Icons.Default.MenuBook),
                    ServiceItem("看视频免流量", Icons.Default.SimCard),
                    ServiceItem("个性装扮", Icons.Default.Checkroom),
                    ServiceItem("邀好友赚红包", Icons.Default.CardGiftcard, badge = "1"),
                    ServiceItem("我的钱包", Icons.Default.AccountBalanceWallet),
                    ServiceItem("游戏中心", Icons.Default.SportsEsports),
                    ServiceItem("会员购中心", Icons.Default.ShoppingBag),
                    ServiceItem("我的直播", Icons.Default.VideoCameraFront),
                    ServiceItem("起飞推广", Icons.Default.RocketLaunch),
                    ServiceItem("社区中心", Icons.Default.Forum),
                    ServiceItem("哔哩哔哩公益", Icons.Default.Favorite),
                    ServiceItem("能量加油站", Icons.Default.FavoriteBorder),
                )
            )
        }
    }
}

@Composable
fun TopActionBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val icons = listOf(
            Icons.Default.Checkroom, Icons.Default.Checkroom, Icons.Default.Checkroom,
        )
        icons.forEach { icon ->
            Icon(
                icon, contentDescription = null,
                modifier = Modifier
                    .padding(start = 20.dp)
                    .size(24.dp),
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun BiliProfileVIPRow() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFef9fba), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "大", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column() {
                Text(text = "成为大会员", fontWeight = FontWeight.Bold, color = Color(0xFFef9fba))
                Text(
                    text = "了解更多权益",
                    fontSize = 12.sp,
                    color = Color(0xFFef9fba).copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "立即开通 >", fontSize = 13.sp, color = Color(0xFFef9fba))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        modifier = Modifier.padding(16.dp),
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
}

@Composable
fun ServiceGrid(services: List<ServiceItem>) {
    services.chunked(4).forEach { rowItems ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            rowItems.forEach { item ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        Icon(
                            item.icon,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.Gray
                        )
                        if (item.badge != null) {
                            Text(
                                text = item.badge,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-4).dp)
                                    .background(Color.Red, CircleShape)
                                    .padding(4.dp),
                                color = Color.White, fontSize = 10.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = item.label, fontSize = 12.sp, color = Color(0xFF333333))
                }
            }
        }
    }
}

@Composable
fun BiliProfileSocialStatusRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BiliProfileSocialStatusItem(
            modifier = modifier,
            text = "动态",
            value = "-"
        )
        VerticalDivider(
            modifier = Modifier
                .height(20.dp)
                .align(Alignment.CenterVertically)
        )
        BiliProfileSocialStatusItem(
            modifier = modifier,
            text = "关注",
            value = "-"
        )
        VerticalDivider(
            modifier = Modifier
                .height(20.dp)
                .align(Alignment.CenterVertically)
        )
        BiliProfileSocialStatusItem(
            modifier = modifier,
            text = "粉丝",
            value = "-"
        )
    }
}

@Composable
fun BiliProfileSocialStatusItem(
    modifier: Modifier = Modifier,
    text: String,
    value: String
) {
    Column(
        modifier = modifier.height(45.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = text, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun BiliProfileAvatar(
    modifier: Modifier = Modifier,
    uiState: BiliProfileState
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(82.dp)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        SubcomposeAsyncImage(
            model = uiState.userInfo?.face,
            contentDescription = "avatar",
            modifier = Modifier
                .clip(CircleShape)
                .size(48.dp),
            contentScale = ContentScale.Crop,
            error = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error")
                }
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = uiState.userInfo?.uname ?: "", fontSize = 16.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = "B币：${uiState.userInfo?.money ?: 0.0}", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "节操值：${uiState.userInfo?.moral ?: 0.0}", fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}

data class ServiceItem(
    val label: String,
    val icon: ImageVector,
    val badge: String? = null
)