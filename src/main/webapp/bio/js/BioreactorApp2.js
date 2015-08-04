angular.module('bioreactor', ['configuration', 'ngAnimate']);

// service to contact server
angular.module('bioreactor').factory('bioService', ['$http', 'config',
	function($http, config) {
		return {

			getSamples: function() {
				var url = "/" + config.webappRoot + "/nmr/samples";
				return $http.get(url);
			},

			getCompoundLabels: function() {
				var url = "/" + config.webappRoot + "/nmr/compound/labels";
				return $http.get(url);
			},

			getReactorState: function() {
				var url = "/" + config.webappRoot + "/nmr/reactor/state";
				return $http.get(url);
			},

			editSamples: function(object) {
				var url = "/" + config.webappRoot + "/nmr/feedback/compound";
				return $http.post(url, object);
			},

			editError: function(object) {
				var url = "/" + config.webappRoot + "/nmr/feedback/errorstatus";
				return $http.post(url, object);
			},

			getCompoundHistory: function(object) {
				var url = "/" + config.webappRoot + "/nmr/reactor/compoundHistory";
				return $http.post(url, object);
			},

			config: function() {
				var margin = {
					top: 150,
					right: 30,
					bottom: 100,
					left: 150
				};

				var start = 40;

				var width = 1400 - margin.left - margin.right;
				var height = 1000 - margin.top - margin.bottom;
				var gridMargin = 2;
				var gridSize = Math.floor(960 / 32);

				var WIDTH = 400,
					HEIGHT = gridSize - gridMargin,
					MARGINS = {
						top: 20,
						right: 0,
						bottom: 20,
						left: 20
					};

				var maxNumberOfSamples = 20;
				var showProbabilities = true;

				var spectraPointCount = 1024;

				return {
					margin: margin,
					start: start,
					width: width,
					height: height,
					gridMargin: gridMargin,
					gridSize: gridSize,
					spectraWidth: WIDTH,
					spectraMargins: MARGINS,
					maxNumberOfSamples: maxNumberOfSamples,
					showProbabilities: showProbabilities,
					spectraPointCount: spectraPointCount
				};
			},

			sampleHistory: ""
		};
	}
]);

