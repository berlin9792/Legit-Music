/** Metrolist Project (C) 2026 Licensed under GPL-3.0 | See git history for contributors */
package legit.music.ayush.widget

/**
 * AppWidgetProvider for Echo Adaptive Player (2-in-1 widget: compact controls that expands to show upcoming songs queue).
 */
open class LegitAdaptiveWidgetReceiver : MusicWidgetReceiver()

/** Keeps the existing launcher-widget component name stable across app updates. */
class EchoAdaptiveWidgetReceiver : LegitAdaptiveWidgetReceiver()
