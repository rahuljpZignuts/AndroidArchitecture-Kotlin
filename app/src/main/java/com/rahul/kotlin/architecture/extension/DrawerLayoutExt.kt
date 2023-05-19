package com.rahul.kotlin.architecture.extension

import androidx.drawerlayout.widget.DrawerLayout

fun DrawerLayout.lock() = this.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

fun DrawerLayout.unlock() = this.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
