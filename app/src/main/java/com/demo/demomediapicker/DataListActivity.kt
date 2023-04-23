package com.demo.demomediapicker

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.demomediapicker.databinding.ActivityDataListBinding
import java.util.concurrent.Exchanger

@Suppress("DEPRECATION")
class DataListActivity : AppCompatActivity() {

    lateinit var binding: ActivityDataListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val type = intent.getIntExtra("type", 0)
        val dataList = intent.getSerializableExtra("dataList") as ArrayList<Uri?>
        Log.d("dataaa", dataList.toString())

        if (type==0) {
            binding.rvMain.layoutManager = GridLayoutManager(this, 2)
            binding.rvMain.adapter = ImageAdapter(this, dataList)
        }
        else {
            binding.rvMain.layoutManager = LinearLayoutManager(this)
            binding.rvMain.adapter = VideoAdapter(this, dataList)
        }
    }
}