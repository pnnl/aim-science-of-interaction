angular.module("documentViewer", ['configuration', 'selection']);

// service to contact server
angular.module('documentViewer').factory('documentViewerService', ['$http', 'config',
    function($http, config) {
        return {
            loadDocument: function(data) {
                console.log("Loading full document: " + data);
                var url = "/" + config.webappRoot + "/fullDocument";

                var o = {};
                o.id = data;

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

            loadCluster: function(data) {
                console.log("Loading cluster data: " + data.id);
                var url = "/" + config.webappRoot + "/getCluster";

                var o = {};
                o.id = data.id;

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

            scrollEvent: function(location) {
                console.log("scrolling " + location);
                var url = "/" + config.webappRoot + "/scroll?location=" + location;
                $.get(url);
            }
        };
    }
]);

angular.module('documentViewer').controller("documentViewerController", ['$scope', 'documentViewerService', '$sce', 'discoveryCanvasService', 'searchService',
    function($scope, documentViewerService, $sce, discoveryCanvasService, searchService) {
        var timerForAnnotation = null;

        $scope.selectedDocumentTitle = "No document selected";
        $scope.originalDocument;

        $scope.$on('documentSelected', function(event, data) {
            // clear selection
            if (data === null) {
                $scope.documentText = "";
                $scope.selectedDocumentTitle = "No document selected";
                return;
            }

            console.log("DocumentSelected", data);

            $scope.originalDocument = {};
            $scope.documentViewerData = {};
            $scope.selectedDocumentTitle = "updating...";
            $scope.documentText = "";

            getDocumentText(data);

            // Mark document as read
            var doc = discoveryCanvasService.getDocuments()[data];
            doc.read = doc.read + 1;
            $scope.$emit('documentRead', doc);
        });

        $scope.$on('documentUpdated', function(event, data) {
            if ($scope.documentViewerData.id === data) {
                console.log("Need to update current document", $scope.documentViewerData.id);
                $scope.documentText = $sce.trustAsHtml($scope.documentViewerData.data);
                processAnnotations();
                processSearch();
            }
        });

        $scope.$on('search', function(event, data) {
            if ($scope.documentViewerData.id === undefined)
                return;

            console.log("Need to update search", $scope.documentViewerData.id);
            $scope.documentText = "";
            getDocumentText($scope.documentViewerData.id);
        });

        function getDocumentText(documentId) {
            console.log("GetDocumentText", documentId);
            documentViewerService.loadDocument(documentId)
                .success(function(result) {
                    if (result.error) {
                        throw new Error(result.message);
                    } else {
                        var doc = discoveryCanvasService.getDocuments()[documentId];
                        var input = {};
                        input.data = result.data;
                        input.id = doc.docID;
                        input.title = doc.title;

                        // Give the document viewer the data
                        updateViewerContent(input);
                    }
                });
        }

        function updateViewerContent(input) {
            console.log("DocViewer", input);

            $scope.originalDocument = input.data;
            $scope.documentViewerData = input;
            $scope.selectedDocumentTitle = input.title;

            $scope.documentText = $sce.trustAsHtml(input.data);
            processAnnotations();
            processSearch();
        }

        function processAnnotations() {
            console.log("ProcessAnnotations", $scope.documentViewerData.id);
            var doc = discoveryCanvasService.getDocuments()[$scope.documentViewerData.id];
            console.log("DocHighlights", doc.highlights);

            setTimeout(function() {
                doc.highlights.forEach(function(d) {
                    $('#text-viewer').blast({
                        search: d.text,
                        tag: "span",
                        customClass: "annotated",
                        stripHTMLTags: false
                    });
                });
            }, 0);
        }

        function processSearch() {
            var search = searchService.getSearch();
            console.log("ProcessSearch", $scope.documentViewerData.id, search);

            setTimeout(function() {
                search.forEach(function(d) {
                    $('#text-viewer').blast({
                        search: d.string,
                        tag: "span",
                        customClass: "search",
                        stripHTMLTags: false
                    });
                });
            }, 0);
        }

        // SCROLLING EVENT --- NOT SURE ABOUT THIS
        onScroll = function(type) {
            // Send rest call with list parameter
            // Destroy event and then re-add after 5 seconds to prevent continuous scroll events
            documentViewerService.scrollEvent(type);
            $("#" + type + "Scroll").off("scroll");
            setTimeout(function() {
                $("#" + type + "Scroll").on("scroll", function() {
                    onScroll(type);
                });
            }, 5000);
        };


        $("#docScroll").on("scroll", function() {
            onScroll("doc");
        });

        $("#listScroll").on("scroll", function() {
            onScroll("list");
        });
        //////////////////////////////

        // ANNOTATIONS
        $("#text-viewer").bind('mouseup', showTextHighlightMenu);

        function showTextHighlightMenu(e) {
            if (timerForAnnotation != null) {
                $("#annotation").hide();
                clearTimeout(timerForAnnotation);
            }

            $scope.currentWindowSelection = window.getSelection();
            var selectedText = $scope.currentWindowSelection.toString().trim();
            console.log("Selected Text:", selectedText);

            if (selectedText !== '') {
                oRange = $scope.currentWindowSelection.getRangeAt(0); //get the text range
                oRect = oRange.getBoundingClientRect();

                console.log('"' + selectedText + '" was selected at ' + e.pageX + '/' + e.pageY, oRange);
                timerForAnnotation = setTimeout(showAnnotationButton(oRect.left - 35, oRect.top - 35), 2000);
            }
        }

        function showAnnotationButton(left, top) {
            $("#annotation").css('left', left + 'px');
            $("#annotation").css('top', top + 'px');
            $("#annotation").html('<button type="submit" class="btn btn-default" ng-click="addHighlight()"><span class="glyphicon glyphicon-comment" aria-hidden="true"></span></button>');
            $("#annotation").show("slow");

            $("#annotation button").bind('click', hideAnnotationButton);
        }

        function hideAnnotationButton() {
                $("#annotation").hide();

                console.log('hideAnnotationButton');
                $scope.addHighlight();
            }
            //////////////////////////////

        $scope.labelCluster = function() {
            console.log("Label cluster");
            var data = {};
            data.id = $scope.documentViewerData.data.clusterId;
            data.label = $scope.label;
            $scope.$emit('labellingCluster', data);
        }

        $scope.removeFromCluster = function() {
            var data = {};
            data.id = $scope.documentViewerData.id;
            $scope.$emit('removingFromCluster', data);
        }

        $scope.createNewClusterWithDocId = function() {
            d3.select("g.documentGroup").selectAll(".document")
                .each(function(d) {
                    if (d.docID == $scope.documentViewerData.id)
                        $scope.$emit('createNewClusterWithDocId', d);
                })
        }

        $scope.addHighlight = function() {
            var data = {};
            data.docId = $scope.documentViewerData.id;
            data.text = $scope.currentWindowSelection.toString().trim();
            data.start = $scope.currentWindowSelection.getRangeAt(0).startOffset;

            discoveryCanvasService.addHighlight(data)
                .success(function(result) {
                    var doc = discoveryCanvasService.getDocuments()[data.docId];
                    if (doc.bookmark) {
                        // already bookmarked, force an update for the highlight
                        $scope.updateDocument(data.docId);
                    } else {
                        // not bookmarked, bookmark, which will cause a doc update
                        $scope.bookmarkSelectedDocument();
                    }
                });
        }
    }
]);