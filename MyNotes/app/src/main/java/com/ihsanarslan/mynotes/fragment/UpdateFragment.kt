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
import com.ihsanarslan.mynotes.database.*
import com.ihsanarslan.mynotes.databinding.FragmentUpdateBinding
import com.ihsanarslan.mynotes.noteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpdateFragment : Fragment() {
    private lateinit var binding: FragmentUpdateBinding
    private lateinit var noteDao: NoteDao
    fun backAlert(view: View){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Your note is not saved !!")
        builder.setIcon(R.drawable.ic_baseline_warning_amber_24)
        builder.setMessage("If you exit, your note will not be updated. Do you want out?")
            .setCancelable(false) //ekranda biryere tıklayınca uyarı mesajının kapanabilmesini ayarlıyoruz.
            .setPositiveButton("YES") { dialog, id ->
                // Evet'e tıklandığında yapılacak işlemler buraya yazılır
                val action= UpdateFragmentDirections.actionUpdateFragmentToListFragment()
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

    fun barcolor(barrenk:Int){
        // Arka plan rengiyle aynı renkte durum çubuğu ayarla
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(requireContext(),barrenk)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateBinding.inflate(inflater, container, false)

        // NoteDao nesnesini oluşturun
        noteDao = noteDatabase.noteDao()
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var colorr="#FFF599"
        //eğer ki herhangi bir renk değişimi yapmadan güncelleme yaparsak rngi #26A69A şeklinde değiştiriyordu.
        //Bu sorunu düzeltmek için aşağıda ki when yapısı yazıldı.
        arguments?.let {
            val color=UpdateFragmentArgs.fromBundle(it).color
            println(color)
            when (color) {
                -24930 -> {colorr="#FF9E9E"}
                -2663 -> {colorr="#FFF599"}
                -7211889 -> {colorr="#91F48F"}
                -157185 -> {colorr="#FD99FF"}
                -6356993 -> {colorr="#9EFFFF"}
                -1 -> {colorr="#FFFFFF"}
                else -> barcolor(R.color.color2)
            }
        }
        binding.toolBarU.backButtonU.setOnClickListener {
            backAlert(it)
        }

        //update fragmanına gelen verileri kullandık burada
        arguments?.let {
            val id=UpdateFragmentArgs.fromBundle(it).id
            val title=UpdateFragmentArgs.fromBundle(it).title
            val content=UpdateFragmentArgs.fromBundle(it).content
            val color=UpdateFragmentArgs.fromBundle(it).color
            val liked=UpdateFragmentArgs.fromBundle(it).liked
            binding.addTitleU.setText(title)
            binding.addContentU.setText(content)
            binding.anaLayoutU.setBackgroundColor(color)
            when (color) {
                -24930 -> barcolor(R.color.color1)
                -2663 -> barcolor(R.color.color2)
                -7211889 -> barcolor(R.color.color3)
                -157185 -> barcolor(R.color.color4)
                -6356993 -> barcolor(R.color.color5)
                -1 -> barcolor(R.color.color6)
                else -> barcolor(R.color.color2)
            }

            //güncelleme yapıyoruz
            binding.toolBarU.updatebuttonU.setOnClickListener {
                if (binding.addTitleU.length()!=0) {
                    GlobalScope.launch {
                        val title = binding.addTitleU.text.toString()
                        val content = binding.addContentU.text.toString()
                        val color = Color.parseColor(colorr)
                        noteDao.update(NoteDB(id, title, content, color, liked))
                    }
                    NoteAdapter(noteList, noteDao).notifyItemChanged(id)

                    val action = UpdateFragmentDirections.actionUpdateFragmentToListFragment()
                    Navigation.findNavController(it).navigate(action)
                }
                else{
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Title not found !!")
                    builder.setIcon(R.drawable.ic_baseline_warning_amber_24)
                    builder.setMessage("If you do not add a title, your note will not be updated. Do you want to continue editing the note ?")
                        .setCancelable(false) //ekranda biryere tıklayınca uyarı mesajının kapanabilmesini ayarlıyoruz.
                        .setPositiveButton("YES") { dialog, id ->
                            dialog.cancel()
                        }
                        .setNegativeButton("NO") { dialog, id ->
                            val action= UpdateFragmentDirections.actionUpdateFragmentToListFragment()
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
            binding.toolBarU.colors.setOnClickListener {
                var previousHeight = 0
                if (binding.colors.layoutParams.height == 0) {
                    previousHeight = binding.colors.height // Önceki yüksekliği kaydet
                    binding.colors.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT // Yüksekliği wrap_content olarak ayarla
                } else {
                    binding.colors.layoutParams.height = 0 // Yüksekliği 0 olarak ayarla
                }
                binding.colors.requestLayout() // Yeniden boyutlandır
            }
        }

        //arka plan rengini değiştiriyoruz
        binding.color1U.setOnClickListener {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color1))
            barcolor(R.color.color1)
            colorr="#FF9E9E"
            setImageViewSize(165,165, R.id.color1U)
            resetImageViewSize(R.id.color2U)
            resetImageViewSize(R.id.color3U)
            resetImageViewSize(R.id.color4U)
            resetImageViewSize(R.id.color5U)
            resetImageViewSize(R.id.color6U)

        }
        binding.color2U.setOnClickListener {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color2))
            barcolor(R.color.color2)
            colorr="#FFF599"
            setImageViewSize(165,165, R.id.color2U)
            resetImageViewSize(R.id.color1U)
            resetImageViewSize(R.id.color3U)
            resetImageViewSize(R.id.color4U)
            resetImageViewSize(R.id.color5U)
            resetImageViewSize(R.id.color6U)
        }
        binding.color3U.setOnClickListener {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color3))
            barcolor(R.color.color3)
            colorr="#91F48F"
            setImageViewSize(165,165, R.id.color3U)
            resetImageViewSize(R.id.color1U)
            resetImageViewSize(R.id.color2U)
            resetImageViewSize(R.id.color4U)
            resetImageViewSize(R.id.color5U)
            resetImageViewSize(R.id.color6U)
        }
        binding.color4U.setOnClickListener {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color4))
            barcolor(R.color.color4)
            colorr="#FD99FF"
            setImageViewSize(165,165, R.id.color4U)
            resetImageViewSize(R.id.color1U)
            resetImageViewSize(R.id.color2U)
            resetImageViewSize(R.id.color3U)
            resetImageViewSize(R.id.color5U)
            resetImageViewSize(R.id.color6U)
        }
        binding.color5U.setOnClickListener {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color5))
            barcolor(R.color.color5)
            colorr="#9EFFFF"
            setImageViewSize(165,165, R.id.color5U)
            resetImageViewSize(R.id.color1U)
            resetImageViewSize(R.id.color2U)
            resetImageViewSize(R.id.color3U)
            resetImageViewSize(R.id.color4U)
            resetImageViewSize(R.id.color6U)
        }
        binding.color6U.setOnClickListener {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color6))
            barcolor(R.color.color6)
            colorr="#FFFFFF"
            setImageViewSize(165,165, R.id.color6U)
            resetImageViewSize(R.id.color1U)
            resetImageViewSize(R.id.color2U)
            resetImageViewSize(R.id.color3U)
            resetImageViewSize(R.id.color4U)
            resetImageViewSize(R.id.color5U)
        }

    }
}