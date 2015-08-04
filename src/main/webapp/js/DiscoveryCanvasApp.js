angular.module('discoveryCanvas', ['configuration', 'selection', 'canvas', 'documentViewer', 'search', 'highlight']);

// service to contact server
angular.module('discoveryCanvas').factory('discoveryCanvasService', ['$http', 'config',
	function($http, config) {
		var documents = {};
		var clusters = {};

		return {
			loadData: function() {
				var url = "/" + config.webappRoot + "/initial";
				return $http.get(url, {
					params: {}
				});
			},

			getNewDocuments: function() {
				var url = "/" + config.webappRoot + "/newdata";
				return $http.get(url, {
					params: {}
				});
			},

			getLatestData: function() {
				var url = "/" + config.webappRoot + "/latest";
				return $http.get(url, {
					params: {}
				});
			},

			getNextIncrement: function() {
				var url = "/" + config.webappRoot + "/next";
				return $http.get(url, {
					params: {}
				});
			},

			loginAIM: function(userdata) {
				console.log(userdata.username + " login to AIM");
				var url = "/" + config.webappRoot + "/initial?username=" + userdata.username;

				return $http({
					withCredentials: false,
					method: 'get',
					url: url
				});
			},

			search: function(data) {
				var url = "/" + config.webappRoot + "/search";

				var o = {};
				o.id = data.query;
				o.count = data.count;

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					headers: {
						'Content-Type': 'application/json'
					},
					data: o
				});
			},

			moveDocument: function(input) {
				var url = "/" + config.webappRoot + "/moveToCluster";
				console.log("Moving document to a new cluster");

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					headers: {
						'Content-Type': 'application/json'
					},
					data: input
				});
			},

			/** Informs the server of a cluster position change. 
			 *
			 * @param clusterMove consists of {clusterId, x, y}
			 */
			moveCluster: function(clusterMove) {
				var url = "/" + config.webappRoot + "/moveCluster";

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					headers: {
						'Content-Type': 'application/json'
					},
					data: clusterMove
				});
			},

			removeFromCluster: function(docId) {
				console.log("Removing document from cluster: " + docId);
				var url = "/" + config.webappRoot + "/removeFromCluster";

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					headers: {
						'Content-Type': 'application/json'
					},
					data: docId
				});
			},

			labelCluster: function(data) {
				console.log("Labelling cluster data", data);
				var url = "/" + config.webappRoot + "/labelCluster";

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					headers: {
						'Content-Type': 'application/json'
					},
					data: data
				});
			},

			createClusterWithDoc: function(docId) {
				console.log("Creating cluster", docId);
				var url = "/" + config.webappRoot + "/createClusterWithDoc";

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					headers: {
						'Content-Type': 'application/json'
					},
					data: docId383
				});
			},

			createNewCluster: function() {
				console.log("Creating new cluster");
				var url = "/" + config.webappRoot + "/createNewCluster";

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					headers: {
						'Content-Type': 'application/json'
					}
				});
			},

			addDocToCluster: function(data) {
				console.log("add new doc to cluster: " + data.docId);
				var url = "/" + config.webappRoot + "/addDocToCluster";

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					headers: {
						'Content-Type': 'application/json'
					},
					data: data
				});
			},

			addHighlight: function(data) {
				console.log("add highlight to document: ", data);
				var url = "/" + config.webappRoot + "/highlightDocument";

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					dataType: 'json',
					headers: {
						'Content-Type': 'application/json'
					},
					mimeType: 'application/json',
					data: data
				});
			},

			bookmarkDocument: function(docId) {
				console.log("bookmark document: ", docId);
				var url = "/" + config.webappRoot + "/bookmarkDocument";

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					dataType: 'json',
					headers: {
						'Content-Type': 'application/json'
					},
					mimeType: 'application/json',
					data: docId
				});
			},

			documentRead: function(docId, readCount) {
				console.log("DocumentRead: ", docId);
				var url = "/" + config.webappRoot + "/documentRead";

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					dataType: 'json',
					headers: {
						'Content-Type': 'application/json'
					},
					mimeType: 'application/json',
					data: {
						docId: docId,
						readCount: readCount
					}
				});
			},

			loginASI: function(userdata) {
				console.log(userdata.username + " login to ASI");
				//				var url = "/" + config.webappRoot + "/login?username=" + userdata.username + "&password=" + userdata.password;
				var url = "/" + config.webappRoot + "/login";

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					dataType: 'json',
					headers: {
						'Content-Type': 'application/json'
					},
					mimeType: 'application/json',
					data: userdata
				});
			},

			// Gets all document weights based on the current boosted weighting. 
			// The weights are in the form of an Object mapping doc ID to 
			// weight (0=smallest to 3=largest).
			getDocumentWeights: function() {
				console.log("get document weights");
				var url = "/" + config.webappRoot + "/getDocumentWeights";

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					headers: {
						'Content-Type': 'application/json'
					}
				});
			},

			// Updates all document weights based on the current boosted weighting.
			// Returns the entire dataset 
			updateDocumentWeights: function() {
				console.log("UpdateDocumentWeights");
				var url = "/" + config.webappRoot + "/updateDocumentWeights";

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					headers: {
						'Content-Type': 'application/json'
					}
				});
			},

			updateDocument: function(documentId) {
				console.log("updating document");
				var url = "/" + config.webappRoot + "/getDocument";

				return $http({
					withCredentials: false,
					method: 'post',
					url: url,
					headers: {
						'Content-Type': 'application/json'
					},
					data: documentId
				});
			},

			// Primary access to the list of documents from the client
			// Map of docID to document
			getDocuments: function() {
				return documents;
			},

			// Primary access to the list of clusters from the client
			// Map of clusterId to cluster
			getClusters: function() {
				return clusters;
			},

			//REST call for clicking on diffenent areas of the site
			siteClick: function(area) {
				console.log("Clicked on " + area);
				var url = "/" + config.webappRoot + "/siteClick?area=" + area;
				$.get(url);
			}
		};
	}
]);

