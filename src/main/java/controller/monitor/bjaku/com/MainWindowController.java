package controller.monitor.bjaku.com;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.monitor.bjaku.com.MemoryFiller;
import model.monitor.bjaku.com.MemoryInformationKeeper;

import javax.management.MBeanServer;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class MainWindowController {
    //Wstrzykiwanie kontrolek
    //Szkielet
    @FXML private SplitPane mainSplitPane;
    @FXML private AnchorPane mainSplitPaneLeftAnchorPane;
    @FXML private AnchorPane mainSplitPaneRightAnchorPane;
    @FXML private SplitPane menuSplitPane;
    @FXML private AnchorPane menuSplitPaneUpperAnchorPane;
    @FXML private AnchorPane menuSplitPaneLowerAnchorPane;

    //Menu
    @FXML private TextField numberOfStructuresTextField;
    @FXML private ChoiceBox singleStructureSizeChoiceBox;
    @FXML private Button insertButton;

    //Zakladka basic
    @FXML private TextArea basicUsedMemoryBeforeOperationTextArea;
    @FXML private TextArea basicUsedMemoryAfterOperationTextArea;
    @FXML private TextArea basicDifferenceBetweenMemoriesTextArea;
    @FXML private TextArea basicInsertedDataToMemoryTextArea;
    @FXML private TextArea basicTotalSizeOfInsertedDataTextArea;

    //Zakladka universal
    @FXML private TextArea universalUsedMemoryBeforeOperationTextArea;
    @FXML private TextArea universalUsedMemoryAfterOperationTextArea;
    @FXML private TextArea universalDifferenceBetweenMemoriesTextArea;
    @FXML private TextArea universalSizeOfInsertedDataToMemory;
    @FXML private TextArea universalTotalSizeOfInsertedDataTextArea;
    @FXML private TextArea universalDifferenceBetweenDifferenceOfMemoriesAndSizeOfInsertedDataTextArea;
    @FXML private TextArea universalUsedMemoryToInitialRatioTextArea;
    @FXML private TextArea universalUsedMemoryToCommittedRatioTextArea;
    @FXML private TextArea universalUsedMemoryToMaxRatioTextArea;
    @FXML private TextArea universalTotalSizeOfInsertedDataToInitialMemoryRatioTextArea;
    @FXML private TextArea universalTotalSizeOfInsertedDataToCommittedMemoryRatioTextArea;
    @FXML private TextArea universalTotalSizeOfInsertedDataToMaxMemoryRatioTextArea;

    //Zakladka overheat
    @FXML private TextArea overheatUsedMemoryToCommittedRatioTextArea;
    @FXML private TextArea overheatUsedMemoryToMaxRatioTextArea;

    //Zakladka pure
    @FXML private TextArea pureInitialMemoryTextArea;
    @FXML private TextArea pureUsedMemoryTextArea;
    @FXML private TextArea pureCommittedMemoryTextArea;
    @FXML private TextArea pureMaxMemoryTextArea;

    //Zakladka text
    @FXML private TextArea commonTextArea;

    //Komponenty JMX
    MBeanServer mBeanServer;
    MemoryMXBean memoryMXBean;
    MemoryUsage memoryUsage;

    //Watki
    Thread viewer;  //Do odswiezania rubryk
    Thread saver;   //Do zapisu danych do pliku .csv oraz rysowania wykresu

    //Wykres
    @FXML private LineChart<?, ?> memoryUsageLineChart;
    XYChart.Series memoryUsageSeries;   //Zbior danych dla wykresu uzywanej pamieci
    XYChart.Series committedMemorySeries;   //Zbior danych dla wykresu mozliwej do wykorzystania pamieci

    //Stale
    private final Integer KILO_FACTOR = 1024;
    private final Integer MEGA_FACTOR = KILO_FACTOR * KILO_FACTOR;
    private final Integer PERIOD_BETWEEN_REFRESHING = 10000;    //Czas, co ktory nastepuje odswiezenie rubryk
    private final Integer VIEW_CLEAR_BREAK = 500;               //Przerwa miedzy wyczyszczeniem rubryki a pojawieniem sie nowych danych
    private final Integer PERIOD_BETWEEN_SAVING = 5000;         //Czas, co ktory nastepuje zapis do pliku oraz odswiezenie wykresu
    private final Path PATH_TO_FILE = Paths.get("MemoryDump.csv");  //Sciezka do pliku .csv, w ktorym zapisywanie sa cyklicznie dane

    //Elementy zwiazane z pamiecia
    List<MemoryFiller> memoryHeaterList; //Lista, ktora sluzy do obserwacji powiekszajacego sie obszaru pamieci uzywanej
    MemoryInformationKeeper memoryInformationKeeper;    //Snapshot ostatniego wrzucenia danych do pamieci

    //Bez argumentowy konstruktor (koniecznie)
    public MainWindowController(){

    }

    @FXML
    public void initialize(){
        mainSplitPaneLeftAnchorPane.maxWidthProperty().bind(mainSplitPane.widthProperty().multiply(0.75));  //Blokowanie rozciagania split paneow
        mainSplitPaneLeftAnchorPane.minWidthProperty().bind(mainSplitPane.widthProperty().multiply(0.75));
        mainSplitPaneRightAnchorPane.maxWidthProperty().bind(mainSplitPane.widthProperty().multiply(0.25));
        mainSplitPaneRightAnchorPane.minWidthProperty().bind(mainSplitPane.widthProperty().multiply(0.25));
        menuSplitPaneUpperAnchorPane.maxHeightProperty().bind(menuSplitPane.heightProperty().multiply(0.85));
        menuSplitPaneUpperAnchorPane.minHeightProperty().bind(menuSplitPane.heightProperty().multiply(0.85));
        menuSplitPaneLowerAnchorPane.maxHeightProperty().bind(menuSplitPane.heightProperty().multiply(0.15));
        menuSplitPaneLowerAnchorPane.minHeightProperty().bind(menuSplitPane.heightProperty().multiply(0.15));
        singleStructureSizeChoiceBox.setItems(FXCollections.observableArrayList("1", "2", "4", "8"));   //Ustawienie mozliwych rozmiarow pojedynczej struktury (w kilobajtach)

        mBeanServer = ManagementFactory.getPlatformMBeanServer();   //Jesli serwer nie jest utworzony to ManagementFactory.createMBeanServer() wystartuje automatycznie
        memoryMXBean = ManagementFactory.getMemoryMXBean(); //Pobranie predefiniowanego MBeana (MXBean) dotyczacego pamieci
        memoryHeaterList = new LinkedList();
        memoryInformationKeeper = new MemoryInformationKeeper();

        Runnable viewingJob = () -> {   //Zadanie odswiezajace rubryki
            while(true) {
                try {
                    if(memoryInformationKeeper.getUsedMemoryAfterOperation() != null){
                        Thread.sleep(PERIOD_BETWEEN_REFRESHING);
                        clearTabs();
                        Thread.sleep(VIEW_CLEAR_BREAK);
                        updateTabs();
                    }
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException in viewingJob.");
                }
            }
        };

        viewer = new Thread(viewingJob);
        viewer.start();

        Runnable savingJob = () -> {    //Zadanie zapisujace dane do pliku CSV oraz rysujace wykres
            Integer controlPoint = 0;    //Jest, to zmienna "x" na wykresie; oznacza ona punkt kontrolny4
            while (true) {
                try {
                    if (memoryInformationKeeper.getUsedMemoryAfterOperation() != null) {
                        Thread.sleep(PERIOD_BETWEEN_SAVING);    //Zapis do pliku ma sie odbywac co PERIOD_BETWEEN_SAVING
                        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(PATH_TO_FILE.toString(), true))) {
                            Long usedMemoryAfterOperationInMb = memoryInformationKeeper.getUsedMemoryAfterOperation();
                            usedMemoryAfterOperationInMb /= (MEGA_FACTOR);
                            Long committedMemoryInMb = memoryInformationKeeper.getCommittedMemory();
                            committedMemoryInMb /= (MEGA_FACTOR);
                            bufferedWriter.append(usedMemoryAfterOperationInMb.toString() + "," + committedMemoryInMb.toString());  //Wpisanie do pliku CSV uzywanej pamieci oraz mozliwej do uzycia pamieci
                            bufferedWriter.newLine();
                            final Integer x = controlPoint;   //Konieczne przypisanie do zmiennej typu final z powodu lambdy
                            final Long y = usedMemoryAfterOperationInMb;
                            final Long y2 = committedMemoryInMb;
                            controlPoint++;
                            Platform.runLater(() -> {
                                memoryUsageSeries.getData().add(new XYChart.Data(x,y)); //Dodawanie nowych wspolrzednych do wykresu uzywanej pamieci
                                committedMemorySeries.getData().add(new XYChart.Data(x, y2));});    //Dodawanie nowych wspolrzednych do wykresu mozliwej do uzycia pamieci
                        } catch (IOException e) {
                            System.out.println("IO Exception in savingJob.");
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException in savingJob.");
                    e.printStackTrace();
                }
            }
        };

        saver = new Thread(savingJob);
        saver.start();

        memoryUsageSeries = new XYChart.Series();   //Zbior danych dla wykresu zuzytej pamieci
        memoryUsageSeries.setName("Usage of memory");  //Ustawienie nazwy dla zbioru danych
        committedMemorySeries = new XYChart.Series();   //Zbior danych dla wykresu mozliwej do uzycia pamieci
        committedMemorySeries.setName("Committed memory");
        memoryUsageLineChart.getData().addAll(memoryUsageSeries, committedMemorySeries);    //Dodanie zbiorow danych do wykresu
    }

    //Metoda do uruchomienia z Maina, wykona sie po nacisnieciu krzyzka ('X')
    @FXML
    public void shutdown(){
        if(viewer != null && viewer.isAlive() == true){
            viewer.interrupt();
        }
        if(saver != null && saver.isAlive() == true){
            saver.interrupt();
        }
        Platform.exit();
        System.exit(0);
    }

    //Metoda do wprowadzania zadanej liczby danych do pamieci
    @FXML
    public void insertToMemory(){
        if(numberOfStructuresTextField == null || singleStructureSizeChoiceBox.getValue() == null ||
                Integer.valueOf(numberOfStructuresTextField.getText().toString()) <= 0){  //Jesli nie podano danych, to nic nie wprowadzaj
            return;
        }

        memoryUsage = memoryMXBean.getHeapMemoryUsage();   //Pobierz informacje o pamieci przed dodaniem nowego zestawu danych
        Long memoryBeforeOperation = memoryUsage.getUsed();    //Wykorzystywana pamiec przed operacja
        Integer numberFromChoiceBox = (Integer.valueOf(singleStructureSizeChoiceBox.getValue().toString()));    //Odczytanie zaznaczonej wielkosci pojedynczej struktury
        Integer numberOfStructures = Integer.valueOf(numberOfStructuresTextField.getText().toString()); //Liczba struktur do wprowadzenia do pamieci
        Integer numberOfElementsToInsert = (numberOfStructures * numberFromChoiceBox * KILO_FACTOR) / MemoryFiller.SINGLE_ELEMENT_SIZE;
        memoryHeaterList.add(new MemoryFiller(numberOfElementsToInsert));   //Dodawanie nowych struktur do listy

        Long memoryAfterOperation = 0L;
        long difference = 0;
        while(difference == 0){ //Poniewaz getHeapMemoryUsage() czasami poda poprzednie dane, musimy wywolywac ta metode do skutku
            memoryUsage = memoryMXBean.getHeapMemoryUsage();
            memoryAfterOperation = memoryUsage.getUsed();
            difference = memoryAfterOperation - memoryBeforeOperation;
        }

        memoryInformationKeeper.update(memoryBeforeOperation, memoryUsage, numberOfElementsToInsert * MemoryFiller.SINGLE_ELEMENT_SIZE); //Nowy snapshot umieszczany jest w obiekcie
    }

    //Metoda do czyszczenia rubryk
    public void  clearTabs(){
        basicUsedMemoryBeforeOperationTextArea.setText("");
        basicUsedMemoryAfterOperationTextArea.setText("");
        basicDifferenceBetweenMemoriesTextArea.setText("");
        basicInsertedDataToMemoryTextArea.setText("");
        basicTotalSizeOfInsertedDataTextArea.setText("");

        universalUsedMemoryBeforeOperationTextArea.setText("");
        universalUsedMemoryAfterOperationTextArea.setText("");
        universalDifferenceBetweenMemoriesTextArea.setText("");
        universalSizeOfInsertedDataToMemory.setText("");
        universalTotalSizeOfInsertedDataTextArea.setText("");
        universalDifferenceBetweenDifferenceOfMemoriesAndSizeOfInsertedDataTextArea.setText("");
        universalUsedMemoryToInitialRatioTextArea.setText("");
        universalUsedMemoryToCommittedRatioTextArea.setText("");
        universalUsedMemoryToMaxRatioTextArea.setText("");
        universalTotalSizeOfInsertedDataToInitialMemoryRatioTextArea.setText("");
        universalTotalSizeOfInsertedDataToCommittedMemoryRatioTextArea.setText("");
        universalTotalSizeOfInsertedDataToMaxMemoryRatioTextArea.setText("");

        overheatUsedMemoryToCommittedRatioTextArea.setText("");
        overheatUsedMemoryToMaxRatioTextArea.setText("");

        pureInitialMemoryTextArea.setText("");
        pureUsedMemoryTextArea.setText("");
        pureCommittedMemoryTextArea.setText("");
        pureMaxMemoryTextArea.setText("");

        commonTextArea.setText("");
    }

    //Metoda do aktualizacji rubryk o odswiezone dane o pamieci
    public void updateTabs(){
        MemoryInformationKeeper copyOfMemoryInformationKeeper = memoryInformationKeeper.copyMe();
        basicUsedMemoryBeforeOperationTextArea.setText(copyOfMemoryInformationKeeper.getUsedMemoryBeforeOperation().toString() + " bytes");
        basicUsedMemoryAfterOperationTextArea.setText(copyOfMemoryInformationKeeper.getUsedMemoryAfterOperation().toString() + " bytes");
        basicDifferenceBetweenMemoriesTextArea.setText(copyOfMemoryInformationKeeper.getDifferenceBetweenMemories().toString() + " bytes");
        basicInsertedDataToMemoryTextArea.setText(copyOfMemoryInformationKeeper.getSizeOfInsertedDataToMemory().toString() + " bytes");
        basicTotalSizeOfInsertedDataTextArea.setText(copyOfMemoryInformationKeeper.getTotalSizeOfInsertedDataToMemory().toString() + " bytes");

        universalUsedMemoryBeforeOperationTextArea.setText(copyOfMemoryInformationKeeper.getUsedMemoryBeforeOperation().toString() + " bytes");
        universalUsedMemoryAfterOperationTextArea.setText(copyOfMemoryInformationKeeper.getUsedMemoryAfterOperation().toString() + " bytes");
        universalDifferenceBetweenMemoriesTextArea.setText(copyOfMemoryInformationKeeper.getDifferenceBetweenMemories().toString() + " bytes");
        universalSizeOfInsertedDataToMemory.setText(copyOfMemoryInformationKeeper.getSizeOfInsertedDataToMemory().toString() + " bytes");
        universalTotalSizeOfInsertedDataTextArea.setText(copyOfMemoryInformationKeeper.getTotalSizeOfInsertedDataToMemory().toString() + " bytes");
        universalDifferenceBetweenDifferenceOfMemoriesAndSizeOfInsertedDataTextArea.setText(copyOfMemoryInformationKeeper.getDifferenceBetweenDifferenceOfMemoriesAndSizeOfInsertedData().toString() + " bytes");
        universalUsedMemoryToInitialRatioTextArea.setText(copyOfMemoryInformationKeeper.getUsedMemoryToInitialRatio().toString() + " %");
        universalUsedMemoryToCommittedRatioTextArea.setText(copyOfMemoryInformationKeeper.getUsedMemoryToCommittedRatio().toString() + " %");
        universalUsedMemoryToMaxRatioTextArea.setText(copyOfMemoryInformationKeeper.getUsedMemoryToMaxRatio().toString()  + " %");
        universalTotalSizeOfInsertedDataToInitialMemoryRatioTextArea.setText(copyOfMemoryInformationKeeper.getTotalSizeOfInsertedDataToInitialMemoryRatio().toString()  + " %");
        universalTotalSizeOfInsertedDataToCommittedMemoryRatioTextArea.setText(copyOfMemoryInformationKeeper.getTotalSizeOfInsertedDataToCommittedMemoryRatio().toString()  + " %");
        universalTotalSizeOfInsertedDataToMaxMemoryRatioTextArea.setText(copyOfMemoryInformationKeeper.getTotalSizeOfInsertedDataToMaxMemoryRatio().toString() + " %");

        overheatUsedMemoryToCommittedRatioTextArea.setText(copyOfMemoryInformationKeeper.getUsedMemoryToCommittedRatio().toString() + " %");
        overheatUsedMemoryToMaxRatioTextArea.setText(copyOfMemoryInformationKeeper.getUsedMemoryToMaxRatio().toString() + " %");

        pureInitialMemoryTextArea.setText(copyOfMemoryInformationKeeper.getInitialMemory().toString() + " bytes");
        pureUsedMemoryTextArea.setText(copyOfMemoryInformationKeeper.getUsedMemoryAfterOperation().toString() + " bytes");
        pureCommittedMemoryTextArea.setText(copyOfMemoryInformationKeeper.getCommittedMemory().toString() + " bytes");
        pureMaxMemoryTextArea.setText(copyOfMemoryInformationKeeper.getMaxMemory().toString() + " bytes");

        commonTextArea.setText(copyOfMemoryInformationKeeper.toString());

    }
}

