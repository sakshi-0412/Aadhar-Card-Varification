package com.aadharcarverificationapp.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aadharcarverificationapp.R
import com.aadharcarverificationapp.utils.AadhaarCard
import com.aadharcarverificationapp.utils.AadhaarXMLParser
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_q_r_code_scanner.view.*

class QRCodeScannerFragment : Fragment() {

    private val TAG = QRCodeScannerFragment::class.java.simpleName

    private lateinit var layoutView: View

    private val MY_CAMERA_REQUEST_CODE = 100
    var text = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layoutView = inflater.inflate(R.layout.fragment_q_r_code_scanner, container, false)

        layoutView.btnScaQRCode.setOnClickListener {
            scanNow()
        }

        return layoutView
    }


    /**
     * Function to check if user has granted access to camera
     * @return boolean
     */
    private fun checkCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                MY_CAMERA_REQUEST_CODE
            )
            return false
        }
        return true
    }

    /**
     * handler for scan card
     */
    private fun scanNow() {
        if (!checkCameraPermission()) {
            return
        }
        val integrator = IntentIntegrator(requireActivity())
        /* integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
         integrator.setPrompt(getString(R.string.scan_qr_code))
         integrator.setResultDisplayDuration(500)
         integrator.setCameraId(0) // Use a specific camera of the device
         integrator.initiateScan()
         startActivityForResult(integrator, IntentIntegrator.REQUEST_CODE)*/

        val intent = IntentIntegrator(requireActivity())
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            .createScanIntent()
        startActivityForResult(intent, IntentIntegrator.REQUEST_CODE)
    }


    /**
     * function handle scan result
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {

        val scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent)
        if (scanningResult != null) {
            val scanContent = scanningResult.contents
            val scanFormat = scanningResult.formatName

            if (scanContent != null && !scanContent.isEmpty()) {
                // processScannedData(scanContent)
                val newCard = AadhaarXMLParser().parse(scanContent)
                setAadharCardData(newCard)
                Log.e(TAG, "newCard: \t $newCard")
                Log.e(TAG, "newCard: \t ${newCard.uid}")
            } else {
                showToast("Scan Cancelled")
            }
        } else {
            showToast("No scan data received!")
        }
    }

    private fun setAadharCardData(newCard: AadhaarCard) {
        if (newCard.uid != "" && newCard.uid != null) {
            layoutView.tvUid.text = getString(R.string.uid).plus(" ").plus(newCard.uid)
        }
        if (newCard.name != "" && newCard.name != null) {
            layoutView.tvName.text = getString(R.string.name).plus(" ").plus(newCard.name)
        }
        if (newCard.gender != "" && newCard.gender != null) {
            layoutView.tvGender.text = getString(R.string.gender).plus(" ").plus(newCard.gender)
        }
        if (newCard.yob != "" && newCard.yob != null) {
            layoutView.tvYob.text = getString(R.string.yob).plus(" ").plus(newCard.yob)
        }
        if (newCard.co != "" && newCard.co != null) {
            layoutView.tvCo.text = getString(R.string.co).plus(" ").plus(newCard.co)
        }
        if (newCard.house != "" && newCard.house != null) {
            layoutView.tvHouse.text = getString(R.string.house).plus(" ").plus(newCard.house)
        }
        if (newCard.lm != "" && newCard.lm != null) {
            layoutView.tvLm.text = getString(R.string.lm).plus(" ").plus(newCard.lm)
        }
        if (newCard.loc != "" && newCard.loc != null) {
            layoutView.tvLoc.text = getString(R.string.loc).plus(" ").plus(newCard.loc)
        }
        if (newCard.vtc != "" && newCard.vtc != null) {
            layoutView.tvVtc.text = getString(R.string.vtc).plus(" ").plus(newCard.vtc)
        }
        if (newCard.po != "" && newCard.po != null) {
            layoutView.tvPo.text = getString(R.string.po).plus(" ").plus(newCard.po)
        }
        if (newCard.dist != "" && newCard.dist != null) {
            layoutView.tvDist.text = getString(R.string.dist).plus(" ").plus(newCard.dist)
        }
        if (newCard.subdist != "" && newCard.subdist != null) {
            layoutView.tvSubDist.text = getString(R.string.subdist).plus(" ").plus(newCard.subdist)
        }
        if (newCard.state != "" && newCard.state != null) {
            layoutView.tvState.text = getString(R.string.state).plus(" ").plus(newCard.state)
        }
        if (newCard.pincode != "" && newCard.pincode != null) {
            layoutView.tvPinCode.text = getString(R.string.pincode).plus(" ").plus(newCard.pincode)
        }
        if (newCard.dob != "" && newCard.dob != null) {
            layoutView.tvDob.text = getString(R.string.dob).plus(" ").plus(newCard.dob)
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

}