package com.danpike.socialapp

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.danpike.socialapp.api.ApiInterface
import com.danpike.socialapp.databinding.ActivityMainBinding
import com.danpike.socialapp.fragments.SignInFragment
import com.danpike.socialapp.fragments.SignUpFragment
import com.google.android.material.snackbar.Snackbar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SignInFragment.ISignInFragmentListener,
    SignUpFragment.ISignUpFragmentListener {

    private var endpoint = ""
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var service: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val ip = sharedPref.getString("ip_address", "") ?: ""
        endpoint = "http://$ip:5000/api/"
        buildRetrofitService()
    }

    private fun buildRetrofitService() {
        val client = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl(endpoint)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        service = retrofit.create(ApiInterface::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun checkEndpoint(latestEndpoint: String) {
        if (endpoint != latestEndpoint) {
            endpoint = latestEndpoint
            buildRetrofitService()
        }
    }

    override fun onSignIn(endpoint: String, email: String, password: String) {
        checkEndpoint(endpoint)

        val response = service.signIn(email, password)
        val fragment = supportFragmentManager.fragments[0]?.childFragmentManager?.fragments?.get(0)

        if (fragment != null) {
            (fragment as SignInFragment).handleUserResponse(response)
        }
    }

    override fun onSignUp(firstName: String, lastName: String, email: String, password: String) {
        val response = service.signUp(firstName, lastName, email, password)
        val fragment = supportFragmentManager.fragments[0]?.childFragmentManager?.fragments?.get(0)

        if (fragment != null) {
            (fragment as SignUpFragment).handleSignUpResponse(response)
        }
    }
}