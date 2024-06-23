import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import smarthome.composeapp.generated.resources.Res
import smarthome.composeapp.generated.resources.compose_multiplatform


enum class DevicesScreen() {
    Start,
    Details
}

@Composable
@Preview
fun App() {
    val devicesViewModel = DevicesViewModel()
    Scaffold { innerPadding ->
        val uiState = devicesViewModel.uiState.collectAsState()
        val navHostController = rememberNavController()
        NavHost(
            navController = navHostController,
            startDestination = DevicesScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = DevicesScreen.Start.name) {
                DevicesPage(
                    uiState,
                    onDeviceClicked = { navHostController.navigate(DevicesScreen.Details.name) }
                )
            }
            composable(route = DevicesScreen.Details.name) {
                DeviceDetails()
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DevicesPage(
    uiState: State<DeviceUiState>,
    onDeviceClicked: (device: Device) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp),
        content = {
            items(uiState.value.devices) {
                DeviceCell(
                    it,
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .combinedClickable(
                            onClick = { onDeviceClicked(it) },
                        )
                )
            }
        }
    )
}

@Preview
@Composable
fun DeviceCell(
    device: Device,
    modifier: Modifier
) {
    Card(modifier = modifier) {
        Box(contentAlignment = Alignment.Center) {
            Column {
                Text(text = device.deviceType,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                Text(text = device.deviceId,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                Text(text = device.status,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            }
        }
    }
}

@Composable
fun DeviceDetails() {
    Text("hello")
}
