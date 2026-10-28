package com.repent.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.repent.app.BuildConfig
import com.repent.app.R
import com.repent.app.data.model.Product
import com.repent.app.ui.theme.RepentBlack
import com.repent.app.ui.theme.RepentGold
import com.repent.app.ui.theme.RepentGray
import com.repent.app.ui.theme.RepentWhite

/**
 * Prefer the product photos bundled with the APK: they work on tablets even
 * when the developer's XAMPP computer is switched off or on another network.
 * Unknown images may still load from an absolute URL or the configured server.
 */
fun productImageModel(image: String?): Any? {
    if (image.isNullOrBlank()) return null
    val name = image.substringAfterLast('/').substringBefore('?').lowercase()
    val drawable = when (name) {
        "tshirt-white.jpg" -> R.drawable.repent_tshirt_white
        "products-collection.jpg" -> R.drawable.repent_products_collection
        "products-flat.jpg" -> R.drawable.repent_products_flat
        "people-wearing.jpg" -> R.drawable.repent_people_wearing
        "hoodies-collection.jpg" -> R.drawable.repent_hoodies_collection
        "cap-coral.jpg" -> R.drawable.repent_cap_coral
        "cap-olive.jpg" -> R.drawable.repent_cap_olive
        "cap-yellow.jpg" -> R.drawable.repent_cap_yellow
        "cap-mint.jpg" -> R.drawable.repent_cap_mint
        "cap-khaki.jpg" -> R.drawable.repent_cap_khaki
        else -> null
    }
    if (drawable != null) return drawable
    if (image.startsWith("https://", ignoreCase = true) ||
        image.startsWith("http://", ignoreCase = true)
    ) return image
    return BuildConfig.BASE_URL.trimEnd('/') + "/assets/images/" + image.trimStart('/')
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepentTopBar(
    title: String,
    cartCount: Int = 0,
    onCartClick: (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Medium) },
        navigationIcon = { navigationIcon?.invoke() },
        actions = {
            if (onCartClick != null) {
                BadgedBox(
                    badge = { if (cartCount > 0) Badge { Text(cartCount.toString()) } },
                    modifier = Modifier.padding(end = 8.dp),
                ) {
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = RepentBlack,
        ),
    )
}

@Composable
fun GoldButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = RepentGold,
            contentColor = RepentBlack,
            disabledContainerColor = RepentGold.copy(alpha = 0.4f),
        ),
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
    }
}

@Composable
fun OutlineDarkButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = RepentBlack),
    ) {
        Text(text, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = RepentGold,
    )
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = RepentGold)
    }
}

@Composable
fun ErrorState(message: String, modifier: Modifier = Modifier, onRetry: (() -> Unit)? = null) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        if (onRetry != null) {
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onRetry) { Text("Retry") }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(RepentWhite)
            .clickable(onClick = onClick),
    ) {
        Box {
            AsyncImage(
                model = productImageModel(product.image),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            )
            if (!product.badge.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(RepentBlack)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(product.badge, color = RepentGold, fontSize = 10.sp)
                }
            }
        }
        Column(Modifier.padding(12.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleMedium, maxLines = 1)
            Spacer(Modifier.height(4.dp))
            Text(
                "R${product.price.toInt()}",
                color = RepentBlack,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth().height(36.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RepentBlack),
            ) {
                Text("Add to Cart", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun EmptyState(icon: String, title: String, subtitle: String? = null) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(icon, fontSize = 40.sp)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        if (subtitle != null) {
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = RepentGray, textAlign = TextAlign.Center)
        }
    }
}
