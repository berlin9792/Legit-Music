/** Metrolist Project (C) 2026 Licensed under GPL-3.0 | See git history for contributors */
package legit.music.ayush.widget

/**
 * AppWidgetProvider for Design 6: Square Vinyl Turntable (Square frosted glass turntable with rotating disc).
 */
open class LegitVinylWidgetReceiver : MusicWidgetReceiver()

/** Keeps the existing launcher-widget component name stable across app updates. */
class EchoVinylWidgetReceiver : LegitVinylWidgetReceiver()
