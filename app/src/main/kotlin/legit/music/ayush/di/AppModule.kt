package legit.music.ayush.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import legit.music.ayush.constants.MaxSongCacheSizeKey
import legit.music.ayush.db.InternalDatabase
import legit.music.ayush.db.MusicDatabase
import legit.music.ayush.db.daos.TasteProfileDao
import legit.music.ayush.generate.GenerationStatus
import legit.music.ayush.generate.GenresRepository
import legit.music.ayush.generate.LocalTasteEngine
import legit.music.ayush.generate.RealGenerationStatus
import legit.music.ayush.generate.RecommendationEngine
import legit.music.ayush.listentogether.ListenTogetherClient
import legit.music.ayush.listentogether.ListenTogetherManager
import legit.music.ayush.utils.dataStore
import legit.music.ayush.utils.get
import legit.music.ayush.utils.lastfm.LastFmTasteApi
import legit.music.ayush.utils.lastfm.RealLastFmTasteApi
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  @Singleton
  @ApplicationScope
  fun provideApplicationScope(): CoroutineScope {
    return CoroutineScope(SupervisorJob() + Dispatchers.Default)
  }

  @Singleton
  @Provides
  fun provideDao(
    database: InternalDatabase,
  ) = database.dao

  @Singleton
  @Provides
  fun provideSongPlayStatsDao(
    database: InternalDatabase,
  ) = database.songPlayStatsDao

  @Singleton
  @Provides
  fun provideRecommendationExclusionDao(
    database: InternalDatabase,
  ) = database.recommendationExclusionDao

  @Singleton
  @Provides
  fun provideTasteProfileDao(
    database: InternalDatabase,
  ) = database.tasteProfileDao

  @Singleton
  @Provides
  fun provideLastFmTasteApi(
    api: RealLastFmTasteApi,
  ): LastFmTasteApi = api

  @Singleton
  @Provides
  fun providePreferencesDataStore(
    @ApplicationContext context: Context,
  ): DataStore<Preferences> = context.dataStore

  @Singleton
  @Provides
  fun provideGenerationStatus(
    status: RealGenerationStatus,
  ): GenerationStatus = status

  @Singleton
  @Provides
  fun provideLocalTasteEngine(
    recommendationEngine: RecommendationEngine,
  ): LocalTasteEngine = recommendationEngine

  @Singleton
  @Provides
  fun provideDatabase(
    internalDatabase: InternalDatabase,
  ): MusicDatabase = MusicDatabase(internalDatabase)

  @Singleton
  @Provides
  fun provideInternalDatabase(
    @ApplicationContext context: Context,
  ): InternalDatabase =
    Room.databaseBuilder(context, InternalDatabase::class.java, InternalDatabase.DB_NAME)
      .addMigrations(
        legit.music.ayush.db.MIGRATION_1_2,
        legit.music.ayush.db.MIGRATION_21_24,
        legit.music.ayush.db.MIGRATION_22_24,
        legit.music.ayush.db.MIGRATION_24_25,
        legit.music.ayush.db.MIGRATION_27_28,
        legit.music.ayush.db.MIGRATION_28_29,
        legit.music.ayush.db.MIGRATION_29_30,
        legit.music.ayush.db.MIGRATION_31_32,
        legit.music.ayush.db.MIGRATION_36_37,
        legit.music.ayush.db.MIGRATION_37_38,
        legit.music.ayush.db.MIGRATION_38_39,
        legit.music.ayush.db.MIGRATION_39_40,
        legit.music.ayush.db.MIGRATION_40_41,
        legit.music.ayush.db.MIGRATION_41_42,
        legit.music.ayush.db.MIGRATION_42_43,
        legit.music.ayush.db.MIGRATION_43_44,
        legit.music.ayush.db.MIGRATION_44_45,
        legit.music.ayush.db.MIGRATION_45_46,
      )
      .setJournalMode(androidx.room.RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
      .setTransactionExecutor(java.util.concurrent.Executors.newFixedThreadPool(4))
      .setQueryExecutor(java.util.concurrent.Executors.newFixedThreadPool(4))
      .addCallback(
        object : androidx.room.RoomDatabase.Callback() {
          override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            super.onOpen(db)
            try {
              db.query("PRAGMA busy_timeout = 60000").close()
              db.query("PRAGMA cache_size = -16000").close()
              db.query("PRAGMA wal_autocheckpoint = 1000").close()
              db.query("PRAGMA synchronous = NORMAL").close()
              db.execSQL("DELETE FROM format WHERE (codecs = 'alac' OR bitrate = 1411200) AND id NOT LIKE 'local_%'")
            } catch (e: Exception) {
              timber.log.Timber.tag("MusicDatabase").e(e, "Failed to set PRAGMA settings")
            }
          }
        }
      )
      .build()

  @Singleton
  @Provides
  fun provideDatabaseProvider(
    @ApplicationContext context: Context,
  ): DatabaseProvider = StandaloneDatabaseProvider(context)

  @Singleton
  @Provides
  @PlayerCache
  fun providePlayerCache(
    @ApplicationContext context: Context,
    databaseProvider: DatabaseProvider,
  ): SimpleCache {
    val cacheSize = context.dataStore[MaxSongCacheSizeKey] ?: 1024
    return SimpleCache(
      context.filesDir.resolve("exoplayer"),
      when (cacheSize) {
        -1 -> NoOpCacheEvictor()
        else -> legit.music.ayush.playback.DynamicLruCacheEvictor(cacheSize * 1024 * 1024L)
      },
      databaseProvider,
    )
  }

  @Singleton
  @Provides
  @DownloadCache
  fun provideDownloadCache(
    @ApplicationContext context: Context,
    databaseProvider: DatabaseProvider,
  ): SimpleCache {
    return SimpleCache(context.filesDir.resolve("download"), NoOpCacheEvictor(), databaseProvider)
  }

  @Singleton
  @Provides
  fun provideListenTogetherClient(
    @ApplicationContext context: Context,
  ): ListenTogetherClient = ListenTogetherClient(context)

  @Singleton
  @Provides
  fun provideListenTogetherManager(
    @ApplicationContext context: Context,
    client: ListenTogetherClient,
  ): ListenTogetherManager = ListenTogetherManager(client, context)
}
