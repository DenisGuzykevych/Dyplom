package com.example.wellminder.ui.screens.food

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

@Composable
fun BarcodeScannerScreen(
    onDismiss: () -> Unit,
    onProductFound: (com.example.wellminder.data.local.entities.FoodWithNutrients, Boolean) -> Unit,
    viewModel: FoodViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scanResult by viewModel.scanResult.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // Реакція на результат сканування
    LaunchedEffect(scanResult) {
        if (scanResult is FoodViewModel.ScanResult.Success) {
            val successResult = scanResult as FoodViewModel.ScanResult.Success
            onProductFound(successResult.food, successResult.isLocal)
            viewModel.resetScanResult()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val executor = ContextCompat.getMainExecutor(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                        val scanner = ImageAnalysis.Builder()
                            .setTargetResolution(Size(1280, 720))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        
                        scanner.setAnalyzer(Executors.newSingleThreadExecutor(), BarcodeAnalyzer { barcode ->
                            // Щоб не спамити запитами, робимо пошук тільки якщо ми в Idle
                            if (viewModel.scanResult.value == FoodViewModel.ScanResult.Idle) {
                                viewModel.searchProductByBarcode(barcode)
                            }
                        })

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                scanner
                            )
                        } catch (e: Exception) {
                            android.util.Log.e("Scanner", "Binding failed", e)
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("Scanner", "Init failed", e)
                    }
                }, executor)
                previewView
            },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Потрібен дозвіл на камеру", color = Color.White)
            }
        }

        // Overlay & UI
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { onDismiss() },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Рамка для сканування (візуальна)
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .background(Color.Transparent)
                    .padding(2.dp)
            ) {
                // Можна додати куточки або рамку
            }

            Spacer(modifier = Modifier.weight(1f))

            // Інфо-панель знизу
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Box(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    when (scanResult) {
                        is FoodViewModel.ScanResult.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFFFF8A00))
                            Text("Шукаємо продукт...", modifier = Modifier.padding(top = 40.dp), fontSize = 14.sp)
                        }
                        is FoodViewModel.ScanResult.NotFound -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Продукт не знайдено", color = Color.Red, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = { viewModel.resetScanResult() },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Спробувати знову")
                                }
                            }
                        }
                        is FoodViewModel.ScanResult.Idle -> {
                            Text("Наведіть камеру на штрих-код", fontWeight = FontWeight.Medium)
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
