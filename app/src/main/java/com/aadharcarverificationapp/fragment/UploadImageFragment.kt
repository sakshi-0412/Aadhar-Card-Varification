package com.aadharcarverificationapp.fragment

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aadharcarverificationapp.R
import kotlinx.android.synthetic.main.fragment_upload_image.view.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


class UploadImageFragment : Fragment() {

    private val TAG = UploadImageFragment::class.java.simpleName

    private lateinit var layoutView: View

    private val REQUEST_PERMISSION = 100
    private val REQUEST_SINGLE_IMAGE_CAPTURE = 1
    private val REQUEST_SINGLE_PICK_IMAGE = 2
    private val REQUEST_MULTIPLE_IMAGE_CAPTURE = 3
    private val REQUEST_MULTIPLE_GALLERY_CAPTURE = 4
    private var isFromCamera = false
    private var isFromGallery = false
    private var imageCount = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutView = inflater.inflate(R.layout.fragment_upload_image, container, false)

        layoutView.btnCamera.setOnClickListener {
            isFromCamera = true
            isFromGallery = false
            setImageSelectionDialog()
        }

        layoutView.btnGallery.setOnClickListener {
            isFromCamera = false
            isFromGallery = true
            setImageSelectionDialog()
        }

        return layoutView
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
    }

    private fun setImageSelectionDialog() {
        val builder1: AlertDialog.Builder = AlertDialog.Builder(context)
        builder1.setMessage(getString(R.string.number_of_image))
        builder1.setCancelable(true)

        if (isFromCamera) {
            builder1.setPositiveButton(
                getString(R.string._1),
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    selectSingleCameraImage()
                })

            builder1.setNegativeButton(
                getString(R.string._2),
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    layoutView.image1.setImageResource(0)
                    layoutView.image2.setImageResource(0)
                    selectMultipleCameraImage()
                })
        } else if (isFromGallery) {
            builder1.setPositiveButton(
                getString(R.string._1),
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    selectSingleGalleryImage()
                })

            builder1.setNegativeButton(
                getString(R.string._2),
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    selectMultipleGalleryImage()
                })
        }

        val alert11: AlertDialog = builder1.create()
        alert11.show()

    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION
            )
        }
    }

    private fun selectSingleCameraImage() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(requireContext().packageManager)?.also {
                startActivityForResult(intent, REQUEST_SINGLE_IMAGE_CAPTURE)
            }
        }
    }

    private fun selectSingleGalleryImage() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            intent.resolveActivity(requireContext().packageManager)?.also {
                startActivityForResult(intent, REQUEST_SINGLE_PICK_IMAGE)
            }
        }
    }

    private fun selectMultipleCameraImage() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(requireContext().packageManager)?.also {
                startActivityForResult(intent, REQUEST_MULTIPLE_IMAGE_CAPTURE)
            }
        }
    }

    private fun selectMultipleGalleryImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, REQUEST_MULTIPLE_GALLERY_CAPTURE)
    }

    private fun convertUriToBitmap(uri: Uri): Bitmap {
        var bitmap: Bitmap? = null
        if(Build.VERSION.SDK_INT < 28) {
             bitmap = MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver,
                uri
            )
            getResizedBitmap(bitmap, 500)
        } else {
            val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
             bitmap = ImageDecoder.decodeBitmap(source)
            getResizedBitmap(bitmap, 500)
        }
        return bitmap!!
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }

        val finalBitmap =  Bitmap.createScaledBitmap(image, width, height, true)
        Log.e(TAG, "finalBitmap: \t ${finalBitmap.byteCount}")
        return finalBitmap
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SINGLE_IMAGE_CAPTURE) {
                val bitmap = data?.extras?.get("data") as Bitmap
                layoutView.image1.setImageBitmap(getResizedBitmap(bitmap, 500))
                layoutView.image2.visibility = View.GONE
            } else if (requestCode == REQUEST_SINGLE_PICK_IMAGE) {
                val uri = data?.data
                val bitmap = convertUriToBitmap(uri!!)
                layoutView.image1.setImageBitmap(bitmap)
                //layoutView.image1.setImageURI(uri)
                layoutView.image2.visibility = View.GONE
            } else if (requestCode == REQUEST_MULTIPLE_IMAGE_CAPTURE) {
                layoutView.image2.visibility = View.VISIBLE
                val bitmap = data?.extras?.get("data") as Bitmap
                if (layoutView.image1.drawable == null) {
                    layoutView.image1.setImageBitmap(getResizedBitmap(bitmap, 500))
                } else if (layoutView.image2.drawable == null) {
                    layoutView.image2.setImageBitmap(getResizedBitmap(bitmap, 500))
                }
                if (layoutView.image2.drawable == null) {
                    selectMultipleCameraImage()
                }
            } else if (requestCode == REQUEST_MULTIPLE_GALLERY_CAPTURE) {
                layoutView.image2.visibility = View.VISIBLE
                if (data!!.clipData != null) {
                    val mClipData: ClipData = data.clipData!!
                    for (i in 0 until mClipData.itemCount) {
                        val item = mClipData.getItemAt(i)
                        val uri: Uri = item.uri
                        Log.e(TAG, "uri1: \t $uri")
                        val bitmap1 = convertUriToBitmap(mClipData.getItemAt(0).uri)
                        val bitmap2 = convertUriToBitmap(mClipData.getItemAt(1).uri)
                        layoutView.image1.setImageBitmap(bitmap1)
                        layoutView.image2.setImageBitmap(bitmap2)
                        //layoutView.image1.setImageURI(mClipData.getItemAt(0).uri)
                       // layoutView.image2.setImageURI(mClipData.getItemAt(1).uri)

                    }
                } else if (data.data != null) {
                    val uri: Uri = data.data!!
                    layoutView.image1.setImageURI(uri)
                    Log.e(TAG, "uri2: \t $uri")
                }
            } else {
                val returnIntent = Intent()
                requireActivity().setResult(Activity.RESULT_CANCELED, returnIntent)
            }
        } else {
            Log.e(TAG, "resultCode: \t $resultCode")
        }
    }
}