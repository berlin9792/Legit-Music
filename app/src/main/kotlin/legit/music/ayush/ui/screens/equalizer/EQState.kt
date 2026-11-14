package legit.music.ayush.ui.screens.equalizer

import legit.music.ayush.dsp.core.models.SavedEQProfile

data class EQState(
  val profiles: List<SavedEQProfile> = emptyList(),
  val activeProfileId: String? = null,
  val importStatus: String? = null,
  val error: String? = null
)
