package com.vadimdorofeev.orthodoxlentendays

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.color.MaterialColors
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        // Установка тёмного режима - важно сделать до создания разметки
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        AppCompatDelegate.setDefaultNightMode(
            when (prefs.getString("nightmode", "system")) {
                "on" -> AppCompatDelegate.MODE_NIGHT_YES
                "off" -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadTitles()
        loadThemes()
        loadSettings()

        toolbar.setNavigationOnClickListener { drawerLayout.open() }
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.previous_year -> {
                    val date = Common.selectedDate.value ?: Common.today
                    if (date.year > 1900)
                        Common.selectedDate.value = date.withYear(date.year - 1)
                    true
                }
                R.id.next_year -> {
                    val date = Common.selectedDate.value ?: Common.today
                    if (date.year < 2099)
                        Common.selectedDate.value = date.withYear(date.year + 1)
                    true
                }
                else -> false
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.section_view_ring ->
                    mode = ViewMode.Ring
                R.id.section_view_rect ->
                    mode = ViewMode.Rect
                R.id.section_view_calendar ->
                    mode = ViewMode.Calendar
                R.id.section_view_schedule ->
                    mode = ViewMode.Schedule
                R.id.section_misc_settings ->
                    settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
                R.id.section_misc_about ->
                    startActivity(Intent(this, AboutActivity::class.java))
            }
            drawerLayout.close()
            false
        }

        toolbar.setOnClickListener {
            val dlg = YearPickerDialog()
            dlg.show(supportFragmentManager, "year_picker_dialog")
        }

        Common.selectedDate.observe(this) {
            val year = (Common.selectedDate.value ?: Common.today).year
            toolbar.title = year.toString()
        }
    }

    private val navigationView by lazy { findViewById<NavigationView>(R.id.navigationView) }
    private val toolbar by lazy { findViewById<MaterialToolbar>(R.id.topToolbar) }
    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawerLayout) }

    private var mode: ViewMode? = null
        set(value) {
            if (value != field) {
                val fragment = when (value) {
                    ViewMode.Ring,
                    ViewMode.Rect,
                    ViewMode.Calendar -> FigureFragment(value)
                    ViewMode.Schedule -> ScheduleFragment()
                    else -> null
                }
                if (fragment != null)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment, fragment)
                        .commit()
                field = value

                PreferenceManager.getDefaultSharedPreferences(this).edit {
                    putString("viewmode", value?.name ?: "Ring")
                }
            }
        }

    private var settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        recreate()
    }

    private fun loadTitles() {
        // Названия постов
        Common.titlesFasting.clear()
        Common.titlesFasting[Fasting.None]      = getString(R.string.fasting_ordinarytime)
        Common.titlesFasting[Fasting.Wednesday] = getString(R.string.fasting_lenten_wednesday)
        Common.titlesFasting[Fasting.Friday]    = getString(R.string.fasting_lenten_friday)
        Common.titlesFasting[Fasting.Daylong]   = getString(R.string.fasting_daylong)
        Common.titlesFasting[Fasting.Great]     = getString(R.string.fasting_greatlent)
        Common.titlesFasting[Fasting.Apostles]  = getString(R.string.fasting_apostles)
        Common.titlesFasting[Fasting.Dormition] = getString(R.string.fasting_dormition)
        Common.titlesFasting[Fasting.Nativity]  = getString(R.string.fasting_nativity)

        // Названия праздников
        Common.titlesHoliday.clear()
        Common.titlesHoliday[Holiday.None]                       = " "
        Common.titlesHoliday[Holiday.NativityOfTheTheotokos]     = getString(R.string.holiday_nativityofthetheotokos)
        Common.titlesHoliday[Holiday.ExaltationOfTheCross]       = getString(R.string.holiday_exaltationofthecross)
        Common.titlesHoliday[Holiday.PresentationOfTheTheotokos] = getString(R.string.holiday_presentationofthetheotokos)
        Common.titlesHoliday[Holiday.ChristmasEve]               = getString(R.string.holiday_christmaseve)
        Common.titlesHoliday[Holiday.NativityOfChrist]           = getString(R.string.holiday_nativityofchrist)
        Common.titlesHoliday[Holiday.TheophanyEve]               = getString(R.string.holiday_theophanyeve)
        Common.titlesHoliday[Holiday.BaptismOfChrist]            = getString(R.string.holiday_baptismofchrist)
        Common.titlesHoliday[Holiday.PresentationOfJesus]        = getString(R.string.holiday_presentationofjesus)
        Common.titlesHoliday[Holiday.Annunciation]               = getString(R.string.holiday_annunciation)
        Common.titlesHoliday[Holiday.EntryIntoJerusalem]         = getString(R.string.holiday_entryintojerusalem)
        Common.titlesHoliday[Holiday.Easter]                     = getString(R.string.holiday_easter)
        Common.titlesHoliday[Holiday.AscensionOfChrist]          = getString(R.string.holiday_ascensionofchrist)
        Common.titlesHoliday[Holiday.Pentecost]                  = getString(R.string.holiday_pentecost)
        Common.titlesHoliday[Holiday.Transfiguration]            = getString(R.string.holiday_transfiguration)
        Common.titlesHoliday[Holiday.DormitionOfTheTheotokos]    = getString(R.string.holiday_dormitionofthetheotokos)
        Common.titlesHoliday[Holiday.BeheadingJohnBaptist]       = getString(R.string.holiday_beheadingjohnbaptist)
        Common.titlesHoliday[Holiday.CircumcisionOfJesus]        = getString(R.string.holiday_circumcisionofjesus)
        Common.titlesHoliday[Holiday.IntercessionOfTheTheotokos] = getString(R.string.holiday_intercessionofthetheotokos)
        Common.titlesHoliday[Holiday.FeastOfSaintsPeterAndPaul]  = getString(R.string.holiday_feastofsaintspeterandpaul)
        Common.titlesHoliday[Holiday.MondayOfTheHolySpirit]      = getString(R.string.holiday_mondayoftheholyspirit)
        Common.titlesHoliday[Holiday.SaturdayOfMeatfareWeek]     = getString(R.string.holiday_saturdayofmeatfareweek)
        Common.titlesHoliday[Holiday.SaturdayBeforePentecost]    = getString(R.string.holiday_saturdaybeforepentecost)
        Common.titlesHoliday[Holiday.Radonitsa]                  = getString(R.string.holiday_radonitsa)

        // Другие нужные текстовые данные
        Common.titlesMonth.addAll(resources.getStringArray(R.array.month_names))
        Common.titlesMonthSingle.addAll(resources.getStringArray(R.array.month_names_single))
        Common.titlesDay.addAll(resources.getStringArray(R.array.day_names))
        Common.dateFormat = getString(R.string.date_format)
    }

    @SuppressLint("DiscouragedApi")
    private fun loadThemes() {
        // Темы, только если не загружены
        if (Common.themes.size == 0) {
            var i = 0
            while (true) {
                val resId = resources.getIdentifier("theme_$i", "array", packageName)
                if (resId != 0) {
                    Common.themes.add(Theme(
                        getString(resources.getIdentifier("theme_$i", "string", packageName)),
                        resources.getIntArray(resId)))
                }
                else
                    break
                i++
            }
        }

        // Другие нужные цвета
        Common.colorBg = MaterialColors.getColor(this, R.attr.bg, 0)
        Common.colorTextFasting = MaterialColors.getColor(this, R.attr.fasting, 0)
        Common.colorTextNonFasting = MaterialColors.getColor(this, R.attr.text, 0)
        Common.colorTodayBg = MaterialColors.getColor(this, R.attr.today_bg, 0)
        Common.colorTodayBorder = MaterialColors.getColor(this, R.attr.today_border, 0)
        Common.colorGlow = MaterialColors.getColor(this, R.attr.shadow, 0)
    }

    private fun loadSettings() {
        // Настройки
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        Common.currentTheme = (prefs.getString("theme", "0") ?: "0").toIntOrNull() ?: 0
        mode = ViewMode.valueOf(prefs.getString("viewmode", ViewMode.Ring.name) ?: ViewMode.Ring.name)
        Common.lightenPast = prefs.getBoolean("lighten_past", true)
        Common.flipRing = prefs.getBoolean("flip_ring", false)
        Common.holidaysMode = when (prefs.getString("show_holidays", "supermain")) {
            "none" -> HolidaysMode.None
            "all" -> HolidaysMode.Great12
            else -> HolidaysMode.SuperMain
        }

        // Тёмный режим определяется по факту наличия, а не берётся из настроек
        Common.nightMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_YES) != 0
    }
}