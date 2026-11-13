package echo.music.iad1tya.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

enum class AppIconType {
  DEFAULT,
  LEGIT2,
  LEGIT3,
  LEGIT4,
  LEGIT5
}

object IconUtils {
  fun setIcon(context: Context, iconType: AppIconType) {
    val pm = context.packageManager

    val selectedAlias =
      when (iconType) {
        AppIconType.DEFAULT -> "MainActivityAlias"
        AppIconType.LEGIT2 -> "MainActivityLegit2"
        AppIconType.LEGIT3 -> "MainActivityLegit3"
        AppIconType.LEGIT4 -> "MainActivityLegit4"
        AppIconType.LEGIT5 -> "MainActivityLegit5"
      }
    val aliases =
      listOf(
        "MainActivityAlias",
        "MainActivityLegit2",
        "MainActivityLegit3",
        "MainActivityLegit4",
        "MainActivityLegit5",
        "MainActivityLegacy",
        "MainActivityStatic",
        "MainActivityCat",
        "MainActivityCrazyBlue",
        "MainActivityPookie",
        "MainActivitySky",
        "MainActivityEchoCat",
        "MainActivityBillieEilish",
        "MainActivitySabrina",
        "MainActivitySabrina2",
        "MainActivityCraft",
        "MainActivitySadie"
      )

    aliases.forEach { alias ->
      pm.setComponentEnabledSetting(
        ComponentName(context, "echo.music.iad1tya.$alias"),
        if (alias == selectedAlias) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
      )
    }
  }
}
