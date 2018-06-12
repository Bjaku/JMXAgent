package model.monitor.bjaku.com;

import java.lang.management.MemoryUsage;

//Klasa do przechowywania pojedynczego snapshota o pamieci
public class MemoryInformationKeeper {
    //Pola
    private Long usedMemoryBeforeOperation; //Uzywana pamiec w poprzednim snapshocie
    private Long usedMemoryAfterOperation;  //Pamiec uzywana w aktualnym snapshocie
    private Long initialMemory; //Pamiec przyznana inicjalnie przez JVM
    private Long committedMemory;   //Pamiec mozliwa do wykorzystania
    private Long maxMemory; //Maksymalna pamiec jaka moze przyznac JVM
    private Long differenceBetweenMemories;
    private Integer sizeOfInsertedDataToMemory; //Wielkosc danych wprowadzonych w aktualnym snapshocie wyrazona w bajtach
    private Long totalSizeOfInsertedDataToMemory;   //Wielkosc danych wprowadzonych w trakcie dzialania calego programu
    private Long differenceBetweenDifferenceOfMemoriesAndSizeOfInsertedData;
    private Integer usedMemoryToInitialRatio;   //Stosunek pamieci wykorzystywanej do inicjalnej
    private Integer usedMemoryToCommittedRatio; //Stosunek pamieci wykorzystywanej do mozliwej do wykorzystania
    private Integer usedMemoryToMaxRatio;   //Stosunek pamieci wykorzystywanej do najwiekszej jaka moze byc przyznana
    private Integer totalSizeOfInsertedDataToInitialMemoryRatio;    //Stosunek wielkosci wszystkich wprowadzonych danych do pamieci inicjalnej
    private Integer totalSizeOfInsertedDataToCommittedMemoryRatio;  //Stosunek wielkosci wszystkich wprowadzonych danych do pamieci mozliwej do wykorzystania
    private Integer totalSizeOfInsertedDataToMaxMemoryRatio;    //Stosunek wielkosci wszystkich wprowadzonych danych do najwiekszej jaka moze byc przyznana

    //Konstruktor
    public MemoryInformationKeeper(){
        this.totalSizeOfInsertedDataToMemory = 0L;
    }

    //Gettery
    public Long getUsedMemoryBeforeOperation() {
        return usedMemoryBeforeOperation;
    }

    public synchronized Long getUsedMemoryAfterOperation() {
        return usedMemoryAfterOperation;
    }

    public Long getInitialMemory() {
        return initialMemory;
    }

    public Long getCommittedMemory() {
        return committedMemory;
    }

    public Long getMaxMemory() {
        return maxMemory;
    }

    public Long getDifferenceBetweenMemories() {
        return differenceBetweenMemories;
    }

    public Integer getSizeOfInsertedDataToMemory() {
        return sizeOfInsertedDataToMemory;
    }

    public Long getTotalSizeOfInsertedDataToMemory() {
        return totalSizeOfInsertedDataToMemory;
    }

    public Long getDifferenceBetweenDifferenceOfMemoriesAndSizeOfInsertedData() {
        return differenceBetweenDifferenceOfMemoriesAndSizeOfInsertedData;
    }

    public Integer getUsedMemoryToInitialRatio() {
        return usedMemoryToInitialRatio;
    }

    public Integer getUsedMemoryToCommittedRatio() {
        return usedMemoryToCommittedRatio;
    }

    public Integer getUsedMemoryToMaxRatio() {
        return usedMemoryToMaxRatio;
    }

    public Integer getTotalSizeOfInsertedDataToInitialMemoryRatio() {
        return totalSizeOfInsertedDataToInitialMemoryRatio;
    }

    public Integer getTotalSizeOfInsertedDataToCommittedMemoryRatio() {
        return totalSizeOfInsertedDataToCommittedMemoryRatio;
    }

    public Integer getTotalSizeOfInsertedDataToMaxMemoryRatio() {
        return totalSizeOfInsertedDataToMaxMemoryRatio;
    }

    //Konstruktor potrzebny do tworzenia obiektu w metodzie copyMe()
    public MemoryInformationKeeper(Long usedMemoryBeforeOperation, Long usedMemoryAfterOperation, Long initialMemory, Long committedMemory, Long maxMemory, Long differenceBetweenMemories, Integer sizeOfInsertedDataToMemory, Long totalSizeOfInsertedDataToMemory, Long differenceBetweenDifferenceOfMemoriesAndSizeOfInsertedData, Integer usedMemoryToInitialRatio, Integer usedMemoryToCommittedRatio, Integer usedMemoryToMaxRatio, Integer totalSizeOfInsertedDataToInitialMemoryRatio, Integer totalSizeOfInsertedDataToCommittedMemoryRatio, Integer totalSizeOfInsertedDataToMaxMemoryRatio) {
        this.usedMemoryBeforeOperation = usedMemoryBeforeOperation;
        this.usedMemoryAfterOperation = usedMemoryAfterOperation;
        this.initialMemory = initialMemory;
        this.committedMemory = committedMemory;
        this.maxMemory = maxMemory;
        this.differenceBetweenMemories = differenceBetweenMemories;
        this.sizeOfInsertedDataToMemory = sizeOfInsertedDataToMemory;
        this.totalSizeOfInsertedDataToMemory = totalSizeOfInsertedDataToMemory;
        this.differenceBetweenDifferenceOfMemoriesAndSizeOfInsertedData = differenceBetweenDifferenceOfMemoriesAndSizeOfInsertedData;
        this.usedMemoryToInitialRatio = usedMemoryToInitialRatio;
        this.usedMemoryToCommittedRatio = usedMemoryToCommittedRatio;
        this.usedMemoryToMaxRatio = usedMemoryToMaxRatio;
        this.totalSizeOfInsertedDataToInitialMemoryRatio = totalSizeOfInsertedDataToInitialMemoryRatio;
        this.totalSizeOfInsertedDataToCommittedMemoryRatio = totalSizeOfInsertedDataToCommittedMemoryRatio;
        this.totalSizeOfInsertedDataToMaxMemoryRatio = totalSizeOfInsertedDataToMaxMemoryRatio;
    }

