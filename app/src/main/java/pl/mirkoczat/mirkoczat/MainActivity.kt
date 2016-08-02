package pl.mirkoczat.mirkoczat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.*
import com.rollbar.android.Rollbar

const val ROLLBAR_ENABLED = true
const val ROLLBAR_ID = "8c454038e4ad43a4ada4f5f12131232d"
const val ROLLBAR_ENV = "production"

class MainActivity : AppCompatActivity(), AnkoLogger {
    var token: String
        get() {
            val settings = getSharedPreferences("mirkoczat", 0)
            return settings.getString("token", "")
        }
        set(token) {
            val settings = getSharedPreferences("mirkoczat", 0)
            val editor = settings.edit()
            editor.putString("token", token)
            editor.commit()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
        if (ROLLBAR_ENABLED)
            Rollbar.init(this, ROLLBAR_ID, ROLLBAR_ENV);

        val viewPager = findViewById(R.id.viewpager) as ViewPager
        val tabLayout = findViewById(R.id.tabs) as TabLayout
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.offscreenPageLimit = 3
        tabLayout.setupWithViewPager(viewPager)

        supportActionBar?.title = "#hydepark"
        Connection.token = token
        Connection.onError = { envelope ->
            runOnUiThread {
                val response = envelope.payload.get("response")
                val reason = response.get("reason")
                toast("Błąd: $reason")
            }
        }
        Connection.connect(this, "hydepark")
        Stream.on("app:changeChannel", {
            title -> supportActionBar?.title = "#${title}"
        })
    }

    override fun onBackPressed() {
        Connection.disconnect()
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_token -> {
            try {
                val intent = Intent("com.google.zxing.client.android.SCAN")
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE")
                startActivityForResult(intent, 0)
            } catch (e: Exception) {
                toast("Zainstaluj com.google.zxing.client.android QRCode")
                //val marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                //val marketIntent = Intent(Intent.ACTION_VIEW, marketUri);
                //startActivity(marketIntent);
            }
            true
        }
        R.id.action_logout -> {
            token = ""
            Connection.token = ""
            Connection.connect(this, "hydepark")
            Stream.trigger("app:changeChannel", "hydepark")
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val ddata = data
                    if (ddata != null) {
                        val contents = ddata.getStringExtra("SCAN_RESULT")
                        token = contents
                        Connection.token = contents
                        Connection.connect(this, "hydepark")
                        Stream.trigger("app:changeChannel", "hydepark")
                    }
                }
                else -> {}
            }
        }
    }

}