// controller business logic
angular.module('bioreactor').controller('bioController', ['$scope', '$rootScope', '$window', 'bioService', '$interval',
	function($scope, $rootScope, $window, bioService, $interval) {
		$scope.data;
		$scope.currentSample = {
			measures: [],
			spectra: {
				points: []
			}
		};
		$scope.spectra = [];
		$scope.compoundHistory = [];
		$scope.sampleColors = {};
		var colors = ['rgba(166,206,227,1)', 'rgba(31,120,180,1)', 'rgba(178,223,138,1)', 'rgba(51,160,44,1)', 'rgba(251,154,153,1)', 'rgba(227,26,28,1)', 'rgba(253,191,111,1)', 'rgba(255,127,0,1)', 'rgba(202,178,214,1)', 'rgba(106,61,154,1)', 'rgba(255,255,153,1)'];


		// Start getting data
		var stop;
		$scope.stream = function() {
			console.log("stream");
			if (angular.isDefined(stop)) return;
			stop = $interval(function() {
				updateState();
			}, 3000);
		};

		$scope.stopStream = function() {
			if (angular.isDefined(stop)) {
				$interval.cancel(stop);
				stop = undefined;
			}
		};

		$scope.showSpectra = function(sample) {
			console.log("showSpectra", sample);
			//If the eye is open
			if ($(event.target).hasClass("eyeopen")) {
				//close eye and hide spectra
				$(event.target).addClass("glyphicon-eye-close eyeclose")
					.removeClass("glyphicon-eye-open eyeopen")
					.css("color", "rgba(0,0,0,.35)");
				$("." + sample.sampleID).hide();
			}
			//If the eye is closed
			else if ($(event.target).hasClass("eyeclose")) {
				//Open eye
				$(event.target).removeClass("glyphicon-eye-close eyeclose")
					.addClass("glyphicon-eye-open eyeopen")
					//Check if colored has been assigned to spectra, if not, assign and use, else use assigned
				if ($scope.sampleColors[sample.sampleID] == undefined) {
					$scope.sampleColors[sample.sampleID] = colors.pop();
					$(event.target).css("color", $scope.sampleColors[sample.sampleID]);
				} else {
					$(event.target).css("color", $scope.sampleColors[sample.sampleID]);
				}
				//Check for existing spectra, if existing hide, else add
				for (var i in $scope.spectra) {
					if ($scope.spectra[i].sampleID == sample.sampleID) {
						$("." + sample.sampleID).show();
						return;
					}
				}
				$scope.spectra.push(sample);
			}
		};

		$scope.showCompoundHistory = function(compound, color) {
			console.log("showCompoundHistory", compound);

			var target = d3.select(d3.event.target);
			if (target.classed('selectedCompound')) {
				var index = -1;
				$scope.compoundHistory.forEach(function(d, i) {
					console.log(d[0], compound);
					if (d[0] === compound) {
						index = i;
					}
				});
				console.log("Index", index);
				if (index >= 0) {
					$scope.compoundHistory.splice(index, 1);
				}

				target.classed('selectedCompound', false);
			} else {
				console.log("target", target);
				target.classed('selectedCompound', true);
				bioService.getCompoundHistory(compound)
					.success(function(result) {
						$scope.compoundHistory.push([compound, result, color]);
					});
			}
		};

		$scope.errorPopup = function(data) {
			console.log(event);
			//			var top = event.clientY - 22;
			//			var left = event.clientX + 14;
			var left = Number($(event.target).offset().left) + 30;
			var top = Number($(event.target).offset().top) - 12;
			//to revert popup to original style if a different element is selected
			//***********
			$(".errPopup").css("width", "90px")
				//				.css("height", "60px")
				.css("top", top);
			$(".errPointer").css("top", "12px");
			$(".errEditInfo").hide();
			$(".errPopupInfo").show();
			$("#errEdit").show();
			//***********

			$(".errPopup").css("left", left)
				.css("top", top)
				.show();
			$("#errPopupClose").on("click", function() {
				$(".errPopup").hide();
			});
			$(".errPopupInfo").html("Code: " + data.error.code + "<br>" +
				data.error.message);
			$("#errEdit").off("click");
			$("#errEdit").on("click", function() {
				$(".errPopupInfo").hide();
				$("#errEdit").hide();
				$(".errPopup").css("width", "225px")
					//					.css("height", "84px")
					.css("top", top);
				//				$(".errPointer").css("top", "32px")
				$(".errEditMessage").html("Is the quality status " + data.error.message + " correct for " + data.sampleID + "?");
				$("#errEditYes").off("click");
				$("#errEditYes").on("click", function() {
					//send sample and compound as true
					var o = {
						errorCondition: data.error.code,
						sampleID: data.sampleID,
						errorCorrect: true
					};
					bioService.editError(o);
					$(".errPopup").hide();
				});
				$("#errEditNo").off("click");
				$("#errEditNo").on("click", function() {
					//send sample and compound as false
					var o = {
						errorCondition: data.error.code,
						sampleID: data.sampleID,
						errorCorrect: false
					};
					bioService.editError(o);
					$(".errPopup").hide();
				});
				//Need close button
				$(".errEditInfo").show();
			});
		};

		$scope.closePopup = function() {
			$(".popup").hide();
			$(".errPopup").hide();
		};

		var xScale = d3.scale.linear().domain([0, bioService.config().spectraPointCount]).range([0, 900]);
		var yScale = d3.scale.linear().domain([0, 1]).range([128, 0]);

		// line function
		var lineGen = d3.svg.line()
			.x(function(d) {
				return xScale(d.x);
			})
			.y(function(d) {
				return yScale(d.y);
			})
			.interpolate("basis");

		var previousSample;
		var lastSample;

		var updateState = function() {
			// Retrieve current reactor state
			bioService.getReactorState()
				.success(function(result) {
					result.history.forEach(function(d) {
						d.timestamp = moment(d.datetime).format("H:mm:ss");
					});

					// result.current.timestamp = moment(result.current.datetime).format("H:mm:ss");

					$scope.ranges = result.ranges;
					$scope.sampleHistory = result.history;
					bioService.sampleHistory = result.history;
					$scope.$broadcast('currentSample', result.current);
					console.log("Broadcast", result.current);

					// if (previousSample == result.current.sampleID || previousSample == undefined) {
					// 	if ($scope.spectra.length > 0) {
					// 		d3.select("." + result.current.sampleID)
					// 			.datum(result.current.spectra.points)
					// 			.transition()
					// 			.duration(1500)
					// 			.attr('d', lineGen)
					// 			.attr('stroke', $scope.sampleColors[result.current.sampleID])
					// 			.attr('stroke-width', 1)
					// 			.attr('fill', 'none')
					// 			.attr("opacity", 1);
					// 	}
					// } else {
					// 	$("#currentView").attr("class", "glyphicon glyphicon-eye-close center eyeclose")
					// 		.css("color", "rgba(0,0,0,.35)");
					// 	if (previousSample != "") {
					// 		$("." + previousSample).hide();
					// 	}
					// }
					// previousSample = result.current.sampleID;

					//Recycle the colors when a sample has been removed from the history
					if (result.history.length == 10) {
						var check = false;
						for (var i in result.history) {
							if (result.history[i].sampleID == lastSample || lastSample == undefined) {
								check = true;
							}
						}
						if (!check) {
							for (var key in $scope.sampleColors) {
								if (key == lastSample) {
									var color = $scope.sampleColors[lastSample];
									colors.push(color);
									delete $scope.sampleColors[lastSample];
									$("." + lastSample).parent().remove();
								}
							}
						}
						lastSample = result.history[9].sampleID;
					}

				});
		};


		// Retrieve compound labels
		bioService.getCompoundLabels()
			.success(function(result) {
				$scope.$broadcast('labels', result);
				updateState();
			});

		$scope.hover = function(div) {
			$("#" + div).css("background", "black")
				.css("color", "#F9F9F9");
		}

		$scope.hoverOut = function(div) {
			$("#" + div).css("background", "#F9F9F9")
				.css("color", "black");
		}

		// Ensure we stop the stream when destroyed	
		$scope.$on('$destroy', function() {
			// Make sure that the interval is destroyed too
			$scope.stopStream();
		});


	}
]);

