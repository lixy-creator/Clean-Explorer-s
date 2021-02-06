package com.cinnamon.cleanexplorers.manager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.fragment.HomeFragment
import com.cinnamon.cleanexplorers.fragment.LoginFragment
import com.cinnamon.cleanexplorers.model.User


/**
 * Created by Fanny on 10/11/2020.
 **/
class CleanFragmentManager {

    companion object {
        private fun changeFragment(activity: AppCompatActivity, fragment: Fragment, containerResID: Int, addToBackStack: Boolean) {
            changeFragment(activity, fragment, containerResID, addToBackStack, 0, 0, 0, 0)
        }


        private fun changeFragment(activity: AppCompatActivity?, fragment: Fragment, containerResID: Int, addToBackStack: Boolean, enter: Int, exit: Int, popEnter: Int, popExit: Int) {
            if (activity != null && !activity.isFinishing) {
                val fm: FragmentManager = activity.supportFragmentManager
                val transaction: FragmentTransaction = fm.beginTransaction()
                if (enter > 0 && exit > 0 && popEnter > 0 && popExit > 0) {
                    transaction.setCustomAnimations(enter, exit, popEnter, popExit)
                }
                transaction.replace(containerResID, fragment)
                if (addToBackStack) {
                    transaction.addToBackStack("tag")
                } else {
                    clearBackStack(activity)
                }
                transaction.commit()
                //transaction.commitAllowingStateLoss();
            }
        }


        fun getCurrentFragment(activity: AppCompatActivity): Fragment? {
            return activity.supportFragmentManager.findFragmentById(R.id.fragment_container)
        }

        fun clearBackStack(activity: AppCompatActivity) {
            val manager: FragmentManager = activity.supportFragmentManager
            for (i in 0 until manager.backStackEntryCount) {
                manager.popBackStack()
            }
        }

        fun back(mainActivity: FragmentActivity?): Boolean {
            if (mainActivity != null && mainActivity.supportFragmentManager.backStackEntryCount > 0) {
                mainActivity.supportFragmentManager.popBackStack()
                return true
            } else if (mainActivity != null) {
                // HomeBagFragmentManager.displayHome((MainActivity) mainActivity);
            }
            return false
        }

        fun displayLogin(activity: AppCompatActivity) {
            changeFragment(activity, LoginFragment(), R.id.fragment_container, false)
        }

        fun displayHome(activity: AppCompatActivity, user: User) {
            val bundle: Bundle = Bundle()
            bundle.putParcelable("user", user)
            val fgt = HomeFragment()
            fgt.arguments =bundle
                    changeFragment(activity, fgt, R.id.fragment_container, false)
        }

    }
}