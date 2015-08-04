angular.module("selection", []);

// service to manage selection
angular.module('selection').service('selectionService', ['$rootScope',
	function($rootScope) {
		this.selected = null;
		
		/** 
		 * SELECTION IS BACK TO BEING SINGLE DOCUMENT ONLY
		 */
		this.setSelection = function(selection) {
			// Support interchangability between single/multiple selections, if possible
			var oldSelection = this.selected;
			this.selected = selection;
			var event = {};
			event.oldSelection = oldSelection;
			event.selection = this.selected;

			console.log("Selection updated", this.selected);
			$rootScope.$broadcast('selection', event);
		};

		this.getSelection = function() {
			return this.selected;
		};

		this.clearSelection = function() {
			var oldSelection = this.selected;
			this.selected = null;
			var event = {};
			event.oldSelection = oldSelection;
			event.selection = this.selected;

			console.log("Selection cleared", this.selected);
			$rootScope.$broadcast('selection', event);
		};
	}
]);
