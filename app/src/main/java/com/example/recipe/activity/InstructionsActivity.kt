package com.example.recipe.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.recipe.R
import com.example.recipe.util.SharedPreferencesHelper

class InstructionsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferencesHelper
    private lateinit var instructionsText: EditText
    private lateinit var btnToOven: Button
    private lateinit var btnCancelRecipe: Button
    private lateinit var btnAddVideo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions)
        window.decorView.setBackgroundColor(Color.TRANSPARENT)
        supportActionBar?.hide()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // SharedPreferencesHelper sınıfı ile veri paylaşımı için nesne oluşturuluyor.
        sharedPreferences = SharedPreferencesHelper(this)

        // Arayüz bileşenlerine erişim için gerekli nesneler tanımlanıyor.
        btnToOven = findViewById(R.id.btnToOven)
        btnCancelRecipe = findViewById(R.id.btnCancelRecipe)
        btnAddVideo = findViewById(R.id.btnAddVideo)

        // Video eklemek için dialog gösteren butona tıklanınca çalışacak fonksiyon atanıyor.
        btnAddVideo.setOnClickListener {
            showAddVideoDialog()
        }

        // İptal işlemi için onaylama dialogu gösteren butona tıklanınca çalışacak fonksiyon atanıyor.
        btnCancelRecipe.setOnClickListener {
            showCancelConfirmationDialog()
        }

        // Fırın sayfasına geçiş yapacak butona tıklanınca çalışacak fonksiyon atanıyor.
        goToOvenPage()
    }
    private fun isValidLink(link: String): Boolean {
        // Implement your validation logic here
        // For example, you can check if the link starts with "http" or "https" to consider it a valid link
        return link.startsWith("http") || link.startsWith("https")
    }
    // Video eklemek için dialog gösteren fonksiyon
    private fun showAddVideoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_video, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add Video")

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        val btnOk: Button = dialogView.findViewById(R.id.btnOk)
        btnOk.setOnClickListener {
            val editTextVideoName: EditText = dialogView.findViewById(R.id.editTextVideoName)
            val editTextVideoLink: EditText = dialogView.findViewById(R.id.editTextVideoLink)

            val videoName = editTextVideoName.text.toString()
            val videoLink = editTextVideoLink.text.toString()

            if (isValidLink(videoLink)) {
                // Video limitine ulaşılıp ulaşılmadığını kontrol et
                if (isVideoLimitReached()) {
                    // Video limitine ulaşıldığını belirten bir uyarı göster
                    showVideoLimitReachedAlert()
                } else if (isDuplicateLink(videoLink)) {
                    // Linkin zaten kayıtlı olduğunu belirten bir uyarı göster
                    showDuplicateLinkAlert()
                } else {
                    saveVideoLink(videoLink)
                    addVideoButton(videoName, videoLink, btnAddVideo)
                    alertDialog.dismiss()
                }
            } else {
                // Linkin boş olamayacağını belirten bir uyarı göster
                Toast.makeText(this, "Link cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Linkin zaten kayıtlı olup olmadığını kontrol eden fonksiyon
    private fun isDuplicateLink(newLink: String): Boolean {
        val currentVideoLinks = sharedPreferences.loadData("videoLink")
        val videoLinksArray = currentVideoLinks.split("\n")
        return videoLinksArray.any { it.trim().equals(newLink.trim(), ignoreCase = true) }
    }

    // Zaten kaydedilmiş bir linkin eklenmek istenmesi durumunda gösterilen uyarı
    private fun showDuplicateLinkAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("This link is already saved.")
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    // Video limitine ulaşılıp ulaşılmadığını kontrol eden fonksiyon
    private fun isVideoLimitReached(): Boolean {
        val currentVideoLinks = sharedPreferences.loadData("videoLink")
        val videoLinksArray = currentVideoLinks.split("\n")
        return videoLinksArray.size > 3
    }

    // Video limitine ulaşıldığını belirten bir uyarı gösteren fonksiyon
    private fun showVideoLimitReachedAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("You have reached the video limit.")
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    // Video linkini kaydeden fonksiyon
    private fun saveVideoLink(videoLink: String) {
        val currentVideoLinks = sharedPreferences.loadData("videoLink")
        val newVideoLinks = "$currentVideoLinks$videoLink\n"
        sharedPreferences.saveStringData("videoLink", newVideoLinks)
    }

    // Eklenen video linkini gösteren buton ekleyen fonksiyon
    // Updated addVideoButton function
    // Updated addVideoButton function
    // Updated addVideoButton function
    private fun addVideoButton(videoName: String, videoLink: String, addVideoButton: Button) {
        val layout: ConstraintLayout = findViewById(R.id.constraintInstructions)
        var linearLayout: LinearLayout? = null

        if (layout.childCount > 0) {
            val lastChild = layout.getChildAt(layout.childCount - 1)
            if (lastChild is LinearLayout && lastChild.orientation == LinearLayout.HORIZONTAL) {
                linearLayout = lastChild
            }
        }

        if (linearLayout == null || linearLayout.childCount == 3) {
            linearLayout = LinearLayout(this)
            linearLayout.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.gravity = Gravity.CENTER
            val linearParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            linearParams.topToBottom = R.id.btnAddVideo
            linearParams.setMargins(0, 16, 0, 0)
            linearLayout.layoutParams = linearParams
            layout.addView(linearLayout)
        }

        val newButton = Button(this)
        newButton.text = videoName
        newButton.layoutParams = ViewGroup.LayoutParams(250, 250)
        if (videoLink.contains("youtube", true) || (videoLink.contains("youtu.be", true))) {
            newButton.setBackgroundResource(R.drawable.youtube)
        } else if (videoLink.contains("instagram", true)) {
            newButton.setBackgroundResource(R.drawable.instagram)
        } else if (videoLink.contains("tiktok", true)) {
            newButton.setBackgroundResource(R.drawable.tiktok)
        }


        newButton.setOnClickListener {
            // Check if the link is valid before opening
                openVideoLink(videoLink)
        }

        newButton.setOnLongClickListener {
            // Check if the link is valid before showing the delete confirmation dialog
            showDeleteConfirmationDialog(newButton, videoLink)
            true
        }
        // Add the newButton above the "Add Video" button
        linearLayout.addView(newButton, 0) // Add to the beginning of the linearLayout

        if (linearLayout.childCount > 1) {
            val params =
                linearLayout.getChildAt(linearLayout.childCount - 1).layoutParams as ViewGroup.MarginLayoutParams
            params.leftMargin = 16
        }
    }
    // Add a function to check if the link is valid




    // Video linkini silmek için onaylama dialogu gösteren fonksiyon
    private fun showDeleteConfirmationDialog(button: Button, videoLink: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure to delete this video link?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Video linkini SharedPreferences'ten sil
            removeVideoLink(videoLink)

            // Butonu parent layout'tan kaldır
            val parentLayout = button.parent as LinearLayout
            parentLayout.removeView(button)
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    // Video linkini SharedPreferences'ten silen fonksiyon
    private fun removeVideoLink(videoLink: String) {
        val currentVideoLinks = sharedPreferences.loadData("videoLink")
        val newVideoLinks = currentVideoLinks.replace("$videoLink\n", "")
        sharedPreferences.saveStringData("videoLink", newVideoLinks)
    }

    // Video linkini açan fonksiyon
    private fun openVideoLink(videoLinks: String) {
        val linksArray = videoLinks.split("\n")

        for (link in linksArray) {
            val trimmedVideoLink = link.trim()
            Log.d("VideoLink", "Video Link: $trimmedVideoLink")

            if (trimmedVideoLink.isNotEmpty()) {
                val videoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(trimmedVideoLink))

                when {
                    trimmedVideoLink.contains("youtube", true) || trimmedVideoLink.contains("youtu.be", true) -> {
                        videoIntent.setPackage("com.google.android.youtube")
                    }
                    trimmedVideoLink.contains("instagram", true) -> {
                        videoIntent.setPackage("com.instagram.android")
                    }
                    trimmedVideoLink.contains("tiktok", true) -> {
                        videoIntent.setPackage("com.zhiliaoapp.musically")
                    }
                    else -> {
                        // If the link doesn't contain any of the specified platforms, consider it invalid
                        Toast.makeText(this, "This is not a valid link", Toast.LENGTH_SHORT).show()
                        return  // Return here to prevent starting the activity for an invalid link
                    }
                }

                startActivity(videoIntent)
            } else {
                Toast.makeText(this, "Please enter a valid video link", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // İptal işlemi için onaylama dialogu gösteren fonksiyon
    private fun showCancelConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure to cancel your recipe?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            sharedPreferences.deleteData("videoLink")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left)
            finish()
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.window?.attributes?.windowAnimations = 0
        alertDialog.show()
    }

    // Fırın sayfasına geçiş yapacak butona tıklanınca çalışacak fonksiyon
    private fun goToOvenPage() {
        btnToOven.setOnClickListener {
            instructionsText = findViewById(R.id.instructionsEditText)
            val instructionsText = instructionsText.text.toString()
            sharedPreferences.saveData("Instructions", instructionsText)

            val intent = Intent(this, OvenActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}
