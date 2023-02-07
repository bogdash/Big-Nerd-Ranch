package com.example.bigbangtheory_quiz

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {

    var currentIndex = 0
    @SuppressLint("StaticFieldLeak")
    var context: Context? = null

    private val questionBank = listOf(
        Question(R.string.question_roommate, true),
        Question(R.string.question_girlfriend, false),
        Question(R.string.question_rajesh_mom, false),
        Question(R.string.question_rajesh_mutism, false),
        Question(R.string.question_emmy_neuroscientist, true),
        Question(R.string.question_howard_doctorate, false),
        Question(R.string.question_comic_books, true)
    )

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestion: Question
        get() = questionBank[currentIndex]

    fun moveToNext(): String {
        currentIndex = (currentIndex + 1) % questionBank.size
        return quizText()
    }

    fun moveToPrev(): String {
        currentIndex = (currentIndex - 1 + questionBank.size) % questionBank.size
        return quizText()
    }

    fun checkAnswer(answer: Boolean): String {
        if(currentQuestion.answerCheated) {
            Toast.makeText(context, R.string.judgment_toast, Toast.LENGTH_SHORT).show()
        } else if (answer == currentQuestion.answer) {
            currentQuestion.userAnswer = true
            Toast.makeText(context, R.string.correct_toast, Toast.LENGTH_SHORT).show()
        } else {
            currentQuestion.userAnswer = false
            Toast.makeText(context, R.string.incorrect_toast, Toast.LENGTH_SHORT).show()
        }
        currentQuestion.isAnswered = true

        return quizText()
    }

    private fun quizText(): String {
        return if (questionBank.all { it.isAnswered }) {
            totalResult()
        } else {
            showQuestion()
        }
    }

    private fun showQuestion(): String {
        return getString(currentQuestion.textResId)
    }

    private fun totalResult(): String {
        val rightAnswers = questionBank.filter { it.userAnswer == true }
        val accuracy = ((rightAnswers.size).toDouble() / (questionBank.size.toDouble())) * 100.0
        val resultAccuracy = accuracy.toInt()

        return getString(R.string.rightAnswers) + " $resultAccuracy%"
    }

    fun getString(textId: Int): String {
        return context?.getString(textId) ?: ""
    }

}