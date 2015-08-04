angular.module("search", []);

// service to manage search
angular.module('search').service('searchService', ['$rootScope',
	function($rootScope) {
		this.searches = [];
		
		this.getSearch = function() {
			return this.searches;
		};

		this.addSearch = function(searchString, results) {
			var exists = false;
			this.searches.forEach(function(d) {
				if(d.string === searchString) {
					exists = true;
				}
			});

			if (exists)
				return;

			// clear any null elements
			var clean = [];
			results.forEach(function(d) {
				if (d != null)
					clean.push(d);
			});

			this.searches.push({string: searchString, ids: clean});
			var event = {};
			event.search = this.searches;
			$rootScope.$broadcast('search', event);
			console.log("Search updated: ", this.searches);
		};

		this.clearSearch = function() {
			this.searches.length = 0;
			var event = {};
			event.search = this.searches;
			$rootScope.$broadcast('search', event);
			console.log("Search updated: ", this.searches);
		};

		this.removeSearch = function(searchString) {
			var index = -1;
			this.searches.forEach(function(d, i) {
				if (d.string === searchString) {
					index = i;
				}
			});

			if (index < 0)
				return;

			this.searches.splice(index, 1);
			var event = {};
			event.search = this.searches;
			$rootScope.$broadcast('search', event);
			console.log("Search updated: ", this.searches);
		};
	}
]);
