package com.cosinum.luke.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CoordinateViewModel : ViewModel() {

    // This is a LiveData field. Choosing this structure because the whole list tend to be updated
    // at once in ML and not individual elements. Updating this once for the entire list makes
    // sense.
    private val _coordinateList = MutableLiveData<List<Coordinate>>()
    val CoordinateList: LiveData<List<Coordinate>> = _coordinateList

    fun updateData(coordinates: List<Coordinate>){
        _coordinateList.postValue(coordinates)
    }

}


/**
 * Simple Data object with two fields for the label and probability
 */
data class Coordinate(var x:Float, var y:Float) {

    // For easy logging
    override fun toString():String{
        return "$xValue / $yValue"
    }
    var xValue = x
    var yValue = y
}