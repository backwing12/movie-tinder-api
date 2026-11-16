# Prosjektplan: Movie Tinder API på Azure

Periode: 16. november – 18. desember 2026 (fem uker, ca. 6 timer i uka, rundt 30 timer totalt)

## Hva prosjektet skal vise

Et lite Kotlin-API som tar over TMDB-kallene fra Movie Tinder og katalogen på backwing.dev, og som kjører på Azure slik en bedrift ville driftet det: med infrastruktur som kode, hemmeligheter i Key Vault, automatisk deploy, overvåking, varsling og kostnadskontroll.

Selve koden holdes enkel. Det er driften rundt den som er poenget. Prosjektet er den praktiske delen av AZ-104.

## Valg som er tatt (16. november)

| Del | Valg | Hvorfor |
|---|---|---|
| Rammeverk | Ktor | Lite og eksplisitt, lett å forklare. Mer tid til Azure-delen |
| Hosting | Azure Container Apps | Lærer Docker og containere. Skalerer til null, gratis innenfor månedskvoten |
| Infrastruktur som kode | Bicep | Azure sitt eget språk, henger direkte sammen med AZ-104 |
| Database | Azure SQL Database (gratistilbudet, auto-pause) | Alltid gratis, kan aldri gi overraskende regning. SQL Server er vanlig i norske Microsoft-miljøer |
| Container-register | GitHub Container Registry | Gratis |
| CI/CD | GitHub Actions | Bygg, test og deploy fra GitHub |
| Budsjett | Ca. 0 kr i måneden, budsjettvarsel på 50 kr | |
| Editor | VS Code med Claude Code (IntelliJ hvis Kotlin-støtten blir for svak) | |

## Arbeidsregler

- Jeg skriver kjernelogikken selv (endepunkter, database, caching). Claude Code forklarer og går gjennom koden.
- Ingenting committes som jeg ikke kan forklare.
- Azure-ressurser lages først for hånd i portalen for å forstå dem, deretter som Bicep.
- Hemmeligheter (TMDB-nøkkel, databasepassord) skal aldri ligge i Git.
- Commit ofte, gjerne flere ganger per økt, hver gang noe lite virker. Korte meldinger som sier hva som er gjort.
- Timene føres i Jobbsøk logg.xlsx hver dag, som vanlig.
- På slutten av hver uke krysses ferdige punkter av i denne planen, så det er lett å se hvor jeg står.

## Uke for uke

### Uke 47 (16.–22. nov): Grunnmur
- [ ] Opprette repo i C:\Kode og på GitHub, med README, .gitignore og denne planen
- [ ] Skrive CLAUDE.md med arbeidsreglene over
- [ ] Ktor-prosjekt med Gradle som svarer på `GET /health`
- [ ] Første test som sjekker at `/health` svarer 200
- [ ] Dockerfile, og kjøre appen i en container lokalt (Docker Desktop)
- [ ] GitHub Actions som bygger og tester ved hver push

**Ferdig når:** `docker run` starter appen lokalt, og GitHub viser grønn hake på siste commit.

### Uke 48 (23.–29. nov): Funksjonen
- [ ] Endepunkter som henter fra TMDB (for eksempel `/movies/popular` og `/movies/{id}`), med TMDB-nøkkel fra miljøvariabel
- [ ] SQL Server lokalt i Docker, og en tabell for cache
- [ ] Caching: svar lagres i databasen og gjenbrukes i en bestemt tid, så TMDB ikke får for mange kall
- [ ] Feilhåndtering (TMDB nede, timeout, ukjent id) med riktige statuskoder
- [ ] Tester for endepunktene og cachen
- [ ] docker-compose som starter app og database sammen

**Ferdig når:** hele appen kjører lokalt med `docker compose up`, og testene går grønt i GitHub Actions.

### Uke 49 (30. nov–6. des): Azure
- [ ] Opprette Azure-gratiskonto (200 dollar i 30 dager, så kreditten også dekker AZ-104-labene)
- [ ] Budsjett med varsel på 50 kr, før noe annet lages
- [ ] Ressursgruppe, Azure SQL Database (gratistilbudet, auto-pause), Key Vault og Container Apps-miljø. Først i portalen
- [ ] Managed identity for appen, slik at den henter hemmeligheter fra Key Vault uten passord i koden
- [ ] Første deploy av containeren for hånd
- [ ] Skrive alt som Bicep, rive ned og bygge opp igjen fra Bicep for å bevise at det virker

**Ferdig når:** API-et svarer på en offentlig Azure-adresse, og hele oppsettet kan bygges fra Bicep-filene.

### Uke 50 (7.–13. des): Automatisering og overvåking
- [ ] GitHub Actions deployer automatisk til Azure ved push til main (innlogging med OIDC, uten lagrede passord)
- [ ] Logger i Log Analytics, og et par nyttige spørringer
- [ ] Varsel på e-post hvis appen svarer med feil eller er nede
- [ ] Sjekke kostnadene i Cost Management

**Ferdig når:** en push til main havner automatisk på Azure, og jeg får varsel hvis noe går galt.

### Uke 51 (14.–18. des): Ferdigstilling
- [ ] Koble Movie Tinder og katalogen på backwing.dev mot det nye API-et
- [ ] README med arkitekturtegning, hvordan prosjektet kjøres og hvilke valg som ble tatt
- [ ] Kort driftsdokumentasjon: hvordan deploye, hvor loggene er, hva man gjør hvis appen er nede
- [ ] Legge prosjektet inn på CV, LinkedIn og backwing.dev
- [ ] Evaluering rundt 18. desember sammen med AZ-104-fremdriften

**Ferdig når:** backwing.dev bruker API-et, og en arbeidsgiver kan forstå prosjektet fra README-en alene.

## Hvis jeg kommer bak skjema

Det viktigste er uke 47–49. Kommer jeg bak, kuttes uke 50–51 ned i denne rekkefølgen:
1. Koblingen til backwing.dev kan vente til januar
2. Logg-spørringer kan forenkles til bare varsling
3. Automatisk deploy kan erstattes av manuell deploy med et skript

Azure-oppsettet med Bicep og Key Vault kuttes ikke, siden det er det som gjør prosjektet relevant for drift.

## Kobling til AZ-104

| AZ-104-tema | Hvor i prosjektet |
|---|---|
| Identitet og tilgang (Entra ID, RBAC, managed identity) | Uke 49–50 |
| Ressursgrupper, tagger, policy, budsjett | Uke 49 |
| Utrulling med ARM/Bicep | Uke 49 |
| Containere i Azure | Uke 49 |
| Overvåking (Azure Monitor, Log Analytics, varsler) | Uke 50 |
| Virtuelle maskiner, nettverk og lagring | AZ-104-labene, utenfor prosjektet |
