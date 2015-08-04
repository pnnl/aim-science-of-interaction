angular.module("highlight", []);

// service to manage highlight
angular.module('highlight').service('highlightService', ['$rootScope',
	function($rootScope) {
		this.highlight = {};

	this.resendHighlight = function() {
			var event = {};
			event.highlight = this.highlight;
			console.log("Highlight resent: ", this.highlight);
			$rootScope.$broadcast('highlight', event);
		};

		this.replaceHighlighted = function(highlight) {
			this.highlight = highlight;

			var event = {};
			event.highlight = this.highlight;
			console.log("Highlight replaced: ", this.highlight);
			$rootScope.$broadcast('highlight', event);
		};

		this.getHighlight = function() {
			return this.highlight;
		};

		this.clearHighlight = function() {
			this.highlight = {};

			var event = {};
			event.highlight = this.highlight;
			console.log("Highlight cleared: ", this.highlight);
			$rootScope.$broadcast('highlight', event);
		};
	}
]);
