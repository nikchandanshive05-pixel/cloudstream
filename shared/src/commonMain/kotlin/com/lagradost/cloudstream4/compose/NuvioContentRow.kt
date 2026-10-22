package com.lagradost.cloudstream4.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> NuvioContentRow(
    items: List<T>,
    key: ((T) -> Any)? = null,
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    itemContent: @Composable (T) -> Unit,
    onExpandClick: @Composable (() -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NuvioSectionHeader(title = title, subtitle = subtitle, action = onExpandClick)
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (key == null) items(items) { itemContent(it) }
            else items(items, key = key) { itemContent(it) }
        }
    }
}
