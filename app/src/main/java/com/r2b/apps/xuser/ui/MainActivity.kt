package com.r2b.apps.xuser.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.r2b.apps.xuser.R
import com.r2b.apps.xuser.databinding.ActivityMainBinding
import com.r2b.apps.xuser.databinding.FragmentUserListBinding
import com.r2b.apps.xuser.ui.common.extensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        actionBar?.setDisplayShowHomeEnabled(true)
        actionBar?.setLogo(R.drawable.ic_logo)
        actionBar?.setDisplayUseLogoEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false)
    }

    /*override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }*/

}