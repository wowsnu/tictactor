package com.example.assignment1.com.example.assignment1

sealed class DrawerItem {
    data object StartButtonItem : DrawerItem()
    data class BoardItem(val moveNumber: Int, val board: List<List<String?>>) : DrawerItem()
}