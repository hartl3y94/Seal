package com.junkfood.seal.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.junkfood.seal.BaseApplication
import com.junkfood.seal.BaseApplication.Companion.downloadDir
import com.junkfood.seal.databinding.FragmentHomeBinding
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        homeViewModel.progress.observe(viewLifecycleOwner) {
            binding.downloadProgressBar.progress = it.toInt()
            binding.downloadProgressText.text = "$it%"
        }
        homeViewModel.updateTime()
        binding.downloadButton.setOnClickListener {
            var url = binding.inputTextUrl.editText?.text.toString()
            if (url == "") {
                url = "https://youtu.be/t5c8D1xbXtw";
            }
            getVideo(url)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getVideo(url: String) {
        Thread {
            Looper.prepare()
            val request = YoutubeDLRequest(url)
            val videoInfo = YoutubeDL.getInstance().getInfo(url)
            val videoTitle = createFilename(videoInfo.title)
            val videoExt = videoInfo.ext
            Toast.makeText(context, "Start to download '$videoTitle'", Toast.LENGTH_SHORT)
                .show()
            request.addOption("-o", "$downloadDir/$videoTitle.$videoExt")
            //request.addOption("--write-thumbnail")
            //request.addOption("-o", "thumbnail:$downloadDir/%(title)s.%(ext)s")
            request.addOption("--proxy", "http://127.0.0.1:7890")
            request.addOption("--force-overwrites")
            YoutubeDL.getInstance().execute(
                request
            ) { progress: Float, _: Long, s: String ->
                Log.d(TAG, s)
                homeViewModel.updateProgress(progress)
            }
            homeViewModel.updateProgress(100f);
            Toast.makeText(context, "Download completed!", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "$downloadDir/$videoTitle.$videoExt")
            startActivity(Intent().apply {
                action = (Intent.ACTION_VIEW)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(
                    FileProvider.getUriForFile(
                        BaseApplication.context,
                        BaseApplication.context.packageName + ".provider",
                        File("$downloadDir/$videoTitle.$videoExt")
                    ), "video/*"
                )
            })
        }.start()
    }

    fun createFilename(title: String): String {
        val cleanFileName = title.replace("[\\\\><\"|*?'%:#/]".toRegex(), "_")
        var fileName = cleanFileName.trim { it <= '_' }.replace("_+".toRegex(), "_")
        if (fileName.length > 127) fileName = fileName.substring(0, 127)
        return fileName
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}