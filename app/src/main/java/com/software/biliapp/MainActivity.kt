package com.software.biliapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.software.biliapp.ui.detail.BiliDetailScreen
import com.software.biliapp.ui.home.BiliHomeUiScreen
import com.software.biliapp.ui.login.BiliLoginUiScreen
import com.software.biliapp.ui.profile.BiliProfileScreen
import com.software.biliapp.ui.recommend.BiliRecommendUiScreen
import com.software.biliapp.ui.theme.BiliAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BiliAppTheme {
                BiliAppContainer()
            }
        }
    }
}

@Composable
fun BiliAppContainer() {
    val biliNavController = rememberNavController()
    val biliBackStackEntry by biliNavController.currentBackStackEntryAsState()
    val biliCurrentDestination = biliBackStackEntry?.destination
    val showBiliBottomBar = biliCurrentDestination?.hasRoute<RouteBiliHome>() == true ||
            biliCurrentDestination?.hasRoute<RouteBiliRecommend>() == true ||
            biliCurrentDestination?.hasRoute<RouteBiliProfile>() == true
    val biliBottomBar = listOf(
        TopLevelRoute(
            name = "Home",
            route = RouteBiliHome,
            icon = R.drawable.ic_home
        ),
        TopLevelRoute(
            name = "Dynamic",
            route = RouteBiliDynamic,
            icon = R.drawable.ic_dynamic
        ),
        TopLevelRoute(
            name = "recommend",
            route = RouteBiliRecommend,
            icon = R.drawable.ic_recommend
        ),
        TopLevelRoute(
            name = "Profile",
            route = RouteBiliProfile,
            icon = R.drawable.ic_profile
        )
    )

    Column() {
        NavHost(
            navController = biliNavController, startDestination = RouteBiliLogin,
            modifier = Modifier.weight(1f)
        ) {
            composable<RouteBiliLogin> {
                BiliLoginUiScreen(
                    onNavigateToRecommend = {
                        biliNavController.navigate(RouteBiliRecommend) {
                            popUpTo<RouteBiliLogin> {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable<RouteBiliProfile> {
                BiliProfileScreen()
            }
            composable<RouteBiliRecommend> {
                BiliRecommendUiScreen(
                    onNavigateToDetail = { avid, cid, qn, type, platform ->
                        biliNavController.navigate(RouteBiliDetail(avid, cid, qn, type, platform))
                    }
                )
            }
            composable<RouteBiliDetail> {
                val route = biliBackStackEntry?.toRoute<RouteBiliDetail>() ?: return@composable
                BiliDetailScreen(
                    route.avid,
                    route.cid,
                    route.qn,
                    route.type,
                    route.platform
                )
            }
            composable<RouteBiliHome> {
                BiliHomeUiScreen()
            }
        }
        if (showBiliBottomBar) {
            NavigationBar {
                biliBottomBar.forEach { topLevelRoute ->
                    val isSelected = biliCurrentDestination.hasRoute(topLevelRoute.route::class)
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            biliNavController.navigate(topLevelRoute.route) {
                                popUpTo(biliNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(topLevelRoute.icon),
                                contentDescription = topLevelRoute.name,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(text = topLevelRoute.name)
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    }
}

data class TopLevelRoute<T>(val name: String, val route: T, val icon: Int)