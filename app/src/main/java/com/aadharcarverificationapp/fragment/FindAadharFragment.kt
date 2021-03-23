package com.aadharcarverificationapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aadharcarverificationapp.R
import com.aadharcarverificationapp.utils.VerhoeffAlgorithm
import kotlinx.android.synthetic.main.fragment_find_aadhar.*
import kotlinx.android.synthetic.main.fragment_find_aadhar.view.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class FindAadharFragment : Fragment() {
    private lateinit var layoutView: View
    private val TAG = FindAadharFragment::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layoutView = inflater.inflate(R.layout.fragment_find_aadhar, container, false)

        layoutView.btnValidateAadhar.setOnClickListener {
            if (layoutView.tilAadharCard.text.toString().trim() != "")
                checkAadharNumber()
        }

        setOnClick()
        return layoutView
    }

    private fun checkAadharNumber() {
        validateAadharNumber(layoutView.tilAadharCard.text.toString().trim())
    }

    private fun setOnClick() {
        layoutView.btnSimpleText1.setOnClickListener {
            if (isValidAadharNumber("9247 98B8 ZI9911111")) {
                Log.e(TAG, "True")
            }
        }
        layoutView.btnSimpleText2.setOnClickListener {
            if (isValidAadharNumber(
                    "4722\n" +
                            "2350\n" +
                            " \n" +
                            "0165"
                )
            ) {
                Log.e(TAG, "True")
            }
        }
        layoutView.btnSimpleText3.setOnClickListener {
            if (isValidAadharNumber(
                    "47Z3 \n" +
                            "\n" +
                            "\n" +
                            "235O \n" +
                            "0175"
                )
            ) {
                Log.e(TAG, "True")
            }
        }
        layoutView.btnSimpleText4.setOnClickListener {
            if (isValidAadharNumber("8332 Z453 4766")) {
                Log.e(TAG, "True")
            }
        }
    }

    private fun isValidAadharNumber(str: String?): Boolean {
        // Regex to check valid Aadhar number.
        var newString = ""
        if (str!!.contains("Z")) {
            newString = str.replace("Z", "2")
        }
        if (str!!.contains("I")) {
            newString = str.replace("I", "1")
        }
        if (str!!.contains("B")) {
            newString = str.replace("B", "3")
        }
        if (str!!.contains("O")) {
            newString = str.replace("O", "0")
        }
        if (str.contains("\n")) {
            newString = str.replace("\n", "")
        }
        if (str.contains(" ")) {
            newString = str.replace("", "")
        }

        Log.e(TAG, "newString: \t $newString")

        val regex = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$"
        val p = Pattern.compile(regex)

        if (newString == null) {
            return false
        }
        // and regular expression.
        val m: Matcher = p.matcher(newString)
        return m.matches()
    }

    private fun validateAadharNumber(aadharNumber: String?): Boolean {
        val aadharPattern: Pattern = Pattern.compile("\\d{12}")
        var isValidAadhar: Boolean = aadharPattern.matcher(aadharNumber).matches()
        if (isValidAadhar) {
            isValidAadhar = VerhoeffAlgorithm.validateVerhoeff(aadharNumber)
            Log.e(TAG, "isValidAadhar: \t $isValidAadhar")
            if (isValidAadhar)
                tvValid.text = aadharNumber.plus(" ").plus(getString(R.string.is_valid))
            else
                tvValid.text = aadharNumber.plus(" ").plus(getString(R.string.is_not_valid))
        } else {
            tvValid.text = aadharNumber.plus(" ").plus(getString(R.string.is_not_valid))
            Log.e(TAG, "Not Validate: \t $isValidAadhar")
        }
        return isValidAadhar
    }
}