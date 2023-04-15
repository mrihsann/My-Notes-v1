package com.ihsanarslan.mynotes.fragment

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.ihsanarslan.mynotes.*
import com.ihsanarslan.mynotes.adapter.Note
import com.ihsanarslan.mynotes.adapter.TrashAdapter
import com.ihsanarslan.mynotes.database.NoteDao
import com.ihsanarslan.mynotes.database.TrashDao
import com.ihsanarslan.mynotes.databinding.FragmentTrashBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

lateinit var noteTrashList: ArrayList<Note>
@Suppress("DEPRECATION")
class TrashFragment : Fragment() {
    private lateinit var binding: FragmentTrashBinding
    private lateinit var noteDao: NoteDao
    private lateinit var trashDao: TrashDao
    private lateinit var trashAdapter: TrashAdapter
    private lateinit var recyclerView: RecyclerView
    lateinit var mAdView : AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(requireContext()) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrashBinding.inflate(inflater, container, false)

        // NoteDao nesnesini oluşturun
        trashDao = noteDatabase.trashDao()
        noteDao = noteDatabase.noteDao()
        //önce tüm notları siliyoruz
        noteTrashList.clear()
        // Tüm notları veritabannından çekip noteLİst'e yazıyoruz
        GlobalScope.launch {
            val trashnotes = trashDao.getAllNotes()
            if (trashnotes.size==0){
                binding.recyclerViewTrash.layoutParams.height = 0 // Yüksekliği 0 olarak ayarla
                binding.item0.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT // Yüksekliği wrap_content olarak ayarla
            }
            else{
                binding.recyclerViewTrash.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT // Yüksekliği wrap_content olarak ayarla
                binding.item0.layoutParams.height = 0 // Yüksekliği 0 olarak ayarla
            }
            trashnotes.forEach { note ->
                val trashnote= Note(note.id,note.title,note.content,note.color,note.liked)
                if (trashnote !in noteTrashList){
                    noteTrashList.add(trashnote)
                }
            }
        }
        binding.recyclerViewTrash.requestLayout()
        binding.item0.requestLayout()

        // RecyclerView'ı oluştur
        recyclerView = binding.recyclerViewTrash
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Adapter'ı oluştur ve RecyclerView'a bağla
        trashAdapter = TrashAdapter(noteTrashList)
        recyclerView.adapter = trashAdapter
        trashAdapter.notifyDataSetChanged()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = binding.bottomAppBarTrash

        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        // SearchView işlemleri burada yapılabilir
        val searchView = binding.searchViewTrash
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                trashAdapter.filter(newText ?: "")
                return true
            }
        })

        binding.adsbutton.setOnClickListener {
            binding.adsbutton.alpha = 0.5f // Geçici olarak alfa değerini azaltın
            Handler().postDelayed({
                binding.adsbutton.alpha = 1.0f // Alfa değerini geri yükleyin
            }, 200) // 200 milisaniye bekletin

            var previousHeight = 0
            if (binding.adView.layoutParams.height == 0) {
                previousHeight = binding.adView.height // Önceki yüksekliği kaydet
                binding.adView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT // Yüksekliği wrap_content olarak ayarla
            } else {
                binding.adView.layoutParams.height = 0 // Yüksekliği 0 olarak ayarla
            }
            binding.adView.requestLayout() // Yeniden boyutlandır
        }

        binding.search.setOnClickListener {
            binding.search.alpha = 0.5f // Geçici olarak alfa değerini azaltın
            Handler().postDelayed({
                binding.search.alpha = 1.0f // Alfa değerini geri yükleyin
            }, 200) // 200 milisaniye bekletin

            var previousHeight = 0
            if (searchView.layoutParams.height == 0) {
                previousHeight = searchView.height // Önceki yüksekliği kaydet
                searchView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT // Yüksekliği wrap_content olarak ayarla
            } else {
                searchView.layoutParams.height = 0 // Yüksekliği 0 olarak ayarla
            }
            searchView.requestLayout() // Yeniden boyutlandır
        }

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    val action = TrashFragmentDirections.actionTrashFragmentToListFragment()
                    Navigation.findNavController(view).navigate(action)
                    true
                }
                R.id.favButton -> {
                    trashAdapter.filterLiked()
                    true
                }

                R.id.addNoteButton -> {
                    //yeni not sayfasına gittiğiimiz zaman, default rengimiz sarı olduğu için bildirim rengini sarı yapıyoruz.
                    val action = TrashFragmentDirections.actionTrashFragmentToAddFragment()
                    Navigation.findNavController(view).navigate(action)
                    true
                }
                R.id.trash -> {
                    val action = TrashFragmentDirections.actionTrashFragmentSelf()
                    Navigation.findNavController(view).navigate(action)
                    true
                }

                else -> false
            }
        }
        //itemi sağa yada sola kaydırarak geri kurtarıyoruz
        val itemTouchHelper = ItemTouchHelper(SwipeToUndoCallback(view,trashAdapter,noteDao,trashDao,requireContext()))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}