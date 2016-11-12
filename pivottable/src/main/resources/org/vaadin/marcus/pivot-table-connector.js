'use strict';
window.org_vaadin_marcus_PivotTable = function() {

	var table = $(this.getElement());
	table.addClass("pivot-table");

	this.onStateChange = function() {
		var connector = this;
		var state = this.getState();
		if (!state.values) {
			return;
		}

		var options = {
			onRefresh : function(config) {
				connector.onUiOptionChange({
					rows : config.rows,
					cols : config.cols,
					aggregatorName : config.aggregatorName,
					xx : connector.aggregator,
				});
			}
		};
		options.rows = state.rows;
		options.cols = state.cols;
		options.aggregatorName = state.aggregatorName;
		
		table.pivotUI(state.values, options, false, state.locale);
	}
};