    //Bezpieczne pobranie stanu obiektu przez synchronizowane jego kopiowanie; metoda uzywana w watku aktualizujacym rubryki(viewer)
    public synchronized MemoryInformationKeeper copyMe(){
        return new MemoryInformationKeeper(this.usedMemoryBeforeOperation, this.usedMemoryAfterOperation, this.initialMemory, this.committedMemory,
                this.maxMemory, this.differenceBetweenMemories, this.sizeOfInsertedDataToMemory, this.totalSizeOfInsertedDataToMemory,
                    this.differenceBetweenDifferenceOfMemoriesAndSizeOfInsertedData, this.usedMemoryToInitialRatio, this.usedMemoryToCommittedRatio, this.usedMemoryToMaxRatio,
                        this.totalSizeOfInsertedDataToInitialMemoryRatio, this.totalSizeOfInsertedDataToCommittedMemoryRatio, this.totalSizeOfInsertedDataToMaxMemoryRatio);
    }

    //Metoda do aktualizacji informacji o stanie pamieci
    public synchronized void update(Long memoryUsedBeforeOperation, MemoryUsage memoryUsage, Integer sizeOfInsertedDataToMemory){
        this.usedMemoryBeforeOperation = memoryUsedBeforeOperation;
        this.usedMemoryAfterOperation = memoryUsage.getUsed();
        this.initialMemory = memoryUsage.getInit();
        this.committedMemory = memoryUsage.getCommitted();
        this.maxMemory = memoryUsage.getMax();
        this.differenceBetweenMemories = usedMemoryAfterOperation - memoryUsedBeforeOperation;
        this.sizeOfInsertedDataToMemory = sizeOfInsertedDataToMemory;
        this.totalSizeOfInsertedDataToMemory += sizeOfInsertedDataToMemory;
        this.differenceBetweenDifferenceOfMemoriesAndSizeOfInsertedData = differenceBetweenMemories - sizeOfInsertedDataToMemory;
        Long tmp = (usedMemoryAfterOperation * 100 / initialMemory) ;   //Wynik chcemy uzyskac w %
        this.usedMemoryToInitialRatio = tmp.intValue();
        tmp = (usedMemoryAfterOperation * 100 / committedMemory) ;
        this.usedMemoryToCommittedRatio = tmp.intValue();
        tmp = (usedMemoryAfterOperation * 100 / maxMemory) ;
        this.usedMemoryToMaxRatio = tmp.intValue();
        tmp = (totalSizeOfInsertedDataToMemory * 100 / initialMemory);
        this.totalSizeOfInsertedDataToInitialMemoryRatio = tmp.intValue();
        tmp = (totalSizeOfInsertedDataToMemory * 100 / committedMemory) ;
        this.totalSizeOfInsertedDataToCommittedMemoryRatio = tmp.intValue();
        tmp = (totalSizeOfInsertedDataToMemory * 100 / maxMemory) ;
        this.totalSizeOfInsertedDataToMaxMemoryRatio = tmp.intValue();
    }

    @Override
    public String toString() {
        return "[BEFORE]: " + usedMemoryBeforeOperation + " bytes\n"
                + "[AFTER]: " + usedMemoryAfterOperation + " bytes\n"
                + "[INITIAL]: " + initialMemory + " bytes\n"
                + "[COMMITTED]: " + committedMemory + " bytes\n"
                + "[MAX]: " + maxMemory + " bytes\n"
                + "[BEFORE - AFTER]: " + differenceBetweenMemories + " bytes\n"
                + "[INSERTED]: " + sizeOfInsertedDataToMemory + " bytes\n"
                + "[TOTAL INSERTED]: " + totalSizeOfInsertedDataToMemory + " bytes\n"
                + "[DIF - INSERTED]: " + differenceBetweenDifferenceOfMemoriesAndSizeOfInsertedData + " bytes\n"
                + "[USED MEMORY TO INITIAL RATIO]: " + usedMemoryToInitialRatio + " %\n"
                + "[USED MEMORY TO COMMITTED RATIO]: " + usedMemoryToCommittedRatio + " %\n"
                + "[USED MEMORY TO MAX RATIO]: " + usedMemoryToMaxRatio + " %\n"
                + "[TOTAL SIZE OF INSERTED DATA TO INITIAL MEMORY RATIO]: " + totalSizeOfInsertedDataToInitialMemoryRatio + " %\n"
                + "[TOTAL SIZE OF INSERTED DATA TO COMMITTED MEMORY RATIO]: " + totalSizeOfInsertedDataToCommittedMemoryRatio + " %\n"
                + "[TOTAL SIZE OF INSERTED DATA TO MAX MEMORY RATIO]: " + totalSizeOfInsertedDataToMaxMemoryRatio + " %\n\n";

    }
}
