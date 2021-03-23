package com.aadharcarverificationapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.aadharcarverificationapp.R
import com.aadharcarverificationapp.fragment.FindAadharFragment
import com.aadharcarverificationapp.fragment.QRCodeScannerFragment
import com.aadharcarverificationapp.fragment.UploadImageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class AadharCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(FindAadharFragment())

        setBottomNavigationView()
    }


    private fun setBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionFindAadhar -> {
                    loadFragment(FindAadharFragment())
                }
                R.id.actionUploadImage -> {
                    loadFragment(UploadImageFragment())
                }
                R.id.actionQrCodeScanner -> {
                    loadFragment(QRCodeScannerFragment())
                }

            }
            true
        })
    }

    private fun loadFragment(fragment: Fragment?) {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, fragment)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            fragmentTransaction.commit()
        }
    }

}