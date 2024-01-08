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

        sharedPreferences = SharedPreferencesHelper(this)
        btnToOven = findViewById(R.id.btnToOven)
        btnCancelRecipe = findViewById(R.id.btnCancelRecipe)
        btnAddVideo = findViewById(R.id.btnAddVideo)
        btnAddVideo.setOnClickListener {
            showAddVideoDialog()
        }
        btnCancelRecipe.setOnClickListener {
            showCancelConfirmationDialog()
        }
        goToOvenPage()
    }

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

            if (videoLink.isNotEmpty()) {
                // Check if the limit is reached (assuming the limit is 3)
                if (isVideoLimitReached()) {
                    // Show an alert indicating that the video limit is reached
                    showVideoLimitReachedAlert()
                } else if (isDuplicateLink(videoLink)) {
                    // Show an alert indicating that the link is already saved
                    showDuplicateLinkAlert()
                } else {
                    saveVideoLink(videoLink)
                    addVideoButton(videoName, videoLink)
                    alertDialog.dismiss()
                }
            } else {
                // Show an alert indicating that the link cannot be empty
                Toast.makeText(this, "Link cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isDuplicateLink(newLink: String): Boolean {
        val currentVideoLinks = sharedPreferences.loadData("videoLink")
        val videoLinksArray = currentVideoLinks.split("\n")
        return videoLinksArray.any { it.trim().equals(newLink.trim(), ignoreCase = true) }
    }

    private fun showDuplicateLinkAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("This link is already saved.")
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private fun isVideoLimitReached(): Boolean {
        val currentVideoLinks = sharedPreferences.loadData("videoLink")
        println(currentVideoLinks)
        val videoLinksArray = currentVideoLinks.split("\n")
        return videoLinksArray.size > 3
    }

    private fun showVideoLimitReachedAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("You have reached the video limit.")
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private fun saveVideoLink(videoLink: String) {
        val currentVideoLinks = sharedPreferences.loadData("videoLink")
        val newVideoLinks = "$currentVideoLinks$videoLink\n"
        sharedPreferences.saveStringData("videoLink", newVideoLinks)
    }

    private fun addVideoButton(videoName: String, videoLink: String) {
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
        }else{
            newButton.setBackgroundResource(R.drawable.default_video)
        }

        newButton.setOnClickListener {
            openVideoLink(videoLink)
        }

        newButton.setOnLongClickListener {
            showDeleteConfirmationDialog(newButton, videoLink)
            true
        }


        linearLayout.addView(newButton)

        if (linearLayout.childCount > 1) {
            val params =
                linearLayout.getChildAt(linearLayout.childCount - 1).layoutParams as ViewGroup.MarginLayoutParams
            params.leftMargin = 16
        }
    }
    private fun showDeleteConfirmationDialog(button: Button, videoLink: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure to delete this video link?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Remove the video link from SharedPreferences
            removeVideoLink(videoLink)

            // Remove the button from its parent layout
            val parentLayout = button.parent as LinearLayout
            parentLayout.removeView(button)
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun removeVideoLink(videoLink: String) {
        val currentVideoLinks = sharedPreferences.loadData("videoLink")
        val newVideoLinks = currentVideoLinks.replace("$videoLink\n", "")
        sharedPreferences.saveStringData("videoLink", newVideoLinks)
    }

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
                        // Handle other platforms if needed
                    }
                }

                if (videoIntent.resolveActivity(packageManager) != null) {
                    startActivity(videoIntent)
                } else {
                    videoIntent.setPackage(null)
                    startActivity(videoIntent)
                }
            } else {
                Toast.makeText(this, "Please enter a valid video link", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
