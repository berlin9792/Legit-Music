package legit.music.ayush.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import legit.music.ayush.core.repository.PlaylistRepositoryImpl
import legit.music.ayush.core.repository.SongRepositoryImpl
import legit.music.ayush.domain.repositories.PlaylistRepository
import legit.music.ayush.domain.repositories.SongRepository
import legit.music.ayush.musicflow.MusicFlowFeedSource
import legit.music.ayush.musicflow.MusicFlowRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainBindingsModule {

  @Binds
  @Singleton
  abstract fun bindSongRepository(
    impl: SongRepositoryImpl
  ): SongRepository

  @Binds
  @Singleton
  abstract fun bindPlaylistRepository(
    impl: PlaylistRepositoryImpl
  ): PlaylistRepository

  @Binds
  @Singleton
  abstract fun bindMusicFlowFeedSource(
    impl: MusicFlowRepository
  ): MusicFlowFeedSource
}
