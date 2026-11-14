/** Metrolist Project (C) 2026 Licensed under GPL-3.0 | See git history for contributors */
package legit.music.ayush.widget

/**
 * AppWidgetProvider for Design 5: Slim Pill Strip (4x1 compact strip with live progress and playback controls).
 */
open class LegitSlimWidgetReceiver : MusicWidgetReceiver()

/** Keeps the existing launcher-widget component name stable across app updates. */
class EchoSlimWidgetReceiver : LegitSlimWidgetReceiver()
