package com.example.android.guesstheword.screens.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.NonCancellable.start
import kotlin.concurrent.thread

/**
Created by sshriwas on 2020-05-27
 */
class GameViewModel : ViewModel() {

    // we use _score as a private mutable live data which is internal to the viewmodel.
    //it acts as the backing property to the public score property which is a liveData.
    private val _score = MutableLiveData<Int>()
    //score is a liveData so it cannot be modified externally. Access modifiers are used to implement encapsulation.
    val score : LiveData<Int>
    get() = _score

    // The current word is a backing property to word
    private val _word = MutableLiveData<String>()
    val word : LiveData<String>
    get() = _word

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    //Flag to indicate game is finished
    val eventGameFinished = MutableLiveData<Boolean>()

    //Count down timer
    private var _countdownTimer = MutableLiveData<Int>()
    val countDownTimer : LiveData<Int>
    get() = _countdownTimer

    lateinit var mythread : Thread

    init {
        println("GameViewModel created!")
        //Init live data values
        _score.value = 0
        _word.value = ""
        _countdownTimer.value = 30

        mythread = thread(start = false) {
            while (_countdownTimer.value!! > 0) {
                Thread.sleep(1000)
                _countdownTimer.postValue(_countdownTimer.value?.minus(1))
            }
        }

        resetList()
        nextWord()
    }


    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            eventGameFinished.value = true
        } else {
            _word.value = wordList.removeAt(0)
        }
    }

    /** Methods for buttons presses **/

    fun onSkip() {
        _score.value = score.value?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = score.value?.plus(1)
        nextWord()
    }

    //called when associated activity or fragment is destroyed
    override fun onCleared() {
        super.onCleared()
        //Terminate any operations
        if (mythread.isAlive) {
            mythread.interrupt()
        }
        println("GameViewModel destroyed!")
    }

    fun onGameFinishedCalled() {
        eventGameFinished.value = false
    }

    fun startCountDown() {
        mythread.start()
    }
}