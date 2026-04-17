# RMA-06-Kotlin — Projektna dokumentacija

## Sadržaj

1. [Pregled projekta](#1-pregled-projekta)
2. [Tehnološki stack](#2-tehnološki-stack)
3. [Struktura direktorijuma](#3-struktura-direktorijuma)
4. [Arhitektura](#4-arhitektura)
5. [Domain sloj](#5-domain-sloj)
6. [Data sloj — Remote](#6-data-sloj--remote)
7. [Response DTO klase](#7-response-dto-klase)
8. [Repository sloj](#8-repository-sloj)
9. [ViewModel sloj](#9-viewmodel-sloj)
10. [UI sloj — Screens](#10-ui-sloj--screens)
11. [UI sloj — Komponente](#11-ui-sloj--komponente)
12. [Dependency Injection](#12-dependency-injection)
13. [Platform-specifične implementacije](#13-platform-specifične-implementacije)
14. [Android aplikacija](#14-android-aplikacija)
15. [Desktop aplikacija](#15-desktop-aplikacija)
16. [Navigacija](#16-navigacija)
17. [API integracija](#17-api-integracija)
18. [Tok podataka](#18-tok-podataka)
19. [Build konfiguracija](#19-build-konfiguracija)

---

## 1. Pregled projekta

**rma-06-kotlin** je Kotlin Multiplatform (KMP) aplikacija za pregled i pretragu filmova. Projekat koristi **Compose Multiplatform** za deljeni UI koji se kompajlira i za Android i za iOS i za JVM/Desktop.

Aplikacija omogućava:
- Prikaz liste filmova sa paginacijom
- Filtriranje filmova po žanru, godini i oceni
- Sortiranje filmova po oceni, godini i popularnosti
- Pretragu filmova po naslovu
- Detaljan prikaz filma sa opisom, kastom i slikama

---

## 2. Tehnološki stack

| Sloj | Tehnologija | Verzija |
|------|-------------|---------|
| UI Framework | Compose Multiplatform | 1.10.3 |
| Programski jezik | Kotlin | 2.3.20 |
| HTTP klijent | Ktor Client | 3.4.0 |
| Type-safe API | Ktorfit | 2.7.1 |
| Serijalizacija | Kotlinx Serialization | 1.10.0 |
| Dependency Injection | Koin | 4.1.1 |
| State Management | StateFlow + ViewModel | — |
| Lokalna baza | Room + SQLite Bundled | 2.8.4 |
| Učitavanje slika | Coil | 3.1.0 |
| Logovanje | Napier | — |
| Code Generation | KSP | 2.3.4 |
| HTTP engine (Android) | OkHttp | — |
| HTTP engine (iOS) | Darwin | — |
| HTTP engine (JVM) | CIO | — |

---

## 3. Struktura direktorijuma

```
rma-06-kotlin/
├── androidApp/                          # Android-specifični modul
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── kotlin/rs/edu/raf/rma/android/
│           ├── MainActivity.kt          # Entry point Android aplikacije
│           └── MovieApplication.kt     # Application klasa, inicijalizuje Koin
│
├── composeApp/                          # Deljeni Compose Multiplatform modul
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/rs/edu/raf/rma/
│       │   ├── Platform.kt              # expect interfejs za platformu
│       │   ├── di/                      # Dependency Injection
│       │   │   ├── AppModule.kt
│       │   │   ├── KoinInitializer.kt
│       │   │   └── NetworkModule.kt
│       │   └── movies/
│       │       ├── coponents/           # Reusable Compose komponente [typo u nazivu paketa]
│       │       │   ├── CastItem.kt
│       │       │   ├── GenreTag.kt
│       │       │   ├── InfoBadge.kt
│       │       │   └── MovieListItem.kt
│       │       ├── data/
│       │       │   ├── Pom.kt           # Placeholder klasa
│       │       │   └── remote/
│       │       │       ├── HttpClientFactory.kt   # expect fun
│       │       │       ├── MovieApiService.kt     # Ktorfit interfejs
│       │       │       └── NetworkConstants.kt
│       │       ├── domain/              # Domain modeli
│       │       │   ├── Cast.kt
│       │       │   ├── Genre.kt
│       │       │   ├── Movie.kt
│       │       │   ├── MovieCollection.kt
│       │       │   ├── MovieDetail.kt
│       │       │   ├── MovieFilterParams.kt
│       │       │   ├── MovieImage.kt
│       │       │   ├── MovieImages.kt
│       │       │   └── SortOption.kt
│       │       ├── navigator/           # Navigacioni destinacije (delimično implementirano)
│       │       ├── repository/
│       │       │   ├── MovieRepository.kt        # Interfejs
│       │       │   └── impl/
│       │       │       └── MovieRepositoryImpl.kt
│       │       ├── response/            # API response DTO klase
│       │       │   ├── CastResponse.kt
│       │       │   ├── GenresResponse.kt
│       │       │   └── MovieResponseDto.kt
│       │       ├── screen/              # Ekrani aplikacije
│       │       │   ├── FilterScreen.kt
│       │       │   ├── MainScreen.kt
│       │       │   ├── MovieScreen.kt
│       │       │   └── MoviesAppRoot.kt
│       │       └── viewmodel/
│       │           ├── MovieDetailsViewModel.kt
│       │           ├── MovieIntent.kt
│       │           ├── MoviesEffect.kt (ili unutar Intent fajla)
│       │           └── MoviesViewModel.kt
│       │
│       ├── androidMain/kotlin/rs/edu/raf/rma/
│       │   ├── Platform.android.kt
│       │   └── movies/data/remote/
│       │       └── HttpClientFactory.android.kt
│       │
│       ├── iosMain/kotlin/rs/edu/raf/rma/
│       │   ├── Platform.ios.kt
│       │   └── MainViewController.kt
│       │
│       └── jvmMain/kotlin/rs/edu/raf/rma/
│           └── Platform.jvm.kt
│
├── desktopApp/                          # Desktop (JVM) modul
│   └── src/main/kotlin/rs/edu/raf/rma/
│       └── Main.kt
│
├── build.gradle.kts                     # Root Gradle konfiguracija
├── settings.gradle.kts                  # Gradle settings
└── gradle/
    └── libs.versions.toml               # Verzije svih zavisnosti
```

---

## 4. Arhitektura

Projekat prati **Clean Architecture** sa jasnom podelom na slojeve:

```
┌─────────────────────────────────────────────┐
│               UI Sloj (Screens)             │
│  MoviesAppRoot → MainScreen / FilterScreen  │
│                  / MovieScreen              │
└──────────────────────┬──────────────────────┘
                       │ collectAsState / onIntent
┌──────────────────────▼──────────────────────┐
│            ViewModel Sloj (MVI)             │
│    MoviesViewModel / MovieDetailsViewModel   │
│    State (StateFlow) + Effects (Channel)    │
└──────────────────────┬──────────────────────┘
                       │ suspend fun
┌──────────────────────▼──────────────────────┐
│           Repository Sloj                   │
│    MovieRepository (interfejs)              │
│    MovieRepositoryImpl (implementacija)     │
└──────────────────────┬──────────────────────┘
                       │
┌──────────────────────▼──────────────────────┐
│           Data Sloj — Remote                │
│    MovieApiService (Ktorfit interfejs)      │
│    HttpClientFactory (expect/actual)        │
└─────────────────────────────────────────────┘
```

### Arhitekturni obrasci

- **MVI (Model-View-Intent):** UI šalje `Intent` ViewModelu, ViewModel ažurira `State` (koji UI konzumira) i emituje `Effect`-e za side-effecte kao što je navigacija.
- **Repository pattern:** Sav pristup podacima prolazi kroz `MovieRepository` interfejs. UI nikad direktno ne poziva API.
- **Expect/Actual:** Kotlin Multiplatform mehanizam za platform-specifične implementacije (npr. HTTP engine).
- **Koin DI:** Constructor injection za sve zavisnosti.

---

## 5. Domain sloj

Paket: `rs.edu.raf.rma.movies.domain`

Sadrži čiste domain modele koji ne zavise ni od jednog frameworka (osim `@Serializable` anotacije).

---

### `Cast.kt`

Predstavlja jednog člana kasta filma.

```kotlin
@Serializable
data class Cast(
    val imdbId: String,       // IMDb ID osobe
    val name: String,         // Ime i prezime
    val professions: String?,  // Zanimanja (npr. "actor,producer")
    val department: String?,   // Odeljenje (npr. "Acting")
    val profilePath: String?   // Putanja do profilne slike na TMDB
)
```

---

### `Genre.kt`

Predstavlja filmski žanr.

```kotlin
@Serializable
data class Genre(
    val id: Int,     // Numerički ID žanra (TMDB standard, npr. 28 = Action)
    val name: String // Naziv žanra (npr. "Action", "Drama")
)
```

---

### `Movie.kt`

Sažeti prikaz filma koji se koristi u listi filmova.

```kotlin
@Serializable
data class Movie(
    val imdbId: String,       // IMDb identifikator (npr. "tt0111161")
    val title: String,        // Naslov filma
    val year: String,         // Godina izlaska
    val imdbRating: Float,    // IMDb ocena (0.0 - 10.0)
    val imdbVotes: Int,        // Broj glasova na IMDb
    val posterPath: String,   // Putanja do postera na TMDB
    val genres: List<Genre>   // Lista žanrova filma
)
```

---

### `MovieCollection.kt`

Predstavlja kolekciju kojoj film pripada (npr. "The Dark Knight Collection").

```kotlin
@Serializable
data class MovieCollection(
    val id: Int,
    val name: String,
    val posterPath: String?,    // Može biti null
    val backdropPath: String?   // Može biti null
)
```

---

### `MovieDetail.kt`

Detaljan prikaz filma sa svim dostupnim informacijama. Koristi se na ekranu detalja.

```kotlin
@Serializable
data class MovieDetail(
    val imdbId: String,
    val tmdbId: Int?,            // TMDB identifikator
    val title: String,
    val originalTitle: String?,  // Originalni naslov (ako nije engleski)
    val overview: String?,       // Sinopsis filma
    val tagline: String?,        // Slogan filma
    val releaseDate: String?,    // Datum izlaska (ISO format)
    val year: Int?,              // Godina izlaska
    val runtime: Int?,           // Trajanje u minutima
    val budget: Long?,           // Budžet u dolarima
    val revenue: Long?,          // Zarada u dolarima
    val languageCode: String?,   // ISO kod originalnog jezika (npr. "en")
    val popularity: Float?,      // TMDB popularity score
    val imdbRating: Float?,      // IMDb ocena
    val imdbVotes: Int?,         // Broj IMDb glasova
    val tmdbRating: Float?,      // TMDB ocena
    val tmdbVotes: Int?,         // Broj TMDB glasova
    val posterPath: String?,     // TMDB putanja do postera
    val backdropPath: String?,   // TMDB putanja do backdrop slike
    val homepage: String?,       // Zvanični sajt filma
    val genres: List<Genre>,     // Lista žanrova
    val collection: MovieCollection? = null  // Kolekcija kojoj pripada (opciono)
)
```

---

### `MovieFilterParams.kt`

Data klasa za parametre filtriranja. Napomena: ova klasa trenutno nije direktno u upotrebi — filteri se prenose kroz `MovieFilterUiState` iz `FilterScreen.kt`.

```kotlin
data class MovieFilterParams(
    val query: String? = null,
    val genres: List<String> = emptyList(),
    val fromYear: Int? = null,
    val toYear: Int? = null,
    val minRating: Float? = null
)
```

---

### `MovieImage.kt`

Jedna slika filma (poster, backdrop ili logo).

```kotlin
@Serializable
data class MovieImage(
    val filePath: String,       // TMDB putanja do slike
    val width: Int? = null,
    val height: Int? = null,
    val voteAverage: Float? = null,  // Prosečna ocena slike
    val language: String? = null     // ISO kod jezika (za lokalizovane slike)
)
```

---

### `MovieImages.kt`

Kontejner za sve slike jednog filma.

```kotlin
@Serializable
data class MovieImages(
    val posters: List<MovieImage>,    // Posteri
    val backdrops: List<MovieImage>,  // Backdrop slike
    val logos: List<MovieImage>       // Logotipi
)
```

---

### `SortOption.kt`

Enum koji definiše opcije sortiranja filmova. Svaka opcija ima `apiValue` koji se direktno šalje API-ju kao `sort_by` parametar.

```kotlin
enum class SortOption(val apiValue: String, val label: String) {
    RATING("imdb_rating", "Rating"),       // Sortiraj po IMDb oceni (default)
    YEAR("year", "Year"),                  // Sortiraj po godini izlaska
    POPULARITY("popularity", "Popularity") // Sortiraj po TMDB popularnosti
}
```

---

## 6. Data sloj — Remote

Paket: `rs.edu.raf.rma.movies.data.remote`

---

### `NetworkConstants.kt`

Sadrži konstantu sa baznom URL adresom API-ja.

```kotlin
object NetworkConstants {
    const val BASE_URL = "https://rma.finlab.rs/"
}
```

---

### `HttpClientFactory.kt`

`expect` funkcija — deklaracija koja zahteva platformsku implementaciju.

```kotlin
expect fun createHttpClient(): HttpClient
```

**Android implementacija** (`androidMain/.../HttpClientFactory.android.kt`):

```kotlin
actual fun createHttpClient(): HttpClient {
    return HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true  // Ignoriše nepoznata polja iz API odgovora
                isLenient = true          // Blagi parser, tolerantan na manje devijacije
            })
        }
    }
}
```

---

### `MovieApiService.kt`

Ktorfit interfejs koji definiše sve API endpoint-e. Ktorfit generiše implementaciju ovog interfejsa koristeći KSP.

```kotlin
interface MovieApiService {

    // Dohvata paginiranu listu filmova sa opcionalnim filterima i sortiranjem
    @GET("movies")
    suspend fun getMovies(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("query") query: String? = null,        // Pretraga po naslovu
        @Query("genre_id") genreId: Int? = null,      // Filter po žanru (ID)
        @Query("min_year") minYear: Int? = null,      // Minimalna godina
        @Query("max_year") maxYear: Int? = null,      // Maksimalna godina
        @Query("min_rating") minRating: Float? = null,// Minimalna IMDb ocena
        @Query("sort_by") sortBy: String? = null      // Polje sortiranja (imdb_rating, year, popularity...)
    ): MoviesResponse

    // Dohvata detalje jednog filma po IMDb ID-u
    @GET("movies/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: String
    ): MovieDetail

    // Dohvata listu svih žanrova (plain JSON array, nije paginiran)
    @GET("genres")
    suspend fun getGenres(): List<Genre>

    // Dohvata paginiran kast filma
    @GET("movies/{id}/cast")
    suspend fun getMovieCast(
        @Path("id") id: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): CastResponse

    // Dohvata slike filma (posteri, backdropi, logotipi)
    @GET("movies/{id}/images")
    suspend fun getMovieImages(
        @Path("id") id: String
    ): MovieImages
}
```

---

## 7. Response DTO klase

Paket: `rs.edu.raf.rma.movies.response`

DTO (Data Transfer Object) klase koje odgovaraju JSON strukturi API odgovora.

---

### `MovieResponseDto.kt` — `MoviesResponse`

Paginiran odgovor za listu filmova.

```kotlin
@Serializable
data class MoviesResponse(
    val page: Int,           // Trenutna stranica
    val pageSize: Int,       // Broj filmova po stranici
    val totalItems: Int,     // Ukupan broj filmova
    val totalPages: Int,     // Ukupan broj stranica
    val items: List<Movie>   // Filmovi na trenutnoj stranici
)
```

---

### `CastResponse.kt`

Paginiran odgovor za kast filma.

```kotlin
@Serializable
data class CastResponse(
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
    val items: List<Cast>   // Članovi kasta na trenutnoj stranici
)
```

---

### `GenresResponse.kt`

> **Napomena:** Ova klasa postoji u projektu ali se **ne koristi direktno** jer API `/genres` endpoint vraća plain JSON array (`[{...}]`), a ne objekat sa `items` poljem. `MovieApiService.getGenres()` stoga vraća `List<Genre>` direktno.

```kotlin
@Serializable
data class GenresResponse(
    val items: List<Genre>
)
```

---

## 8. Repository sloj

Paket: `rs.edu.raf.rma.movies.repository`

---

### `MovieRepository.kt` — interfejs

Definiše sve operacije koje aplikacija može da izvrši nad podacima o filmovima. ViewModeli komuniciraju isključivo kroz ovaj interfejs.

```kotlin
interface MovieRepository {

    // Dohvata prvih 20 filmova, sortiranih po zadatom polju
    suspend fun getMovies(sortBy: String? = null): List<Movie>

    // Dohvata filmove primenjujući filtere i sortiranje
    suspend fun getFilteredMovies(
        query: String? = null,
        genreId: Int? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null,
        sortBy: String? = null
    ): List<Movie>

    // Dohvata detalje jednog filma
    suspend fun getMovieDetails(id: String): MovieDetail

    // Dohvata kast filma
    suspend fun getMovieCast(id: String): List<Cast>

    // Dohvata slike filma
    suspend fun getMovieImages(id: String): MovieImages

    // Dohvata listu svih žanrova
    suspend fun getGenres(): List<Genre>
}
```

---

### `MovieRepositoryImpl.kt` — implementacija

Konkretna implementacija koja koristi `MovieApiService`. Registrovana u Koin DI kao `single<MovieRepository>`.

```kotlin
class MovieRepositoryImpl(
    private val api: MovieApiService
) : MovieRepository {

    override suspend fun getMovies(sortBy: String?): List<Movie> {
        return api.getMovies(sortBy = sortBy).items
    }

    override suspend fun getFilteredMovies(
        query: String?, genreId: Int?, minYear: Int?,
        maxYear: Int?, minRating: Float?, sortBy: String?
    ): List<Movie> {
        return api.getMovies(
            query = query, genreId = genreId,
            minYear = minYear, maxYear = maxYear,
            minRating = minRating, sortBy = sortBy
        ).items
    }

    override suspend fun getGenres(): List<Genre> = api.getGenres()

    override suspend fun getMovieDetails(id: String): MovieDetail =
        api.getMovieDetails(id)

    override suspend fun getMovieCast(id: String): List<Cast> =
        api.getMovieCast(id).items

    override suspend fun getMovieImages(id: String): MovieImages =
        api.getMovieImages(id)
}
```

---

## 9. ViewModel sloj

Paket: `rs.edu.raf.rma.movies.viewmodel`

---

### `MovieIntent.kt`

Sealed interfejs koji definiše sve akcije koje korisnik može da pokrene na ekranu liste filmova.

```kotlin
sealed interface MoviesIntent {
    data object LoadMovies : MoviesIntent                          // Učitaj filmove bez filtera
    data class OnMovieClicked(val imdbId: String) : MoviesIntent  // Klik na film → navigacija
    data class ChangeSortBy(val sortOption: SortOption) : MoviesIntent  // Promena sortiranja
}
```

---

### `MoviesEffect.kt`

Definisani su side-effecti — događaji koji se dešavaju jednom i ne čuvaju se u state-u (najčešće navigacija).

```kotlin
sealed interface MoviesEffect {
    data class NavigateToDetails(val imdbId: String) : MoviesEffect
}
```

---

### `MoviesViewModel.kt`

Glavni ViewModel za ekran liste filmova. Implementira MVI obrazac.

#### `MovieState`

```kotlin
data class MovieState(
    val loading: Boolean = false,           // Da li se trenutno učitava
    val movies: List<Movie> = emptyList(),  // Lista filmova za prikaz
    val error: String? = null,              // Poruka greške (null ako nema greške)
    val genres: List<Genre> = emptyList(),  // Lista žanrova za FilterScreen
    val sortBy: SortOption = SortOption.RATING  // Trenutno aktivno sortiranje
)
```

#### Privatne promenljive

| Promenljiva | Tip | Opis |
|---|---|---|
| `_state` | `MutableStateFlow<MovieState>` | Interni mutable state |
| `state` | `StateFlow<MovieState>` | Javni immutable state koji UI konzumira |
| `_effect` | `Channel<MoviesEffect>` | Kanal za side-effecte |
| `effect` | `Flow<MoviesEffect>` | Javni flow od efekata |
| `genresMap` | `Map<String, Int>` | Mapa žanr naziv → ID, za lookup pri filtiranju |
| `currentFilters` | `MovieFilterUiState?` | Trenutno aktivni filteri (null = bez filtera) |

#### Metode

**`init`**
Poziva `loadGenres()` pri kreiranju ViewModela.

**`onIntent(intent: MoviesIntent)`**
Dispatch metoda — prosleđuje intent odgovarajućem handleru:
- `LoadMovies` → `loadMovies()`
- `OnMovieClicked` → `navigateToDetails(imdbId)`
- `ChangeSortBy` → `changeSortBy(sortOption)`

**`loadGenres()`** *(private)*
Asinhrono dohvata listu žanrova sa API-ja. Popunjava `genresMap` (za pretvaranje naziva žanra u ID) i ažurira `state.genres` (za prikazivanje u FilterScreen-u).

**`loadMovies()`**
Resetuje `currentFilters` na null, pa dohvata filmove sortirane po trenutnom `state.sortBy`. Koristi `.copy()` da sačuva `genres` i `sortBy` iz state-a.

**`applyFilters(filters: MovieFilterUiState)`**
Čuva `filters` u `currentFilters`, pa dohvata filtrirane filmove. Pretvara naziv žanra u ID koristeći `genresMap`. Koristi trenutni `state.sortBy` za sortiranje.

**`changeSortBy(sortOption: SortOption)`** *(private)*
Ažurira `state.sortBy`, zatim ponovo dohvata filmove:
- Ako postoje aktivni filteri (`currentFilters != null`) → ponovo primenjuje filtere sa novim sort-om
- Inače → poziva `loadMovies()`

**`navigateToDetails(imdbId: String)`** *(private)*
Šalje `MoviesEffect.NavigateToDetails` kroz `_effect` kanal.

---

### `MovieDetailsViewModel.kt`

ViewModel za ekran detalja filma.

#### `MovieDetailsState`

```kotlin
data class MovieDetailsState(
    val loading: Boolean = false,
    val movie: MovieDetail? = null,        // Detalji filma
    val cast: List<Cast> = emptyList(),    // Kast
    val images: List<MovieImage> = emptyList(),  // Backdrop slike za galeriju
    val error: String? = null
)
```

#### Metode

**`loadMovie(imdbId: String)`**
Paralelno dohvata detalje filma, kast i slike koristeći `async/await`. Ažurira state jednom kad svi pozivi završe.

---

## 10. UI sloj — Screens

Paket: `rs.edu.raf.rma.movies.screen`

---

### `MoviesAppRoot.kt`

Koreni Composable koji upravlja navigacijom kroz state-based pristup (ne koristi NavHost).

**State promenljive:**
| Promenljiva | Tip | Opis |
|---|---|---|
| `viewModel` | `MoviesViewModel` | Deljeni ViewModel (koin scope = Activity) |
| `state` | `MovieState` | Kolektovani state iz ViewModela |
| `showFilter` | `Boolean` | Da li je FilterScreen prikazan |
| `selectedMovieId` | `String?` | ID filma čiji detalji se prikazuju (null = nije prikazano) |
| `filters` | `MovieFilterUiState` | Trenutno aktivni filteri |

**Logika navigacije:**
```
selectedMovieId != null  →  prikazuje MovieScreen
showFilter == true       →  prikazuje FilterScreen
else                     →  prikazuje MainScreen
```

---

### `MainScreen.kt`

Ekran sa listom filmova.

**Parametri:**
| Parametar | Tip | Opis |
|---|---|---|
| `onMovieClick` | `(String) -> Unit` | Callback kad se klikne na film (prosleđuje imdbId) |
| `onFilterClick` | `() -> Unit` | Callback za otvaranje FilterScreen-a |
| `activeFilters` | `MovieFilterUiState` | Aktivni filteri iz MoviesAppRoot-a |

**`LaunchedEffect(activeFilters)`**
Svaki put kad se `activeFilters` promeni, proverava da li postoje aktivni filteri:
- Ako da → poziva `viewModel.applyFilters(activeFilters)`
- Ako ne → poziva `viewModel.loadMovies()`

**`TopSection` Composable**

Prikazuje gornji deo ekrana:
- Naslov aplikacije ("Premiere")
- Filter dugme (crveno, otvara FilterScreen)
- Sort dropdown (`DropdownMenu`) sa opcijama Rating / Year / Popularity
- Broj filmova

**Parametri TopSection:**
| Parametar | Tip | Opis |
|---|---|---|
| `movieCount` | `Int` | Broj filmova u listi |
| `sortBy` | `SortOption` | Trenutno aktivno sortiranje |
| `onFilterClick` | `() -> Unit` | Callback za filter |
| `onSortChange` | `(SortOption) -> Unit` | Callback za promenu sorta — šalje `ChangeSortBy` intent |

---

### `FilterScreen.kt`

Ekran za filtriranje filmova.

**`MovieFilterUiState`**

Data klasa koja predstavlja UI stanje filter ekrana:

```kotlin
data class MovieFilterUiState(
    val searchQuery: String = "",        // Pretraga po naslovu
    val selectedGenre: String? = null,   // Naziv izabranog žanra (null = bez filtera)
    val fromYear: String = "1923",       // Minimalna godina (kao String za TextField)
    val toYear: String = "2025",         // Maksimalna godina
    val minRating: Float = 10f           // Minimalna IMDb ocena (0–10, default = bez filtera)
)
```

**Parametri FilterScreen:**
| Parametar | Tip | Opis |
|---|---|---|
| `filters` | `MovieFilterUiState` | Trenutni state filtera |
| `genres` | `List<Genre>` | Lista žanrova dobijena sa API-ja |
| `onFiltersChange` | `(MovieFilterUiState) -> Unit` | Callback za svaku izmenu (instant update) |
| `onBackClick` | `() -> Unit` | Nazad bez primene filtera |
| `onApplyFilters` | `(MovieFilterUiState) -> Unit` | Primeni filtere i vrati se na MainScreen |

**Sekcije ekrana:**
1. **Top bar** — Naslov "Filter Movies", Back dugme, "Clear All" dugme
2. **SEARCH** — `BasicTextField` za pretragu po naslovu
3. **GENRE** — `FlowRow` sa `GenreChip` komponentama (single selection)
4. **YEAR RANGE** — Dva `BasicTextField` polja za "From" i "To" godinu
5. **MINIMUM RATING** — `Slider` (0–10) sa prikazom vrednosti
6. **Apply Filters dugme** — Crveno dugme koje aktivira filtere

**Privatni Composable-i:**

`SectionTitle(text: String)` — Stilizovani naslov sekcije (krem boja, bold)

`SmallLabel(text: String)` — Mali label (sivi tekst, 11sp)

`GenreChip(text, selected, onClick, selectedColor, unselectedColor)` — Klikabilni chip za žanr; prikazuje se crveno ako je selektovan

`RoundedInputField(value, onValueChange, hint, fieldColor, hintColor, textColor, centerText, modifier)` — Zaobljeno polje za unos teksta sa placeholder-om

---

### `MovieScreen.kt`

Ekran sa detaljima filma.

**Parametri:**
| Parametar | Tip | Opis |
|---|---|---|
| `imdbId` | `String` | IMDb ID filma čiji detalji se prikazuju |
| `onBackClick` | `() -> Unit` | Callback za povratak na listu |

**Sadržaj ekrana:**
1. **Backdrop slika** — Fullwidth slika sa play dugmetom i back ikonom
2. **Poster + osnovne info** — Poster slika (140dp), naslov, godina, trajanje, IMDb i TMDB ocene
3. **Žanrovi** — `FlowRow` sa `GenreTag` komponentama
4. **Overview** — Sinopsis filma
5. **Info badges** — Budžet, Zarada, Jezik, Popularnost (`InfoBadge` komponente)
6. **Galerija slika** — Horizontalno skrolujuća lista backdrop slika
7. **Kast** — Horizontalno skrolujuća lista `CastItem` komponenti

Slike se učitavaju sa TMDB CDN-a: `https://image.tmdb.org/t/p/w500{path}`

---

## 11. UI sloj — Komponente

Paket: `rs.edu.raf.rma.movies.coponents`

> **Napomena:** Typo u nazivu paketa — "coponents" umesto "components". Nije ispraviti da se ne bi pokvarila kompatibilnost.

---

### `MovieListItem.kt`

Kartica filma u listi.

**Parametri:**
| Parametar | Tip | Opis |
|---|---|---|
| `movie` | `Movie` | Film koji se prikazuje |
| `onClick` | `(Movie) -> Unit` | Callback pri kliku |

**Prikaz:**
- Tamna kartica (0xFF1E1E2E pozadina)
- Poster slika levo (80dp širine), detalji desno
- Naslov, godina, IMDb ocena sa zvezdicom, broj glasova
- `GenreTag` komponente za žanrove

---

### `GenreTag.kt`

Mali zaobljeni badge za prikazivanje žanra.

**Parametri:**
| Parametar | Tip | Opis |
|---|---|---|
| `genre` | `Genre` | Žanr koji se prikazuje |

---

### `InfoBadge.kt`

Komponenta za prikaz jedne informacije sa labelom i vrednošću (npr. "Budget" / "$150M").

**Parametri:**
| Parametar | Tip | Opis |
|---|---|---|
| `label` | `String` | Naziv informacije |
| `value` | `String` | Vrednost informacije |

---

### `CastItem.kt`

Prikaz jednog člana kasta u horizontalnoj listi.

**Parametri:**
| Parametar | Tip | Opis |
|---|---|---|
| `name` | `String` | Ime glumca/člana kasta |
| `imagePath` | `String?` | TMDB putanja do profilne slike (null = prikazuje placeholder) |

Slika je kružna, 44dp dijametra.

---

## 12. Dependency Injection

Paket: `rs.edu.raf.rma.di`

Projekat koristi **Koin** framework za dependency injection.

---

### `AppModule.kt`

Trenutno prazan modul. Placeholder za buduće dependency-je na nivou aplikacije. Sadrži `initKoin()` funkciju koja startuje Koin sa svim modulima.

```kotlin
val appModule = module {
    // placeholder
}

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    return startKoin {
        config?.invoke(this)
        modules(appModule, networkModule)
    }
}
```

---

### `NetworkModule.kt`

Centralni DI modul koji registruje sve mrežne i ViewModel zavisnosti.

```kotlin
val networkModule = module {

    // Singleton HttpClient — platforma-specifičan (OkHttp / Darwin / CIO)
    single<HttpClient> {
        createHttpClient()
    }

    // Singleton Ktorfit instanca — konfigurisan sa baznom URL adresom
    single<Ktorfit> {
        Ktorfit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .httpClient(get<HttpClient>())
            .build()
    }

    // MovieApiService — Ktorfit generiše implementaciju
    single<MovieApiService> {
        get<Ktorfit>().create<MovieApiService>()
    }

    // Repository — interfejs bind-ovan za implementaciju
    single<MovieRepository> {
        MovieRepositoryImpl(get())
    }

    // ViewModels — automatski registrovani koristeći viewModelOf helper
    viewModelOf(::MoviesViewModel)
    viewModelOf(::MovieDetailsViewModel)
}
```

---

### `KoinInitializer.kt`

Rezervisan fajl, trenutno bez aktivnog koda. Postoji kao placeholder.

---

## 13. Platform-specifične implementacije

---

### `Platform.kt` (common)

```kotlin
interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
```

### `Platform.android.kt`

```kotlin
class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()
```

### `Platform.ios.kt`

```kotlin
class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()
```

### `Platform.jvm.kt`

```kotlin
class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()
```

---

## 14. Android aplikacija

Paket: `rs.edu.raf.rma.android`

---

### `MovieApplication.kt`

`Application` klasa — entry point Android procesa. Inicijalizuje Koin dependency injection pri startu.

```kotlin
class MovieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("MovieApp", "Application started")
        initKoin {
            androidContext(this@MovieApplication)
        }
    }
}
```

---

### `MainActivity.kt`

Jedina Activity u Android aplikaciji. Postavlja Compose sadržaj.

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoviesAppRoot()
        }
    }
}
```

---

## 15. Desktop aplikacija

Paket: `rs.edu.raf.rma` (u `desktopApp` modulu)

### `Main.kt`

Entry point za JVM/Desktop aplikaciju.

```kotlin
fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Demo"
        ) {
            PasswordsApp()  // Napomena: referenca na PasswordsApp koja nije implementirana u vidljivim fajlovima
        }
    }
}
```

> **Napomena:** Desktop aplikacija je u delimično implementiranom stanju i verovatno je ostatak od prethodne verzije projekta.

---

## 16. Navigacija

Navigacija je implementirana kao **state-based navigation** direktno u `MoviesAppRoot.kt`, bez korišćenja `NavHost`-a.

```
MoviesAppRoot
    │
    ├── selectedMovieId != null  ──→  MovieScreen(imdbId)
    │                                    └── Back → selectedMovieId = null
    │
    ├── showFilter == true       ──→  FilterScreen(filters, genres)
    │                                    ├── Back → showFilter = false
    │                                    └── Apply → filters = newFilters, showFilter = false
    │
    └── else (default)           ──→  MainScreen(activeFilters)
                                         ├── Filter click → showFilter = true
                                         └── Movie click → selectedMovieId = imdbId
```

Navigacioni destinacije (`MainDestination`, `MovieDetailsDestination`) su definisane u `navigator/` paketu ali nisu u aktivnoj upotrebi — ostavljene su kao priprema za eventualni prelaz na `NavHost`.

---

## 17. API integracija

**Base URL:** `https://rma.finlab.rs/`

**Dokumentacija:** `https://rma.finlab.rs/movies/docs`

### Endpoints

| Method | Path | Opis |
|--------|------|------|
| `GET` | `/movies` | Paginirana lista filmova sa filterima i sortiranjem |
| `GET` | `/movies/{id}` | Detalji jednog filma |
| `GET` | `/movies/{id}/cast` | Kast filma (paginiran) |
| `GET` | `/movies/{id}/images` | Slike filma (posteri, backdropi, logotipi) |
| `GET` | `/genres` | Lista svih žanrova (nije paginiran, vraća plain array) |

### Query parametri za `/movies`

| Parametar | Tip | Default | Opis |
|-----------|-----|---------|------|
| `page` | Int | 1 | Broj stranice |
| `page_size` | Int | 20 | Filmova po stranici (max 100) |
| `query` | String? | — | Pretraga po naslovu |
| `genre_id` | Int? | — | Filter po žanru (ID iz `/genres`) |
| `min_year` | Int? | — | Minimalna godina izlaska |
| `max_year` | Int? | — | Maksimalna godina izlaska |
| `min_rating` | Float? | — | Minimalna IMDb ocena (0.0–10.0) |
| `sort_by` | String? | `imdb_votes` | Polje sortiranja |
| `sort_order` | String? | `desc` | Smer sortiranja (`asc` / `desc`) |

### Dozvoljene vrednosti za `sort_by`

`imdb_rating`, `imdb_votes`, `year`, `tmdb_rating`, `popularity`, `title`

### Slike (TMDB CDN)

Sve slike se učitavaju sa TMDB CDN-a koristeći putanje iz API odgovora:

```
https://image.tmdb.org/t/p/w500{posterPath}
```

---

## 18. Tok podataka

### Učitavanje liste filmova

```
Korisnik otvori app
    → MoviesAppRoot kompozicija
    → koinViewModel<MoviesViewModel>() — kreira ViewModel, poziva loadGenres()
    → MainScreen LaunchedEffect(activeFilters) → viewModel.loadMovies()
    → MovieRepositoryImpl.getMovies() → api.getMovies(sortBy = "imdb_rating")
    → HTTP GET https://rma.finlab.rs/movies?sort_by=imdb_rating
    → MoviesResponse deserialized
    → _state.value.copy(movies = ...) ← ažurira state
    → MainScreen recomposuje, prikazuje LazyColumn
```

### Primena filtera

```
Korisnik klikne "Apply Filters" u FilterScreen
    → onApplyFilters(filters) u MoviesAppRoot
    → filters state ažuriran, showFilter = false
    → MainScreen LaunchedEffect(activeFilters) — detektuje promenu
    → viewModel.applyFilters(filters)
    → currentFilters = filters  ← čuva se za re-sort
    → genresMap["Action"] → 28 (lookup ID iz naziva)
    → api.getMovies(genreId=28, minYear=2000, ..., sortBy="imdb_rating")
    → _state.value.copy(movies = filteredMovies)
    → Lista se osvežava
```

### Promena sortiranja

```
Korisnik klikne na Sort dropdown
    → DropdownMenuItem onClick
    → viewModel.onIntent(ChangeSortBy(SortOption.YEAR))
    → changeSortBy(SortOption.YEAR)
    → _state.value.copy(sortBy = SortOption.YEAR)
    → currentFilters != null → applyFilters(currentFilters)  (ili loadMovies ako nema filtera)
    → api.getMovies(..., sortBy = "year")
    → Lista se sortira po godini
```

### Navigacija na detalje

```
Korisnik klikne na film
    → MovieListItem onClick → onMovieClick(imdbId)
    → MoviesAppRoot: selectedMovieId = imdbId
    → MovieScreen(imdbId) se prikazuje
    → MovieDetailsViewModel.loadMovie(imdbId)
    → Paralelno (async): getMovieDetails() + getMovieCast() + getMovieImages()
    → state ažuriran sa svim podacima
    → Ekran se iscrtava
```

---

## 19. Build konfiguracija

### `gradle/libs.versions.toml`

Centralni katalog verzija svih zavisnosti.

**Ključne verzije:**

| Zavisnost | Verzija |
|-----------|---------|
| kotlin | 2.3.20 |
| compose-multiplatform | 1.10.3 |
| ktor | 3.4.0 |
| ktorfit | 2.7.1 |
| koin-bom | 4.1.1 |
| room | 2.8.4 |
| ksp | 2.3.4 |
| coil | 3.1.0 |
| android-compileSdk | 36 |
| android-minSdk | 28 |

### `composeApp/build.gradle.kts`

Ključne konfiguracije:

- **Targets:** Android, iOS (arm64 + simulatorArm64), JVM
- **iOS framework:** static, naziv `ComposeApp`
- **Android:** namespace `rs.edu.raf.rma`, compileSdk 36, minSdk 28
- **Java compatibility:** VERSION_11

### `gradle.properties`

| Svojstvo | Vrednost | Opis |
|----------|----------|------|
| `kotlin.daemon.jvm.options` | `-Xmx3g` | Max 3GB RAM za Kotlin daemon |
| `org.gradle.jvmargs` | `-Xmx4g -Dfile.encoding=UTF-8` | Max 4GB RAM za Gradle |
| `org.gradle.caching` | `true` | Build cache omogućen |
| `org.gradle.configuration-cache` | `true` | Configuration cache omogućen |
| `android.useAndroidX` | `true` | AndroidX enabled |
| `android.nonTransitiveRClass` | `true` | Non-transitive R klase |
