package com.example.habit_bridge_demo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.habit_bridge_demo.ui.screens.auth.LoginScreen
import com.example.habit_bridge_demo.ui.screens.auth.RegisterScreen
import com.example.habit_bridge_demo.ui.screens.challenge.ChallengeDetailScreen
import com.example.habit_bridge_demo.ui.screens.challenge.CreateChallengeScreen
import com.example.habit_bridge_demo.ui.screens.participation.ParticipationConfirmScreen
import com.example.habit_bridge_demo.ui.screens.participation.ParticipationDetailScreen
import com.example.habit_bridge_demo.ui.screens.participation.ParticipationPendingScreen
import com.example.habit_bridge_demo.ui.screens.participation.ParticipationResultScreen
import com.example.habit_bridge_demo.ui.screens.participation.VerificationUploadScreen
import com.example.habit_bridge_demo.ui.screens.splash.SplashScreen

@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onAuthenticated = {
                    nav.navigate(Routes.MAIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onUnauthenticated = {
                    nav.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    nav.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateRegister = { nav.navigate(Routes.REGISTER) },
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegistered = { nav.popBackStack() },
                onBack = { nav.popBackStack() },
            )
        }

        composable(Routes.MAIN) {
            MainTabScaffold(
                rootNav = nav,
                onLoggedOut = {
                    nav.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Routes.CHALLENGE_DETAIL,
            arguments = listOf(navArgument("challengeId") { type = NavType.StringType }),
        ) {
            ChallengeDetailScreen(
                onBack = { nav.popBackStack() },
                onParticipate = { id ->
                    nav.navigate(Routes.participationConfirm(id))
                },
                onOpenMyParticipation = { pid ->
                    nav.navigate(Routes.participationDetail(pid))
                },
            )
        }

        composable(Routes.CHALLENGE_CREATE) {
            CreateChallengeScreen(
                onClose = { nav.popBackStack() },
                onCreated = { id ->
                    nav.navigate(Routes.challengeDetail(id)) {
                        popUpTo(Routes.CHALLENGE_CREATE) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Routes.PARTICIPATION_CONFIRM,
            arguments = listOf(navArgument("challengeId") { type = NavType.StringType }),
        ) {
            ParticipationConfirmScreen(
                onBack = { nav.popBackStack() },
                onSigningStarted = { participationId ->
                    nav.navigate(Routes.participationPending(participationId)) {
                        popUpTo(Routes.PARTICIPATION_CONFIRM) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Routes.PARTICIPATION_PENDING,
            arguments = listOf(navArgument("participationId") { type = NavType.StringType }),
        ) {
            ParticipationPendingScreen(
                onActive = { id ->
                    nav.navigate(Routes.participationDetail(id)) {
                        popUpTo(Routes.PARTICIPATION_PENDING) { inclusive = true }
                    }
                },
                onBackground = {
                    nav.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = false }
                    }
                },
            )
        }

        composable(
            route = Routes.PARTICIPATION_DETAIL,
            arguments = listOf(navArgument("participationId") { type = NavType.StringType }),
        ) { entry ->
            val pid = entry.arguments?.getString("participationId").orEmpty()
            ParticipationDetailScreen(
                onBack = { nav.popBackStack() },
                onUpload = { slot ->
                    nav.navigate(Routes.verificationUpload(pid, slot))
                },
                onResult = {
                    nav.navigate(Routes.participationResult(pid))
                },
            )
        }

        composable(
            route = Routes.VERIFICATION_UPLOAD,
            arguments = listOf(
                navArgument("participationId") { type = NavType.StringType },
                navArgument("slotIndex") { type = NavType.StringType },
            ),
        ) {
            VerificationUploadScreen(
                onClose = { nav.popBackStack() },
                onUploaded = { nav.popBackStack() },
            )
        }

        composable(
            route = Routes.PARTICIPATION_RESULT,
            arguments = listOf(navArgument("participationId") { type = NavType.StringType }),
        ) {
            ParticipationResultScreen(
                onBack = { nav.popBackStack() },
                onOpenChallenge = { id ->
                    nav.navigate(Routes.challengeDetail(id))
                },
            )
        }
    }
}
