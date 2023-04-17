package com.example.kotlinstudy

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.*
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.kotlinstudy.data.Constants
import com.example.kotlinstudy.data.DataStoreUtils
import com.example.kotlinstudy.data.dataStore
import com.example.kotlinstudy.databinding.ActivityMainBinding
import com.example.kotlinstudy.ui.MyViewPaint
import com.example.kotlinstudy.ui.MyViewWave
import com.example.kotlinstudy.util.RamUitl
import com.example.kotlinstudy.util.RamUitl.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding



    private lateinit var MyView: MyViewWave
    private lateinit var MyView2: MyViewWave
    private lateinit var myViewPaint: MyViewPaint
    private lateinit var grainAnimation: Animation
    private lateinit var alphaAnimation: Animation
    private lateinit var raiseAnimation: Animation
    private lateinit var imageAnim: ValueAnimator

    private var mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> {
                    binding.appGrain.startAnimation(grainAnimation)
                    imageAnim.start()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        val a = 1
        var str = "I have $a apple"
        var str1 = "I have $a apple"
        println(str)
        println(str1)
        println("${str1.replace("have", "had")} lala")

        initView()
        initListener()
        //xieCheng()
        ramAppGet()

        lifecycleScope.launch(Dispatchers.Main) { //ui线程
            saveUserData("zhangSan")
            getUserData()
            delay(1000L)
            saveUserData("liSi")
//            lifecycleScope.async { saveUserData("zhangSan") }
//            lifecycleScope.async { getUserData() }
//            delay(1000L)
//            lifecycleScope.async { saveUserData("liSi") }
        }

    }

    private suspend fun getUserData(): String {
        //dataStore获取数据，collect 是一个挂起函数，所以会一直挂起，只要name的值发起变更，collect 就会回调
        val nameFlow = this.dataStore.data.map {
            it[DataStoreUtils.keyName] ?: ""
        }
        nameFlow.collect { name ->
            Log.d("datastore", "name $name")
        }


//        dataStore.data.collect {
//            val name = it[nameKey] ?: ""
//            Log.d("datastore", "getUserData: $name")
//        }
        return  nameFlow.first()
    }

    private suspend fun saveUserData(name: String) {
        dataStore.edit {
            it[DataStoreUtils.keyName] = name
        }
    }

    private fun initView() {
        //MyView = MyViewWrite(this)
        MyView = MyViewWave(this, null)
        MyView2 = MyViewWave(this, null)
        myViewPaint = MyViewPaint(this)
        binding.root.addView(MyView)
        MyView.startMove()

        binding.root.addView(myViewPaint)

        /*waterViewAnim()*/

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            binding.success2.startPlay()
        }, 2000)


        val list = mutableListOf<String>()

        batteryAnim()

    }

    private fun ramAppGet() {
        //getRunningApp(this)
        getRunningApp2(this)
        //getRunning3rdApp(this)

//        val AppList = fetchInstalledApps(this)
//        val noSysAppList = mutableListOf<Map<String,Any>>()
//        AppList.forEach {
//            val label = it["lable"]
//            val pkgName = it["desc"].toString()
//            Log.d("ram", "ramAppGet:label  ${label}  ")
//            Log.d("ram", "ramAppGet:pkgName  ${pkgName}  ")
//            val systemApp = isSystemApp(this, pkgName)
//            if(!systemApp){ noSysAppList.add(it)}
//            //Drawable icon = (Drawable)appMap.get("icon")
//        }
//        Log.d("ram", "AppList:size  ${AppList.size}  ")
//        Log.d("ram", "noSysAppList:size  ${noSysAppList.size}  ")


        val appList = mutableListOf<String>()
        val packageManager: PackageManager = packageManager
        val installedPackages = packageManager.getInstalledPackages(0)

        for (appInfo in installedPackages) {
            if (ApplicationInfo.FLAG_SYSTEM and appInfo.applicationInfo.flags == 0
                && ApplicationInfo.FLAG_UPDATED_SYSTEM_APP and appInfo.applicationInfo.flags == 0
            ) { //非系统应用
                appList.add(appInfo.packageName)
            }
            //Log.d("ram", "ramAppGet:packageName  ${appInfo.packageName}  ")
        }

        appList.forEach {
            val uid = getPackageUid(this, it)
            if (uid > 0) {
                val rstA = isAppRunning(this, it)
                val rstB = isProcessRunning(this, uid)
                if (rstA || rstB) {
                    //指定包名的程序正在运行中
                    //Log.d("ram", "ramAppGet: $it is running ")
                } else {
                    //指定包名的程序未在运行中
                    //Log.d("ram", "ramAppGet: $it no running ")
                }
            } else {
                //应用未安装

            }
        }

    }

    private fun batteryAnim() {
        //粒子的上升动画
        grainAnimation = AnimationUtils.loadAnimation(this, R.anim.battery_grain_translate)
        grainAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                binding.appGrain.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(p0: Animation?) {
                binding.appGrain.visibility = View.GONE
            }

            override fun onAnimationRepeat(p0: Animation?) {}
        })

        //小图标的消失动画
        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim)
        alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                binding.app1.visibility = View.INVISIBLE
                binding.app2.visibility = View.INVISIBLE
                binding.app3.visibility = View.INVISIBLE
                binding.app4.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(p0: Animation?) {}
        })

        //小图标的出现动画
        raiseAnimation = AnimationUtils.loadAnimation(this, R.anim.raise)
        raiseAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                binding.app1.visibility = View.VISIBLE
                binding.app2.visibility = View.VISIBLE
                binding.app3.visibility = View.VISIBLE
                binding.app4.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(p0: Animation?) {}
            override fun onAnimationRepeat(p0: Animation?) {}
        })

        //小图标的颜色变化动画
        val matrix = ColorMatrix()

        imageAnim = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 1000
        }
        imageAnim.addUpdateListener {
            val value = it.animatedValue as Float
            matrix.setSaturation(value)
            val filter = ColorMatrixColorFilter(matrix)
            binding.app1.colorFilter = filter
            binding.app2.colorFilter = filter
            binding.app3.colorFilter = filter
            binding.app4.colorFilter = filter
        }
        imageAnim.doOnStart {
            binding.app1.startAnimation(raiseAnimation)
            binding.app2.startAnimation(raiseAnimation)
            binding.app3.startAnimation(raiseAnimation)
            binding.app4.startAnimation(raiseAnimation)
        }
        imageAnim.doOnEnd {
            binding.app1.startAnimation(alphaAnimation)
            binding.app2.startAnimation(alphaAnimation)
            binding.app3.startAnimation(alphaAnimation)
            binding.app4.startAnimation(alphaAnimation)
        }


        //动画的循环播放
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val msg = Message()
                msg.what = 1
                mHandler.sendMessage(msg)
            }
        }, 0, 2000)
        //binding.appGrain.startAnimation(grainAnimation)

        //5s 后  动画停止
        GlobalScope.launch {
            delay(5000L)
            timer.cancel()
        }


