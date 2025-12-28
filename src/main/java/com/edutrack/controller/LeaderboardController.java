package com.edutrack.controller;

import java.io.IOException;
import java.util.List;

import com.edutrack.Main;
import com.edutrack.dao.SessionDAO;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class LeaderboardController {

    @FXML
    private TableView<LeaderboardEntry> leaderboardTable;
    @FXML
    private TableColumn<LeaderboardEntry, Integer> rankColumn;
    @FXML
    private TableColumn<LeaderboardEntry, String> userColumn;
    @FXML
    private TableColumn<LeaderboardEntry, Integer> timeColumn;

    private final SessionDAO sessionDAO = new SessionDAO();

    public void initialize() {
        rankColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().rank).asObject());
        userColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().username));
        timeColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().totalTime).asObject());

        loadData();
    }

    private void loadData() {
        List<Object[]> data = sessionDAO.getLeaderboardData();
        ObservableList<LeaderboardEntry> entries = FXCollections.observableArrayList();

        int rank = 1;
        for (Object[] row : data) {
            entries.add(new LeaderboardEntry(rank++, (String) row[0], (Integer) row[1]));
        }

        leaderboardTable.setItems(entries);
    }

    @FXML
    private void handleBack() throws IOException {
        Main.setRoot("Dashboard");
    }

    public static class LeaderboardEntry {
        private final int rank;
        private final String username;
        private final int totalTime;

        public LeaderboardEntry(int rank, String username, int totalTime) {
            this.rank = rank;
            this.username = username;
            this.totalTime = totalTime;
        }
    }
}
