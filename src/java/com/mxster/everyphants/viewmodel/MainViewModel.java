// package com.yourproject.viewmodel;

// import com.yourproject.model.data.Query;
// import com.yourproject.model.data.Result;
// import com.yourproject.model.plugin.PluginManager;
// import javafx.beans.property.ObjectProperty;
// import javafx.beans.property.SimpleObjectProperty;
// import javafx.beans.property.SimpleStringProperty;
// import javafx.beans.property.StringProperty;
// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;

// public class MainViewModel {
// private final StringProperty queryText = new SimpleStringProperty("");
// private final ObservableList<Result> results =
// FXCollections.observableArrayList();
// private final ObjectProperty<Result> selectedResult = new
// SimpleObjectProperty<>();

// private final PluginManager pluginManager = PluginManager.getInstance();

// public MainViewModel() {
// queryText.addListener((obs, old, query) -> search());
// }

// private void search() {
// Query q = new Query(queryText.get());
// var allResults = pluginManager.queryAll(q);
// results.setAll(allResults);
// }

// public void executeSelected() {
// Result result = selectedResult.get();
// if (result != null) {
// result.getAction().execute(); // ICommand 的 execute
// }
// }

// // ----- getters and property getters -----
// public StringProperty queryTextProperty() {
// return queryText;
// }

// public ObservableList<Result> resultsProperty() {
// return results;
// }

// public ObjectProperty<Result> selectedResultProperty() {
// return selectedResult;
// }

// public void setSelectedResult(Result r) {
// selectedResult.set(r);
// }
// }