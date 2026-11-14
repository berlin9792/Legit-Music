/** Metrolist Project (C) 2026 Licensed under GPL-3.0 | See git history for contributors */
package legit.music.ayush.widget

/**
 * AppWidgetProvider for Design 3: Compact Glow (2x2 squircle widget with centered art and vibrant controls).
 */
open class LegitSquareWidgetReceiver : MusicWidgetReceiver()

/** Keeps the existing launcher-widget component name stable across app updates. */
class EchoSquareWidgetReceiver : LegitSquareWidgetReceiver()
