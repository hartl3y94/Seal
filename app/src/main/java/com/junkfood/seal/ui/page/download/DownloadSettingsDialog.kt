package com.junkfood.seal.ui.page.download

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.junkfood.seal.R
import com.junkfood.seal.ui.component.*
import com.junkfood.seal.ui.page.settings.download.AudioFormatDialog
import com.junkfood.seal.ui.page.settings.download.CommandTemplateDialog
import com.junkfood.seal.ui.page.settings.download.VideoFormatDialog
import com.junkfood.seal.ui.page.settings.download.VideoQualityDialog
import com.junkfood.seal.util.PreferenceUtil

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DownloadSettingDialog(
    drawerState: ModalBottomSheetState,
    confirm: () -> Unit,
    hide: () -> Unit
) {
    var audio by remember { mutableStateOf(PreferenceUtil.getValue(PreferenceUtil.EXTRACT_AUDIO)) }
    var thumbnail by remember { mutableStateOf(PreferenceUtil.getValue(PreferenceUtil.THUMBNAIL)) }
    var customCommand by remember { mutableStateOf(PreferenceUtil.getValue(PreferenceUtil.CUSTOM_COMMAND)) }

    var showAudioFormatEditDialog by remember { mutableStateOf(false) }
    var showVideoQualityDialog by remember { mutableStateOf(false) }
    var showVideoFormatDialog by remember { mutableStateOf(false) }
    var showCustomCommandDialog by remember { mutableStateOf(false) }

    BottomDrawer(drawerState = drawerState, sheetContent = {
        Icon(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            imageVector = Icons.Outlined.DoneAll,
            contentDescription = stringResource(R.string.settings)
        )
        Text(
            text = stringResource(R.string.settings_before_download),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )
        Text(
            text = stringResource(R.string.settings_before_download_text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        DrawerSheetSubtitle(text = stringResource(id = R.string.options))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            FilterChipWithAnimatedIcon(
                selected = audio,
                enabled = !customCommand,
                onClick = { audio = !audio },
                label = stringResource(R.string.extract_audio)
            )
            FilterChipWithAnimatedIcon(
                selected = thumbnail, enabled = !customCommand,
                onClick = { thumbnail = !thumbnail },
                label = stringResource(R.string.create_thumbnail)
            )


            FilterChipWithAnimatedIcon(
                selected = customCommand,
                onClick = { customCommand = !customCommand },
                label = stringResource(R.string.custom_command)
            )
        }

        DrawerSheetSubtitle(text = stringResource(id = R.string.additional_settings))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(visible = !audio) {
                Row{
                ButtonChip(
                    onClick = { showVideoFormatDialog = true }, enabled = !customCommand && !audio,
                    label = stringResource(R.string.video_format),
                    icon = Icons.Outlined.VideoFile
                )

                ButtonChip(
                    onClick = { showVideoQualityDialog = true }, enabled = !customCommand && !audio,
                    label = stringResource(R.string.video_quality),
                    icon = Icons.Outlined._4k
                )}
            }
            AnimatedVisibility(visible = audio) {
                ButtonChip(
                    onClick = { showAudioFormatEditDialog = true }, enabled = !customCommand,
                    label = stringResource(R.string.convert_audio),
                    icon = Icons.Outlined.AudioFile
                )
            }
            ButtonChip(
                onClick = { showCustomCommandDialog = true },
                label = stringResource(
                    R.string.edit_custom_command_template
                ),
                icon = Icons.Outlined.Code, enabled = customCommand
            )

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp), horizontalArrangement = Arrangement.End
        ) {

            OutlinedButtonWithIcon(
                modifier = Modifier.padding(horizontal = 12.dp),
                onClick = hide,
                icon = Icons.Outlined.Cancel,
                text = stringResource(R.string.cancel)
            )

            FilledButtonWithIcon(
                onClick = {
                    PreferenceUtil.updateValue(PreferenceUtil.EXTRACT_AUDIO, audio)
                    PreferenceUtil.updateValue(PreferenceUtil.THUMBNAIL, thumbnail)
                    PreferenceUtil.updateValue(PreferenceUtil.CUSTOM_COMMAND, customCommand)
                    hide()
                    confirm()
                }, icon = Icons.Outlined.DownloadDone,
                text = stringResource(R.string.start_download)
            )

        }
    }
    )

    if (showAudioFormatEditDialog) {
        AudioFormatDialog(onDismissRequest = { showAudioFormatEditDialog = false })
    }
    if (showVideoQualityDialog) {
        VideoQualityDialog(onDismissRequest = { showVideoQualityDialog = false })
    }
    if (showVideoFormatDialog) {
        VideoFormatDialog(onDismissRequest = { showVideoFormatDialog = false })
    }
    if (showCustomCommandDialog) {
        CommandTemplateDialog(onDismissRequest = { showCustomCommandDialog = false })
    }
}