package com.lagradost.cloudstream4.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class NuvioSearchResultItem(
    val id: String,
    val name: String,
    val posterUrl: String?,
    val rating: String? = null,
    val type: String? = null,
    val apiName: String? = null,
)

@Immutable
data class NuvioSearchHistoryEntry(
    val key: String,
    val query: String,
)

@Composable
fun NuvioSearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClearQuery: () -> Unit,
    results: List<NuvioSearchResultItem>,
    history: List<NuvioSearchHistoryEntry>,
    onHistoryItemClick: (String) -> Unit,
    onHistoryItemRemove: (String) -> Unit,
    onClearHistory: () -> Unit,
    onResultClick: (NuvioSearchResultItem) -> Unit,
    onFilterClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NuvioColors.Background),
    ) {
        // Top Search Bar
        NuvioSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = {
                keyboardController?.hide()
                onSearch(it)
            },
            onClear = onClearQuery,
            onFilterClick = onFilterClick,
            isLoading = isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        )

        // Content Area: History vs Results
        if (query.isBlank()) {
            if (history.isNotEmpty()) {
                NuvioSearchHistorySection(
                    history = history,
                    onItemClick = onHistoryItemClick,
                    onItemRemove = onHistoryItemRemove,
                    onClearAll = onClearHistory,
                )
            } else {
                NuvioSearchEmptyPrompt(
                    title = "Discover Movies & Shows",
                    subtitle = "Type a title, genre, actor, or studio in the search bar above.",
                )
            }
        } else {
            if (results.isEmpty() && !isLoading) {
                NuvioSearchEmptyPrompt(
                    title = "No Results Found",
                    subtitle = "Try adjusting your spelling or searching for a different keyword.",
                )
            } else {
                NuvioSearchResultsGrid(
                    results = results,
                    onItemClick = onResultClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun NuvioSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    onFilterClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            color = NuvioColors.Surface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (query.isNotBlank()) NuvioColors.Gold.copy(alpha = 0.8f) else NuvioColors.Card
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = if (query.isNotBlank()) NuvioColors.Gold else NuvioColors.TextSecondary,
                    modifier = Modifier.size(20.dp),
                )

                Spacer(Modifier.width(10.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search titles, genres, actors...",
                            color = NuvioColors.TextTertiary,
                            fontSize = 14.sp,
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            color = NuvioColors.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        cursorBrush = SolidColor(NuvioColors.Gold),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = NuvioColors.Gold,
                    )
                } else if (query.isNotEmpty()) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = NuvioColors.TextSecondary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }

        // Filter / Tune Button
        Surface(
            onClick = onFilterClick,
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = NuvioColors.Surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NuvioColors.Card),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Tune,
                    contentDescription = "Providers & Filters",
                    tint = NuvioColors.Gold,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun NuvioSearchHistorySection(
    history: List<NuvioSearchHistoryEntry>,
    onItemClick: (String) -> Unit,
    onItemRemove: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.History,
                    contentDescription = null,
                    tint = NuvioColors.Gold,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "RECENT SEARCHES",
                    color = NuvioColors.TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                )
            }

            Text(
                text = "Clear All",
                color = NuvioColors.TextTertiary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onClearAll() },
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(history, key = { it.key }) { entry ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NuvioColors.Surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NuvioColors.Card),
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { onItemClick(entry.query) }
                            .padding(start = 12.dp, top = 6.dp, bottom = 6.dp, end = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = entry.query,
                            color = NuvioColors.TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(Modifier.width(6.dp))
                        IconButton(
                            onClick = { onItemRemove(entry.key) },
                            modifier = Modifier.size(18.dp),
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Remove",
                                tint = NuvioColors.TextTertiary,
                                modifier = Modifier.size(12.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NuvioSearchResultsGrid(
    results: List<NuvioSearchResultItem>,
    onItemClick: (NuvioSearchResultItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 110.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        items(results, key = { it.id }) { item ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onItemClick(item) },
            ) {
                NuvioPosterCard(
                    title = item.name,
                    posterUrl = item.posterUrl,
                    subtitle = item.type ?: item.apiName,
                    rating = item.rating,
                )
            }
        }
    }
}

@Composable
private fun NuvioSearchEmptyPrompt(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(NuvioColors.Surface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = NuvioColors.Gold,
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                color = NuvioColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = subtitle,
                color = NuvioColors.TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}
