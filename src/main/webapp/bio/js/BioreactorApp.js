angular.module('bioreactor', ['configuration']);

// service to contact server
angular.module('bioreactor').factory('bioService', ['$http', 'config',
	function($http, config) {
		return {
			//			streamData: function() {
			//				var url = "/" + config.webappRoot + "/bio/stream";
			//				return $http.get(url);
			//			},

			getSamples: function() {
				var url = "/" + config.webappRoot + "/nmr/samples";
				return $http.get(url);
			},
		
			editSamples: function(object) {
				var url = "/" + config.webappRoot + "/nmr/edit";
				return $http.post(url, {msg: object});
			}
		};
	}
]);

// controller business logic
angular.module('bioreactor').controller('bioController', ['$scope', '$window', 'bioService', '$interval',
	function($scope, $window, bioService, $interval) {
		$scope.data;
		// Start getting data

		var stop;
		$scope.stream = function() {
			console.log("stream");
			getData();
			//close popup if open
			$(".popup").hide();
			if (angular.isDefined(stop)) return;
			stop = $interval(function() {
				getData();
				//close popup if open
				$(".popup").hide();
			}, 3000);
		}

		$scope.stopStream = function() {
			if (angular.isDefined(stop)) {
				$interval.cancel(stop);
				stop = undefined;
			}
		};

		$scope.$on('$destroy', function() {
			// Make sure that the interval is destroyed too
			$scope.stopStream();
		});

		var getData = function() {
			bioService.getSamples()
				.success(function(result) {
					$scope.data = result;
					//					$scope.$broadcast('update');
					$scope.$broadcast('samples', result);
				});
		}

		getData();
		
		$scope.editHover = function(){
			$("#edit").css("background","black")
				.css("color","#F9F9F9");
		}
		
		$scope.editHoverOut = function(){
			$("#edit").css("background","#F9F9F9")
				.css("color","black");
		}
		
	}
]);

