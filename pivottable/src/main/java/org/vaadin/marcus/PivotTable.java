package org.vaadin.marcus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.vaadin.marcus.client.PivotTableState;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.server.JsonCodec;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.JavaScriptFunction;

import elemental.json.JsonArray;
import elemental.json.JsonObject;

@JavaScript({ "jquery-1.8.3.min.js", "jquery-ui-1.9.2.custom.min.js",
		// "jquery.csv-0.71.min.js",
		"pivot.js", "pivot.de.js",
		// "d3.v3.min.js",
		// "c3.min.js",
		"pivot-table-connector.js" })
@StyleSheet({ "pivot.css", })
public class PivotTable extends AbstractJavaScriptComponent {

	public static final class UIChangeImpl implements UiChange {
		public String[] rows;
		public String[] cols;
		public String aggregatorName;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.vaadin.marcus.UiChange#getRows()
		 */
		@Override
		public String[] getRows() {
			return rows;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.vaadin.marcus.UiChange#getCols()
		 */
		@Override
		public String[] getCols() {
			return cols;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.vaadin.marcus.UiChange#getAggregatorName()
		 */
		@Override
		public String getAggregatorName() {
			return aggregatorName;
		}

		@Override
		public String toString() {
			return "Change [rows=" + Arrays.toString(rows) + ", cols=" + Arrays.toString(cols) + ", aggregatorName="
					+ aggregatorName + "]";
		}
	}

	public static final class UIChangeEvent {
		public final PivotTable pt;
		public final UiChange uiChange;

		UIChangeEvent(PivotTable pt, UiChange uiChange) {
			this.pt = pt;
			this.uiChange = uiChange;
		}
	}

	public interface UiChangeListener {
		void uiChange(UIChangeEvent e);
	}

	@Override
	public void setParent(HasComponents parent) {
		super.setParent(parent);
		setLocale(getState());
	}

	private final ArrayList<UiChangeListener> uicListeners = new ArrayList<UiChangeListener>();

	public void addUiChangeListener(UiChangeListener listener) {
		uicListeners.add(listener);
	}

	// public void setData(String csv) {
	// getState().csv = csv;
	// }
	public PivotTable() {
		// TODO Auto-generated constructor stub
		setLocale(getState());
		setStyleName("pivot-table");

		this.addFunction("onUiOptionChange", new JavaScriptFunction() {
			@Override
			public void call(JsonArray arguments) {
				// System.err.println(PivotTable.toString(arguments));
				JsonObject o = arguments.getObject(0);
				UIChangeImpl c = (UIChangeImpl) JsonCodec.decodeCustomType(UIChangeImpl.class, o, null);
				for (UiChangeListener uicListener : uicListeners) {
					uicListener.uiChange(new UIChangeEvent(PivotTable.this, c));
				}
				System.err.println(c);
			}
		});
	}

	public void setContainerDataSource(Container container) {
		Collection<?> propertyIds = container.getContainerPropertyIds();
		Collection<?> itemIds = container.getItemIds();

		Collection<Object[]> valuesList = new ArrayList<Object[]>(itemIds.size() * (itemIds.size() + 1));
		valuesList.add(propertyIds.toArray());
		System.err.println(itemIds.size());
		for (Object itemId : itemIds) {
			valuesList.add(getArray(container.getItem(itemId), propertyIds));
		}
		PivotTableState state = getState();
		state.values = valuesList.toArray(new Object[itemIds.size()][]);
		setLocale(state);
	}

	private Object[] getArray(Item item, Collection<?> propertyIds) {
		assert item != null;
		Object[] values = new Object[propertyIds.size()];
		int index = 0;
		for (Object propertyId : propertyIds) {
			@SuppressWarnings("rawtypes")
			Property itemProperty = item.getItemProperty(propertyId);
			assert itemProperty != null;
			values[index++] = itemProperty.getValue();
		}
		return values;
	}

	public void setData(List<String> rows, List<String> cols) {
		PivotTableState state = getState();
		state.rows = rows;
		state.cols = cols;
		setLocale(state);
	}

	private void setLocale(PivotTableState state) {
		Locale locale = this.getLocale();
		state.locale = (locale != null ? locale : Locale.ENGLISH).getLanguage();
		System.err.println("settings locale to : " + state.locale);
	}

	@Override
	protected PivotTableState getState(boolean markAsDirty) {
		return (PivotTableState) super.getState(markAsDirty);
	}

	@Override
	protected PivotTableState getState() {
		return (PivotTableState) super.getState();
	}
}
