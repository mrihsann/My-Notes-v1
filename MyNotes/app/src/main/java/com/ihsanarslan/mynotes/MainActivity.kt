package com.ihsanarslan.mynotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ihsanarslan.mynotes.database.NoteDatabase
import com.ihsanarslan.mynotes.databinding.ActivityMainBinding
import com.ihsanarslan.mynotes.fragment.noteList
import com.ihsanarslan.mynotes.fragment.noteTrashList

lateinit var noteDatabase: NoteDatabase
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //database oluşturuyoruz
        noteDatabase = NoteDatabase.getInstance(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        noteList= ArrayList()
        noteTrashList =ArrayList()

    }

}