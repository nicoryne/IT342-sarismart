package edu.cit.sarismart.ui.onboarding


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.sarismart.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    onSkip: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboarding_store,
            titleLine1 = "Smarter Stores,",
            titleLine2 = "Simpler Sales",
            description = "Manage your inventory, track sales, and grow your business with SariSmart"
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_growth,
            titleLine1 = "Your Partner in",
            titleLine2 = "Growth",
            description = "Connect with suppliers, access credit, and get business insights to help you grow"
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_success,
            titleLine1 = "Simplify, Sell,",
            titleLine2 = "Succeed",
            description = "Streamline your operations and focus on what matters most - your customers"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App name at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SariSmart",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Pager content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { position ->
            OnboardingPage(page = pages[position])
        }

        // Bottom navigation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Skip button
            if (pagerState.currentPage < pages.size - 1) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(
                        text = "Skip",
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 14.sp
                    )
                }
            }

            // Page indicators
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(pages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Next or Get Started button
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (pagerState.currentPage < pages.size - 1) "Next" else "Get Started",
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun OnboardingPage(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 32.dp)
        )

        // Title
        Text(
            text = page.titleLine1,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = page.titleLine2,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.primary
        )

        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        )
    }
}
