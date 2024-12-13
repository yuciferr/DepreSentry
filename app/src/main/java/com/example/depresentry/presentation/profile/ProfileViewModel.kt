package com.example.depresentry.presentation.profile

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.depresentry.domain.usecase.auth.GetCurrentUserIdUseCase
import com.example.depresentry.domain.usecase.auth.LogoutUserUseCase
import com.example.depresentry.domain.usecase.profile.GetLocalProfileImageUseCase
import com.example.depresentry.domain.usecase.profile.GetUserProfileUseCase
import com.example.depresentry.domain.usecase.userData.SyncDailyDataUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getLocalProfileImageUseCase: GetLocalProfileImageUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val syncDailyDataUseCase: SyncDailyDataUseCase,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var fullName = mutableStateOf("")
    var email = mutableStateOf("")
    var localProfileImagePath = mutableStateOf<String?>(null)
    var logoutSuccess = mutableStateOf(false)
    var isLoading = mutableStateOf(true)
        private set

    private val _permissionStates = mutableStateMapOf(
        "Notification Settings" to false,
        "Physical Activity" to false,
        "App Usage Data" to false,
        "Location" to false
    )
    val permissionStates: Map<String, Boolean> = _permissionStates

    init {
        loadUserProfile()
        checkPermissions()
    }

    private fun checkPermissions() {
        viewModelScope.launch {
            // Bildirim izni kontrolü
            _permissionStates["Notification Settings"] = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                NotificationManagerCompat.from(context).areNotificationsEnabled()
            }

            // Physical Activity izni kontrolü
            _permissionStates["Physical Activity"] = context.checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED

            // App Usage izni kontrolü
            val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOpsManager.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                )
            } else {
                appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                )
            }
            _permissionStates["App Usage Data"] = mode == AppOpsManager.MODE_ALLOWED
        }
    }

    fun togglePermission(permission: String) {
        when (permission) {
            "Notification Settings" -> {
                if (_permissionStates[permission] == true) {
                    // İzni kapat
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                } else {
                    // İzni aç
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    } else {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    }
                }
            }
            "Physical Activity" -> {
                if (_permissionStates[permission] == true) {
                    // İzni kapat - kullanıcıyı app settings'e yönlendir
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                } else {
                    // İzni aç
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            }
            "App Usage Data" -> {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        }
        
        // İzin durumunu kontrol et ve güncelle
        viewModelScope.launch {
            delay(500) // İzin değişikliğinin uygulanması için kısa bir bekleme
            checkPermissions()
        }
    }

    fun requestPermission(permission: String) {
        when (permission) {
            "Notification Settings", "Physical Activity", "App Usage Data" -> {
                togglePermission(permission)
            }
        }
    }

    fun refreshPermissions() {
        checkPermissions()
    }

    fun logout() {
        viewModelScope.launch {
            logoutUserUseCase().onSuccess {
                logoutSuccess.value = true
            }
        }
    }

    private fun loadUserProfile() {
        val userId = getCurrentUserIdUseCase()
        if (userId != null) {
            isLoading.value = true
            email.value = auth.currentUser?.email ?: ""
            
            viewModelScope.launch {
                try {
                    // Firestore'dan profil bilgilerini yükle
                    getUserProfileUseCase(userId).onSuccess { profile ->
                        profile?.let {
                            fullName.value = it.fullName ?: ""
                        }
                    }

                    // Room DB'den profil fotoğrafını yükle
                    getLocalProfileImageUseCase(userId).onSuccess { localImagePath ->
                        localProfileImagePath.value = localImagePath
                    }
                } finally {
                    isLoading.value = false
                }
            }
        }
    }

    fun syncData() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserIdUseCase() ?: return@launch
                Log.d("ProfileViewModel", "Senkronizasyon başlatılıyor...")
                syncDailyDataUseCase(userId)
                Log.d("ProfileViewModel", "Senkronizasyon başarıyla tamamlandı")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Senkronizasyon sırasında hata oluştu", e)
            }
        }
    }

} 