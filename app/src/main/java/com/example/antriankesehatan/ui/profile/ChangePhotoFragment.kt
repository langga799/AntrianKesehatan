package com.example.antriankesehatan.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.antriankesehatan.R
import com.example.antriankesehatan.databinding.FragmentChangePhotoBinding
import com.example.antriankesehatan.network.NetworkConfig
import com.example.antriankesehatan.utils.Helper
import com.example.antriankesehatan.utils.SharedPreference
import com.example.antriankesehatan.utils.loadImageView
import com.example.antriankesehatan.utils.toMultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ChangePhotoFragment : Fragment() {

    private lateinit var binding: FragmentChangePhotoBinding
    private lateinit var preference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangePhotoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        preference = SharedPreference(requireActivity())


        val dataPhoto = arguments?.getString("URL_PHOTO") ?: ""
        activity?.loadImageView(Helper.BASE_IMAGE_URL_USER + dataPhoto, binding.ivPhotoProfile)
        Log.d("PHOTO", dataPhoto)


        binding.btnChangeProfile.setOnClickListener {
            checkPermission()
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_DENIED
        ) {
            //permission denied
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            //show popup to request runtime permission
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            //permission already granted
            pickImageFromGallery()
        }

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(requireActivity(), "Permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            binding.ivPhotoProfile.setImageURI(data?.data)


            val selectedImageUri: Uri = data?.data!!
            val picturePath: String = getPath(requireActivity(), selectedImageUri)
            Log.d("Picture Path", picturePath)

            binding.btnSaveImage.setOnClickListener {
                fileUpload(File(picturePath))
            }


        }
    }

    private fun getPath(context: Context, uri: Uri?): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri!!, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }


    private fun fileUpload(file: File?) {

        val token = preference.getToken()
        val image = file.toMultipartBody()

        val builder = AlertDialog.Builder(requireActivity())
            .setView(R.layout.progress)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()
        NetworkConfig().getApiService().uploadPhoto("Bearer $token", image!!)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>,
                ) {
                    if (response.isSuccessful) {
                        dialog.dismiss()
                        Toast.makeText(requireActivity().baseContext,
                            response.message(),
                            Toast.LENGTH_SHORT)
                            .show()

                        findNavController().navigate(R.id.action_changePhotoFragment_to_profileFragment)

                    } else {
                        Toast.makeText(requireActivity().baseContext,
                            response.message(),
                            Toast.LENGTH_SHORT)
                            .show()
                    }


                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    dialog.dismiss()
                    Toast.makeText(requireActivity(), t.message, Toast.LENGTH_SHORT).show()
                    Log.d("Failure", t.message.toString())
                }

            })

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.message_menu).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigate(R.id.action_changePhotoFragment_to_profileFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    companion object {
        private const val PICK_IMAGE = 100
        private const val PERMISSION_CODE = 101
    }
}