package com.example.bigbangtheory_quiz

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var prevButton: ImageButton
    private lateinit var cheatButton: Button

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this)[QuizViewModel::class.java]
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex
        quizViewModel.context = this

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        questionTextView =  findViewById(R.id.question_text_View)
        cheatButton = findViewById(R.id.cheat_button)

        trueButton.setOnClickListener {
            questionTextView.text = quizViewModel.checkAnswer(true)
            isAnswerButtonsTappable()
        }

        falseButton.setOnClickListener {
            questionTextView.text = quizViewModel.checkAnswer(false)
            isAnswerButtonsTappable()
        }

        prevButton.setOnClickListener {
            questionTextView.text = quizViewModel.moveToPrev()
            isAnswerButtonsTappable()
        }

        nextButton.setOnClickListener {
            questionTextView.text = quizViewModel.moveToNext()
            isAnswerButtonsTappable()
        }

        cheatButton.setOnClickListener { view ->
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options = ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            } else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }

        questionTextView.text = quizViewModel.getString(quizViewModel.currentQuestion.textResId)
        isAnswerButtonsTappable()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.currentQuestion.answerCheated =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOW, false) ?: false
            isAnswerButtonsTappable()
        }
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    private fun isAnswerButtonsTappable() {
        if (quizViewModel.currentQuestion.isAnswered) {
            falseButton.isEnabled = false
            trueButton.isEnabled = false
        } else {
            falseButton.isEnabled = true
            trueButton.isEnabled = true
        }
    }

}