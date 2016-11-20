package org.vaadin.marcus.demo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.vaadin.marcus.PivotTable;
import org.vaadin.marcus.PivotTable.UIChangeEvent;
import org.vaadin.marcus.PivotTable.UiChangeListener;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import au.com.bytecode.opencsv.CSVReader;

@Theme("demotheme")
@Title("PivotTable Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

	// private ByteArrayOutputStream tempCSV;
	private PivotTable pivotTable;
	private Grid t;

	@Override
	protected void init(VaadinRequest request) {

		final VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		setContent(layout);
		setLocale(Locale.GERMANY);

		// Upload upload = new Upload("Upload CSV", new Upload.Receiver() {
		// @Override
		// public OutputStream receiveUpload(String name, String mime) {
		// return tempCSV = new ByteArrayOutputStream();
		// }
		// });
		// upload.setImmediate(true);

		// upload.addFinishedListener(new Upload.FinishedListener() {
		// @Override
		// public void uploadFinished(Upload.FinishedEvent finishedEvent) {
		// pivotTable.setContainerDataSource(buildContainerFromCSV());
		// }
		//
		// });
		t = new Grid();

		pivotTable = new PivotTable();
		pivotTable.addUiChangeListener(new UiChangeListener() {
			@Override
			public void uiChange(UIChangeEvent e) {
				System.err.println(e.uiChange);
			}
		});
		// doExample();
		doRandom();

		Button randomButton = new Button("random");
		randomButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				doRandom();
			}

		});

		// layout.addComponents(randomButton, new
		// HorizontalSplitPanel(pivotTable, t));
		layout.addComponents(randomButton, pivotTable);

	}

	private void doRandom() {
		Indexed src = getRandom(50000);
		t.setContainerDataSource(src);
		pivotTable.setContainerDataSource(src);
		pivotTable.setData(Arrays.asList("Bereich", "Herkunft", "Mitarbeiter"),
				Arrays.asList("Jahr_", "Quartal_", "Monat_"));
	}

	@SuppressWarnings("unused")
	private void doExample() {
		IndexedContainer src = buildContainerFromCSV();
		t.setContainerDataSource(src);
		pivotTable.setContainerDataSource(src);
		pivotTable.setData(Arrays.asList("Product", "Sub-product"), Arrays.asList("State"/* , "ZIP code" */));
	}

	enum Herkunft {
		Plan, Manuell
	}

	enum Type_ {
		Typ1(0), T2(-0.2), T3(0.2), T4(-0.4), andererTyp(0.4);

		private double d;

		Type_(double d) {
			this.d = d;
		}
	}

	static Random r = new Random();

	public static <T> T getRandom(T... values) {
		return values[r.nextInt(values.length)];
	}

	public static String[] bereiche = new String[] { "Haus1/Wb1", "Haus Sonnenschein", "Haus Abendfrieden",
			"Haus Morgenröthe", "Margarethe" };
	public static String[] mitarbeiter = new String[] { "Weisz; Dieter", "Nussbaum; Ralph", "Austerlitz; Barbara",
			"Kunze; Eric", "Fuhrmann; Mandy", "Neudorf; Julia", "Wexler; Ulrich", "Beich; Patrick", "Sommer; Niklas",
			"Hoch; Jörg"/*
						 * , "Drescher; Mario", "Fink; Tim", "Bosch; Karin",
						 * "Beyer; Melanie", "Kaufmann; Sabrina",
						 * "Schulz; Klaus", "Schwarz; Sophie", "Abend; Steffen",
						 * "Rothstein; Philipp", "Fenstermacher; Lisa",
						 * "Nacht; Barbara", "Huber; Philipp",
						 * "Goldschmidt; Stefan", "Schroeder; Tanja",
						 * "Ebersbach; Andreas", "Holtzmann; Felix",
						 * "Eisenhower; Max", "Nussbaum; Franziska",
						 * "Waechter; Wolfgang", "Meyer; Leon", "Neudorf; Leon",
						 * "Hoover; Christian", "Seiler; Nicole",
						 * "Rothschild; Jan", "Neustadt; Jörg", "Wolf; Dieter",
						 * "Beyer; Luca", "Abend; Jonas", "Scherer; Brigitte",
						 * "Kappel; Sabrina", "Becker; Andreas",
						 * "Meister; Claudia", "Weissmuller; Robert",
						 * "Schweitzer; Antje", "Becker; Torsten",
						 * "Sommer; Tom", "Strauss; Thorsten",
						 * "Gottlieb; Robert", "Aachen; Lena", "Luft; Dennis"
						 */ };

	@SuppressWarnings({ "unchecked", "deprecation" })
	static Indexed getRandom(int cnt) {
		IndexedContainer indexedContainer = new IndexedContainer();
		indexedContainer.addContainerProperty("Bereich", String.class, null);
		indexedContainer.addContainerProperty("Herkunft", Herkunft.class, null);
		indexedContainer.addContainerProperty("Typ", Type_.class, null);
		indexedContainer.addContainerProperty("Mitarbeiter", String.class, null);
//		indexedContainer.addContainerProperty("Jahr", Number.class, null);
//		indexedContainer.addContainerProperty("Quartal", String.class, null);
//		indexedContainer.addContainerProperty("Monat", String.class, null);
		indexedContainer.addContainerProperty("Stunden", Number.class, null);
		indexedContainer.addContainerProperty("Datum", Date.class, null);

		final long date2015_01_01 = new Date(2015-1900, 0, 1).getTime();
		
		for (int i = 0; i < cnt; i++) {
			Item item = indexedContainer.getItem(indexedContainer.addItem());
			{
				item.getItemProperty("Bereich").setValue(getRandom(bereiche));
				item.getItemProperty("Herkunft").setValue(getRandom(Herkunft.values()));
				Type_ typ = getRandom(Type_.values());
				item.getItemProperty("Typ").setValue(typ);
				item.getItemProperty("Mitarbeiter").setValue(getRandom(mitarbeiter));
//				int month = r.nextInt(12);
//				int jahr = r.nextInt(3) + 2011;
//				item.getItemProperty("Jahr").setValue(jahr);
//				item.getItemProperty("Quartal").setValue("Q" + ((month / 3) + 1) + "/" + jahr);
//				item.getItemProperty("Monat").setValue((month + 1) + "/" + jahr);
				item.getItemProperty("Stunden").setValue((r.nextDouble() - 0.5 - typ.d) * 120);

				item.getItemProperty("Datum").setValue(new Date(date2015_01_01 + r.nextInt(500) * 86400000L));
			}
		}
		return indexedContainer;
	}

	private IndexedContainer buildContainerFromCSV() {
		return buildContainerFromCSV(new InputStreamReader(getClass().getResourceAsStream("/Consumer_Complaints.csv")));
	}

	protected static IndexedContainer buildContainerFromCSV(Reader reader) {
		IndexedContainer container = new IndexedContainer();
		CSVReader csvReader = new CSVReader(reader, ',');
		String[] columnHeaders = null;
		String[] record;
		try {
			while ((record = csvReader.readNext()) != null) {
				if (columnHeaders == null) {
					columnHeaders = record;
					addItemProperties(container, columnHeaders);
				} else {
					addItem(container, columnHeaders, record);
				}
			}
		} catch (IOException e) {
			assert false;
		}
		return container;
	}

	/**
	 * Set's up the item property ids for the container. Each is a String (of
	 * course, you can create whatever data type you like, but I guess you need
	 * to parse the whole file to work it out)
	 *
	 * @param container
	 *            The container to set
	 * @param columnHeaders
	 *            The column headers, i.e. the first row from the CSV file
	 */
	private static void addItemProperties(IndexedContainer container, String[] columnHeaders) {
		for (String propertyName : columnHeaders) {
			container.addContainerProperty(propertyName, String.class, null);
		}
	}

	/**
	 * Adds an item to the given container, assuming each field maps to it's
	 * corresponding property id. Again, note that I am assuming that the field
	 * is a string.
	 *
	 * @param container
	 * @param propertyIds
	 * @param fields
	 */
	@SuppressWarnings("unchecked")
	private static void addItem(IndexedContainer container, String[] propertyIds, String[] fields) {
		if (propertyIds.length != fields.length) {
			throw new IllegalArgumentException("Different number of columns to fields in the record");
		}
		Object itemId = container.addItem();
		Item item = container.getItem(itemId);
		for (int i = 0; i < fields.length; i++) {
			String propertyId = propertyIds[i];
			String field = fields[i];
			item.getItemProperty(propertyId).setValue(field);
		}
	}
}
