angular.module('configuration', []);

// service to manage selection
angular.module('configuration').service('config', ['$rootScope',
	function($rootScope) {
		this.webappRoot = "discoveryStreaming";
	}
]);