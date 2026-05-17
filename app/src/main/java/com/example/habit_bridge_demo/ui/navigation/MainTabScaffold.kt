package com.example.habit_bridge_demo.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.PlaylistAddCheck
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.habit_bridge_demo.HabitBridgeApp
import com.example.habit_bridge_demo.ui.screens.home.HomeScreen
import com.example.habit_bridge_demo.ui.screens.participation.MyParticipationsScreen
import com.example.habit_bridge_demo.ui.screens.profile.ProfileScreen
import com.example.habit_bridge_demo.ui.screens.ranking.RankingScreen

private data class TabItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val TABS = listOf(
    TabItem(Routes.HOME, "홈", Icons.Outlined.Explore),
    TabItem(Routes.MY_PARTICIPATIONS, "내 참여", Icons.Outlined.PlaylistAddCheck),
    TabItem(Routes.RANKINGS, "랭킹", Icons.Outlined.EmojiEvents),
    TabItem(Routes.PROFILE, "마이", Icons.Outlined.AccountCircle),
)

@Composable
fun MainTabScaffold(
    rootNav: NavHostController,
    onLoggedOut: () -> Unit,
) {
    val tabNav = rememberNavController()
    val backStackEntry by tabNav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val context = LocalContext.current
    val container = (context.applicationContext as HabitBridgeApp).container
    LaunchedEffect(Unit) {
        container.tabSelectionEvents.requestedTab.collect { route ->
            tabNav.navigate(route) {
                popUpTo(tabNav.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                TABS.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            tabNav.navigate(tab.route) {
                                popUpTo(tabNav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = tabNav,
            startDestination = Routes.HOME,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onChallengeClick = { id -> rootNav.navigate(Routes.challengeDetail(id)) },
                    onCreateChallenge = { rootNav.navigate(Routes.CHALLENGE_CREATE) },
                    contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
                )
            }
            composable(Routes.MY_PARTICIPATIONS) {
                MyParticipationsScreen(
                    onOpenParticipation = { id -> rootNav.navigate(Routes.participationDetail(id)) },
                    onOpenResult = { id -> rootNav.navigate(Routes.participationResult(id)) },
                    onBrowseChallenges = {
                        tabNav.navigate(Routes.HOME) { launchSingleTop = true }
                    },
                    contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
                )
            }
            composable(Routes.RANKINGS) {
                RankingScreen(
                    contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onLoggedOut = onLoggedOut,
                    contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
                )
            }
        }
    }
}
