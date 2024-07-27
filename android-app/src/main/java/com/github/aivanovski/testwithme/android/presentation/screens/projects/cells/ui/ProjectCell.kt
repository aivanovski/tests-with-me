package com.github.aivanovski.testwithme.android.presentation.screens.projects.cells.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.github.aivanovski.testwithme.android.presentation.core.compose.AppIcons
import com.github.aivanovski.testwithme.android.presentation.core.compose.PreviewIntentProvider
import com.github.aivanovski.testwithme.android.presentation.core.compose.ThemedPreview
import com.github.aivanovski.testwithme.android.presentation.core.compose.rememberOnClickedCallback
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.AppTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.CardCornerSize
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.ElementMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.HalfMargin
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LargeIconSize
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.LightTheme
import com.github.aivanovski.testwithme.android.presentation.core.compose.theme.TwoLineMediumItemHeight
import com.github.aivanovski.testwithme.android.presentation.screens.projects.cells.model.ProjectCellIntent
import com.github.aivanovski.testwithme.android.presentation.screens.projects.cells.model.ProjectCellModel
import com.github.aivanovski.testwithme.android.presentation.screens.projects.cells.viewModel.ProjectCellViewModel

@Composable
fun ProjectCell(viewModel: ProjectCellViewModel) {
    val model = viewModel.model

    val onClick = rememberOnClickedCallback {
        viewModel.sendIntent(ProjectCellIntent.OnClick(model.id))
    }

    Card(
        shape = RoundedCornerShape(size = CardCornerSize),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.theme.colors.cardOnSecondaryBackground
        ),
        modifier = Modifier
            .padding(horizontal = ElementMargin)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = ElementMargin)
                .height(height = TwoLineMediumItemHeight)
        ) {
            if (model.iconUrl != null) {
                AsyncImage(
                    model = model.iconUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(size = LargeIconSize)
                )
            } else {
                Icon(
                    imageVector = AppIcons.ProjectPlaceholder,
                    tint = AppTheme.theme.colors.primaryIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(size = LargeIconSize)
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = HalfMargin)
                    .weight(weight = 1f)
            ) {
                Text(
                    text = model.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = AppTheme.theme.colors.primaryText,
                    style = AppTheme.theme.typography.bodyLarge
                )

                Text(
                    text = model.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = AppTheme.theme.colors.secondaryText,
                    style = AppTheme.theme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
@Preview
fun ProjectCellPreview() {
    ThemedPreview(
        theme = LightTheme,
        background = LightTheme.colors.secondaryBackground
    ) {
        ProjectCell(newProjectViewModel())
    }
}

fun newProjectViewModel(): ProjectCellViewModel {
    return ProjectCellViewModel(
        model = ProjectCellModel(
            id = "id",
            title = "KeePassVault",
            description = "KeePass database client app",
            iconUrl = null
        ),
        intentProvider = PreviewIntentProvider
    )
}