# CLAUDE.md

Kontekst for Claude Code i dette repoet. Les også PROSJEKTPLAN.md, som har ukeplanen og alle valg som er tatt.

## Om prosjektet

Et lite Kotlin-API (Ktor) som tar over TMDB-kallene fra Movie Tinder og katalogen på backwing.dev, med caching i database. Det kjører på Azure Container Apps med Azure SQL Database, Key Vault og managed identity, og alt Azure-oppsett skrives som Bicep. Deploy skjer med GitHub Actions.

Koden skal holdes enkel. Målet med prosjektet er å vise drift på Azure, ikke avansert backend-kode. Det er også den praktiske delen av mine studier til AZ-104.

## Om meg

- Nyutdannet IT-bachelor. Sterkest i Python, har bare så vidt brukt Kotlin og Java i studiet.
- Når du forklarer Kotlin, sammenlign gjerne med Python når det hjelper.
- Jeg jobber på Windows, med VS Code og GitHub Desktop.

## Arbeidsregler

- **Jeg skriver kjernelogikken selv:** endepunkter, databaselogikk og caching. Du forklarer, foreslår, går gjennom koden min og hjelper når jeg står fast. Ikke skriv ferdige løsninger på kjernelogikken før jeg har prøvd selv, med mindre jeg ber om det.
- Oppsett, konfigurasjon og standardkode (Gradle-filer, Dockerfile, GitHub Actions, Bicep-maler) kan du skrive, men forklar hva hver del gjør.
- **Ingenting committes som jeg ikke kan forklare.** Hvis jeg ikke forstår noe, stopp og forklar før vi går videre.
- Små steg og hyppige commits, med korte commit-meldinger på engelsk.
- Hold løsningene enkle. Ikke legg til biblioteker, lag eller abstraksjoner prosjektet ikke trenger.

## Sikkerhet

- Hemmeligheter (TMDB-nøkkel, connection strings, passord) skal aldri ligge i Git. Lokalt brukes miljøvariabler eller en `.env` som står i `.gitignore`. På Azure ligger de i Key Vault.
- Sjekk `git status` før commit hvis noe kan inneholde hemmeligheter.

## Teknisk

- Kotlin med Ktor, bygget med Gradle (Kotlin DSL)
- Database: Azure SQL Database (SQL Server). Lokalt kjøres SQL Server i Docker
- Docker og docker-compose for lokal kjøring
- GitHub Container Registry for Docker-imaget
- Bicep for alt Azure-oppsett, i mappen `infra/`
- GitHub Actions for bygg, test og deploy, i `.github/workflows/`

## Kostnader

Prosjektet skal koste ca. 0 kr i måneden. Før du foreslår en Azure-ressurs, si om den koster noe. Azure SQL bruker gratistilbudet med auto-pause, og Container Apps skal kunne skalere til null.
