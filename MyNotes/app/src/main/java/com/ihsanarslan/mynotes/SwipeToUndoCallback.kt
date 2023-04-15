package com.ihsanarslan.mynotes

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ihsanarslan.mynotes.adapter.TrashAdapter
import com.ihsanarslan.mynotes.database.NoteDB
import com.ihsanarslan.mynotes.database.NoteDao
import com.ihsanarslan.mynotes.database.TrashDB
import com.ihsanarslan.mynotes.database.TrashDao
import com.ihsanarslan.mynotes.fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SwipeToUndoCallback(val view: View,private val adapter: TrashAdapter,private var noteDao: NoteDao, private var trashDao: TrashDao,private var context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val currentitem = noteTrashList[viewHolder.adapterPosition]
        when (direction){
            ItemTouchHelper.RIGHT -> { // sağa kaydırma kurtar
                GlobalScope.launch(Dispatchers.IO) {
                    val newnote = NoteDB(
                        currentitem.id,
                        currentitem.title,
                        currentitem.content,
                        currentitem.color,
                        currentitem.liked
                    )
                    val trashnote = TrashDB(
                        currentitem.id,
                        currentitem.title,
                        currentitem.content,
                        currentitem.color,
                        currentitem.liked
                    )
                    noteDao.insert(newnote)
                    trashDao.delete(trashnote)
                }
                noteTrashList.remove(currentitem)
                noteList.add(currentitem)
                adapter.notifyDataSetChanged()
                // Geri alma işlemi için kullanıcıya bir mesaj gösterme
                Snackbar.make(
                    viewHolder.itemView,
                    "Not Kurtarıldı",
                    Snackbar.LENGTH_LONG
                )
                    .setTextColor(Color.GREEN)
                    .setActionTextColor(Color.WHITE)
                    .show()
                val action= TrashFragmentDirections.actionTrashFragmentSelf()
                Navigation.findNavController(view).navigate(action)

            }

            ItemTouchHelper.LEFT -> {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Warning !!")
                builder.setIcon(R.drawable.ic_baseline_warning_amber_24)
                builder.setMessage("Your note will be permanently deleted, do you want to delete it?")
                    .setCancelable(false) //ekranda biryere tıklayınca uyarı mesajının kapanabilmesini ayarlıyoruz.
                    .setPositiveButton("YES") { dialog, id ->
                        GlobalScope.launch(Dispatchers.IO) {
                            val trashnote = TrashDB(
                                currentitem.id,
                                currentitem.title,
                                currentitem.content,
                                currentitem.color,
                                currentitem.liked
                            )
                            trashDao.delete(trashnote)
                        }
                        noteTrashList.remove(currentitem)
                        adapter.notifyDataSetChanged()
                        // Geri alma işlemi için kullanıcıya bir mesaj gösterme
                        Snackbar.make(
                            viewHolder.itemView,
                            "Not Kalıcı olarak silindi",
                            Snackbar.LENGTH_LONG
                        )
                            .setTextColor(Color.RED)
                            .show()
                        // Geri alma işlemi için kullanıcıya bir mesaj gösterme
                        val action= TrashFragmentDirections.actionTrashFragmentSelf()
                        Navigation.findNavController(view).navigate(action)
                    }
                    .setNegativeButton("NO") { dialog, id ->
                        // Hayır'a tıklandığında yapılacak işlemler buraya yazılır
                        dialog.cancel() // Uyarı mesajını kapatır
                        adapter.notifyDataSetChanged()
                    }
                val alert = builder.create()
                alert.show()
                // Evet düğmesinin yazı rengini kırmızı olarak ayarla
                alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED)

                // Hayır düğmesinin yazı rengini yeşil olarak ayarla
                alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#2C9430"))

                }
            }
        }


    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        if (dX > 0) { // sağa kaydırıldı
            val background = GradientDrawable()
            background.setBounds(
                itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom
            )
            background.shape = GradientDrawable.RECTANGLE
            background.cornerRadius = 30f
            background.setColor(Color.GREEN)
            background.draw(c)

            val icon = ContextCompat.getDrawable(
                itemView.context,
                R.drawable.ic_baseline_recycling_24
            )!!
            val iconMargin = (itemHeight - icon.intrinsicHeight) / 2
            val iconTop = itemView.top + iconMargin
            val iconBottom = iconTop + icon.intrinsicHeight
            val iconLeft = itemView.left + iconMargin
            val iconRight = iconLeft + icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            icon.draw(c)

        } else { // sola kaydırıldı
            val background = GradientDrawable()
            background.setBounds(
                itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom
            )
            background.shape = GradientDrawable.RECTANGLE
            background.cornerRadius = 30f
            background.setColor(Color.RED)
            background.draw(c)
            background.draw(c)

            val icon = ContextCompat.getDrawable(
                itemView.context,
                R.drawable.ic_baseline_delete_forever_24
            )!!
            val iconMargin = (itemHeight - icon.intrinsicHeight) / 2
            val iconTop = itemView.top + iconMargin
            val iconBottom = iconTop + icon.intrinsicHeight
            val iconRight = itemView.right - iconMargin
            val iconLeft = iconRight - icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            icon.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}
