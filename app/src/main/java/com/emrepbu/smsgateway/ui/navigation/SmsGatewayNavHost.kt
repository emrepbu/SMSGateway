package com.emrepbu.smsgateway.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.emrepbu.smsgateway.ui.screens.*

@Composable
fun SmsGatewayNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "sms_list",
        modifier = modifier
    ) {
        composable("sms_list") {
            SmsListScreen(
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateToFilterRules = {
                    navController.navigate("filter_rules")
                },
                onNavigateToEmailConfig = {
                    navController.navigate("email_config")
                },
                onBack = navController::popBackStack,
            )
        }

        composable("filter_rules") {
            FilterRuleListScreen(
                onNavigateToFilterRuleDetail = { ruleId ->
                    navController.navigate("filter_rule_detail/$ruleId")
                },
                onNavigateToAddRule = {
                    navController.navigate("filter_rule_detail/new")
                },
                onBack = navController::popBackStack,
            )
        }

        composable(
            route = "filter_rule_detail/{ruleId}",
            arguments = listOf(
                navArgument("ruleId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val ruleId = backStackEntry.arguments?.getString("ruleId") ?: "new"
            FilterRuleDetailScreen(
                ruleId = ruleId,
                onSaved = navController::popBackStack,
                onBack = navController::popBackStack,
            )
        }

        composable("email_config") {
            EmailConfigScreen(
                onBack = navController::popBackStack,
            )
        }
    }
}