//chart directive
angular.module('bioreactor').directive('compoundLabels', ['bioService',
	function(bioService) {
		var margin = bioService.config().margin;
		var width = bioService.config().width;
		var height = 150;
		var start = bioService.config().start;
		var gridSize = bioService.config().gridSize;

		var c20 = d3.scale.category20();
		var c20b = d3.scale.category20b();

		function link($scope, $element, $attrs, $window) {
			// configure svg
			var svg = d3.select($element[0])
				.append("svg")
				.attr("width", width + margin.left + margin.right)
				.attr("height", height)
				.classed("compundLabelSvg", true)
				.append("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

			var labelGroup = svg.append('g').classed('labels', true);

			// Resize management
			function resize() {
				width = $("#compoundLabels").innerWidth() - margin.left - margin.right;
				height = $("#compoundLabels").innerHeight() - margin.top - margin.bottom;

				svg.attr("width", width)
					.attr("height", height);
			}

			// D3 management
			function update(event, data) {
				console.log("Updating Compound Labels", data);

				// create compound labels
				var compoundLabels = labelGroup.selectAll(".compoundLabel")
					.data(data);

				compoundLabels.enter().append("text")
					.text(function(d) {
						return d;
					})
					.style("text-anchor", "begin")
					.classed('compoundLabel', true)
					.on('mouseover', function(d, i) {
						var color;
						if (i >= 20) {
							color = c20b(i - 20);
						} else {
							color = c20(i);
						}
						d3.select(this)
							.attr('fill', color);
					})
					.on('mouseout', function(d) {
						d3.select(this)
							.attr('fill', 'black');
					})
					.on('click', function(d, i) {
						var color;
						if (i >= 20) {
							color = c20b(i - 20);
						} else {
							color = c20(i);
						}
						$scope.showCompoundHistory(d, color);
					});

				// update **********************
				compoundLabels.transition()
					.attr("transform", function(d, i) {
						return "translate(" + (start + i * gridSize + gridSize / 2) + ", -6)rotate(-45)";
					});
			}

			resize();

			// Event handlers
			$scope.$on('windowResize', resize);
			$scope.$on('labels', update);
		};

		return {
			template: '<div id="compoundLabels"></div>',
			replace: true,
			restrict: 'E',
			link: link
		}
	}
]);

//chart directive
angular.module('bioreactor').directive('singleSampleChart', ['bioService',
	function(bioService) {
		var margin = bioService.config().margin;
		var width = bioService.config().width;
		var start = bioService.config().start;
		var gridSize = bioService.config().gridSize;
		var height = gridSize;
		var gridMargin = bioService.config().gridMargin;
		var showProbabilities = bioService.config().showProbabilities;

		var chartData = {
			compoundLabels: [],
			samples: []
		};

		var currentPopup = {
			top: "",
			left: "",
			sample: "",
			index: ""
		}

		function link($scope, $element, $attrs, $window) {
			// configure svg
			var svg = d3.select($element[0])
				.append("svg")
				.attr("width", 960)
				.attr("height", height)
				.classed("bioChartSvg", true)
				.append("g")
				.attr("transform", "translate(0,0)");

			update();

//			//If popup is visible and last history row is removed hide popup
//			for (var i in bioService.sampleHistory) {
//				if (bioService.sampleHistory[i].sampleID == currentPopup.sample) {
//					break;
//				}
//				if (i == bioService.sampleHistory.length - 1) {
//					$(".popup").hide();
//				}
//			}

			$scope.$on('currentSample', function(event, data) {
				console.log('Update', data);
				update();
			});

			// D3 management
			function update() {
				console.log("Updating", $scope.sample, $scope.ranges);
				var data = $scope.sample.measures;

				var compoundAbundanceSize = {};
				$scope.ranges.forEach(function(d) {
					var abundanceChangeSize = d3.scale.quantize().domain([d.min, d.max]).range([0.2 * gridSize, 0.4 * gridSize, 0.6 * gridSize, 0.8 * gridSize, 1.0 * gridSize]);
					compoundAbundanceSize[d.compound] = abundanceChangeSize;
				});

				var probExtent = d3.extent(data, function(d) {
					return d.probability;
				});
				console.log("Prob extent", probExtent);

				var probColor = d3.scale.quantize().domain(probExtent).range(colorbrewer.PuBu[9]);

				var sampleRects = svg.selectAll('.sampleRect')
					.data(data);

				var rectEnter = sampleRects.enter()
					.append("rect")
					.attr("x", function(d, i) {
						return (i * gridSize) + (gridSize / 2);
					})
					.attr("y", function(d) {
						return gridSize / 2;
					})
					.attr("rx", 0)
					.attr("ry", 0)
					.attr("value", $scope.sample.sampleID)
					.attr("id", function(d, i) {
						return "S" + $scope.sample.sampleID + i;
					})
					.classed("sampleRect", true)
					.attr("width", 0)
					.attr("height", 0)
					.style("fill", "white")
					.on("click", function(d, i) {
						console.log(d);
						var sampleRow = $(this).attr("value");
						currentPopup.left = Number($(this).offset().left) + Number($(this).attr("width")) + 15;
						currentPopup.top = Number($(this).position().top) + Number($(this).attr("height") / 2) - 30;
						console.log(currentPopup.top);
						currentPopup.sample = sampleRow;
						currentPopup.index = i;
						//to revert popup to original style if a different element is selected
						//***********
						$(".popup").css("width", "90px")
							//							.css("height", "60px")
							.css("top", currentPopup.top);
						$(".pointer").css("top", "20px");
						$(".editInfo").hide();
						$(".popupInfo").show();
						$("#edit").show();
						//***********

						//						var pos = d3.mouse(this);
						//						var left = d3.event.clientX + ($(this).attr("width") - (pos[0] - $(this).attr("x"))) + 15;
						//						var top = d3.event.clientY + (($(this).attr("height") / 2) - (pos[1] - $(this).attr("y"))) - 30;
						$(".popup").css("left", currentPopup.left)
							.css("top", currentPopup.top)
							.show();
						$("#popupClose").on("click", function() {
							$(".popup").hide();
						});
						$(".popupInfo").html(Math.round(d.concentration * 100) / 100 + " µM<br>" +
							Math.round(d.probability * 100) + "%<br>" +
							function(d) {
								return "N/A";

								if (d.veto) {
									return "Vetoed";
								} else {
									return "Not Vetoed";
								}
							}
							(d));
						$("#edit").off("click");
						$("#edit").on("click", function() {
							$(".popupInfo").hide();
							$("#edit").hide();
							$(".popup").css("width", "225px");
							$(".editMessage").html("Is " + d.compoundName + " present in " + sampleRow + "?");
							$("#editYes").off("click");
							$("#editYes").on("click", function() {
								//send sample and compound as true
								var o = {
									sampleID: sampleRow,
									compoundName: d.compoundName,
									compoundPresence: true
								};
								console.log("Edit " + d.compoundName + " in " + sampleRow);
								bioService.editSamples(o);
								//revert popup style to original
								$(".popup").css("width", "90px")
									//									.css("height", "60px")
									.css("top", currentPopup.top);
								$(".pointer").css("top", "20px");
								$(".editInfo").hide();
								$(".popupInfo").show();
								$("#edit").show();
							});
							$("#editNo").off("click");
							$("#editNo").on("click", function() {
								//send sample and compound as false
								var o = {
									sampleID: sampleRow,
									compoundName: d.compoundName,
									compoundPresence: false
								};
								console.log("Edit " + d.compoundName + " in " + sampleRow);
								bioService.editSamples(o);
								//revert popup style to origina
								$(".popup").css("width", "90px")
									//									.css("height", "60px")
									.css("top", currentPopup.top);
								$(".pointer").css("top", "20px");
								$(".editInfo").hide();
								$(".popupInfo").show();
								$("#edit").show();
							});
							//Need close button
							$(".editInfo").show();
						});
					});

				var vetoRects = svg.selectAll('.vetoGroup')
					.data(data);

				var vetoEnter = vetoRects.enter()
					.append('g')
					.classed('vetoGroup', true)
					.attr('opacity', 0)
					.attr('transform', function(d, i) {
						return 'translate(' + ((i * gridSize) + 4) + ',' + (-1) + ')';
					})
					.each(function(d) {
						var t = d3.select(this);
						t.append('rect')
							.attr('width', (gridSize - 8))
							.attr('height', (gridSize - 8))
							.attr('rx', 2)
							.attr('ry', 2)
							.attr('stroke', 'rgba(255,0,0,.5)')
							.attr('fill', 'none')
							.attr('x', function(d) {
								return gridSize / 2 - (gridSize - 8) / 2;
							})
							.attr('y', function(d) {
								return gridSize / 2 - (gridSize - 8) / 2;
							})
							.attr('transform', 'translate(-5, 0)'); // XXX: hacks

						t.append('line')
							.attr('x1', '5')
							.attr('y1', '5')
							.attr('x2', (gridSize - 5))
							.attr('y2', (gridSize - 5))
							.attr('stroke', 'rgba(255,0,0,.25)')
							.attr('stroke-width', 2)
							.attr('transform', 'translate(-5,0)'); // XXX: hacks

						t.append('line')
							.attr('x1', '25')
							.attr('y1', '5')
							.attr('x2', (gridSize - 25))
							.attr('y2', (gridSize - 5))
							.attr('stroke', 'rgba(255,0,0,.25)')
							.attr('stroke-width', 2)
							.attr('transform', 'translate(-5,0)'); // XXX: hacks
					});

				// update **********************

				sampleRects
					.attr("value", $scope.sample.sampleID)
					.attr("id", function(d, i) {
						return "S" + $scope.sample.sampleID + i;
					})
					.transition()
					.duration(1500)
					.attr("x", function(d, i) {
						var abundanceChangeSize = compoundAbundanceSize[d.compoundName];
						if (showProbabilities)
							return (i * gridSize) + (gridSize / 2) - (abundanceChangeSize(d.concentration) / 2);
						else
							return (i * gridSize);
					})
					.attr("y", function(d, i) {
						var abundanceChangeSize = compoundAbundanceSize[d.compoundName];
						if (showProbabilities)
							return (gridSize / 2) - (abundanceChangeSize(d.concentration) / 2);
						else
							return 0;
					})
					.attr("rx", function(d) {
						var abundanceChangeSize = compoundAbundanceSize[d.compoundName];
						if (showProbabilities && (abundanceChangeSize(d.concentration) < gridSize * 0.5))
							return 1;
						else
							return 4;
					})
					.attr("ry", function(d) {
						var abundanceChangeSize = compoundAbundanceSize[d.compoundName];
						if (showProbabilities && (abundanceChangeSize(d.concentration) < gridSize * 0.5))
							return 1;
						else
							return 4;
					})
					.attr("width", function(d) {
						var abundanceChangeSize = compoundAbundanceSize[d.compoundName];
						return abundanceChangeSize(d.concentration) - gridMargin;
					})
					.attr("height", function(d) {
						var abundanceChangeSize = compoundAbundanceSize[d.compoundName];
						return abundanceChangeSize(d.concentration) - gridMargin;
					})
					.style("fill", function(d) {
						return probColor(d.probability);
					})
					.each('end', function() {
						updatePopup();
					});

				vetoRects
					.classed('hideError', function(d, i) {
						return d.vetoed === false;
					})
					.transition()
					.duration(1500)
					.attr('opacity', 1);


				updatePopup();

			}

			function updatePopup() {

				if ($(".popup:visible").length > 0) {
					console.log("Updating popup!!!!!");
					var samp = currentPopup.sample;
					var i = currentPopup.index;
					var elem = $("#" + samp + i);
					if (elem.length > 0) {
						currentPopup.left = Number(elem.offset().left) + Number(elem.attr("width")) + 15;
						currentPopup.top = Number(elem.offset().top) + Number(elem.attr("height") / 2) - 30;
						$(".popup").css("top", currentPopup.top).css("left", currentPopup.left);
					}
				}
			}


		};

		return {
			template: '<div class="sampleBoxes"></div>',
			replace: true,
			restrict: 'E',
			scope: {
				sample: '=',
				current: '=',
				ranges: '='
			},
			link: link
		}
	}
]);

//chart directive
angular.module('bioreactor').directive('singleSpectra', ['bioService',
	function(bioService) {
		var width = 256;
		var gridSize = 28;
		var height = gridSize;

		var xScale = d3.scale.linear().domain([0, bioService.config().spectraPointCount]).range([0, width]);
		var yScale = d3.scale.linear().domain([0, 1]).range([gridSize, 0]);

		// area function
		var area = d3.svg.area()
			.x(function(d) {
				return xScale(d.x);
			})
			.y0(gridSize)
			.y1(function(d) {
				return yScale(d.y);
			})
			.interpolate("basis");

		var preArea = d3.svg.area()
			.x(function(d) {
				return xScale(d.x);
			})
			.y0(gridSize)
			.y1(function(d) {
				return gridSize;
			})
			.interpolate("basis");

		// line function
		var lineGen = d3.svg.line()
			.x(function(d) {
				return xScale(d.x);
			})
			.y(function(d) {
				return yScale(d.y);
			})
			.interpolate("basis");

		var firstGen = d3.svg.line()
			.x(function(d) {
				return xScale(d.x);
			})
			.y(function(d) {
				return gridSize;
			})
			.interpolate("basis");

		function link($scope, $element, $attrs, $window) {
			// configure svg
			var svg = d3.select($element[0])
				.append("svg")
				.attr("width", width)
				.attr("height", height)
				.classed("bioChartSvg", true)
				.append("g")
				.attr("transform", "translate(0,0)");

			update();
			$scope.$on('currentSample', function(event, data) {
				// console.log('Update spectra', data);
				// $scope.sample = data;
				update();
			});

			// D3 management
			function update() {
				console.log("Updating spectra", $scope.sample);
				var data = [$scope.sample.spectra];

				// add spectra
				var specsEnter = svg.selectAll('.spectrum')
					.data(data)
					.enter()
					.append("g")
					.attr("width", 200)
					.attr("height", height)
					.classed("spectrum", true);

				// render area
				specsEnter.append("svg:path")
					.attr("opacity", 0)
					.datum(function(d) {
						return d.points;
					})
					.attr("class", "area")
					.attr('d', preArea);

				// render line
				specsEnter.append('svg:path')
					.attr("opacity", 0)
					.datum(function(d) {
						return d.points;
					})
					.attr('d', firstGen)
					.classed("line", true);

				// update **********************

				var spectrum = svg.selectAll(".spectrum");

				spectrum.select(".area")
					.datum(function(d) {
						return d.points;
					})
					.transition()
					.duration(1500)
					.attr("d", area)
					.attr("opacity", 1);

				spectrum.select(".line")
					.datum(function(d) {
						return d.points;
					})
					.transition()
					.duration(1500)
					.attr('d', lineGen)
					.attr('stroke', 'darkBlue')
					.attr('stroke-width', 1)
					.attr('fill', 'none')
					.attr("opacity", 1);
			}
		};

		return {
			template: '<div class="singleSpectra"></div>',
			replace: true,
			restrict: 'E',
			scope: {
				sample: '='
			},
			link: link
		}
	}
]);

// chart directive
angular.module('bioreactor').directive('spectraGraph', ['bioService',
	function(bioService) {
		var width = 900;
		var gridSize = 128;
		var height = gridSize;

		function link($scope, $element, $attrs, $window) {
			// configure svg
			var svg = d3.select($element[0])
				.append("svg")
				.attr("width", width + 20)
				.attr("height", height + 40)
				.classed("bioChartSvg", true)
				.append("g")
				.attr("transform", "translate(10,20)");

			var xScale = d3.scale.linear().domain([0, bioService.config().spectraPointCount]).range([0, width]);
			var yScale = d3.scale.linear().domain([0, 1]).range([gridSize, 0]);

			var firstGen = d3.svg.line()
				.x(function(d) {
					return xScale(d.x);
				})
				.y(function(d) {
					return gridSize;
				})
				.interpolate("basis");

			// line function
			var lineGen = d3.svg.line()
				.x(function(d) {
					return xScale(d.x);
				})
				.y(function(d) {
					return yScale(d.y);
				})
				.interpolate("basis");

			var xAxis = d3.svg.axis()
				.scale(xScale);

			var yAxis = d3.svg.axis()
				.scale(yScale)
				.orient("left");

			svg.append("svg:g")
				.attr("class", "x axis")
				.attr("transform", "translate(0," + (height) + ")")
				.call(xAxis)

			svg.append("svg:g")
				.attr("class", "y axis")
				.attr("transform", "translate(0,0)")
				.call(yAxis);

			//			update();
			$scope.$watchCollection("spectra", function(newValue, oldValue) {
				console.log('Update spectra graph', newValue);
				$scope.sample = newValue;
				if ($scope.sample.length > 0) {
					update();
				}
			});

			// D3 management
			function update() {
				console.log("Updating spectra", $scope.sample);
				var data = $scope.sample;

				var max = d3.max($scope.sample, function(array) {
					console.log("array", array);
					return d3.max(array.spectra.points, function(d) {
						return d.y;
					});
				});

				console.log("Max", max);

				yScale.domain([0, max]);
				d3.select('.y.axis').call(yAxis);

				// add spectra
				var specsEnter = svg.selectAll('.spectrum')
					.data(data)
					.enter()
					.append("g")
					.attr("width", 200)
					.attr("height", height)
					.classed("spectrum", true);

				// render line
				specsEnter.append('svg:path')
					.attr("opacity", 0)
					.attr("class", function(d) {
						return d.sampleID;
					})
					.datum(function(d) {
						return d.spectra.points;
					})
					.attr('d', firstGen)
					.classed("line", true)


				// update **********************

				var spectrum = svg.selectAll(".spectrum");

				spectrum.select(".line")
					.attr('stroke', function(d) {
						return $scope.sampleColors[d.sampleID];
					})
					.datum(function(d) {
						console.log(d);
						return d.spectra.points;
					})
					.transition()
					.duration(1500)
					.attr('d', lineGen)
					.attr('stroke-width', 1)
					.attr('fill', 'none')
					.attr("opacity", 1);

			}
		};

		return {
			template: '<div id="spectraGraph"></div>',
			replace: true,
			restrict: 'E',
			link: link
		}
	}
]);

// chart directive
angular.module('bioreactor').directive('compoundGraph', ['bioService',
	function(bioService) {
		var width = 900;
		var gridSize = 128;
		var height = gridSize;

		function link($scope, $element, $attrs, $window) {
			// configure svg
			var svg = d3.select($element[0])
				.append("svg")
				.attr("width", width + 20)
				.attr("height", height + 40)
				.classed("bioChartSvg", true)
				.append("g")
				.attr("transform", "translate(10,20)");

			var xScale = d3.scale.linear().domain([0, 50]).range([0, width]);
			var yScale = d3.scale.linear().domain([0, 1]).range([gridSize, 0]);

			var firstGen = d3.svg.line()
				.x(function(d) {
					return xScale(d.x);
				})
				.y(function(d) {
					return gridSize;
				})
				.interpolate("basis");

			// line function
			var lineGen = d3.svg.line()
				.x(function(d) {
					return xScale(d.x);
				})
				.y(function(d) {
					return yScale(d.y);
				})
				.interpolate("basis");

			var xAxis = d3.svg.axis()
				.scale(xScale);

			var yAxis = d3.svg.axis()
				.scale(yScale)
				.orient("left");

			svg.append("svg:g")
				.attr("class", "x axis")
				.attr("transform", "translate(0," + (height) + ")")
				.call(xAxis)

			svg.append("svg:g")
				.attr("class", "y axis")
				.attr("transform", "translate(0,0)")
				.call(yAxis);

			//			update();
			$scope.$watchCollection("compoundHistory", function(newValue, oldValue) {
				console.log('Update compounds graph', newValue);
				$scope.compounds = newValue;
				update();
			});

			// D3 management
			function update() {
				console.log("Updating compounds", $scope.compounds);
				var data = $scope.compounds;

				var yMax = d3.max($scope.compounds, function(array) {
					return d3.max(d3.values(array[1].map), function(d) {
						return d;
					});
				});
				yScale.domain([0, yMax]);
				d3.select('.y.axis').call(yAxis);

				var xMax = d3.max($scope.compounds, function(array) {
					return d3.max(d3.keys(array[1].map), function(d) {
						return d;
					});
				});
				xScale.domain([0, xMax]);
				d3.select('.x.axis').call(xAxis);
				console.log(xMax, yMax);

				// add spectra
				var specsEnter = svg.selectAll('.spectrum')
					.data(data)
					.enter()
					.append("g")
					.attr("width", 200)
					.attr("height", height)
					.classed("spectrum", true);

				// render line
				specsEnter.append('svg:path')
					.attr("opacity", 0)
					.attr("class", function(d) {
						return d.sampleID;
					})
					.datum(function(d) {
						var data = d[1].map;
						var arr = [];
						d3.keys(data).forEach(function(d) {
							arr.push({
								x: parseInt(d),
								y: data[d]
							});
						});
						console.log("Data", arr);
						return arr;
					})
					.attr('d', firstGen)
					.classed("line", true)

				// update **********************
				var spectrum = svg.selectAll(".spectrum");

				spectrum.select(".line")
					.attr('stroke', function(d) {
						return $scope.sampleColors[d.sampleID];
					})
					.datum(function(d) {
						var data = d[1].map;
						var arr = [];
						d3.keys(data).forEach(function(d) {
							arr.push({
								x: parseInt(d),
								y: data[d]
							});
						});
						return arr;
					})
					.transition()
					.duration(1500)
					.attr('d', lineGen)
					.attr('stroke', function(d, i) {
						if ($scope.compounds.length == 0)
							return "black";
						console.log("Stroke", $scope.compounds[i][2]);
						return $scope.compounds[i][2];
					})
					.attr('stroke-width', 1)
					.attr('fill', 'none')
					.attr("opacity", 1);
			}
		};

		return {
			template: '<div id="compoundGraph"></div>',
			replace: true,
			restrict: 'E',
			link: link
		}
	}
]);