// sparklines directive
angular.module('bioreactor').directive('bioGraph', ['bioService',
	function(bioService) {


		function link($scope, $element, $attrs, $window) {
			//D3 for graphs

			//			//header
			//			d3.select(".bioGraph").insert("div")
			//			.attr("class","specTitle")
			//			.text("NMR Spectra");
			//			
			//			$scope.$watch('update');
			//			$scope.$on('update', function(event) {
			//				console.log("update");
			//				var data = $scope.data;
			//				chartLines(data);
			//			});
			//			
			//			var chartLines = (function (data) {
			//						
			//				console.log("chartlines");
			//				console.log(data);
			//				var WIDTH = 400,
			//				HEIGHT = 20,
			//				MARGINS = {
			//					top: 20,
			//					right: 0,
			//					bottom: 20,
			//					left: 20
			//				},
			//				
			//				//draw svg on canvas
			//				//JOIN
			//				vis = d3.select(".bioGraph").selectAll("svg")
			//					.data(data);
			//				
			//				//UPDATE
			//				//because the data array is unshifted with every new sample D3 will be seeing all new data nodes 
			//				//on every update
			//				
			//				//ENTER
			//				 	vis.enter()
			//						.append("svg")
			//						.attr("width", WIDTH)
			//						.attr("height", HEIGHT);
			//				
			//				//define scale for graph
			//				var xScale = d3.scale.linear().range([MARGINS.left, WIDTH - MARGINS.right]).domain([0,data[0].spectra.length]),
			//				yScale = d3.scale.linear().range([HEIGHT - MARGINS.top, MARGINS.bottom]).domain([0,1]),
			//				
			//				xAxis = d3.svg.axis()
			//			    	.scale(xScale),
			//			  
			//			    yAxis = d3.svg.axis()
			//			    	.scale(yScale)
			//			    	.orient("left");
			//				
			//				//area function
			//				var area = d3.svg.area()
			//			    .x(function(d) { return xScale(d[0]); })
			//			    .y0(HEIGHT)
			//			    .y1(function(d) { return yScale(d[1]); })
			//			    .interpolate("basis");
			//				
			//				//line function
			//				var lineGen = d3.svg.line()
			//				  .x(function(d) {
			//				    return xScale(d[0]);
			//				  })
			//				  .y(function(d) {
			//				    return yScale(d[1]);
			//				  })
			//				  .interpolate("basis");
			//				
			//				//ENTER & UPDATE
			//				d3.select(".bioGraph").selectAll("svg > *").remove();
			//				//add x axis
			//				vis.append("svg:g")
			//				.attr("class","axis")
			//				.attr("transform", "translate(0," + (HEIGHT - MARGINS.bottom) + ")")
			//			    .call(xAxis)
			//			    //add y axis
			//			    vis.append("svg:g")
			//				.attr("class","axis")
			//				.attr("transform", "translate(" + (MARGINS.left) + ",0)")
			//				.call(yAxis);
			//				
			//				//render area
			//				vis.append("svg:path")
			//		        .datum(function(d){
			//		        	var spec = [];
			//		        	for(var i in d.spectra){
			//		        		spec.push([i,d.spectra[i]]);
			//		        	}
			//		        	return spec;
			//		        })
			//		        .attr("class", "area")
			//		        .attr("d", area);
			//				
			//		        //render line
			//		        vis.append('svg:path')
			//				.datum(function(d){
			//					var spec = [];
			//		        	for(var i in d.spectra){
			//		        		spec.push([i,d.spectra[i]]);
			//		        	}
			//		        	return spec;
			//		        })
			//			  .attr('d', lineGen)
			//			  .attr('stroke', 'darkBlue')
			//			  .attr('stroke-width', 1)
			//			  .attr('fill', 'none');				
			//				
			//		        //EXIT
			//				vis.exit().remove();
			//		    });
			//			
			//			//Get data
			//			//Setup events
			//			//Update canvas
			//						
			//			//Handle events
		};

		return {
			template: '<div class = "bioGraph"></div>',
			replace: true,
			restrict: 'E',
			link: link
		}
	}
]);

