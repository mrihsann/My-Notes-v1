package com.ihsanarslan.mynotes.fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.ihsanarslan.mynotes.adapter.Note
import com.ihsanarslan.mynotes.adapter.NoteAdapter
import com.ihsanarslan.mynotes.R
import com.ihsanarslan.mynotes.SwipeToDeleteCallback
import com.ihsanarslan.mynotes.database.*
import com.ihsanarslan.mynotes.databinding.FragmentListBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

lateinit var noteList: ArrayList<Note>
@Suppress("DEPRECATION")
class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var noteDao: NoteDao
    private lateinit var trashDao: TrashDao
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var recyclerView: RecyclerView
    lateinit var mAdView : AdView


    fun barcolor(barrenk:Int){
        // Arka plan rengiyle aynı renkte durum çubuğu ayarla
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(requireContext(), barrenk)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(requireContext()) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        // NoteDatabase nesnesi oluşturun
        val noteDatabase = NoteDatabase.getInstance(requireContext())
        // NoteDao nesnesini oluşturun
        noteDao = noteDatabase.noteDao()
        trashDao = noteDatabase.trashDao()

        //önce tüm notları siliyoruz

        noteList.clear()
        // Tüm notları veritabannından çekip noteLİst'e yazıyoruz
        GlobalScope.launch {
            val notes = noteDao.getAllNotes()
            if (notes.size==0){
                binding.recyclerView.layoutParams.height = 0 // Yüksekliği 0 olarak ayarla
                binding.item0.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT // Yüksekliği wrap_content olarak ayarla
            }
            else{
                binding.recyclerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT // Yüksekliği wrap_content olarak ayarla
                binding.item0.layoutParams.height = 0 // Yüksekliği 0 olarak ayarla
            }

            notes.forEach { note ->
                val newnote= Note(note.id,note.title,note.content,note.color,note.liked)
                if (newnote !in noteList){
                    noteList.add(newnote)
                }
            }
        }
        binding.recyclerView.requestLayout()
        binding.item0.requestLayout()

        // RecyclerView'ı oluştur
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Adapter'ı oluştur ve RecyclerView'a bağla
        noteAdapter = NoteAdapter(noteList,noteDao)
        recyclerView.adapter = noteAdapter
        barcolor(R.color.background)
        noteAdapter.notifyDataSetChanged()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        // SearchView işlemleri burada yapılabilir
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                noteAdapter.filter(newText ?: "")
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

        val bottomNav = binding.bottomAppBar
        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    val action=ListFragmentDirections.actionListFragmentSelf()
                    Navigation.findNavController(view).navigate(action)
                    true
                }
                R.id.favButton -> {
                    noteAdapter.filterLiked()
                    true
                }
                R.id.addNoteButton -> {
                    //yeni not sayfasına gittiğiimiz zaman, default rengimiz sarı olduğu için bildirim rengini sarı yapıyoruz.
                    barcolor(R.color.color2)
                    val action=ListFragmentDirections.actionListFragmentToAddFragment()
                    Navigation.findNavController(view).navigate(action)
                    true
                }
                R.id.trash -> {
                    val action=ListFragmentDirections.actionListFragmentToTrashFragment()
                    Navigation.findNavController(view).navigate(action)
                    true
                }

                else -> false
            }
        }
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(view,noteAdapter,noteDao,trashDao,requireContext()))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}