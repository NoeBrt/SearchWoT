package controller;

import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.ComboBoxTableCell;

public class Concept {
private String name;
private ComboBox levelPrivacy;
public Concept(String name, ComboBox levelPrivacy) {
	super();
	this.name = name;
	this.levelPrivacy = levelPrivacy;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public ComboBox getLevelPrivacy() {
	return levelPrivacy;
}
public void setLevelPrivacy(ComboBox levelPrivacy) {
	this.levelPrivacy = levelPrivacy;
}

}
