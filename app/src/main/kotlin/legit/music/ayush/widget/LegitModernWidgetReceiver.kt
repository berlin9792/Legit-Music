/** Metrolist Project (C) 2026 Licensed under GPL-3.0 | See git history for contributors */
package legit.music.ayush.widget

/**
 * AppWidgetProvider for Design 1: Modern Flow (4x2 card with progress bar, favorites, and library).
 */
open class LegitModernWidgetReceiver : MusicWidgetReceiver()

/** Keeps the existing launcher-widget component name stable across app updates. */
class EchoModernWidgetReceiver : LegitModernWidgetReceiver()
