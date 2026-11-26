package com.mertcan.pilot.feature.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mertcan.pilot.core.ui.PilotColors

@Composable
fun AssistantScreen(
    isListening: Boolean,
    isAwake: Boolean,
    voiceStatus: String,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onClosePilot: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PilotColors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Header(isListening = isListening)
            WelcomePanel()
            VoicePanel(
                isListening = isListening,
                isAwake = isAwake,
                voiceStatus = voiceStatus,
                onStartListening = onStartListening,
                onStopListening = onStopListening
            )
            QuickActions()
            SafetyNote()
            OutlinedButton(
                onClick = onClosePilot,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("PILOT'U TAMAMEN KAPAT")
            }
            Text(
                text = "PILOT • EARLY ACCESS 0.1.0",
                modifier = Modifier.fillMaxWidth(),
                color = PilotColors.TextSecondary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun Header(isListening: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "PILOT",
                color = PilotColors.TextPrimary,
                fontSize = 25.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 5.sp
            )
            Text(
                text = "AI DRIVING ASSISTANT",
                color = PilotColors.TextSecondary,
                fontSize = 9.sp,
                letterSpacing = 2.2.sp
            )
        }
        Surface(
            color = if (isListening) PilotColors.AccentSoft else PilotColors.Surface,
            shape = CircleShape
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(
                            if (isListening) PilotColors.Success else PilotColors.TextSecondary,
                            CircleShape
                        )
                )
                Text(
                    text = if (isListening) "DİNLİYOR" else "BEKLEMEDE",
                    color = if (isListening) PilotColors.Success else PilotColors.TextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun WelcomePanel() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = PilotColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(ColorTop, ColorBottom)))
                .padding(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "YOL SENİN.",
                    color = PilotColors.Accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Dikkatin yolda.\nGerisini Pilot'a bırak.",
                    color = PilotColors.TextPrimary,
                    fontSize = 25.sp,
                    lineHeight = 31.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Sesli komut, navigasyon ve araç deneyimin tek kokpitte.",
                    color = PilotColors.TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun VoicePanel(
    isListening: Boolean,
    isAwake: Boolean,
    voiceStatus: String,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = PilotColors.SurfaceElevated)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "VOICE COMMAND",
                color = PilotColors.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .background(
                        if (isAwake) Brush.radialGradient(
                            listOf(PilotColors.Accent, PilotColors.AccentSoft)
                        ) else Brush.radialGradient(
                            listOf(PilotColors.Surface, PilotColors.AccentSoft)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "P",
                    color = PilotColors.TextPrimary,
                    fontSize = 43.sp,
                    fontWeight = FontWeight.Light
                )
            }
            Text(
                text = when {
                    isAwake -> "Buradayım."
                    isListening -> "“Hey Pilot” de"
                    else -> "Sesli asistanı başlat"
                },
                color = PilotColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = voiceStatus,
                color = PilotColors.TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "AÇ: “Hey Pilot”     •     OTURUMU KAPAT: “Pilot kapan”",
                color = PilotColors.Accent,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = if (isListening) onStopListening else onStartListening,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isListening) PilotColors.Surface else PilotColors.Accent,
                    contentColor = if (isListening) PilotColors.TextPrimary else PilotColors.Background
                )
            ) {
                Text(
                    text = if (isListening) "DİNLEMEYİ DURDUR" else "PILOT'U ETKİNLEŞTİR",
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun QuickActions() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "KOKPİT",
            color = PilotColors.TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionCard(Modifier.weight(1f), "⌖", "Navigasyon", "Yakında")
            ActionCard(Modifier.weight(1f), "♫", "Medya", "Yakında")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionCard(Modifier.weight(1f), "◉", "Bildirimler", "Yakında")
            ActionCard(Modifier.weight(1f), "⌁", "Araç bağlantısı", "Yakında")
        }
    }
}

@Composable
private fun ActionCard(
    modifier: Modifier,
    symbol: String,
    title: String,
    status: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PilotColors.Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(symbol, color = PilotColors.Accent, fontSize = 25.sp)
            Text(title, color = PilotColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(status, color = PilotColors.TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
private fun SafetyNote() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PilotColors.Surface, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("!", color = PilotColors.Warning, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "Pilot sürüş sırasında dikkat dağıtmayacak şekilde kullanılmalıdır. Ekrana değil yola odaklan.",
            color = PilotColors.TextSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    }
}

private val ColorTop = androidx.compose.ui.graphics.Color(0xFF152C40)
private val ColorBottom = androidx.compose.ui.graphics.Color(0xFF0F1925)