// controller business logic
angular.module('discoveryCanvas').controller('discoveryCtrl', ['$scope', '$window', 'documentViewerService', 'discoveryCanvasService', 'selectionService', 'searchService', 'highlightService',
	function($scope, $window, documentViewerService, discoveryCanvasService, selectionService, searchService, highlightService) {
		$scope.documentViewerData = {};

		// Initialize login
		function login() {
			var userdata = {};
			userdata.username = $(".login-username").val();
			//userdata.password = $(".login-password").val();
			//$scope.$emit('loginASI', userdata);
			discoveryCanvasService.loginAIM(userdata)
				.success(function(result) {
					if (result) $scope.$broadcast('init', result);
				});
			$('.login-modal').modal('hide');
		}

		// register login button event.
		$('#login-modal').on('shown.bs.modal', function() {
			$('#login-username').focus();
		})

		$(".login-button").on('click', function(e) {
			login();
		});

		$(".login-modal input").keypress(function(event) {
			if (event.which == 13) {
				login();
			}
		});

		$('#cluster-label').on('shown.bs.modal', function() {
			$('#clusterLabelText').focus();
		})

		$("#labelCluster").submit(function(event) {
			event.preventDefault();
			$('#cluster-label').modal('hide');
		});

		$("#submitLabel").on('click', function(event) {
			event.preventDefault();
			$('#cluster-label').modal('hide');
			$scope.renameHighlightedGroup();
		});

		// SUPPORT FUNCTIONS
		$scope.updateDocument = function(documentId) {
			discoveryCanvasService.updateDocument(documentId)
				.success(function(result) {
					var existingDoc = discoveryCanvasService.getDocuments()[documentId];
					mergeDocument(existingDoc, result);

					console.log("UpdatedDocument", documentId);
					$scope.$broadcast('documentUpdated', documentId);
				});
		}

		// UI Functions /////////////////////

		$scope.search = function() {
			console.log("Query: " + $scope.query);
			discoveryCanvasService.search({
				query: $scope.query,
				count: 1000
			}).success(function(result) {
				console.log("Hits: " + result);
				if (result.length == 0) {
					alert("No documents matched your search.");
				} else {
					searchService.addSearch($scope.query, result);
				}
			});
		};

		$scope.clearHighlight = function() {
			highlightService.clearHighlight();
		};

		$scope.clearSelection = function() {
			selectionService.clearSelection();
		};

		$scope.bookmarkSelectedDocument = function() {
			var docId = selectionService.getSelection().id;

			discoveryCanvasService.bookmarkDocument(docId)
				.success(function(result) {
					$scope.updateDocument(docId);
					getLatestData();
				});
		};

		$scope.renameHighlightedGroup = function() {
			var input = {
				clusterId: highlightService.getHighlight().id,
				label: $scope.label
			};

			discoveryCanvasService.labelCluster(input)
				.success(function(result) {
					// result is the updated full dataset
					$scope.mergeNewData(result);
				});
		};

		$scope.nextIncrement = function() {
			discoveryCanvasService.getNextIncrement()
				.success(function(result) {
					console.log("NewDocuments", result);
					if (result.length > 0) {
						getLatestData();
					}
				});
		};

		$scope.setListenToStream = function(listen) {
			$scope.$broadcast('listen', listen);
		};

		$scope.selectDocumentFromList = function(doc) {
			var sel = {};
			sel.id = doc.docID;
			sel.title = doc.title;
			selectionService.setSelection(sel);
		};

		$scope.getDocumentListIcon = function(doc) {
			if (doc.selected) {
				return "images/selected-icon.png";
			}
			if (doc.highlighted) {
				return "images/highlighted-icon.png";
			}
		};

		$scope.isGroupHighlighted = function() {
			var highlight = highlightService.getHighlight();
			return highlight.type === 'group';
		};

		$scope.isDocumentSelected = function() {
			var selected = selectionService.getSelection();
			return selected != undefined && selected.id != undefined;
		};

		$scope.createGroupFromDocument = function() {
			if ($scope.isDocumentSelected() == +false)
				return;

			console.log("CreateGroup", selectionService.getSelection().id);
			discoveryCanvasService.createClusterWithDoc(selectionService.getSelection().id)
				.success(function(result) {
					// this returns the entire cluster data again
					$scope.mergeNewData(result);
				});
		};

		$scope.createGroup = function() {
			console.log("CreateGroup");
			discoveryCanvasService.createNewCluster()
				.success(function(result) {
					// this returns the entire cluster data again
					$scope.mergeNewData(result);
				});
		};

		function getLatestData() {
			discoveryCanvasService.getLatestData()
				.success(function(result) {
					$scope.mergeNewData(result);
				});
		}

		/** 
		 * Gets the document sizes from the server, and broadcasts an event.
		 * Should be called after any operation that affects feature weighting.
		 */
		function updateDocSizes() {
			console.log("UpdateDocSizes");
			discoveryCanvasService.updateDocumentWeights()
				.success(function(data) {
					$scope.mergeNewData(data);
				});
		}

		function mergeDocument(existingDoc, newDoc) {
			existingDoc.bookmarked = newDoc.bookmarked;
			existingDoc.docWeight = newDoc.docWeight;
			existingDoc.highlights = newDoc.highlights;
			existingDoc.clusterId = newDoc.clusterId;
			existingDoc.read = newDoc.read;
		}

		$scope.mergeNewData = function(newData) {
			console.log("MergingNewData", newData);

			var documents = discoveryCanvasService.getDocuments();
			var clusters = discoveryCanvasService.getClusters();

			newData.forEach(function(cluster) {
				// Check to see if we already have this cluster
				var existing = clusters[cluster.clusterId];
				if (existing != undefined) {
					// Cluster exists, update data to match
					existing.clusterLabel = cluster.clusterLabel;

					// Existing members should match incoming members
					existing.members = cluster.members.map(function(doc) {
						return doc.docID;
					});
				} else {
					// This is a new cluster

					// Store cluster by ID
					clusters[cluster.clusterId] = cluster;

					// Remap cluster members to just ids
					cluster.members = cluster.members.map(function(doc) {
						return doc.docID;
					});
				}

				// Ensure we have a document for each of the members
				cluster.members.forEach(function(doc) {
					var existingDoc = documents[doc.docID];

					if (existingDoc != undefined) {
						// Doc exists, update all fields
						mergeDocument(existingDoc, doc);
					} else {
						// New doc
						documents[doc.docID] = doc;
						doc.selected = false;
						doc.highlighted = false;
					}
				});
			});

			$scope.$broadcast('groupDataUpdated');

			var highlight = highlightService.getHighlight();
			if (highlight.type === 'group') {
				var cluster = discoveryCanvasService.getClusters()[highlight.id];
				highlight.title = cluster.clusterLabel;
				highlightService.resendHighlight();
			}
		}

		$scope.$on('dataLoaded', function(event) {
			// updateDocSizes();
		});

		$scope.$on('documentClusterChange', function(event, data) {
			console.log("Document moved to new cluster", data);

			// Update local data structure
			var clusters = discoveryCanvasService.getClusters();
			var newCluster = clusters[data.newClusterId];
			var oldCluster = clusters[data.oldClusterId];

			var index = oldCluster.members.indexOf(data.docId);
			oldCluster.members.splice(index, 1);
			newCluster.members.push(data.docId);

			// Update server
			discoveryCanvasService.moveDocument(data)
				.success(function(result) {
					getLatestData();
				});

			// Potentially update highlights
			if ($scope.isGroupHighlighted()) {
				var highlight = highlightService.getHighlight();
				var clusterId = null;
				if (data.newClusterId === highlight.id) {
					clusterId = data.newClusterId;
				} else if (data.oldClusterId === highlight.id) {
					clusterId = data.oldClusterId;
				} else {
					return;
				}

				// clicked a group, highlight that group
				var clusters = discoveryCanvasService.getClusters();
				var cluster = clusters[clusterId];
				var docIds = cluster.members;

				var highlight = {
					type: 'group',
					id: clusterId,
					title: cluster.clusterLabel,
					docIds: docIds
				};

				searchService.clearSearch();
				highlightService.replaceHighlighted(highlight);
			}
		});

		// Forward move notification to server
		$scope.$on('clusterMoved', function(event, data) {
			discoveryCanvasService.moveCluster(data)
				.success(function(result) {
					console.log("ClusterMoved");
				});
		});

		$scope.$on('addDocToCluster', function(event, data) {
			discoveryCanvasService.addDocToCluster(data)
				.success(function(result) {
					$scope.$broadcast('updateDocList', data);
				});
		});

		$scope.$on('loginASI', function(event, userdata) {
			discoveryCanvasService.loginASI(userdata);
		});

		$scope.$on('loginAIM', function(event, userdata) {
			discoveryCanvasService.loginAIM(userdata)
				.success(function(result) {
					if (result) $scope.$broadcast('init', result);
				});;
		});

		$scope.$on('createClusterWithDoc', function(event, data) {
			discoveryCanvasService.createClusterWithDoc(data);
		});

		$scope.$on('createNewClusterWithDocId', function(event, data) {
			var cluster = {};
			cluster.label = "";
			cluster.x = Math.floor(data.x);
			cluster.y = Math.floor(data.y);
			discoveryCanvasService.createNewCluster(cluster)
				.success(function(newClusterId) {
					var input = {};
					input.docId = data.docID;
					input.oldClusterId = data.clusterId;
					input.newClusterId = newClusterId;
					discoveryCanvasService.moveDocument(input)
						.success(function(result) {
							var info = {};
							info.doc = data;
							info.newClusterId = newClusterId;
							$scope.$broadcast('createNewClusterViewer', info);
						});
				});
		});

		$scope.$on('labellingCluster', function(event, data) {
			console.log("Labelling cluster: " + data);
			discoveryCanvasService.labelCluster(data)
				.success(function(result) {
					console.log("Label changed.");
					$scope.$broadcast('labels', result);
				});
		});

		$scope.$on('removingFromCluster', function(event, data) {
			console.log("Removing cluster: " + data);
			discoveryCanvasService.removeFromCluster(data)
				.success(function(result) {
					console.log("Document removed from cluster.");
					$scope.$broadcast('canvasData', result);
				});
		});

		$scope.$on('search', function(event, data) {
			var search = data.search;
			console.log("OnSearch:", search);

			// Update labels under search
			var searchTagData = d3.select('#searchTags')
				.selectAll('span.tag')
				.data(search, function(d) {
					return d.string;
				});

			var spans = searchTagData.enter()
				.append('span')
				.classed({
					'tag': true,
					'label': true,
					'label-search': true
				});

			spans.append('span')
				.classed('tagText', true);

			spans.append('a')
				.append('i')
				.classed({
					'remove': true,
					'glyphicon': true,
					'glyphicon-remove': true,
					'glyphicon-white': true
				})
				.on('click', function(d) {
					searchService.removeSearch(d.string);
				});

			searchTagData.select('span')
				.text(function(d) {
					return d.string;
				});

			searchTagData.exit().remove();

			var allIds = [];
			search.forEach(function(d) {
				var ids = d.ids;
				ids.forEach(function(d) {
					if (allIds.indexOf(d) < 0) {
						allIds.push(d);
					}
				});
			});

			var highlight = {
				type: 'search',
				title: 'Search Results',
				docIds: allIds
			};

			highlightService.replaceHighlighted(highlight);
		});

		$scope.$on('highlight', function(event, data) {
			console.log("Got Highlight", data);

			// cleared highlight
			if (d3.keys(data.highlight).length === 0) {
				d3.selectAll("svg.canvas .document").classed('highlighted', function(d) {
					d.highlighted = false;
					return false;
				});

				$scope.documentList = [];
				$scope.documentListTitle = "Nothing Highlighted";

				if (!$scope.$$phase) {
					$scope.$apply();
				}

				return;
			}

			var ids = data.highlight.docIds;
			var documents = discoveryCanvasService.getDocuments();

			// d3.selectAll('svg.canvas .document').filter(function(d) {
			// 	ids.forEach(function(s) {
			// 		return s.indexOf(d.docID);
			// 	});
			// }).moveToFront();

			// Update highlighted class on nodes
			d3.selectAll("svg.canvas .document").classed('highlighted', function(d) {
				var found = false;
				ids.forEach(function(s) {
					if (s.indexOf(d.docID) >= 0) {
						found = true;
					} else {
						d.highlighted = false;
					}
				});

				// update the highlighted flag for each
				d.highlighted = found;
				return found;
			});

			var documents = discoveryCanvasService.getDocuments();
			var docs = ids.map(function(d) {
				return documents[d];
			});

			console.log("Highlighted docs:", docs);
			$scope.documentList = docs;

			if (ids.length == 0 && data.highlight.type != 'group') {
				$scope.documentListTitle = "Nothing Highlighted";
			} else {
				$scope.label = highlightService.getHighlight().title;
				$scope.documentListTitle = data.highlight.title;
			}

			if (!$scope.$$phase) {
				$scope.$apply();
			}
		});

		$scope.$on("selection", function(event, data) {
			console.log("Selection", data);
			var selection = data.selection;

			// Seleciton cleared
			if (selection === null) {
				// Updated selected class on nodes
				d3.selectAll("svg.canvas .document")
					.classed("selected", function(d) {
						d.selected = false;
						return false;
					});

				$scope.$broadcast('documentSelected', null);
				return;
			}

			// Move it to the front
			// d3.selectAll('svg.canvas .document').filter(function(d) {
			// 	return selection.id === d.docID;
			// }).moveToFront();

			// Updated selected class on nodes
			d3.selectAll("svg.canvas .document")
				.classed("selected", function(d) {
					var selected = selection.id === d.docID;
					d.selected = selected;
					return selected;
				});

			// Load the data
			$scope.$broadcast('documentSelected', selection.id);

			// If a document has been selected, we need to test
			// to see if it was highlighted, if so, don't clear 
			// highlighted state
			var isHighlighted = false;
			d3.selectAll("svg.canvas .document.selected")
				.each(function(d) {
					if (d3.select(this).classed('highlighted')) {
						isHighlighted = true;
					}
				});

			if (!isHighlighted) {
				var highlight = {
					type: 'single',
					title: "Single Document Highlighted",
					docIds: [selection.id]
				};

				searchService.clearSearch();
				highlightService.replaceHighlighted(highlight);
			}
		});

		$scope.$on("documentRead", function(event, data) {
			console.log("DocRead", data);
			d3.selectAll("svg.canvas .document")
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

			discoveryCanvasService.documentRead(data.docID, data.read)
				.success(function(result) {
					$scope.updateDocument(data.docID);
				});
		});

		$scope.documentList = [];
		$scope.documentListTitle = "Nothing Highlighted";

		$scope.$on('init', function(event, data) {
			var result = data;
			// Load the data
			if (result.error) {
				throw new Error(result.message);
			} else {
				var documents = discoveryCanvasService.getDocuments();
				var clusters = discoveryCanvasService.getClusters();

				result.forEach(function(cluster) {
					// Store cluster by ID
					clusters[cluster.clusterId] = cluster;

					// Store document by ID
					cluster.members.forEach(function(doc) {
						documents[doc.docID] = doc;
						doc.selected = false;
						doc.highlighted = false;
						doc.clusterId = cluster.clusterId;
					})

					// Remap cluster members to just ids
					cluster.members = cluster.members.map(function(doc) {
						return doc.docID;
					});
				});

				console.log("Initial", documents, clusters);

				$scope.$broadcast('dataLoaded');
			}
		});


		$("#visualization").on("click", function() {
			discoveryCanvasService.siteClick("canvas");
		});

		$("#documentList").on("click", function() {
			discoveryCanvasService.siteClick("list");
		});

		$("#documentViewer").on("click", function() {
			discoveryCanvasService.siteClick("doc");
		});
	}

]);