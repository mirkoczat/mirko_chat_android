package pl.mirkoczat.mirkoczat

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import org.jetbrains.anko.AnkoLogger

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm), AnkoLogger {
    val fRooms: RoomsFragment
    val fUsers: UsersFragment
    val fMessages: MessagesFragment
    init {
        fRooms = RoomsFragment()
        fUsers = UsersFragment()
        fMessages = MessagesFragment()
    }
    override fun getItem(position: Int) = when (position) {
        0 -> fRooms
        2 -> fUsers
        else -> fMessages
    } as Fragment
    override fun getCount() = 3
    override fun getPageTitle(position: Int) = when (position) {
        0 -> "Dyskusje"
        2 -> "Obecni"
        else -> "Rozmowa"
    }
}