/*        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if(timer!=null)
                timer.cancel()
        },5000)*/
    }


    private fun waterViewAnim() {
/*        binding.waterCleaner.startRotate()
        binding.waterCleaner.startChangeColor()
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            binding.waterCleaner.stopRotate()
        }, 4000)*/
    }

    private fun junkCleanAnim() {
        Handler(Looper.getMainLooper()).postDelayed(Runnable {

            binding.success2.visibility = View.VISIBLE
            binding.success2.startPlay()
            binding.cleanJunk.visibility = View.INVISIBLE
        }, 7000)

        val sizeAnimator = ValueAnimator.ofFloat(0.00f, 60.0f, 0.00f)
        sizeAnimator.duration = 7000
        sizeAnimator.interpolator = DecelerateInterpolator()
        sizeAnimator.addUpdateListener {
            var size = it.animatedValue as Float
            binding.cleanJunk.setTextSize(formatNumber3(size))
        }
        sizeAnimator.start()


        binding.cleanJunk.setTextUnit("KB")
    }

    private fun initListener() {
        binding.button.setOnClickListener {
            val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_anim)
            scaleAnimation.interpolator = DecelerateInterpolator()
            binding.ivTarget.startAnimation(scaleAnimation)
            //popWindow()
            initNotification()
        }

        binding.button2.setOnClickListener {
            val alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim)
            binding.ivTarget.startAnimation(alphaAnimation)
            //findViewById<MyViewWave>(R.id.wave_ball).startMove()
            //binding.waveBall.startMove()
        }

        binding.button3.setOnClickListener {
            val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim)
            binding.ivTarget.startAnimation(rotateAnimation)
        }

        binding.button4.setOnClickListener {
            val translateAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_anim)
            binding.ivTarget.startAnimation(translateAnimation)
        }

        binding.button5.setOnClickListener {
            val rotationHolder = PropertyValuesHolder.ofFloat(
                "Rotation",
                60f,
                -60f,
                40f,
                -40f,
                -20f,
                20f,
                10f,
                -10f,
                0f
            )
            val colorHolder = PropertyValuesHolder.ofInt("BackgroundColor", -0x1, -0xff01, -0x100)
            val animator =
                ObjectAnimator.ofPropertyValuesHolder(binding.ivTarget, rotationHolder, colorHolder)
            animator.duration = 3000
            animator.interpolator = AccelerateInterpolator()
            animator.start()
        }
    }


    fun xieCheng() {

        GlobalScope.launch {
            println(" World")
            delay(1000L)
        }
        println("Hello,") // 协程已在等待时主线程还在继续
        Thread.sleep(2000L) // 阻塞主线程 2 秒钟来保证 JVM 存活
    }


    //只存在三位数字
    private fun formatNumber3(size: Float): String {
        var s = ""
        //<10 算2位小数
        s = if (size < 10) {
            val format = DecimalFormat("#.##")
            //舍弃规则，RoundingMode.FLOOR表示直接舍弃。
            format.roundingMode = RoundingMode.FLOOR
            return format.format(size)
        } else if (size < 100) {
            val format = DecimalFormat("#.#")
            //舍弃规则，RoundingMode.FLOOR表示直接舍弃。
            format.roundingMode = RoundingMode.FLOOR
            return format.format(size)
        } else {
            size.toString()
        }
        return s
    }


    private fun popWindow() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = NotificationCompat.Builder(this, "chat")
            .setContentTitle("收到一条聊天消息")
            .setContentText("今天中午吃什么？")
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_launcher_background
                )
            )
            .setAutoCancel(true)
            .build()
        manager.notify(1, notification)

    }


    private fun initNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channelId = "mirror"
            var channelName = "Beauty Mirror"
            var importance = NotificationManager.IMPORTANCE_HIGH
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    importance
                )
            )
        }

        val remoteViews = RemoteViews(packageName, R.layout.layout_dialog)


        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val build = NotificationCompat.Builder(this, "mirror")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(Notification.PRIORITY_MAX)
            .setTicker("Beauty Mirror")
            .setContent(remoteViews)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_ALL)
            .build()

        notificationManager.notify(1, build)
    }


}


