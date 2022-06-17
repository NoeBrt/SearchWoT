package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Concept {
	private String name;
	private final StringProperty levelPrivacy = new SimpleStringProperty();

	public Concept(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StringProperty getlevelPrivacy() {
		return levelPrivacy;
	}

	public String getlevelPrivacyValue() {
		return levelPrivacy.get();
	}

	public void setlevelPrivacy(String value) {
		levelPrivacy.set(value);
	}

	@Override
	public String toString() {
		return "Concept [name=" + name + ", levelPrivacy=" + levelPrivacy.get() + "]";
	}

}
