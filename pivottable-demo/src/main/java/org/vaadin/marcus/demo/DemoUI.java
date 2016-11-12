package org.vaadin.marcus.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import org.vaadin.marcus.PivotTable;
import org.vaadin.marcus.PivotTable.UIChangeEvent;
import org.vaadin.marcus.PivotTable.UiChangeListener;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import au.com.bytecode.opencsv.CSVReader;

@Theme("valo")
@Title("PivotTable Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

	private ByteArrayOutputStream tempCSV;
	private PivotTable pivotTable;

	@Override
	protected void init(VaadinRequest request) {

		final VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		setContent(layout);

		Upload upload = new Upload("Upload CSV", new Upload.Receiver() {
			@Override
			public OutputStream receiveUpload(String name, String mime) {
				return tempCSV = new ByteArrayOutputStream();
			}
		});
		upload.setImmediate(true);

		upload.addFinishedListener(new Upload.FinishedListener() {
			@Override
			public void uploadFinished(Upload.FinishedEvent finishedEvent) {
				pivotTable.setContainerDataSource(buildContainerFromCSV());
			}

		});

		pivotTable = new PivotTable();
		pivotTable.addUiChangeListener(new UiChangeListener() {
			@Override
			public void uiChange(UIChangeEvent e) {
				System.err.println(e.uiChange);
			}
		});

		layout.addComponents(upload, pivotTable);

	}

	private IndexedContainer buildContainerFromCSV() {
		return buildContainerFromCSV(new StringReader(tempCSV.toString()));
	}

	protected static IndexedContainer buildContainerFromCSV(StringReader reader) {
		IndexedContainer container = new IndexedContainer();
		CSVReader csvReader = new CSVReader(reader, ';');
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
			throw new IllegalArgumentException("Hmmm - Different number of columns to fields in the record");
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
