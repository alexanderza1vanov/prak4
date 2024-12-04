package com.example.a4kotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DateListAdapter
    private var isReceiverRegistered = false

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("ListFragment", "Получено обновление списка фотографий")
            adapter.updatePhotoPaths(loadPhotos())
            Toast.makeText(context, "Список обновлён", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = DateListAdapter(loadPhotos()) { photoPath ->
            openPhoto(photoPath)
        }
        recyclerView.adapter = adapter

        registerReceiver()
    }

    override fun onResume() {
        super.onResume()
        if (!isReceiverRegistered) {
            registerReceiver()
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterReceiver()
    }

    private fun registerReceiver() {
        if (!isReceiverRegistered) {
            requireContext().registerReceiver(updateReceiver, IntentFilter("UPDATE_PHOTO_LIST"))
            isReceiverRegistered = true
        }
    }

    private fun unregisterReceiver() {
        if (isReceiverRegistered) {
            try {
                requireContext().unregisterReceiver(updateReceiver)
                isReceiverRegistered = false
            } catch (e: IllegalArgumentException) {
                Log.w("ListFragment", "Ресивер не зарегистрирован: ${e.message}")
            }
        }
    }

    private fun loadPhotos(): List<String> {
        val outputDir = requireContext().getExternalFilesDir("photos")
        return outputDir?.listFiles { file -> file.extension == "jpg" }
            ?.sortedByDescending { it.lastModified() }
            ?.map { it.nameWithoutExtension + " - " + it.lastModified().toFormattedDate() } ?: emptyList()
    }

    private fun openPhoto(photoPath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val photoFile = File(requireContext().getExternalFilesDir("photos"), photoPath)
        intent.setDataAndType(Uri.fromFile(photoFile), "image/*")
        startActivity(intent)
    }

    private fun Long.toFormattedDate(): String {
        val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        return format.format(java.util.Date(this))
    }
}
