// create module for custom directives
angular.module('canvas', ['selection', 'configuration', 'highlight']);

// service to contact server
angular.module('canvas').factory('canvasService', ['$http', 'config',
	function($http, config) {
		return {};
	}
]);

// controller business logic
angular.module('canvas').controller('canvasCtrl', ['$scope', '$window', 'canvasService', 'discoveryCanvasService', 'highlightService',
	function($scope, $window, canvasService, discoveryCanvasService, highlightService) {
		// Handle resize events
		$window.addEventListener('resize', function() {
			$scope.$broadcast('windowResize');
		});

		$scope.$on('dataLoaded', function(event) {
			$scope.$broadcast('canvasDataUpdated');
		});
	}
]);

// vis directive
angular.module('canvas').directive('canvasVisualization', ['canvasService', 'selectionService', 'discoveryCanvasService', 'highlightService', 'searchService',
	function(canvasService, selectionService, discoveryCanvasService, highlightService, searchService) {

		// constants
		var margin = 20,
			width = 960,
			height = 500 - .5 - margin;

		var minimumWidth = 600;
		var minimumHeight = 400;

		var radius = 7;
		var graphMargin = 85;

		var newStreamingData = [];
		var newCluster = [];

		// Called by the directive
		function link($scope, $element, $attrs, $window) {
			// set up initial svg object
			var svg = d3.select($element[0])
				.append("svg")
				.attr("width", width)
				.attr("height", height)
				.classed("canvas", true);

			var documentGroup = svg.append('g').classed('documentGroup', true);
			var clusterGroup = svg.append('g').classed('clusterGroup', true);

			var scaleFactor = 1;
			var translation = [0, 0];

			var force = null;
			var clusters;
			var documents;

			var scaleX = d3.scale.linear().domain([0, 1]).rangeRound([graphMargin, width - graphMargin]);
			var scaleY = d3.scale.linear().domain([0, 1]).rangeRound([graphMargin, height - graphMargin]);

			var inverseScaleX = scaleX.invert();
			var inverseScaleY = scaleY.invert();

			var x = d3.scale.linear()
				.domain([0, width])
				.range([0, width]);

			var y = d3.scale.linear()
				.domain([0, height])
				.range([0, height]);

			var mode = "pan";

			var groups;

			var dataNodes = [];

			var dataLinks = [];

			$scope.tickCounter = 0;

			$scope.$on('windowResize', resize);
			$scope.$on('canvasDataUpdated', update);
			$scope.$on('groupDataUpdated', update);

			// Sizes documents based on their boosted weights
			$scope.$on('updateDocSizes', function(event, docIdToWeightMap) {
				console.log("Weights", docIdToWeightMap);

				var docs = discoveryCanvasService.getDocuments();
				d3.keys(docIdToWeightMap).forEach(function(d) {
					docs[d].weight = docIdToWeightMap[d];
				});

				// sizes then update on 'tick'
			});

			// Behaviors
			var drag = d3.behavior.drag()
				.origin(function(d) {
					return d;
				})
				.on("dragstart", dragstarted)
				.on("drag", dragged)
				.on("dragend", dragended);


			var semanticZoom = d3.behavior.zoom()
				.x(x).y(y)
				.scaleExtent([0.3, 15])
				.on("zoom", zoom);

			svg.call(semanticZoom);

			d3.selection.prototype.moveToFront = function() {
				return this.each(function() {
					console.log("MovingToFront:", this);
					this.parentNode.appendChild(this);
				});
			};

			// Resize management
			function resize() {
				width = $("#visualization").innerWidth();
				height = $("#visualization").innerHeight();

				svg.attr("width", width)
					.attr("height", height);

				scaleX = d3.scale.linear().domain([0, 1]).range([graphMargin, width - graphMargin]);
				scaleY = d3.scale.linear().domain([0, 1]).range([graphMargin, height - graphMargin]);
			}

			//  Format the data for the force layout
			function formatDataForForceLayout(data) {
				console.log('Formatting data to fit force layout', data, dataNodes);
				if (force != null) {
					force.stop();
				}

				// Restructure input to support force layout
				d3.values(data.clusters).forEach(function(d) {
					// Skip the ungrouped cluster
					if (d.clusterId == -1) {
						return;
					}

					var source = d;
					var sourceIndex = dataNodes.indexOf(source);

					// Check to see if we already have a node for the cluster
					if (sourceIndex > -1) {
						// We already have this cluster
					} else {
						// New cluster
						source.className = "cluster";
						source.fixed = true;
						source.x = scaleX(source.x);
						source.y = scaleY(source.y);
						sourceIndex = dataNodes.push(source) - 1;
					}

					source.members.forEach(function(member, index) {
						// member is document id
						var targetIndex = dataNodes.indexOf(data.documents[member]);

						// Check to see if we already have a node for the cluster
						if (targetIndex > -1) {
							// We already have this document
						} else {
							// New document
							targetIndex = dataNodes.push(data.documents[member]) - 1;
						}

						// Check to see if the document is connected to the correct cluster
						var foundLink = false;
						var linkToRemove = null;
						dataLinks.forEach(function(d) {
							if (d.target === data.documents[member]) {
								if (d.source != source) {
									removeLink = d;
								}
								foundLink = true;
							}
						});

						var link = {};
						link.source = sourceIndex;
						link.target = targetIndex;

						if (linkToRemove != null) {
							dataLinks.splice(dataLinks.indexOf(linkToRemove), 0);
						}

						if (foundLink == false) {
							dataLinks.push(link);
						}
					});
				});

				console.log("Nodes: " + dataNodes.length);
				console.log("Links: " + dataLinks.length);

				// Construct force layout
				force = d3.layout.force()
					.size([1280, 1024])
					.nodes(dataNodes)
					.links(dataLinks)
					.gravity(0)
					.start();
			}

			// Update the data
			function update() {
				var data = {
					clusters: discoveryCanvasService.getClusters(),
					documents: discoveryCanvasService.getDocuments()
				};

				console.log("Cluster", data.clusters, "Doc", data.documents);
				if (d3.keys(data.clusters).length == 0 || typeof(data.clusters) === 'undefined') {
					return;
				}

				formatDataForForceLayout(data);

				// Create clusters - ENTER ---------------------------------------
				clusters = clusterGroup.selectAll(".cluster")
					.data(dataNodes.filter(function(d) {
						return d.className === "cluster";
					}), function(d) {
						return d.clusterId;
					});

				var clustersEnter = clusters.enter()
					.append("g")
					.attr("cluster-id", function(d) {
						return d.clusterId;
					})
					.classed("node", true)
					.classed("cluster", true)
					.on("click", function(d) {
						if ($scope.draggingCluster === d.clusterId) {
							$scope.draggingCluster = null;
							return;
						}

						if (d3.event.defaultPrevented)
							return;

						// clicked a group, highlight that group
						var clusters = discoveryCanvasService.getClusters();
						var cluster = clusters[d.clusterId];
						var docIds = cluster.members;

						var highlight = {
							type: 'group',
							id: d.clusterId,
							title: cluster.clusterLabel,
							docIds: docIds
						};

						searchService.clearSearch();
						highlightService.replaceHighlighted(highlight);

						d3.event.stopPropagation();
					})
					.on('mouseover', function(d) {
						var dragging = d3.select('.dragging');
						if (dragging.empty())
							return;

						var draggingDocument = dragging.classed('document');

						// check here to see if its over its current cluster
						if (draggingDocument) {
							var docClusterId;
							dragging.each(function(doc) {
								docClusterId = doc.clusterId;
							});

							var dropCluster = d3.select(this);
							var thisClusterId;
							dropCluster.each(function(cluster) {
								thisClusterId = cluster.clusterId;
							});
							dropCluster.classed('dropTarget', (docClusterId != thisClusterId));
						}
					})
					.on('mouseout', function(d) {
						d3.select(this).classed('dropTarget', false);
					})
					.call(drag);

				var nodeLabelGroup = clustersEnter.append('g')
					.classed("node-label-group", true)

				nodeLabelGroup.append("rect")
					.classed("node-label-background", true);

				nodeLabelGroup.append("text")
					.classed("node-label", true);

				// Clusters -- UPDATE -----------------------------------------------
				// var badge = clusters.append('foreignObject')
				// 	.attr({
				// 		'x': 0,
				// 		'y': -10,
				// 		'width': 50,
				// 		'height': 50,
				// 		'class': 'cluster-badge'
				// 	});

				// var div = badge.append('xhtml:div')
				// 	.append('span')
				// 	.attr('class', 'badge')
				// 	.html("");

				clusters.selectAll("text")
					.text(function(d) {
						if (d.clusterLabel === '') {
							return '<unlabeled>'
						}
						return d.clusterLabel;
					});

				clusters.selectAll("rect")
					.attr("width", function(d) {
						return d3.select(this.parentNode).select("text").node().getBBox().width + 6;
					})
					.attr("height", function(d) {
						return d3.select(this.parentNode).select("text").node().getBBox().height + 6;
					})
					.attr("x", function(d) {
						return d3.select(this.parentNode).select("text").node().getBBox().x - 3;
					})
					.attr("y", function(d) {
						return d3.select(this.parentNode).select("text").node().getBBox().y - 3;
					});

				clusters.selectAll('.node-label-group').attr("transform", function(d) {
					var width = d3.select(this).select("rect").node().getBBox().width - 6;
					var height = d3.select(this).select("rect").node().getBBox().height + 6;

					return "translate(" + (-width / 2) + ", " + (height / 2) + ")";
				});

				// Create documents -- ENTER ----------------------------------------------
				console.log("Data Nodes", dataNodes);
				documents = documentGroup.selectAll(".document")
					.data(dataNodes.filter(function(d) {
						return d.className === "document";
					}, function(d) {
						return d.docID;
					}));

				var docGroupEnter = documents.enter()
					.append("g")
					.classed("node", true)
					.classed("document", true)
					.on("click", function(d) {
						if (d3.event.defaultPrevented)
							return;

						var sel = {};
						sel.type = "document";
						sel.id = d.docID;
						sel.title = d.title;
						console.log("Canvas: DocumentSelected:", sel);
						selectionService.setSelection(sel);

						if (d3.event.sourceEvent)
							d3.event.sourceEvent.stopPropagation();
					}).each(function(d) {
						d3.select(this).append("rect")
							.attr("width", "8.5")
							.attr("height", "11");
						d3.select(this).append("rect")
							.attr("width", "2")
							.attr("height", "11")
							.classed("bookmarkRect", true);
					})
					.call(drag);

				// Documents -- UPDATE -------------------------------------------------
				documents
					.classed('read-0', function(d) {
						return d.read == 0;
					})
					.classed('read-1', function(d) {
						return d.read == 1;
					})
					.classed('read-2', function(d) {
						return d.read == 2;
					})
					.classed('read-3', function(d) {
						return d.read == 3;
					})
					.classed('read-4', function(d) {
						return d.read > 3;
					})
					.selectAll('.bookmarkRect')
					.classed("bookmarked", function(d) {
						return d.bookmarked;
					});

				forceRestart();
				force.on("tick", tick);

				// EXIT
				// Currently nothing leaves...
				// clusters.exit().remove();
				// documents.exit().remove();
			}

			// ZUI

			function forceRestart() {
				$scope.tickCounter = 0;
				force.resume();
			}

			function zoom() {
				scaleFactor = d3.event.scale;
				translation = d3.event.translate;
				force.resume();
			}

			function stopped() {
				if (d3.event.defaultPrevented) d3.event.stopPropagation();
			}

			function tick() {
				$scope.tickCounter++;

				documents.attr("transform", function(d) {
					return "translate(" + (translation[0] + scaleFactor * d.x) + ", " + (translation[1] + scaleFactor * d.y) + ")" +
						"scale(" + (0.25 * d.docWeight + 0.75) + ")";
				});

				if ($scope.tickCounter > 125) {
					force.stop();
				}

				clusters.attr("transform", function(d) {
					return "translate(" + (translation[0] + scaleFactor * d.x) + ", " + (translation[1] + scaleFactor * d.y) + ")";
				});

			}

			function dragstarted(d) {
				d3.event.sourceEvent.stopPropagation();
				d3.event.sourceEvent.preventDefault();

				d3.select(this).classed("dragging", true);
				d.oldFixed = d.fixed;
				d.fixed = true;
				// force.resume();
			}

			function dragged(d) {
				d3.event.sourceEvent.stopPropagation();
				d3.event.sourceEvent.preventDefault();

				if (d3.event.dx != 0 || d3.event.dy != 0) {
					$scope.draggingCluster = d.clusterId;
					forceRestart();
				} else {
					$scope.draggingCluster = null;				}

				// update "previous" positions to ensure graph layout works correctly
				d.px = d.x;
				d.py = d.y;

				// get mouse coordinates relative to the visualization coordinate system:
				var mouse = d3.mouse(svg.node());
				d.x = (mouse[0] - translation[0]) / scaleFactor;
				d.y = (mouse[1] - translation[1]) / scaleFactor;
				// force.resume();
			}

			function dragended(d) {
				d3.event.sourceEvent.stopPropagation();
				d3.event.sourceEvent.preventDefault();

				d3.select(this).classed("dragging", false);
				d.fixed = d.oldFixed;
				// force.resume();

				if (d.className === "cluster" && $scope.draggingCluster == d.clusterId) {
					// Moved a cluster; tell server
					var move = {};
					move.clusterId = d.clusterId;
					move.x = scaleX.invert(d.x);
					move.y = scaleY.invert(d.y);
					$scope.$emit('clusterMoved', move);
					return;
				}

				// need to check to see if we dropped a document on a cluster
				var dropTarget = d3.select('.dropTarget');
				if (dropTarget.empty())
					return;

				// We got here, do d is a document, and dropTarget is a selection of the cluster
				dropTarget.each(function(cluster) {
					var e = {};
					e.docId = d.docID;
					e.oldClusterId = d.clusterId;
					e.newClusterId = cluster.clusterId;

					// move the document locally
					moveDocument(e);

					// let the server know
					$scope.$emit('documentClusterChange', e);
				});
			}

			function moveDocument(input) {
				var links = force.links();
				var nodes = force.nodes();

				links.forEach(function(d, i) {
					if ((d.source.clusterId == input.oldClusterId && d.target.docID == input.docId) || (d.source.docID == input.docId && d.target.clusterId == input.oldClusterId)) {
						// remove existing link
						links.splice(i, 1);

						// find new cluster
						var newCluster;
						nodes.forEach(function(d) {
							if (d.clusterId == input.newClusterId && d.className == 'cluster') {
								newCluster = d;
							}
						})

						// find current document
						var currentDocument;
						if (d.source.docID == input.docId)
							currentDocument = d.source;
						else
							currentDocument = d.target;

						// add new link
						var link = {};
						link.source = newCluster;
						link.target = currentDocument;
						links.push(link);
					}

					force.start();
				});
			}

			// Do Stuff
			resize();

			// // move the badge
			// labels.selectAll(".cluster-badge")
			// 	.attr("x", function(d) {
			// 		return d3.select(this.parentNode).select("rect").node().getBBox().x + d3.select(this.parentNode).select("rect").node().getBBox().width;
			// 	})
			// 	.attr("y", function(d) {
			// 		return d3.select(this.parentNode).select("rect").node().getBBox().y - d3.select(this.parentNode).select("rect").node().getBBox().height / 2;
			// 	});
			// }

			// 	var badge = labelGroup.append('foreignObject')
			// 		.attr({
			// 			'x': 0,
			// 			'y': -10,
			// 			'width': 50,
			// 			'height': 50,
			// 			'class': 'cluster-badge'
			// 		});

			// 	var div = badge.append('xhtml:div')
			// 		.append('span')
			// 		.attr('class', 'badge')
			// 		.html("");

		}

		return {
			template: '<div class="chart col-xs"></div>',
			replace: true,
			restrict: 'E',
			link: link
		}
	}
]);