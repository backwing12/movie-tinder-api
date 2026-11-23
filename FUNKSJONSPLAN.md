# Funksjonsplan: filmfunksjonene på backwing.dev

Planlagt 23. november 2026. Denne fila beskriver hva API-et skal kunne og hvordan det henger sammen. PROSJEKTPLAN.md beskriver når det gjøres.

Alt er bare for meg. Det finnes ingen andre brukere.

## Krav

### Movie Tinder
1. Viser én film om gangen, filtrert på tiår, sjanger, språk, minste vurdering og så videre.
2. Går ikke tom for filmer så lenge det finnes flere som passer filtrene. I dag velger siden én tilfeldig side fra TMDB og gir opp hvis alle filmene der er brukt.
3. Filmer jeg har sett vises aldri igjen.
4. Filmer jeg svarer «Nei, ikke sett» på lagres med tidspunkt og skjules en periode. Hvor lang perioden er settes i `application.yaml` og bestemmes senere.

### Søk
5. Tittel og filtre kan kombineres, for eksempel horrorfilmer fra 80-tallet, eller actionfilmer med «battle» i tittelen.
6. Resultatene vises som en liste. Hver rad har en hurtigknapp for å markere filmen som sett eller legge den i en liste.

### Katalogen
7. Viser filmene jeg har sett, hentet fra min egen database og ikke fra TMDB.
8. Viser utgivelsesdato i stedet for datoen filmen ble lagt til.
9. Viser også de egne listene mine. Det er her jeg oppretter, endrer og sletter lister.

### Filmside
10. Egen side med all tilgjengelig info om én film. Kan åpnes fra søk, Movie Tinder, katalogen og listene.
11. Viser på en enkel måte om jeg har sett filmen.
12. «Legg til i liste» viser alle listene mine og hvilke filmen allerede ligger i, slik Spotify gjør med spillelister.

### Data
13. Datoen en film ble lagt til som sett lagres automatisk. Den vises ikke nå, men kan brukes til sortering og statistikk senere.
14. De 45 filmene i Supabase flyttes over, med opprinnelig dato.

### Senere
15. Rating og anmeldelser.

## Viktig om TMDB

TMDB kan ikke kombinere tittelsøk og filtre i samme kall:
- `discover/movie` filtrerer på sjanger, år, språk, vurdering og så videre, men kan ikke søke i titler.
- `search/movie` søker i titler, men kan bare filtrere på år.

Søket i API-et må derfor velge riktig kall selv. Uten tittel brukes discover. Med tittel brukes search, og API-et fjerner selv filmene som ikke passer filtrene.

TMDB lar deg hente det meste om en film i ett kall med `append_to_response`: skuespillere og crew, trailere, lignende filmer, aldersgrense og hvor filmen kan strømmes i Norge. Strømmedataene kommer fra JustWatch, og da må det stå på siden at de er kilden.

## Tabeller

| Tabell | Innhold | Hvorfor |
|---|---|---|
| `movie_cache` (finnes) | Hele TMDB-svaret for én film, med tidspunkt | Filmsiden, så TMDB ikke spørres hver gang |
| `movies` | tmdb_id, tittel, utgivelsesdato, plakat, sjangre | Det katalogen og listene trenger, uten kall til TMDB |
| `watched` | tmdb_id, logged_at | Filmer jeg har sett, og når de ble lagt til |
| `not_seen` | tmdb_id, answered_at | «Nei, ikke sett» i Movie Tinder |
| `lists` | id, navn, created_at | Egne lister |
| `list_items` | list_id, tmdb_id, added_at | Hvilke filmer som ligger i hvilken liste |

En film får en rad i `movies` første gang den blir markert som sett eller lagt i en liste.

## Endepunkter

### Filmer fra TMDB
| Endepunkt | Gjør |
|---|---|
| `GET /movies/{id}` (finnes) | All info om én film, til filmsiden. Utvides med `append_to_response` |
| `GET /movies/{id}/status` | Om jeg har sett filmen, og hvilke lister den ligger i. Brukes av filmsiden og «Legg til i liste» |
| `GET /movies/search` | Søk med tittel og/eller filtre, og `page`. Hvert treff sier om jeg har sett filmen |
| `GET /tinder/next` | Én film som passer filtrene, som jeg ikke har sett og ikke nylig har svart nei på. Blar videre i TMDB til den finner en |

### Mine data
| Endepunkt | Gjør |
|---|---|
| `GET /watched` | Filmene jeg har sett, til katalogen |
| `PUT /watched/{id}` | Marker som sett |
| `DELETE /watched/{id}` | Angre |
| `PUT /not-seen/{id}` | «Nei, ikke sett» fra Movie Tinder |
| `GET /lists` | Alle listene mine |
| `POST /lists` | Ny liste |
| `PATCH /lists/{id}` | Gi nytt navn |
| `DELETE /lists/{id}` | Slett liste |
| `GET /lists/{id}` | Filmene i én liste |
| `PUT /lists/{id}/movies/{movieId}` | Legg film i liste |
| `DELETE /lists/{id}/movies/{movieId}` | Fjern film fra liste |

`PUT` brukes for å legge til fordi det er trygt å gjøre to ganger. Trykker jeg «sett» på en film som allerede er sett, skjer det ingenting galt.

## Ikke avklart ennå
- **Innlogging:** Alt som endrer data må være beskyttet, siden API-et ligger åpent på nettet. I dag har backwing et signert admin-token i Vercel. Det må bestemmes om Kotlin-API-et skal bruke samme løsning eller noe enklere.
- **Hvor lenge «Nei, ikke sett» gjelder.**
- **Overgangen:** Hvordan backwing.dev flyttes fra Vercel-funksjonen og Supabase til API-et, uten at noe slutter å virke underveis.

## Rekkefølge

Azure-ukene i PROSJEKTPLAN.md kommer først. Det er driften som gjør prosjektet relevant for jobbsøket og AZ-104.

1. **Uke 48 (nå):** `GET /movies/search` med filtre og `page`, foreløpig bare discover. Det erstatter `/movies/popular` fra prosjektplanen.
2. **Uke 49–51:** Azure som planlagt.
3. **Etter uke 51, i denne rekkefølgen:**
   1. Tabellene `movies` og `watched`, endepunktene for sett-filmer, og flytting fra Supabase. Katalogen kan da byttes over.
   2. `not_seen` og `/tinder/next`. Movie Tinder kan da byttes over.
   3. Filmsiden med full info og `/movies/{id}/status`.
   4. Lister.
   5. Søk med tittel og filtre kombinert.
   6. Rating og anmeldelser.
