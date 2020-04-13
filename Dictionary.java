package com.company;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class Dictionary extends Application {
    private static final String PRIMARY_COLOR = "#191970";
    private static final String DICT_FILE = Dictionary.class.getResource("data.txt").toString().replace("file:/", "");

    private BorderPane rootPane;

    private Label topLabel;

    private TextField searchTextField;
    private ListView<String> wordsListView;

    private Label wordLabel;
    private Label definitionLabel;
    // data
    private ArrayList<String> wordList;
    private HashMap<String, String> dictMap;

    @Override
    public void start(Stage stage) {
        loadDict(DICT_FILE);
        System.out.println(wordList.size() + " words found");
        initLayout(stage);
    }
    public void loadDict(String dir) {
        dictMap = new HashMap<>();
        wordList = new ArrayList<>();
        File file = new File(dir);
        BufferedReader input;
        try {
            input = new BufferedReader(new FileReader(file));
            String line;
            while ((line = input.readLine()) != null) {
                String key = input.readLine().strip();
                String value = input.readLine().strip();
                input.readLine(); // skips newline
                if (!dictMap.containsKey(key)) {
                    wordList.add(key);
                    dictMap.put(key, value);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void initLayout(Stage stage) {
        stage.setTitle("English dictionary");
        stage.getIcons().add(new Image(Dictionary.class.getResourceAsStream("icon.png")));

        rootPane = new BorderPane();
        // top part settings
        VBox topPart = new VBox();
        topLabel = new Label("English Dictionary");
        topLabel.setStyle(String.format("-fx-font-family: 'Roboto Thin'; -fx-font-size: 30px; -fx-background-color: %s;", PRIMARY_COLOR));
        topLabel.setPadding(new Insets(10, 10, 10, 10));
        topLabel.setTextFill(Paint.valueOf("white"));
        topLabel.minWidthProperty().bind(stage.widthProperty());
        topLabel.setAlignment(Pos.CENTER);
        topPart.getChildren().addAll(topLabel);
        rootPane.setTop(topPart);

        // left part settings
        searchTextField = new TextField();
        searchTextField.setPromptText("Search..");
        searchTextField.setOnKeyTyped(event -> handleSearch());
        ObservableList<String> words = FXCollections.observableArrayList(wordList);
        wordsListView = new ListView<>(words);
        wordsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            wordLabel.setText(newValue);
            definitionLabel.setText(dictMap.get(newValue));
        });
        VBox leftCol = new VBox();
        leftCol.heightProperty().add(rootPane.heightProperty());

        leftCol.setMinWidth(200);
        leftCol.getChildren().addAll(searchTextField, wordsListView);
        rootPane.setLeft(leftCol);

        // center part settings
        VBox centerCol = new VBox();
        centerCol.setPadding(new Insets(20, 20, 20, 20));
        wordLabel = new Label(wordList.get(0));
        wordLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold");
        definitionLabel = new Label(dictMap.get(wordList.get(0)));
        definitionLabel.setStyle("-fx-font-size: 15px");
        definitionLabel.setWrapText(true);
        centerCol.getChildren().addAll(wordLabel, definitionLabel);
        rootPane.setCenter(centerCol);
        stage.setResizable(false);
        stage.setScene(new Scene(rootPane, 600, 500));
        stage.show();
    }
    public void handleSearch() {
        String query = searchTextField.getText().strip().toLowerCase();
        if(query.length() == 0) {
            ObservableList<String> words = FXCollections.observableArrayList(wordList);
            wordsListView.setItems(words);
        } else {
            List<String> foundWords = new ArrayList<>();
            for (String word : wordList) {
                if(word.startsWith(query)) foundWords.add(word);
            }
            ObservableList<String> words = FXCollections.observableArrayList(foundWords);
            wordsListView.setItems(words);
            if(foundWords.size() == 0) {
                wordLabel.setText("No matching word for " + query);
                definitionLabel.setText("");
            } else {
                wordLabel.setText(foundWords.get(0));
                definitionLabel.setText(dictMap.get(foundWords.get(0)));
            }
        }
    }
    public String addNewLines(String line) {
        String format = "";
        for (int i = 0; i < line.length(); i++) {
            char sym = line.charAt(i);
            if(Character.isDigit(sym)) format += "\n";
            format += sym;
        }
        return format;
    }
}
