
# Aplikacja Desktopowa do Drukowania Biletów

Aplikacja Desktopowa do Drukowania Biletów to rozwiązanie napisane w języku Java, stworzone z myślą o drukowaniu biletów na wizyty lekarskie w przychodni. Aplikacja posiada prosty interfejs graficzny, umożliwiający użytkownikom dostosowanie parametrów wydruku. Działa w ścisłej integracji z bazą danych Oracle – po uruchomieniu pobiera niezbędne dane i automatycznie drukuje bilety, bez konieczności dalszego udziału użytkownika.

## Biblioteki
Aplikacja korzysta z biblioteki escpos-coffee.
\
[https://github.com/anastaciocintra/escpos-coffee](https://github.com/anastaciocintra/escpos-coffee)


## Działanie

Aby uruchomić aplikację należy pobrac repozytorium, przejść do folderu `ThermalPrinterDesktop\out\artifacts\ThermalPrinterDesktop_jar` oraz z poziomu konsoli cmd wykonać polecenie  `java -jar ThermalPrinterDesktop.jar`

Aplikacja po pierwszym uruchomieniu utworzy w folderze użytkownika `C:\Users\USERNAME\` folder `PrinterSettings`, a w nim dwa pliki:

- `db.properties`
- `settings.properties`

W pliku `db.properties` należy wpisać dane do połączenia z bazą danych.

```
db.url= URL
db.username= USERNAME
db.password= PASSWORD
```
W pliku `settings.properties` są zapisywane ustawienia wydruku, oraz logo i informacje dodatkowe które zostaną wydrukowane.

Aby na bilecie zostało wydrukowane logo należy uzupełnić pole `image=` ścieżką do pliku. Dodatkowo pole `imageAlt=` można uzupełnić tekstem alternatywnym, który zostanie wydrukowany w przypadku problemów z odnalezieniem zdjęcia.

Pole `additionalInformation=` można uzupełnić tekstem który zostanie wydrukowany na końcu biletu.

---

Po uruchomieniu aplikacji, wyświetla się GUI w którym użytkownik może:

- ustawić parametry wydruku
- wybrać drukarkę
- wpisać maskę (maska powinna być ciągiem znakiem o długości 10. Po wpisaniu maski, aplikacja zamieni numer biletu na wartość odpowiadającą z maski)
- ustawić numer stanowiska (pole MACHINE_ID z bazy danych)
\
![gui](https://github.com/Stowarzyszenie-Medycyna-Polska/printer-desktop/blob/dpytka/feature/src/main/resources/images/gui.png)

Po zmianie ustawień, należy je zapisać przyciskiem `Zapisz`.
Aplikacja zacznie pobierać dane i drukować bilety po wciśnięciu przycisku `Start`.

**ABY WPROWADZIĆ ZMIANY W TRAKCIE DZIAŁANIA APLIKACJI, NALEŻY WCZEŚNIEJ ZATRZYMAĆ APLIKACJĘ PRZYCISKIEM `Stop`, ZATWIERDZIĆ ZMIANY PRZYCISKIEM `Zapisz` I PONOWNIE URUCHOMIĆ APLIKACJĘ PRZYCISKIEM `Start`.**

Jeśli system na którym uruchamiana jest aplikacja posiada zasobnik systemowy, aplikacja domyślnie po wyłączeniu pozostaje uruchomiona w zasobniku systemowym. Aby wyłączyć aplikację należy z zasobnika systemowego kliknąć prawym przyciskiem myszy na ikone aplikacji oraz `Zamknij`.

---

Aplikacja po uruchomieniu i wciśnięciu przycisku `Start` pobiera dane biletów, z odpowiadającym numerem stanowiska, z bazy danych co 2 sekundy. Prawidłowa struktura danych wygląda nastepująco:
```
{
      DAILY_NUMBER: '0083',
      INFO_TEXT: 'Dzisiejsze wizyty:@DEMO#10#09:30@DEMO2#20#12:30',
      MACHINE_ID: 2547,
}
```
## Przkładowe Bilety

![tickets](https://github.com/Stowarzyszenie-Medycyna-Polska/printer-desktop/blob/dpytka/feature/src/main/resources/images/tickets.jpg)

# Klasy

## NewMain
Klasa służąca do uruchamiana aplikacji. Wymagana aby uruchomić projekt z poziomu konsoli komendą `java -jar ThermalPrinterDesktop.jar`.
\
*(Klasa do uruchamiana nie może rozszerzać klasy Application)*

## PrinterApplication
Główna klasa odpowiadająca za:
- Tworzenie sceny
- Zarzadzanie aplikacją w zasobniku systemowym
- Tworzenie plików z ustawieniami

## PrinterController
Klasa zarządzająca sceną, obsługująca działanie przycisków. Widok sceny jest zdefinowany w pliku `printer-resp.fxml`. Inicjalizuje właściwości wydruku. Tworzy/zatrzymuje nowy scheduler.


## OracleDatabaseConnection
Klasa służąca do połączenia z bazą danych Oracle.

## AppSettingsManager
Klasa odpowiedzialna za zapisywanie/wczytywanie ustawień wydruku.

## AutoStartManager
Klasa dopisująca plik wykonawczy aplikacji do rejestru, w celu uruchamiana z startem systemu.
\
*(Wymaga utworzenia działającego pliku exe. Obecnie dopisuje do rejestru tymczasowy pusty plik `TicketPrinter.exe`)*

## Ticket
Klasa modelowa biletu. Zawiera metody pobierające biura i godziny wizyt z informacji pobranych z bazy danych.

## TicketPoller
Klasa pobierająca dane biletu z bazy danych.

## PrinterTask
Klasa implementująca interfejs `Runnable`. Gdy do utworzenia wątku używany jest interfejs implementujący obiekt `Runnable`, uruchomienie wątku powoduje wywołanie metody `run()` obiektu w tym oddzielnie wykonującym się wątku.

## TicketPrinterImpl
Klasa odpowiedzialna za drukowanie biletów. Korzysta z poleceń ESC POS. Wszystkie polecenia są wysyłane na jeden strumień wyjściowy.
