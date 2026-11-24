# Esport Bracket API

REST API backend (output JSON murni) untuk Sistem Manajemen Turnamen Esport, Penjadwalan Match, dan Perhitungan Poin Klasemen Otomatis.

## Cara Menjalankan

1. Jalankan server Spring Boot:
   ```bash
   mvn spring-boot:run
   ```
2. Jalankan seluruh unit test:
   ```bash
   mvn test
   ```

## Daftar Endpoint

- `GET /api/tournament/participants` : Menampilkan daftar peserta terdaftar (SoloPlayer & EsportTeam).
- `POST /api/tournament/participants/solo` : Mendaftarkan peserta individu (1v1).
- `POST /api/tournament/participants/team` : Mendaftarkan peserta tim (validasi minimal 5 pemain dan nama kapten).
- `GET /api/tournament/matches` : Menampilkan semua jadwal pertandingan (`MatchSeries`).
- `GET /api/tournament/matches/{matchId}` : Menampilkan detail rangkaian pertandingan beserta daftar `GameRound`.
- `POST /api/tournament/matches` : Menjadwalkan pertandingan baru (misal Best-of-3).
- `PATCH /api/tournament/matches/{matchId}/phase` : Mengubah fase pertandingan (`SCHEDULED`, `LIVE`, `DISPUTED`, `FINISHED`).
- `POST /api/tournament/matches/{matchId}/rounds` : Mencatat hasil ronde baru (hanya diizinkan saat fase `LIVE`).
- `GET /api/tournament/standings?rule=GROUP_STAGE` : Menampilkan klasemen otomatis (`GROUP_STAGE` atau `PLAYOFF_BONUS`).

## Melihat Output di Browser

Setelah server berjalan (`mvn spring-boot:run`), buka URL berikut di browser. Data dummy sudah tersedia saat aplikasi pertama kali dijalankan.

| URL | Isi |
|---|---|
| http://localhost:8080/api/tournament/participants | Daftar peserta (`TM-01`, `TM-02`, `PL-01`) |
| http://localhost:8080/api/tournament/matches | Semua match (`MCH-001`, `MCH-002`) |
| http://localhost:8080/api/tournament/matches/MCH-001 | Detail match beserta ronde-rondenya |
| http://localhost:8080/api/tournament/standings?rule=GROUP_STAGE | Klasemen aturan fase grup |
| http://localhost:8080/api/tournament/standings?rule=PLAYOFF_BONUS | Klasemen aturan bonus playoff |
