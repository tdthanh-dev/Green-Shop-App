package com.tdthanh.greenshop

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tdthanh.greenshop.ui.theme.GreenShopTheme
import com.tdthanh.greenshop.ui.theme.SpringBootGreen
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GreenShopTheme {
                SplashScreen {
                    // Navigate to MainActivity after animation
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var iconVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    var showGreenShop by remember { mutableStateOf(true) }
    
    // Animation states
    val iconScale by animateFloatAsState(
        targetValue = if (iconVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "iconScale"
    )
    
    val iconRotation by animateFloatAsState(
        targetValue = if (iconVisible) 0f else -180f,
        animationSpec = tween(800, easing = FastOutSlowInEasing), label = "iconRotation"
    )
    
    val textAlpha by animateFloatAsState(
        targetValue = if (textVisible) 1f else 0f,
        animationSpec = tween(600), label = "textAlpha"
    )
    
    // Infinite rotation for icon
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )
    
    LaunchedEffect(Unit) {
        // Icon animation sequence
        delay(300)
        iconVisible = true
        
        delay(800)
        textVisible = true
        
        // Text transition after 1 second
        delay(1000)
        showGreenShop = false
        
        // Wait and finish
        delay(1500)
        onSplashFinished()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF1F8E9),
                        Color.White,
                        Color(0xFFE8F5E8)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon with ripple background
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Ripple effect behind icon
                RippleEffect(
                    isVisible = iconVisible,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Spring Boot Icon with animations
                Image(
                    painter = painterResource(id = R.drawable.icons8_spring_boot_144),
                    contentDescription = "Spring Boot Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .scale(iconScale)
                        .rotate(iconRotation + if (iconVisible) rotation * 0.1f else 0f)
                        .alpha(iconScale)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Animated text transition
            AnimatedContent(
                targetState = showGreenShop,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(600)) + scaleIn(
                        initialScale = 0.8f,
                        animationSpec = tween(600)
                    )) togetherWith (fadeOut(animationSpec = tween(300)) + scaleOut(
                        targetScale = 1.2f,
                        animationSpec = tween(300)
                    ))
                }, label = "textTransition"
            ) { showGreenShop ->
                Text(
                    text = if (showGreenShop) "GreenShop" else "TDThanh",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = SpringBootGreen,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(textAlpha)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle with fade in
            Text(
                text = if (showGreenShop) "Loading..." else "Welcome!",
                fontSize = 16.sp,
                color = SpringBootGreen.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(textAlpha * 0.8f)
            )
        }
        
        // Loading dots at bottom
        if (textVisible) {
            LoadingDots(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            )
        }
    }
}

@Composable
fun LoadingDots(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "dotsTransition")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 200)
                ), label = "dotAlpha$index"
            )
            
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        SpringBootGreen.copy(alpha = alpha),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}

@Composable
fun RippleEffect(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rippleTransition")
    
    // Create subtle ripples with gentle fading effect
    val ripples = listOf(
        Pair(0.15f, 0),      // First ripple - very subtle
        Pair(0.12f, 1000),   // Second ripple
        Pair(0.08f, 2000),   // Third ripple
        Pair(0.05f, 3000)    // Fourth ripple - barely visible
    )
    
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (isVisible) {
            ripples.forEach { (maxAlpha, delay) ->
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1.8f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(4000, easing = LinearOutSlowInEasing),
                        repeatMode = RepeatMode.Restart,
                        initialStartOffset = StartOffset(delay)
                    ), label = "rippleScale$delay"
                )
                
                val alpha by infiniteTransition.animateFloat(
                    initialValue = maxAlpha,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(4000, easing = FastOutLinearInEasing),
                        repeatMode = RepeatMode.Restart,
                        initialStartOffset = StartOffset(delay)
                    ), label = "rippleAlpha$delay"
                )
                
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale)
                        .border(
                            width = 2.dp,
                            color = SpringBootGreen.copy(alpha = alpha),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}
