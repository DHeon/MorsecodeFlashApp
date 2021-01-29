package com.example.kotvmorse

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var map : MutableMap<String, String>
    private val permissionList = mutableListOf(Manifest.permission.CAMERA)
    private lateinit var cameraManager : CameraManager
    private var cameraId : String? = null

    private fun checkPermission(){
        for (permission in permissionList) {
            val chk = checkCallingOrSelfPermission(permission)
            if (chk == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permissionList.toTypedArray(), 0)
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "권한을 설정해주세요", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    //플래시 길게 키는 메소드
    private fun toMosL() {
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
        }
        this.flashLightOn()
        try {
            Thread.sleep(300)
        } catch (e: InterruptedException) {
        }
        this.flashLightOff()
    }

    //플래시 짧게 키는 메소드
    private fun toMosS() {
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
        }
        this.flashLightOn()
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
        }
        this.flashLightOff()
    }
     private fun startMorse(mos: String?) {
        try {
            val a = map[mos]

            val num = a?.length
            for (i in 0 until num!!) {
                if (a[i] == '0') {
                    toMosS()
                } else if (a[i] == '1') {
                    toMosL()
                }
            }
        } catch (e: NullPointerException) {
        }
    }

    fun startButton(view: View?) {
        Thread {
            val et = findViewById<EditText>(R.id.editText)
            val st = et.text.toString().toLowerCase(Locale.ROOT).trim { it <= ' ' }

            val array = st.split("").toTypedArray()
            for (i in array) {
                startMorse(i)

                try {
                    Thread.sleep(300)
                } catch (e: InterruptedException) {
                }
            }
        }.start()
    }

    private fun flashLightOn() {
        try {
            cameraManager.setTorchMode(cameraId!!, true)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

     private fun flashLightOff() {
        try {
            cameraManager.setTorchMode(cameraId!!, false)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(
                applicationContext,
                "There is no camera flash.\n The app will finish!",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager

        map = mutableMapOf()
        map["a"] = "01"
        map["b"] = "1000"
        map["c"] = "1010"
        map["d"] = "100"
        map["e"] = "0"
        map["f"] = "0010"
        map["g"] = "110"
        map["h"] = "0000"
        map["i"] = "00"
        map["j"] = "0111"
        map["k"] = "101"
        map["l"] = "0100"
        map["m"] = "11"
        map["n"] = "10"
        map["o"] = "111"
        map["p"] = "0110"
        map["q"] = "1101"
        map["r"] = "010"
        map["s"] = "000"
        map["t"] = "1"
        map["u"] = "001"
        map["v"] = "0001"
        map["w"] = "011"
        map["x"] = "1001"
        map["y"] = "1011"
        map["z"] = "1100"
        map["1"] = "01111"
        map["2"] = "00111"
        map["3"] = "00011"
        map["4"] = "00001"
        map["5"] = "00000"
        map["6"] = "10000"
        map["7"] = "11000"
        map["8"] = "11100"
        map["9"] = "11110"
        map["0"] = "11111"
        if (cameraId == null) {
            try {
                for (id in cameraManager.cameraIdList) {
                    val c = cameraManager.getCameraCharacteristics(id)
                    val flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
                    val lensFacing = c.get(CameraCharacteristics.LENS_FACING)
                    if (flashAvailable != null && flashAvailable && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                        cameraId = id
                        break
                    }
                }
            } catch (e: CameraAccessException) {
                cameraId = null
                e.printStackTrace()
                return
            }
        }
    }
}