//chart directive
angular.module('bioreactor').directive('bioChart', ['bioService',
	function(bioService) {
		// constants
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

		var chartData = {
			compoundLabels: [],
			samples: []
		};

		var maxNumberOfSamples = 20;
		var showProbabilities = true;

		var chartColor = d3.scale.quantize().domain([0, 1]).range(colorbrewer.PuBu[5]);
		var probSize = d3.scale.quantize().domain([0, 1]).range([0.33 * gridSize, 0.67 * gridSize, 1.0 * gridSize]);

		
		function link($scope, $element, $attrs, $window) {
			// configure svg
			var svg = d3.select($element[0])
				.append("svg")
				.attr("width", width + margin.left + margin.right)
				.attr("height", height + margin.top + margin.bottom)
				.classed("bioChartSvg", true)
				.append("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

			var labelGroup = svg.append('g').classed('labels', true);
			var sampleGroup = svg.append('g').classed('samples', true)

			// Resize management
			function resize() {
				width = $("#bioChart").innerWidth() - margin.left - margin.right;
				height = $("#bioChart").innerHeight() - margin.top - margin.bottom;

				svg.attr("width", width)
					.attr("height", height);
			}

			// Data management
			function newSamples(event, samples) {
				// Add new samples
				samples.forEach(function(d) {
					// Update compoundLabels, no support for changes currently
					chartData.compoundLabels = d.compoundNames;

					var sample = {
						label: d.sampleID,
						timestamp: d.datetime,
						error: {
							code: d.errorCondition,
							message: d.errorConditionMessage
						},
						items: [],
						spectra: function() {
							var spec = [];
							for (var i = 0; i < d.spectra.length; i++) {
								spec.push([i, d.spectra[i]]);
							}
							return spec;
						}()
					};

					for (var i = 0; i < d.compoundNames.length; i++) {
						var o = {
							concentration: d.compoundConcentrations[i],
							probability: d.compoundProbabilities[i],
							veto: d.compoundVeto[i]
						};
						sample.items.push(o);
					}

					chartData.samples.splice(0, 0, sample);
				});

				// Age off eldest samples
				if (chartData.samples.length > maxNumberOfSamples) {
					chartData.samples.splice(maxNumberOfSamples, chartData.samples.length - maxNumberOfSamples);
				};

				// Update the chart
				update(chartData);
			}

			// D3 management
			function update(data) {
				console.log("Updating", data);

				var xScale = d3.scale.linear().range([MARGINS.left, WIDTH - MARGINS.right]).domain([0, data.samples[0].spectra.length]);
				var yScale = d3.scale.linear().range([HEIGHT - MARGINS.top, MARGINS.bottom]).domain([0, 1]);

				// area function
				var area = d3.svg.area()
					.x(function(d) {
						return xScale(d[0]);
					})
					.y0(HEIGHT)
					.y1(function(d) {
						return yScale(d[1]);
					})
					.interpolate("basis");

				var preArea = d3.svg.area()
					.x(function(d) {
						return xScale(d[0]);
					})
					.y0(HEIGHT)
					.y1(function(d) {
						return HEIGHT;
					})
					.interpolate("basis");

				// line function
				var lineGen = d3.svg.line()
					.x(function(d) {
						return xScale(d[0]);
					})
					.y(function(d) {
						return yScale(d[1]);
					})
					.interpolate("basis");

				var firstGen = d3.svg.line()
					.x(function(d) {
						return xScale(d[0]);
					})
					.y(function(d) {
						return HEIGHT;
					})
					.interpolate("basis");

				// create compound labels
				var compoundLabels = labelGroup.selectAll(".compoundLabel")
					.data(data.compoundLabels);

				compoundLabels.enter().append("text")
					.text(function(d) {
						return d;
					})
					.style("text-anchor", "begin")
					.classed('compoundLabel', true);

				// create sample rows
				var sampleRows = sampleGroup.selectAll('g.sampleRow')
					.data(data.samples, function(d) {
						return d.label;
					});

				// exit ***************************
				sampleRows.exit().remove();

				// enter **********************
				var rowsEnter = sampleRows.enter().append('g')
					.classed('sampleRow', true);

				rowsEnter.append('text')
					.classed('sampleLabel', true)
					.attr('transform', 'translate(-6, 20)')
					.style("text-anchor", "end")
					.text(function(d) {
						return moment(d.timestamp).format("H:mm:ss") + " - " + d.label;
					});

				rowsEnter.append('g')
					.classed('errorGroup', true)
					.attr('transform', 'translate(4, 0)')
					.each(function(d) {
						var t = d3.select(this);
						t.append('rect')
							.attr('width', 20)
							.attr('height', 20)
							.attr('rx', 2)
							.attr('ry', 2)
							.attr('fill', 'red')
							.attr('x', function(d) {
								return gridSize / 2 - 10;
							})
							.attr('y', function(d) {
								return gridSize / 2 - 10;
							})
							.attr('transform', 'translate(-5, 0)'); // XXX: hacks

						t.append('text')
							.attr('fill', 'white')
							.text('!')
							.attr('transform', 'translate(8,20)'); // XXX: hacks
					});

				rowsEnter.each(function(d) {
					d3.select(this).selectAll('.sampleRect').data(d.items)
						.enter()
						.append("rect")
						.attr("value", data.samples[0].label)
						.attr("x", function(d, i) {
							return start + (i * gridSize) + (gridSize / 2);
						})
						.attr("y", function(d) {
							return gridSize / 2;
						})
						.attr("rx", 0)
						.attr("ry", 0)
						.classed("sampleRect", true)
						.attr("width", 0)
						.attr("height", 0)
						.style("fill", "white")
						.on("click", function(d,i){
							var sampleRow = $(this).attr("value");
							//to revert popup to original style if a different element is selected
							//***********
							$(".popup").css("width","90px")
							.css("height","60px")
							.css("top", top);
							$(".pointer").css("top", "20px");
							$(".editInfo").hide();
							$(".popupInfo").show();
							$("#edit").show();
							//***********
							
							var pos = d3.mouse(this);
							var left = d3.event.clientX + ($(this).attr("width") - (pos[0] - $(this).attr("x"))) + 15;
							var top = d3.event.clientY + (($(this).attr("height")/2) - (pos[1] - $(this).attr("y"))) - 30;
							$(".popup").css("left", left)
							.css("top", top)							
							.show();
							$("#popupClose").on("mouseenter", function(){
								$("#popupClose").css("color","rgba(0,0,0,1)");
							});
							$("#popupClose").on("mouseleave", function(){
								$("#popupClose").css("color","rgba(0,0,0,0)");
							});
							$("#popupClose").on("click", function(){
								$(".popup").hide();
								$(".popup").css("width","90px")
								.css("height","60px")
								.css("top", top);
								$(".pointer").css("top", "20px");
								$(".editInfo").hide();
								$(".popupInfo").show();
								$("#edit").show();
							});
							$(".popupInfo").html(Math.round(d.concentration * 100) / 100 + " mg/L<br>" +
									Math.round(d.probability * 100) + "%<br>" +
									function(d){
										if(d.veto){
											return "Vetoed";
										}
										else{
											return "Not Vetoed";
										}}(d));
							$("#edit").off("click");
							$("#edit").on("click", function(){
								$(".popupInfo").hide();
								$("#edit").hide();
								$(".popup").css("width","180px")
									.css("height","84px")
									.css("top", top - 12);
								$(".pointer").css("top", "32px")
								$(".editMessage").html("Is " + data.compoundLabels[i] + " present in " + sampleRow + "?");
								$("#editYes").off("click");
								$("#editYes").on("click", function(){
									//send sample and compound as true
									var o = {
											sample: sampleRow,
											compound: data.compoundLabels[i],
											present: true
									};
									console.log("Edit " + data.compoundLabels[i] + " in " + sampleRow);
									bioService.editSamples(o);
									//revert popup style to original
									$(".popup").css("width","90px")
									.css("height","60px")
									.css("top", top);
									$(".pointer").css("top", "20px");
									$(".editInfo").hide();
									$(".popupInfo").show();
									$("#edit").show();
								});
								$("#editNo").off("click");
								$("#editNo").on("click", function(){
									//send sample and compound as false
									var o = {
											sample: sampleRow,
											compound: data.compoundLabels[i],
											present: false
									};
									console.log("Edit " + data.compoundLabels[i] + " in " + sampleRow);
									bioService.editSamples(o);
									//revert popup style to origina
									$(".popup").css("width","90px")
									.css("height","60px")
									.css("top", top);
									$(".pointer").css("top", "20px");
									$(".editInfo").hide();
									$(".popupInfo").show();
									$("#edit").show();
								});
								//Need close button
								$(".editInfo").show();
							});
						});
				});

				rowsEnter.each(function(d, i) {
					d3.select(this).selectAll('.vetoGroup').data(d.items)
						.enter()
						.append('g')
						.classed('vetoGroup', true)
						.attr('opacity', 0)
						.attr('transform', function(d, i) {
							return 'translate(' + (start + (i * gridSize) + 4) + ',' +  (-1) + ')';
						})
						.each(function(d) {
							var t = d3.select(this);
							t.append('rect')
								.attr('width', (gridSize - 8))
								.attr('height', (gridSize - 8))
								.attr('rx', 2)
								.attr('ry', 2)
								.attr('stroke', 'red')
								.attr('fill', 'none')
								.attr('x', function(d) {
									return gridSize / 2 - (gridSize - 8) / 2;
								})
								.attr('y', function(d) {
									return gridSize / 2 - (gridSize - 8) / 2;
								})
								.attr('transform', 'translate(-5, 0)'); // XXX: hacks

							t.append('text')
								.attr('fill', 'red')
								.attr('font-size', 37)
								.attr('opacity', 0.5)
								.text('x')
								.attr('transform', 'translate(1,25)'); // XXX: hacks
						});
				});

				// add spectra
				var specs = rowsEnter
					.append("g")
					.attr("width", WIDTH)
					.attr("height", HEIGHT)
					.classed("spectrum", true)
					.attr('transform', 'translate(940,0)'); // XXX: Hacks

				// render area
				specs.append("svg:path")
					.attr("opacity", 0)
					.datum(function(d) {
						return d.spectra;
					})
					.attr("class", "area")
					.attr('d', preArea);

				// render line
				specs.append('svg:path')
					.attr("opacity", 0)
					.datum(function(d) {
						return d.spectra;
					})
					.attr('d', firstGen)
					.classed("line", true);

				// update **********************
				compoundLabels.transition()
					.attr("transform", function(d, i) {
						return "translate(" + (start + i * gridSize + gridSize / 2) + ", -6)rotate(-45)";
					});

				sampleRows
					.transition()
					.attr('transform', function(d, i) {
						return 'translate(0, ' + (gridSize * i) + ')';
					});

				sampleRows.each(function(d) {
					d3.select(this).selectAll('.sampleRect').data(d.items)
						.transition()
						.duration(1500)
						.attr("x", function(d, i) {
							if (showProbabilities)
								return start + (i * gridSize) + (gridSize / 2) - (probSize(d.probability) / 2);
							else
								return start + (i * gridSize);
						})
						.attr("y", function(d, i) {
							if (showProbabilities)
								return (gridSize / 2) - (probSize(d.probability) / 2);
							else
								return 0;
						})
						.attr("rx", function(d) {
							if (showProbabilities && (probSize(d.probability) < gridSize * 0.5))
								return 1;
							else
								return 4;
						})
						.attr("ry", function(d) {
							if (showProbabilities && (probSize(d.probability) < gridSize * 0.5))
								return 1;
							else
								return 4;
						})
						.attr("width", function(d) {
							return probSize(d.probability) - gridMargin;
						})
						.attr("height", function(d) {
							return probSize(d.probability) - gridMargin;
						})
						.style("fill", function(d) {
							return chartColor(d.probability);
						});
				});

				rowsEnter.each(function(d, i) {
					d3.select(this).selectAll('.vetoGroup')
						.classed('hideError', function(d,i) {
//							console.log("d", d);
							return d.veto === false;
						})
						.transition()
						.duration(1500)
						.attr('opacity', 1);
					});

				sampleRows.select('.sampleLabel')
					.classed('error', function(d) {
						return d.error.code > 0;
					});

				sampleRows.select('.errorGroup')
					.classed('hideError', function(d) {
						return d.error.code == 0;
					});

				var spectrum = sampleRows.select(".spectrum");

				spectrum.select(".area")
					.datum(function(d) {
						return d.spectra;
					})
					.transition()
					.duration(1500)
					.attr("d", area)
					.attr("opacity", 1);


				spectrum.select(".line")
					.datum(function(d) {
						return d.spectra;
					})
					.transition()
					.duration(1500)
					.attr('d', lineGen)
					.attr('stroke', 'darkBlue')
					.attr('stroke-width', 1)
					.attr('fill', 'none')
					.attr("opacity", 1);

			}

			// Event handlers
			$scope.$on('windowResize', resize);
			$scope.$on('samples', newSamples);
		};
		

		return {
			template: '<div id="bioChart"></div>',
			replace: true,
			restrict: 'E',
			link: link
		}
	}
]);