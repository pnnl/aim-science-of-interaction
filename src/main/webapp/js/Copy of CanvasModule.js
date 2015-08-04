// create module for custom directives
angular.module('canvas', ['selection', 'configuration', 'highlight']);

// service to contact server
angular.module('canvas').factory('canvasService', ['$http', 'config',
	function($http, config) {
		return {
		};
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

		// Called by the directive?
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

			var checkNewDocumentsTimer;

			var selectedOriginalDocument;

			$scope.$on('windowResize', resize);
			$scope.$on('canvasDataUpdated', update);

			// Sizes documents based on their boosted weights
			$scope.$on('updateDocSizes', function(event, docIdToWeightMap) {
				console.log("Weights", docIdToWeightMap);

				var docs = discoveryCanvasService.getDocuments();
				d3.keys(docIdToWeightMap).forEach(function(d) {
					docs[d].weight = docIdToWeightMap[d];
				});
			});

			$scope.$on('labels', function(event, data) {
				labelChanged(data);
			});
			$scope.$on('removeFromCluster', function(event, data) {
				removeFromCluster(data);
			});

			$scope.$on('listen', function(event, listen) {
				if (listen) {
					startStream();
					checkNewDocumentsTimer = setInterval(checkNewDocuments, 5000);
				} else {
					stopStream();
					clearInterval(checkNewDocumentsTimer);
				}
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

				if (typeof brush != 'undefined') {
					brush.x(d3.scale.identity().domain([0, width - graphMargin]))
						.y(d3.scale.identity().domain([0, height - graphMargin]));
				}
			}

			// initialize the force layout at the beginning
			function initialize(data) {
				console.log('Formatting data to fit force layout');
				dataNodes = [];
				dataLinks = [];

				// Restructure input to support force layout
				d3.keys(data.clusters).forEach(function(d) {
					var source = data.clusters[d];
					source.className = "cluster";
					source.fixed = true;
					source.x = scaleX(source.x);
					source.y = scaleY(source.y);

					if (source.clusterId != -1) {
						// Only add cluster if not the 'ungrouped' cluster
						var sourceIndex = dataNodes.push(source) - 1;
					}

					source.members.forEach(function(member, index) {
						// member is document id
						var targetIndex = dataNodes.push(data.documents[member]) - 1;
						member.className = "document";
						member.clusterId = source.clusterId;

						if (source.clusterId != -1) {
							// Only add cluster if not the 'ungrouped' cluster
							var link = {};
							link.source = sourceIndex;
							link.target = targetIndex;
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

			var dataNodes = [];
			var dataLinks = [];

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

				initialize(data);

				// Create clusters
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

				// Create label for clusters
				var labelGroup = clustersEnter.append("g")
					.classed("node-label-group", true);

				labelGroup.append("rect")
					.classed("node-label-background", true);

				labelGroup.append("text")
					.classed("node-label", true);

				var labels = clusters.selectAll(".node-label-group");

				var badge = labels.append('foreignObject')
					.attr({
						'x': 0,
						'y': -10,
						'width': 50,
						'height': 50,
						'class': 'cluster-badge'
					});

				var div = badge.append('xhtml:div')
					.append('span')
					.attr('class', 'badge')
					.html("");

				labels.selectAll("text")
					.text(function(d) {
						if (d.clusterLabel === '') {
							return '<unlabelled>'
						}
						return d.clusterLabel;
					});

				labels.selectAll("rect")
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

				labelGroup.attr("transform", function(d) {
					var width = d3.select(this).select("rect").node().getBBox().width - 6;
					var height = d3.select(this).select("rect").node().getBBox().height + 6;

					return "translate(" + (-width / 2) + ", " + (height / 2) + ")";
				});

				// Create documents
				console.log("Data Nodes", dataNodes);
				documents = documentGroup.selectAll(".document")
					.data(dataNodes.filter(function(d) {
						return d.className === "document";
					}, function(d) {
						d.clusterId + "-" + d.docID;
					}));

				var docGroupEnter = documents.enter()
					.append("g")
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
					.on("click", function(d) {
						if (d3.event.defaultPrevented)
							return;

						var sel = {};
						sel.type = "document";
						sel.id = d.docID;
						sel.title = d.title;
						selectionService.setSelection(sel);
						console.log("Document Selected:", sel);
						updateVisualIndicators(d.docID);
						d3.event.stopPropagation();
					})
					.classed("node", true)
					.classed("document", true)
					.call(drag);

				docGroupEnter
					.append("rect")
					.attr("width", "8.5")
					.attr("height", "11");

				docGroupEnter
					.append("rect")
					.attr("width", "2")
					.attr("height", "11")
					.classed("bookmarkRect", true)
					.classed("bookmarked", function(d) {
						return d.bookmarked;
					});

				force.on("tick", tick);

				// EXIT
				// clusters.exit().remove();
				// documents.exit().remove();

				selectionChanged();
			}

			function labelChanged(input) {
				console.log("Changing labels...");

				var clusterMap = {};
				input.forEach(function(d, index) {
					clusterMap[d.clusterId] = d;
				});

				var labels = clusters.selectAll(".node-label-group");
				labels.each(function(d) {
					var cl = clusterMap[d.clusterId];
					d.clusterLabel = cl.clusterLabel;
				});

				labels.selectAll("text")
					.text(function(d) {
						if (d.clusterLabel === '') {
							return "<unlabelled>";
						}
						return d.clusterLabel;
					});

				labels.selectAll("rect")
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

				labels.attr("transform", function(d) {
					var width = d3.select(this).select("rect").node().getBBox().width - 6;
					var height = d3.select(this).select("rect").node().getBBox().height + 6;

					return "translate(" + (-width / 2) + ", " + (height / 2) + ")";
				});

				// move the badge
				labels.selectAll(".cluster-badge")
					.attr("x", function(d) {
						return d3.select(this.parentNode).select("rect").node().getBBox().x + d3.select(this.parentNode).select("rect").node().getBBox().width;
					})
					.attr("y", function(d) {
						return d3.select(this.parentNode).select("rect").node().getBBox().y - d3.select(this.parentNode).select("rect").node().getBBox().height / 2;
					});
			}

			function tick() {
				clusters.attr("transform", function(d) {
					return "translate(" + (translation[0] + scaleFactor * d.x) + ", " + (translation[1] + scaleFactor * d.y) + ")";
				});

				documents.attr("transform", function(d) {
					return "translate(" + (translation[0] + scaleFactor * d.x) + ", " + (translation[1] + scaleFactor * d.y) + ")" +
						"scale(" + (0.25 * d.weight + 0.75) + ")";
				});
			}

			function dragstarted(d) {
				d3.event.sourceEvent.stopPropagation();
				d3.select(this).classed("dragging", true);
				d.oldFixed = d.fixed;
				d.fixed = true;
				force.resume();
			}

			function dragged(d) {
				d3.event.sourceEvent.stopPropagation();

				// update "previous" positions to ensure graph layout works correctly
				d.px = d.x;
				d.py = d.y;

				// get mouse coordinates relative to the visualization coordinate system:
				var mouse = d3.mouse(svg.node());
				d.x = (mouse[0] - translation[0]) / scaleFactor;
				d.y = (mouse[1] - translation[1]) / scaleFactor;
				force.resume();
				// tick(); //re-position this node and any links
			}

			function dragended(d) {
				d3.event.sourceEvent.stopPropagation();

				d3.select(this).classed("dragging", false);
				d.fixed = d.oldFixed;
				force.resume();

				if (d.className === "cluster") {
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
				});
			}

			function addDocToCluster(input, doc) {
				var links = force.links();
				var nodes = force.nodes();

				// find cluster
				var cluster;
				nodes.forEach(function(d) {
					if (d.className == 'cluster' && d.clusterId == input.clusterId) {
						cluster = d;
					}
				});

				var newDoc = {};
				newDoc.docID = input.docId;
				newDoc.title = input.title;
				newDoc.docText = input.docText;

				newDoc.className = "document";
				newDoc.clusterId = input.clusterId;
				newDoc.px = cluster.px;
				newDoc.py = cluster.py;
				newDoc.weight = cluster.weight;
				newDoc.x = cluster.x;
				newDoc.y = cluster.y;
				newDoc.status = "new";
				newDoc.notificationLevel = doc.notificationLevel;
				newDoc.snippet = doc.snippet;
				var documentIndex = nodes.push(newDoc) - 1;

				var link = {};
				link.source = cluster;
				link.target = documentIndex;
				links.push(link);
			}

			function createCluster(input) {
				var links = force.links();
				var nodes = force.nodes();

				var newCluster = {};
				newCluster.className = "cluster";
				newCluster.clusterId = input.clusterId;
				newCluster.clusterLabel = input.label;
				newCluster.x = input.x;
				newCluster.y = input.y;
				newCluster.px = input.x;
				newCluster.py = input.y;
				newCluster.fixed = true;
				newCluster.status = "new";
				var clusterIndex = nodes.push(newCluster) - 1;
			}

			// Selection management

			function selectionChanged() {
			}

			// ZUI

			function zoom() {
				scaleFactor = d3.event.scale;
				translation = d3.event.translate;
				force.resume();
			}

			function stopped() {
				if (d3.event.defaultPrevented) d3.event.stopPropagation();
			}

			resize();

			//////////////////timer for checking the new documents ////////////
			// TODO: These should be added to the service not in the directive
			function checkNewDocuments() {
				$.ajax({
					url: "/discoveryStreaming/newdata",
					type: 'GET',
					success: function(newDocs, status) {
						newStreamingData.push.apply(newStreamingData, newDocs);
						redrawing(newDocs);
					}
				});
			}

			function startStream() {
				$.ajax({
					url: "/discoveryStreaming/start",
					type: 'GET',
					success: function(newDocs, status) {
						console.print("stream monitor started");
					}
				});
			}

			function stopStream() {
				$.ajax({
					url: "/discoveryStreaming/stop",
					type: 'GET',
					success: function(newDocs, status) {
						console.print("stream monitor stopped");
					}
				});
			}

			function updateDataNode(d) {
				var data = d.datum();
				d.classed("read", true);

				data.status = "read";
				var newDocs = $.grep(dataNodes, function(e) {
					return e.clusterId == data.clusterId && e.className == "document" && e.status == "new";
				});

				$("[cluster-id=" + data.clusterId + "] .cluster-badge span")
					.html(newDocs.length == 0 ? "" : newDocs.length);
			}

			function updateVisualIndicators(docId) {
				var docDom = documents.filter(function(e) {
					return e.docID == docId && e.className == "document";
				}).call(updateDataNode);
			}

			// redrawing without rendering
			function redrawing(newDocs) {
				console.log("new Doc size: " + newDocs.length);
				if (newDocs.length == 0 || typeof(newDocs.length) === 'undefined') {
					return;
				}

				// add new nodes and links of new documents
				var len = newDocs.length;
				while (len--) {
					dataNodes = force.nodes();
					dataLinks = force.links();

					var newDoc = newDocs[len];
					console.log("clusterID: " + newDoc.clusterId + ", docID: " + newDoc.member.docID + ", docText: " + newDoc.member.docText);
					var cluster = $.grep(dataNodes, function(e) {
						return e.className == "cluster" && e.clusterId == newDoc.clusterId;
					});
					if (cluster.length > 0) {
						var clusterIndex = cluster[0].index;

						var e = {};
						e.docId = newDoc.member.docID;
						e.title = newDoc.member.title;
						e.docText = newDoc.member.docText;
						e.clusterId = cluster[0].clusterId;

						// move the document locally
						addDocToCluster(e, newDoc);

						// let the server know
						$scope.$emit('addDocToCluster', e);
					} else { // when it needs a new cluster
						// add a new cluster
						console.log("create a new cluster; id: " + newDoc.clusterId);

						var e = {};
						e.clusterId = newDoc.clusterId;
						e.label = '';
						e.x = 0;
						e.y = 0;
						e.docId = newDoc.member.docID;
						e.docText = newDoc.member.docText;

						// create the cluster locally
						createCluster(e);
						addDocToCluster(e, newDoc);
						$scope.$emit('createClusterWithDoc', e);
					}
				}

				dataNodes = force.nodes();
				dataLinks = force.links();

				// sort 
				var $clusterGroup = $('g.clusterGroup'),
					$clusters = $clusterGroup.children('g');

				$clusters.sort(function(a, b) {
					var aId = a.getAttribute('cluster-id'),
						bId = b.getAttribute('cluster-id');
					if (aId > bId) {
						return 1;
					}
					if (aId < bId) {
						return -1;
					}
					return 0;
				});

				$clusters.detach().appendTo($clusterGroup);

				// Create clusters
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
						if (d3.event.defaultPrevented)
							return;

						var sel = {};
						sel.type = "cluster";
						sel.id = d.clusterId;
						sel.action = "Click on " + d.clusterId;
						selectionService.setSelection(sel);
						console.log("Cluster Selected: " + JSON.stringify(sel));
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

				// Create label for clusters
				var labelGroup = clustersEnter.append("g")
					.classed("node-label-group", true);

				labelGroup.append("rect")
					.classed("node-label-background", true)
					.on("mouseenter", function() {
						d3.select(this.parentNode.parentNode).moveToFront();
					});

				labelGroup.append("text")
					.classed("node-label", true);

				var badge = labelGroup.append('foreignObject')
					.attr({
						'x': 0,
						'y': -10,
						'width': 50,
						'height': 50,
						'class': 'cluster-badge'
					});

				var div = badge.append('xhtml:div')
					.append('span')
					.attr('class', 'badge')
					.html("");
				///////// add to inbox
				len = newDocs.length;
				var i = 0;
				while (i < len) {
					var newDoc = newDocs[i];
					if (newDoc.notificationLevel == "high" && newDoc.snippet != "") {
						if ($("#newdoc .panel.recommend").length == 0) {
							$("#newdoc.row")
							//								.css("width", $("body").width()*.25)
							.html('<div class="panel panel-default recommend" style="margin-bottom:0px;">' +
								'<div class="panel-heading">Recommended Documents From Stream</div>' +
								'<div class="panel-body"><div class="list-group" style="max-height:' + 200 + 'px;overflow-y:scroll;"></div></div></div>');
						}

						$("#newdoc .panel.recommend .list-group").prepend(
							'<a href="#" class="list-group-item" doc-id="' + newDoc.member.docID + '">' +
							'<h4 class="list-group-item-heading">' + newDoc.member.title + '</h4>' +
							'<p class="list-group-item-text">' + newDoc.snippet + '</p></a>');

						$("#newdoc .panel.recommend .list-group-item")
							.on("mouseover", function(e) {
								$(this).addClass("active");
							})
							.on("mouseout", function(e) {
								$(this).removeClass("active");
							})
							.on("click", function(e) {
								var sel = {};
								sel.type = "document";
								sel.id = $(this).attr("doc-id");
								sel.action = "Click on " + sel.id + " from a list of NEW documents";
								selectionService.setSelection(sel);
								sel.title = $(this).children("h4").html();
								console.log("Document Selected: " + JSON.stringify(sel));
								updateVisualIndicators(sel.id);
								e.stopPropagation();
							});
					}

					i++;
				}

				console.log("Nodes: " + dataNodes.length);
				console.log("Links: " + dataLinks.length);

				var labels = clusters.selectAll(".node-label-group")
					.attr("visibility", function(d) {
						var newDocs = $.grep(dataNodes, function(e) {
							return e.clusterId == d.clusterId && e.className == "document" && e.status == "new";
						});

						if (d.clusterLabel != '' || newDocs.length > 0) {
							return "visible";
						} else {
							return "hidden";
						}
					});

				labels.selectAll("text")
					.text(function(d) {
						if (d.clusterLabel) {
							return d.clusterLabel;
						} else {

						}
					});

				labels.selectAll("rect")
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

				// add badges
				clusters.selectAll(".cluster-badge")
					.attr("x", function(d) {
						return d3.select(this.parentNode).select("rect").node().getBBox().x + d3.select(this.parentNode).select("rect").node().getBBox().width;
					})
					.attr("y", function(d) {
						return d3.select(this.parentNode).select("rect").node().getBBox().y;
					});

				clusters.selectAll(".cluster-badge span")
					.html(function(d) {
						var newDocs = $.grep(dataNodes, function(e) {
							return e.clusterId == d.clusterId && e.className == "document" && e.status == "new";
						});
						return newDocs.length == 0 ? "" : newDocs.length;
					});

				// Create documents
				documents = documentGroup.selectAll(".document")
					.data(dataNodes.filter(function(d) {
						return d.className === "document"; // && d.notificationLevel != "low";
					}, function(d) {
						d.clusterId + "-" + d.docID;
					}));

				documents.enter()
					.append("rect")
					.attr("width", "8.5")
					.attr("height", "11")
					.attr("class", function(d) {
						return "cluster-" + d.clusterId;
					})
					.classed("node", true)
					.classed("document", true)
					.style("display", function(d) {
						if (d.notificationLevel != "low") {
							return 'block';
						} else {
							return 'none';
						}
					})
					.on("click", function(d) {
						if (d3.event.defaultPrevented)
							return;

						var sel = {};
						sel.type = "document";
						sel.id = d.docID;
						sel.action = "Click on " + d.docID;
						selectionService.setSelection(sel);
						sel.title = d.title;
						console.log("Document Selected: " + JSON.stringify(sel));
						// update the visual  indicators
						updateVisualIndicators(d.docID);
						d3.event.stopPropagation();
					})
					.call(drag);
				resize();
				force.start();
			}

			// redrawing without rendering
			function updateNodes() {
				dataNodes = force.nodes();
				dataLinks = force.links();

				// sort 
				//				var $clusterGroup = $('g.clusterGroup'),
				//				$clusters = $clusterGroup.children('g');
				//				
				//				$clusters.sort(function(a, b){
				//					var aId = a.getAttribute('cluster-id'),
				//					bId = b.getAttribute('cluster-id');
				//					if (aId > bId) {
				//						return 1;
				//					}
				//					if (aId < bId) {
				//						return -1;
				//					}
				//					return 0;
				//				});
				//				
				//				$clusters.detach().appendTo($clusterGroup);

				// Create clusters
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
						if (d3.event.defaultPrevented)
							return;

						var sel = {};
						sel.type = "cluster";
						sel.id = d.clusterId;
						sel.action = "Click on " + d.clusterId;
						selectionService.setSelection(sel);
						console.log("Cluster Selected: " + JSON.stringify(sel));
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


				// Create label for clusters
				var labelGroup = clustersEnter.append("g")
					.classed("node-label-group", true);

				labelGroup.append("rect")
					.classed("node-label-background", true)
					.on("mouseenter", function() {
						d3.select(this.parentNode.parentNode).moveToFront();
					});

				labelGroup.append("text")
					.classed("node-label", true);

				var badge = labelGroup.append('foreignObject')
					.attr({
						'x': 0,
						'y': -10,
						'width': 50,
						'height': 50,
						'class': 'cluster-badge'
					});

				var div = badge.append('xhtml:div')
					.append('span')
					.attr('class', 'badge')
					.html("");

				console.log("Nodes: " + dataNodes.length);
				console.log("Links: " + dataLinks.length);

				var labels = clusters.selectAll(".node-label-group")
					.attr("visibility", function(d) {
						var newDocs = $.grep(dataNodes, function(e) {
							return e.clusterId == d.clusterId && e.className == "document" && e.status == "new";
						});

						if (d.clusterLabel != '' || newDocs.length > 0) {
							return "visible";
						} else {
							return "hidden";
						}
					});

				labels.selectAll("text")
					.text(function(d) {
						if (d.clusterLabel) {
							return d.clusterLabel;
						} else {

						}
					});

				labels.selectAll("rect")
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

				// add badges
				clusters.selectAll(".cluster-badge")
					.attr("x", function(d) {
						return d3.select(this.parentNode).select("rect").node().getBBox().x + d3.select(this.parentNode).select("rect").node().getBBox().width;
					})
					.attr("y", function(d) {
						return d3.select(this.parentNode).select("rect").node().getBBox().y;
					});

				clusters.selectAll(".cluster-badge span")
					.html(function(d) {
						var newDocs = $.grep(dataNodes, function(e) {
							return e.clusterId == d.clusterId && e.className == "document" && e.status == "new";
						});
						return newDocs.length == 0 ? "" : newDocs.length;
					});

				// Create documents
				documents = documentGroup.selectAll(".document")
					.data(dataNodes.filter(function(d) {
						return d.className === "document"; // && d.notificationLevel != "low";
					}, function(d) {
						d.clusterId + "-" + d.docID;
					}));

				documents.enter()
					.append("rect")
					.attr("width", "8.5")
					.attr("height", "11")
					.attr("class", function(d) {
						return "cluster-" + d.clusterId;
					})
					.classed("node", true)
					.classed("document", true)
					.style("display", function(d) {
						if (d.notificationLevel != "low") {
							return 'block';
						} else {
							return 'none';
						}
					})
					.on("click", function(d) {
						if (d3.event.defaultPrevented)
							return;

						var sel = {};
						sel.type = "document";
						sel.id = d.docID;
						sel.action = "Click on " + d.docID;
						selectionService.setSelection(sel);
						sel.title = d.title;
						console.log("Document Selected: " + JSON.stringify(sel));
						// update the visual  indicators
						updateVisualIndicators(d.docID);
						d3.event.stopPropagation();
					})
					.call(drag);
				resize();
				force.start();
			}
		}

		return {
			template: '<div class="chart col-xs"></div>',
			replace: true,
			restrict: 'E',
			link: link
		}
	}
]);