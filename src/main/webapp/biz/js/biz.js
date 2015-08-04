(function() {

	var lobs = ["Clothing-OPA", "Clothing-Shyre", "Automotive-OPA", "Automotive-Shyre", "Appliances-OPA", "Appliances-Shyre", "Superstores-OPA", "Superstores-Shyre", "Department Stores-OPA", "Department Stores-Shyre", "Athletic Stores-OPA", "Athletic Stores-Shyre"];
	var lobs2 = ["clothing-OPA", "clothing-Shyre", "automotive-OPA", "automotive-Shyre", "appliances-OPA", "appliances-Shyre", "superstores-OPA", "superstores-Shyre", "department stores-OPA", "department stores-Shyre", "athletic stores-OPA", "athletic stores-Shyre"];
	var palette = d3.scale.category20();
	palette.domain(lobs);
	var palette2 = d3.scale.category20();
	var palette3 = d3.scale.category20();
	palette3.domain(lobs2);

	var biz = {
		version : "3.5.3",
		companies : {},
		linesOfBusiness : [],
		maxSeriesSize : 50,
		chartWidth : 800,
		chartHeight : 300,
		currentCompID : -1,
		bubbleUpdateInterval : 2000,
		epsilon : .1,
		scale : 7,
		threshold : 2,
		exUpdater : null
	};

	var dendrogram;
	var bubbles;
	var bubblesData;

	for (i = 0; i < lobs.length; i++) {
		biz.linesOfBusiness.push({
			name: lobs[i],
			color: palette(lobs[i])
		});
	}

	biz.initApp = function() {

		for (i = 0; i < lobs.length; i++) {
			$("#leg-labels") .append("<li><span class='legend-key' style='background:" + biz.linesOfBusiness[i].color.toString() + ";'></span>" + biz.linesOfBusiness[i].name + "</li>");
		}

		$('[data-toggle="tooltip"]').tooltip();

		dendrogram = d3.select("#dendrogram-holder")
				.append("svg")
				.attr("class", "Dendrogram")
				.style("width", 650)
				.style("height", 200) // function of the dataaaa
				.chart("Dendrogram")
						.key(function(d,i){
							if(d.key)
							{
								if(d.parent)
								{
									return d.parent.key+d.key;
								}
									else {
										return d.key;
									}
							}
							else {
								return i;
							}
						})
						.text(function(d){
								if ("key" in d) {
										return d.key;
								}
								return d.obj;
						})
						.margin({top: 10, bottom: 10, left: -80, right:200});

	    bubbles = d3.select("#bubble-holder")
			.append("svg")
			.attr("class", "bubbles")
			.style("width", 550)
			.style("height", 400)
			.chart("Bubble")
	        .key(function(d){return d.key;})
	        .value(function(d){
	            return Math.sqrt(d.score) * biz.scale;
	        })
	        .threshold(biz.threshold * biz.scale)
	        .text(function(d){return d.name;});

		// set the attribute based on the type of feature (uppercase = port, lowercase = type)
		bubbles.layer("bubbles")
		    .on("enter", function(){
		        this.attr("feature-type", function(d){
		            return d.name === d.name.toUpperCase() ? "port" : "merchandise";
		        });
				this.attr("lob", function(d) {
					return d.lineOfBusiness.replace(' ', '');
				});
//				this.style("fill", function(d) {
//					var c = palette3(d.lineOfBusiness + "-OPA");
//					return c;
//				});
		    })
		    .on("merge", function(){
		        this.style("stroke-dasharray", function(d){
		            return d.inMean > d.outMean ? null : "5,5";
		        });
		    });
	}

	biz.addCompany = function(colID, compID, compName, elChart, elAxis) {

		$("#column1") .append(
				'<div class="panel panel-default">'
						+ '<div class="panel-heading" style="font-size: 16pt; color: black; font-weight: bold;">'
						+ '<a href="#' + compID + '"">'
						+ compName
						+ '</a> '
						+ '<span class="pull-right" id="alert-' + compID + '">'

						+ '<div class="dropdown">'
						+ '  <button class="btn btn-danger dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">'
						+ '    <span class="glyphicon glyphicon-certificate" style="color: white;" aria-hidden="true"></span>'
						+ '    <span class="caret"></span>'
						+ '  </button>'
						+ '  <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">'
						+ '    <li><a href="#' + compID + '">Explore <strong>' + compName + '</strong> further ...</a></li>'
						+ '    <li><a href="javascript: biz.clearAlert(' + compID + ');">Clear Alert</a></li>'
						+ '  </ul>'
						+ '</div>'

						// + '<button type="button" class="btn btn-danger" data-toggle="tooltip" data-placement="bottom" title="POP has detected an anomaly."><span class="glyphicon glyphicon-fire" style="color: white;" aria-hidden="true"></span></button>'

						+ '</div>'
						+ '	<div class="panel-body text-center">'
						+ '	<div class="chart_container">'
						+ '		<div id="chart' + compID + '" class="chart"></div>'
						+ '	</div>' + '</div>');

        $("#dropcompanies").append("<li><a href=\"#" + compID + "\">" + compName + "</a></li>");

		$("#alert-" + compID).hide();

		var s = [],
				color = palette2
				margin = {top: 5, bottom: 30, left: 10, right: 170};

		s = biz.linesOfBusiness.map(function(d) {
				return {
							key: d.name,
							values: d3.range(biz.maxSeriesSize).map(function(d) { return {x: d, y: 0}; })
						};
				});

		var g = d3.select("#chart" + compID)
				.append("svg")
				.attr("class", "Flow")
				.attr("width", biz.chartWidth)
				.attr("height", biz.chartHeight)
				.chart("Flow")  // Line
						// .size([biz.chartWidth, biz.chartHeight])
						.y(d3.scale.linear().domain([0,1]))
						.threshold(0.3)
						.margin(margin)
						.axis(true)
						.label(true)
						.align('right');

		g.layer("data").on("enter", function(){
				this.attr("class", "Flow")
				this.style("fill", function(_,i) { return color(i); })
				this.style("stroke", function(_,i) { return color(i); });

				// onclick mouse event stub
				this.on("click", function(d){
					console.log(d);
				});

				// mouseover event stub
				// this.on("mouseover", function(d){
				// 	console.log(d);
				// });
		});

		d3.select("#chart" + compID).selectAll("svg").append("text")
      .attr("class", "label")
      .attr("text-anchor", "middle")
      .attr("transform", "translate(" + (biz.chartWidth - margin.right)/2 + ", " + biz.chartHeight + ")")
			.attr("id", "xlabel")
      .text("Seconds");

		biz.companies[compName.toLowerCase()] = {
			id: compID,
			name : compName,
			counter : biz.maxSeriesSize,
			graph : g,
			series : s
		};
	};

	biz.changeChartType = function(type, compID, compName) {
		color = palette2;

		d3.select("#chart" + compID).select('svg').html("");

		if(type == 'Area')
		{
			var g = d3.select("#chart" + compID)
					.select("svg")
					.attr("class", type)
					.attr("width", biz.chartWidth)
					.attr("height", biz.chartHeight)
					.chart(type)  // Line, Bar, Flow, Area
							// .size([biz.chartWidth, biz.chartHeight])
							.y(d3.scale.linear().domain([0,1]))
							.threshold(.3)
							.margin(margin)
							.axis(true)
							.label(true)
							.align('right')
							.offset('zero');
		}
		else {
			var g = d3.select("#chart" + compID)
					.select("svg")
					.attr("class", type)
					.attr("width", biz.chartWidth)
					.attr("height", biz.chartHeight)
					.chart(type)  // Line, Bar, Flow, Area
							// .size([biz.chartWidth, biz.chartHeight])
							.y(d3.scale.linear().domain([0,1]))
							.threshold(.3)
							.margin(margin)
							.axis(true)
							.label(true)
							.align('right');
		}


		g.layer("data").on("enter", function(){
				this.attr("class", type)
				this.style("fill", function(_,i) { return (type == 'Line') ? "none" : color(i); });
				this.style("stroke", function(_,i) { return color(i); });
				this.style("stroke-width", function(_,i) { return (type == 'Line') ? 3 : 1; });

				// onclick mouse event stub
				this.on("click", function(d){
					console.log(d);
				});

				// mouseover event stub
				// this.on("mouseover", function(d){
				// 	console.log(d);
				// });
				//this.style("fill", function(_,i) { return color(i); });
		});

		d3.select("#chart" + compID).selectAll("svg").append("text")
      .attr("class", "label")
      .attr("text-anchor", "middle")
      .attr("transform", "translate(" + (biz.chartWidth - margin.right)/2 + ", " + biz.chartHeight + ")")
			.attr("id", "xlabel")
      .text("Seconds");

		biz.companies[compName.toLowerCase()].graph = g;
	};

	biz.resizeChart = function(compID, compName) {
		compName = compName.toLowerCase();
		var g = d3.select("#chart" + compID)
				.select("svg")
				.attr("width", biz.chartWidth)
				.attr("height", biz.chartHeight);

		d3.select("#xlabel")
      .attr("transform", "translate(" + (biz.chartWidth - margin.right)/2 + ", " + biz.chartHeight + ")");

		biz.companies[compName].graph.draw(biz.companies[compName].series);
	};

	biz.init = function(compName) {
		compName = compName.toLowerCase();
		biz.companies[compName].graph.draw(biz.companies[compName].series);
	};

	biz.addLOBProbability = function(compName, probability) {
		compName = compName.toLowerCase();
		if (compName == "jcpenny") {
			compName = "jcpenney";
		}
		biz.companies[compName].series.forEach(function(d, i) {

			d.values.push({x: d.values[d.values.length-1].x + 1, y: probability[i]});
			if (d.values.length > biz.maxSeriesSize) {
				d.values.splice(0, 1);
			}
		});

		biz.companies[compName].counter++;
		biz.companies[compName].graph.draw(biz.companies[compName].series);
	};

	biz.setAlert = function(compName, state) {

		compName = compName.toLowerCase();
		if (compName == "jcpenny") {
			compName = "jcpenney";
		}

		var compID = biz.companies[compName].id;
		if (state) {
			$("#alert-" + compID).show();
		} else {
			$("#alert-" + compID).hide();
		}
	};

	biz.clearAlert = function(compID) {
		$("#alert-" + compID).hide();
	};

	biz.getCompany = function(compID) {

		for (var company in biz.companies) {
			if (biz.companies[company].id == compID) {
				return biz.companies[company];
			}
		}
		return null;
	}

	biz.hideDetails = function(compID) {
		clearInterval(biz.exUpdater);
	};

	biz.displayDetails = function(compID) {

		// Get the company object
		var company = biz.getCompany(compID);

		$("#dropcompname").text(company.name);
		//$("#droplob").text("Automotive");

		// Load records
		biz.loadOPAExplainers(compID);
		biz.loadShyreExplainers(compID);
		biz.loadRecords(compID);
		biz.loadContext(compID);
		//biz.loadEmerging(compID);
	}

	biz.loadEmerging = function(compID) {

	    console.log("load emerging");

		$("#ehead").empty();
		$("#ebody").empty();

		$.ajax({
			url: "/discoveryStreaming/lob/company/emerging",
			type: 'GET',
			success: function(recordset, status) {

				// Append the column header
				var headers = "";
				for (i = 0; i < recordset.columns.length; i++) {
					headers += "<th>" + recordset.columns[i] + "</th>";
				}
				$("#ehead").append('<tr>' + headers + '</tr>');

				// Append each record
				for (i = 0; i < recordset.records.length; i++) {
					var record = recordset.records[i];

					var recordVals = "";
					var recordRow = "";
					for (j = 0; j < record.values.length; j++) {

						recordVals += "|" + record.values[j];
						var menu = "";
						if (j ==  record.values.length - 1) {
							menu = "<div class=\"btn-group pull-right\">" +
								   "<button class=\"btn btn-default btn-xs\" type=\"button\" data-company=\"" + compID + "\" data-id=\"" + recordVals.substring(1) + "\" id=\"add-model-" + i + "\">Add to Model</button>" +
						           "</div>";
						}

						recordRow += "<td>" + record.values[j] + menu + "</td>";
					}
					$("#ebody").append("<tr>" + recordRow + "</tr>");


					$('#add-model-' + i).on('click', function () {
						var $btn = $(this).text('Added!').removeClass("btn-default").addClass("btn-success");
						var data = $(this).data('id');
						var id = $(this).data('company');
						biz.addToModel(id, data);
					});
				}
			}
		});
	}

	biz.addToModel= function(compID, value) {

		// Get the company object
		var company = biz.getCompany(compID);

		$.ajax({
			url: "/discoveryStreaming/lob/company/model/add?company=" + company.name + "&value=" + value,
			type: 'GET',
			success: function(data, status) {
				console.log("Success sending addToModel");
			}
		});
	}

	biz.loadContext = function(compID) {

	    console.log("load context");

	    $("#knowlege-status").html("Retrieving information from knowlege graph <img src=\"https://cdn.gonitro.com/img/pdfreader/ajax-loader2.gif\"/>");
	    $("#knowlege-status").show();
	    $("#dendrogram-holder").hide();

		// Get the company object
		var company = biz.getCompany(compID);

		$.ajax({
			url: "/discoveryStreaming/lob/company/context?company=" + company.name,
			type: 'GET',
			success: function(data, status) {

			    $("#knowlege-status").hide();
			    $("#dendrogram-holder").show();
			    $("#infobox").text(data.infobox);
			    $("#infopath").text(data.paths[0]);


				var company_data = data.profile.map(function(d){
			        var ary = d.split("|")
			        return {
			            subject: ary[0],
			            verb: ary[1],
			            obj: ary[2]
			        };
			    });

                var nest = d3.nest()
				        .key(function(d){return d.subject;})
				        .key(function(d){return d.verb;});
				// var dendrogram_data = nest.entries(company_data)[0];
				var num_leaves = d3.nest()
						.rollup(function(leaves) { return leaves.length; })
						.entries(company_data);
				d3.select("#dendrogram-holder").select('svg').style("height", num_leaves*25);
				var dendrogram_data = {key:"root", values:nest.entries(company_data)};
				dendrogram.draw(dendrogram_data);
			}
		});
	};

	biz.z_score = function(x, stats) {
        return (x - stats.mean)/(stats.stdev === 0 ? 1 : stats.stdev);
	};

	biz.updateExplainers = function() {

	    var company = biz.getCompany(biz.currentCompID);

		$.ajax({
			url: "/discoveryStreaming/lob/company/proportions/opa?company=" + company.name,
			type: 'GET',
			success: function(proportions, status) {

				bubblesData.forEach(function(d){
					d.currentValue = proportions[d.key];
				});

				biz.computeZScores(proportions);

		        bubbles.draw(bubblesData);
			}
		});
	};

	biz.computeZScores = function(currVals) {
	    // compute the current score
		bubblesData.forEach(function(d){
	        var zin = Math.abs(biz.z_score(currVals[d.key], {mean: d.inMean, stdev: d.inStdDev})),
	            zout = Math.abs(biz.z_score(currVals[d.key], {mean: d.outMean, stdev: d.outStdDev}));

	        d.score = (d.inMean > d.outMean) * ((zout + biz.epsilon)/(zin + biz.epsilon));
//	        d.score = ((zout + eps)/(zin + eps));

	    });
	};

	biz.loadOPAExplainers = function(compID) {

	    console.log("load opa explainers");

	    var company = biz.getCompany(compID);

		$.ajax({
			url: "/discoveryStreaming/lob/company/explainers/opa?company=" + company.name,
			type: 'GET',
			success: function(explainers, status) {

				bubblesData = explainers[0].features;
	            var currVals = [];

				for (var i = 0; i < explainers.length; i++) {
					var explainer = explainers[i];
					for (var j = 0; j < explainer.features.length; j++) {
						var feature = explainer.features[j];
						currVals[feature.name + "-" + feature.lineOfBusiness] = feature.currentValue;
					}
				}

				biz.computeZScores(currVals);

		        bubbles.draw(bubblesData);

		        biz.currentCompID = compID;
		        biz.exUpdater = setInterval(biz.updateExplainers, biz.bubbleUpdateInterval);
			}
		});
	};

	biz.loadShyreExplainers = function(compID) {

	    console.log("load shyreexplainers");

		$("#explainers-shyre").empty();

	    var company = biz.getCompany(compID);

		$.ajax({
			url: "/discoveryStreaming/lob/company/explainers/shyre?company=" + company.name,
			type: 'GET',
			success: function(explainers, status) {

				for (var i = 0; i < explainers.length; i++) {

					var explainer = explainers[i];

					var explainerHTML = '<div class="panel panel-default" style="margin-left: 5px; margin-right: 5px;">' +
										'	<div class="panel-heading"><h5><b>' + company.name + '</b> is behaving like a <b class="' + explainer.lineOfBusiness.replace(' ', '') + '">' + explainer.lineOfBusiness + '</b> company.</h5></div>' +
										'	<div class="panel-body">' +
										//'		<h4>' + explainer.codeCount + ' of ' + explainer.totalCount + ' HSCODE items imported</h4>' +
										'		<h4>' + explainer.codeCount + ' HSCODE items imported</h4>' +
										'		   <div style="height: 400px; overflow-y: scroll;">' +
										'			<table id="datatable" class="table">' +
										'		      <thead>' +
										'		        <tr>' +
										'		          <th>HSCODE</th>' +
										'		          <th>Product Description</th>' +
										'		        </tr>' +
										'		      </thead>' +
										'		      <tbody>';

					for (var j = 0; j < explainer.codes.length; j++) {
						explainerHTML += 	'<tr><td scope="row">' + explainer.codes[j] + '</td><td style="word-wrap: break-word;">' + explainer.descriptions[j] + '</td></tr>';
					}

					explainerHTML += 	'			  </tbody>' +
										'			 </table>' +
										'			</div>' +
										'		</div>' +
										'	</div>';

					$("#explainers-shyre").append(explainerHTML);
				}

			}
		});
	};

	biz.loadRecords = function(compID) {

	    var company = biz.getCompany(compID);

		$("#datatablehead").empty();
		$("#datatablebody").empty();

		$.ajax({
			url: "/discoveryStreaming/lob/company/records?company=" + company.name,
			type: 'GET',
			success: function(recordset, status) {
				
				var skipCols = [];
				skipCols[0] = true;
				skipCols[1] = true;
				skipCols[2] = true;
				skipCols[3] = true;

				// Append the column header
				var headers = "";
				headers += '<th><span style="white-space: nowrap">Flagged</nobr></th>';
				for (i = 0; i < recordset.columns.length - 1; i++) {
					if (skipCols[i]) {
					} else {
						headers += '<th><span style="white-space: nowrap">' + recordset.columns[i] + '</nobr></th>';
					}
				}
				$("#datatablehead").append('<tr>' + headers + '</tr>');

				// Append each record
				for (i = 0; i < recordset.records.length; i++) {
					var record = recordset.records[i];

					var recordRow = "";
					for (j = 0; j < record.values.length - 1; j++) {
						if (skipCols[j]) {
						} else {
							recordRow += "<td>" + record.values[j] + "</td>";
						}
					}

					var attention = "";
					if (record.values[record.values.length - 1] == "Yes") {
						attention = ""; //" class=\"danger\"";
						recordRow = '<td align="center"><span class="glyphicon glyphicon-certificate" style="color: red; font-size: 14pt;" aria-hidden="true"></span></td>' + recordRow;
					} else {
						recordRow = '<td></td>' + recordRow;
					}
					$("#datatablebody").append("<tr" + attention + ">" + recordRow + "</tr>");
				}
			}
		});
	};

	if (!window.biz) {
		window.biz = biz;
	}
})();
