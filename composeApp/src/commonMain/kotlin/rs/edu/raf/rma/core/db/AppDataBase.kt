package rs.edu.raf.rma.core.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import rs.edu.raf.rma.core.db.converters.DateConverters
import rs.edu.raf.rma.movies.db.CastEntity
import rs.edu.raf.rma.movies.db.GenreEntity
import rs.edu.raf.rma.movies.db.MovieDao
import rs.edu.raf.rma.movies.db.MovieDetailsEntity
import rs.edu.raf.rma.movies.db.MovieEntity
import rs.edu.raf.rma.movies.db.MovieGenreCrossRef
import rs.edu.raf.rma.movies.db.MovieImageEntity
import rs.edu.raf.rma.movies.db.FavoriteEntity
import rs.edu.raf.rma.movies.db.WatchlistEntity

@Database(
    entities = [
        MovieEntity::class,
        MovieDetailsEntity::class,
        GenreEntity::class,
        MovieGenreCrossRef::class,
        CastEntity::class,
        MovieImageEntity::class,
        FavoriteEntity::class,
        WatchlistEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(DateConverters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun buildAppDatabase(
    builder: RoomDatabase.Builder<AppDatabase>,
): AppDatabase {
    return builder
        .fallbackToDestructiveMigration(dropAllTables = true)
        .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
