package com.ihsanarslan.mynotes.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.ihsanarslan.mynotes.adapter.NoteAdapter
import com.ihsanarslan.mynotes.R
import com.ihsanarslan.mynotes.database.NoteDB
import com.ihsanarslan.mynotes.database.NoteDao
import com.ihsanarslan.mynotes.databinding.FragmentAddBinding
import com.ihsanarslan.mynotes.noteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddBinding
    private lateinit var noteDao: NoteDao

    fun backAlert(view: View){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Your note is not saved !!")
        builder.setIcon(R.drawable.ic_baseline_warning_amber_24)
        builder.setMessage("If you exit, your note will not be added. Do you want out?")
            .setCancelable(false) //ekranda biryere tıklayınca uyarı mesajının kapanabilmesini ayarlıyoruz.
            .setPositiveButton("YES") { dialog, id ->
                // Evet'e tıklandığında yapılacak işlemler buraya yazılır
                val action= AddFragmentDirections.actionAddFragmentToListFragment()
                Navigation.findNavController(view).navigate(action)
            }
            .setNegativeButton("NO") { dialog, id ->
                // Hayır'a tıklandığında yapılacak işlemler buraya yazılır
                dialog.cancel() // Uyarı mesajını kapatır
            }
        val alert = builder.create()
        alert.show()
        // Evet düğmesinin yazı rengini kırmızı olarak ayarla
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED)

        // Hayır düğmesinin yazı rengini yeşil olarak ayarla
        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#2C9430"))
    }
    fun setImageViewSize(width: Int, height: Int,colorview:Int) {
        val imageView = view?.findViewById<ImageView>(colorview)
        val layoutParams = imageView?.layoutParams
        layoutParams?.width = width
        layoutParams?.height = height
        imageView?.layoutParams = layoutParams
    }

    fun resetImageViewSize(colorview:Int) {
        val imageView = view?.findViewById<ImageView>(colorview)
        val layoutParams = imageView?.layoutParams
        layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        imageView?.layoutParams = layoutParams
    }

    fun barcolor(view: View,barrenk:Int){
        // Arka plan rengiyle aynı renkte durum çubuğu ayarla
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(requireContext(), barrenk)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        noteDao = noteDatabase.noteDao()
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var colorr="#FFF599"
        binding.toolBar.backButton.setOnClickListener {
            backAlert(it)
        }

        binding.toolBar.addbutton.setOnClickListener {
            val titlee=binding.addTitle.text
            val contentt=binding.addContent.text

            if (titlee.length!=0){
                GlobalScope.launch {
                    val newnotee= NoteDB(0,titlee.toString(),contentt.toString(), Color.parseColor(colorr),false)
                    noteDao.insert(newnotee)
                }
                val action=AddFragmentDirections.actionAddFragmentToListFragment()
                Navigation.findNavController(it).navigate(action)
                NoteAdapter(noteList,noteDao).notifyDataSetChanged()
            }
            else{
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Title not found !!")
                builder.setIcon(R.drawable.ic_baseline_warning_amber_24)
                builder.setMessage("If you do not add a title, your note will not be added. Do you want to continue editing the note ?")
                    .setCancelable(false) //ekranda biryere tıklayınca uyarı mesajının kapanabilmesini ayarlıyoruz.
                    .setPositiveButton("YES") { dialog, id ->
                        dialog.cancel()
                    }
                    .setNegativeButton("NO") { dialog, id ->
                        val action= AddFragmentDirections.actionAddFragmentToListFragment()
                        Navigation.findNavController(view).navigate(action)
                    }
                val alert = builder.create()
                alert.show()
                // Evet düğmesinin yazı rengini kırmızı olarak ayarla
                alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#2C9430"))

                // Hayır düğmesinin yazı rengini yeşil olarak ayarla
                alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }

        }
        binding.toolBar.colors.setOnClickListener {
            var previousHeight = 0
            if (binding.colors.layoutParams.height == 0) {
                previousHeight = binding.colors.height // Önceki yüksekliği kaydet
                binding.colors.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT // Yüksekliği wrap_content olarak ayarla
            } else {
                binding.colors.layoutParams.height = 0 // Yüksekliği 0 olarak ayarla
            }
            binding.colors.requestLayout() // Yeniden boyutlandır
        }
        //arka plan rengini değiştiriyoruz
        binding.color1.setOnClickListener {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color1))
            colorr="#FF9E9E"
            barcolor(view,R.color.color1)
            setImageViewSize(165,165,R.id.color1)
            resetImageViewSize(R.id.color2)
            resetImageViewSize(R.id.color3)
            resetImageViewSize(R.id.color4)
            resetImageViewSize(R.id.color5)
            resetImageViewSize(R.id.color6)

        }
        binding.color2.setOnClickListener {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color2))
            colorr="#FFF599"
            barcolor(view,R.color.color2)
            setImageViewSize(165,165,R.id.color2)
            resetImageViewSize(R.id.color1)
            resetImageViewSize(R.id.color3)
            resetImageViewSize(R.id.color4)
            resetImageViewSize(R.id.color5)
            resetImageViewSize(R.id.color6)
        }
        binding.color3.setOnClickListener {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color3))
            colorr="#91F48F"
            barcolor(view,R.color.color3)
            setImageViewSize(165,165,R.id.color3)
            resetImageViewSize(R.id.color1)
            resetImageViewSize(R.id.color2)
            resetImageViewSize(R.id.color4)
            resetImageViewSize(R.id.color5)
            resetImageViewSize(R.id.color6)
        }
        binding.color4.setOnClickListener {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color4))
            colorr="#FD99FF"
            barcolor(view,R.color.color4)
            setImageViewSize(165,165,R.id.color4)
            resetImageViewSize(R.id.color1)
            resetImageViewSize(R.id.color2)
            resetImageViewSize(R.id.color3)
            resetImageViewSize(R.id.color5)
            resetImageViewSize(R.id.color6)
        }
        binding.color5.setOnClickListener {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color5))
            colorr="#9EFFFF"
            barcolor(view,R.color.color5)
            setImageViewSize(165,165,R.id.color5)
            resetImageViewSize(R.id.color1)
            resetImageViewSize(R.id.color2)
            resetImageViewSize(R.id.color3)
            resetImageViewSize(R.id.color4)
            resetImageViewSize(R.id.color6)
        }
        binding.color6.setOnClickListener {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color6))
            colorr="#FFFFFF"
            barcolor(view,R.color.color6)
            setImageViewSize(165,165,R.id.color6)
            resetImageViewSize(R.id.color1)
            resetImageViewSize(R.id.color2)
            resetImageViewSize(R.id.color3)
            resetImageViewSize(R.id.color4)
            resetImageViewSize(R.id.color5)
        }
    }

}