/** Metrolist Project (C) 2026 Licensed under GPL-3.0 | See git history for contributors */
package legit.music.ayush.widget

/**
 * AppWidgetProvider for Design 4: Showcase Card (4x4 hero widget with logo header, full banner artwork, and controls).
 */
open class LegitShowcaseWidgetReceiver : MusicWidgetReceiver()

/** Keeps the existing launcher-widget component name stable across app updates. */
class EchoShowcaseWidgetReceiver : LegitShowcaseWidgetReceiver()
