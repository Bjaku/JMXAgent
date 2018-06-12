# JMX Agent (Monitoring pamięci na stercie)
---
## O programie
Program ma za zadanie monitorować zmiany w obszarze pamięci zwanym stertą (Heap). 

## Wybór metryki
Wybrałem stertę ponieważ w tym miejscu JVM odkłada tworzone obiekty. Dzięki monitorowaniu tego obszaru można sprawdzić czy w programie nie dochodzi przypadkiem do wycieku pamięci.

## Symulacja
Programem można zaobserwować zmiany na stercie spowodowane dokładaniem do listy  ``List<MemoryFiller> memoryHeaterList`` kolejnych obiektów, powiększając tym samym zajmowaną pamięć, a w rezultacie dokonując symulacji jej wycieku. 

## Przeprowadzenie symulacji
1. W pole opisane etykietą ``Number of structures to insert`` podajemy liczbę elementów, które chcemy wprowadzić do pamięci.
2. Z listy ``Size of single structure`` wybieramy rozmiar pojedynczego elementu
3. Przycisk ``Insert`` powoduje dodanie do ``memoryHeaterList`` tylu obiektów aby zająć sprecyzowany wyżej rozmiar pamięci
4. Wyniki można obejrzeć w zakładkach (odświeżenie następuje co czas PERIOD_BETWEEN_REFRESHING, który domyślnie ustawiony jest na 10 s.)

## Przykładowa symulacja
1. ``Number of structures to insert`` : 20000
2. ``Size of single structure``: 1
3. ``Insert``

## Eksport do CSV oraz rysowanie wykresu
Do pliku MemoryDump.csv zapisywana jest aktualnie zużywana pamięć (used memory) oraz pamięć możliwa do wykorzystania (committed memory). Zapis oraz rysowanie wykresu odbywa się automatycznie co PERIOD_BETWEEN_SAVING. Funkcjonalności znajdują się w ``savingJob`` (zadanie dla wątku ``saver``) w metodzie ``initialize()`` klasy ``MainController``.

## Odświeżanie rubryk
Zrealizowane w ``viewingJob`` (zadanie dla wątku ``viewer``) poprzez wykonanie metody``updateTabs()``.

## Wprowadzenie nowych elementów do pamięci
Zrealizowane w metodzie ``public void insertToMemory()`` z klasy ``MainController`` poprzez `` memoryHeaterList.add(new MemoryFiller(numberOfElementsToInsert))``.

## Aktualizacja informacji o pamięci
Zrealizowane w metodzie ``public void insertToMemory()`` z klasy ``MainController`` poprzez ``memoryInformationKeeper.update(memoryBeforeOperation, memoryUsage, numberOfElementsToInsert * MemoryFiller.SINGLE_ELEMENT_SIZE)``.

## Opis GUI
Do dyspozycji jest 6 zakładek:
(Wielkości w nich wyrażone są w bajtach)
* Basic - udostępnia 5 rubryk:
-- Used memory before operation - rozmiar pamięci na stercie (sprzed 10 s) 
-- Used memory after operation - aktualnie znany rozmiar pamięci na stercie
--Difference between memories - rożnica między Used memory before operation a Used memory after operation
--Size of inserted data - rozmiar ostanio wrzuconej struktury
--Total size of inserted data to memory - całkowity rozmiar wrzuconych do tej pory elementów
* Universal - udostępnia wszystkie rubryki, które można wyświetlić wedle swojego uznania
* Overheat:
--Used memory to committed ratio - stosunek zajmowanej pamięci do możliwej do użycia
--Used memory to max ratio - stosunek zajmowanej pamięci do maksymalnej jaka może (ale nie musi) być przydzielona
* Pure - informacje pobrane bezpośrednio z MemoryMXBean
* Text - wszystkie informacje w wersji tekstowej
* Chart:
--Wykres używanej pamięci od punktu kontrolnego
--Wykres możliwej do użycia pamięci od punktu kontrolnego

## Wzorzec projektowy
* Inspirowany MVC

## Technologie + narzędzia
* Java (JDK 8)
* JavaFx
* Java Management Extensions (JMX)
* Maven
* Scene Builder

## Do dodania w przyszłości
* Docelowo dwie aplikacje Agent i Client, Agent udostępnia dane a Client je zbiera (własna wersja JConsole).
* Automatyzowane testy
