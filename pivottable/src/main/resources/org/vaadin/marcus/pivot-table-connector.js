'use strict';
window.org_vaadin_marcus_PivotTable = function() {

	var table = $(this.getElement());
//	table.addClass("pivot-table");

	this.onStateChange = function() {
		var connector = this;
		var state = this.getState();
		if (!state.values) {
			return;
		}
		var dateFormat = $.pivotUtilities.derivers.dateFormat;
		var options = {
			onRefresh : function(config) {
				connector.onUiOptionChange({
					rows : config.rows,
					cols : config.cols,
					aggregatorName : config.aggregatorName,
					xx : connector.aggregator,
				});
			},
			derivedAttributes: {
				"Jahr (Datum)": dateFormat("Datum", "%y", true),
				"Quartal (Datum)": dateFormat("Datum", "Q%Q/%y", true),
				"Monat (Datum)": dateFormat("Datum", "%n %y", true),
			},
			renderers: $.extend(
                    $.pivotUtilities.renderers,
                    $.pivotUtilities.c3_renderers
//                    $.pivotUtilities.export_renderers
                    )
		};
		options.rows = state.rows;
		options.cols = state.cols;
		options.aggregatorName = state.aggregatorName;
		$(state.values[0]).each(function(index, v) {
			if (v.startsWith("date$_")) {
				$(state.values).each(function(index1, v0) {
					v0[index] = new Date(v0[index]);
				});
				state.values[0][index] = v.substring(6);
			}
		});
			
		table.pivotUI(state.values, options, false, state.locale);
	}
};