angular.module('linesOfBusiness', ['configuration', 'ngRoute']);

// service to contact server
angular.module('linesOfBusiness').factory('linesOfBusinessService', ['$http', 'config',
	function($http, config) {
		return {
			getData: function() {
				var url = "/" + config.webappRoot + "/lob/company/stream";

				return $http({
					withCredentials: false,
					method: 'get',
					url: url,
					headers: {
						'Content-Type': 'application/json'
					}
				});
			}
		};
	}
]);

// controller business logic
angular.module('linesOfBusiness').controller('lobController', ['$scope', '$window', 'linesOfBusinessService',
	function($scope, $window, linesOfBusinessService) {
    	linesOfBusinessService.getData()
    		.success(function(result) {
    			// call function to handle initial data
    			//console.log("Result", result);
    			$scope.businessData = result;
    		});
    	
	}
]);

// vis directive
angular.module('linesOfBusiness').directive('lobGraph', ['linesOfBusinessService',
	function(linesOfBusinessService) {		

		function link($scope, $element, $attrs, $window) {
			var width = $element[0].parentNode.offsetWidth,
			height = $element[0].parentNode.offsetHeight,
			color = d3.scale.ordinal()
				.range(["#1f77b4", "#ff7f0e", "#2ca02c", "#d62728", "#8361a3", "#8c564b", "#ffaae7", "#7f7f7f", "#bcbd22", "#17becf"]),
			// init data (time X LoB) (30 time slices X 5 LoBs)
			twoDArray = d3.range(30).map(function(d) {
					return d3.range(5).map(Math.random);
				}),
			chart = d3.select($element[0])
						.append("svg")
						.attr("width", width)
						.attr("height", height)
						.append("g")
							.chart("Area")
								.size([width, height]);
	
		chart.layer("area").on("enter", function(){
			this
				.style("fill", function(_,i) { return color(i); });
		});
	
		chart.draw(twoDArray);

		};

		return {
			template: '<div class = "lobGraph"></div>',
			replace: true,
			restrict: 'E',
			link: link
		}
	}
]);

// routing
angular.module('linesOfBusiness').config(function ($routeProvider){
	$routeProvider 
		.when('/',
		{
			controller:'',
			templateUrl:''
		})
		.when('/another',
		{
			controller:'',
			templateUrl:''
		})
		.otherwise({
			redirectTo:'/'
		});
});

//Card front page, name of the card, rank of the card
/*angular.module('linesOfBusiness').controller('lobController', function($scope, $http){
	$http.get('../lob/company/stream').success(function(response){
		$scope.businessData = response;
	});
	
	//Ranks
	$http.get('../lob/company/ranks').success(function(res){
	
		//TODO: Sorting function
		sortedVal = Object.keys(res).sort(function (a, b){
			return res[a] - res[b];
		});
		//console.log(sortedVal);
	});
});*/
