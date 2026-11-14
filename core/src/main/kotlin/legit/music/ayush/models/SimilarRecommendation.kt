package legit.music.ayush.models

import com.music.innertube.models.YTItem
import legit.music.ayush.db.entities.LocalItem

data class SimilarRecommendation(
  val title: LocalItem,
  val items: List<YTItem>,
)
