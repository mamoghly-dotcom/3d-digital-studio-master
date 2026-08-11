package com.tapempire.idle.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tapempire.idle.ui.theme.DeepPurple
import com.tapempire.idle.ui.theme.Gold
import com.tapempire.idle.ui.theme.GoldDark
import com.tapempire.idle.ui.theme.TextPrimary
import com.tapempire.idle.ui.theme.TextSecondary
import com.tapempire.idle.util.NumberFormatter
import kotlinx.coroutines.launch
import kotlin.random.Random

private data class FloatingText(val id: Long, val text: String, val xOffsetDp: Int)

@Composable
fun TapArea(
    clickPower: Double,
    comboMultiplierPercent: Int,
    onTap: () -> Double,
    modifier: Modifier = Modifier
) {
    val floatingTexts = remember { mutableStateListOf<FloatingText>() }
    var nextId by remember { mutableLongStateOf(0L) }
    val scaleAnim = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    val gain = onTap()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    floatingTexts.add(
                        FloatingText(
                            id = nextId++,
                            text = "+" + NumberFormatter.format(gain),
                            xOffsetDp = Random.nextInt(-60, 60)
                        )
                    )
                    scope.launch {
                        scaleAnim.snapTo(0.9f)
                        scaleAnim.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    }
                })
            },
        contentAlignment = Alignment.Center
    ) {
        floatingTexts.forEach { ft ->
            FloatingScoreText(text = ft.text, xOffsetDp = ft.xOffsetDp) {
                floatingTexts.remove(ft)
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (comboMultiplierPercent > 0) {
                Text(
                    text = "🔥 Kombo +%$comboMultiplierPercent",
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Box(
                modifier = Modifier
                    .size(190.dp)
                    .scale(scaleAnim.value)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Gold, GoldDark))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "₺", fontSize = 72.sp, fontWeight = FontWeight.ExtraBold, color = DeepPurple)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Dokunuş gücü: ${NumberFormatter.format(clickPower)}",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun FloatingScoreText(text: String, xOffsetDp: Int, onFinished: () -> Unit) {
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        launch {
            offsetY.animateTo(-140f, animationSpec = tween(durationMillis = 900))
        }
        alpha.animateTo(0f, animationSpec = tween(durationMillis = 900))
        onFinished()
    }

    Text(
        text = text,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        modifier = Modifier
            .offset(x = xOffsetDp.dp, y = offsetY.value.dp)
            .alpha(alpha.value)
    )
}
