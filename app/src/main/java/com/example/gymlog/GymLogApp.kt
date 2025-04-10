package com.example.gymlog

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//Необхідно використовувати GymLogApp+HiltAndroidApp замість Application
@HiltAndroidApp
class GymLogApp: Application()