(function() {

	var lobs = ["Clothing-OPA", "Clothing-Shyre", "Automotive-OPA", "Automotive-Shyre", "Appliances-OPA", "Appliances-Shyre", "Superstores-OPA", "Superstores-Shyre", "Department Stores-OPA", "Department Stores-Shyre", "Athletic Stores-OPA", "Athletic Stores-Shyre"];
	var palette = d3.scale.category20();
	palette.domain(lobs);
	var palette2 = d3.scale.category20();

	var biz = {
		version : "3.5.3",
		maxSeriesSize : 50,
		companies : {},
		linesOfBusiness : []
	};

	for (i = 0; i < lobs.length; i++) {
		biz.linesOfBusiness.push({
			name: lobs[i],
			color: palette(lobs[i])
		});
	}

	biz.initApp = function() {

		for (i = 0; i < lobs.length; i++) {
			$("#leg-labels") .append("<li><span style='background:" + biz.linesOfBusiness[i].color.toString() + ";'></span>" + biz.linesOfBusiness[i].name + "</li>");
		}

		$('[data-toggle="tooltip"]').tooltip();
	}

	biz.addCompany = function(colID, compID, compName, elChart, elAxis) {

		$("#column" + colID) .append(
				'<div class="panel panel-default">'
						+ '<div class="panel-heading" style="font-size: 16pt; color: black; font-weight: bold;">'
						+ '<a href="#' + compID + '"">'
						+ compName
						+ '</a> '
						+ '<span class="pull-right" id="alert-' + compID + '">'

						+ '<div class="dropdown">'
						+ '  <button class="btn btn-danger dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">'
						+ '    <span class="glyphicon glyphicon-flash" style="color: white;" aria-hidden="true"></span>'
						+ '    <span class="caret"></span>'
						+ '  </button>'
						+ '  <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">'
						+ '    <li><a href="#">Explore <strong>' + compName + '</strong> further ...</a></li>'
						+ '    <li><a href="#">Feedback: Alert was useful</a></li>'
						+ '    <li><a href="#">Feedback: Alert was a false alarm</a></li>'
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
				width = 350,
				height = 300,
				color = palette2;

		s = biz.linesOfBusiness.map(function(d) {
				return {
							key: d.name,
							values: d3.range(50).map(function(d) { return {x: d, y: 0}; })
						};
				});

		var g = d3.select("#chart" + compID)
				.append("svg")
				// .attr("width", width)
				// .attr("height", height)
				.chart("Line")  // Line, Bar, Flow, Area
						.size([width, height])
						.y(d3.scale.linear().domain([0,1]))
						.threshold(0)
						.margin({top: 0, bottom: 20, left: 10, right:0})
						.axis(true);
						// .label(true);

		g.layer("data").on("enter", function(){
				this.style("stroke", function(_,i) { return color(i); });
				//this.style("fill", function(_,i) { return color(i); });

				// onclick mouse event stub
				this.on("click", function(d){
					console.log(d);
				});

				// mouseover event stub
				this.on("mouseover", function(d){
					console.log(d);
				});
				//this.style("fill", function(_,i) { return color(i); });
		});

		biz.companies[compName.toLowerCase()] = {
			id: compID,
			name : compName,
			counter : 50,
			graph : g,
			series : s
		};
	};

	biz.changeChartType = function(type) {
		//foreach c in biz.companies.graph. change chart
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

	biz.getCompany = function(compID) {

		for (var company in biz.companies) {
			if (biz.companies[company].id == compID) {
				return biz.companies[company];
			}
		}
		return null;
	}

	biz.displayDetails = function(compID) {

		// Get the company object
		var company = biz.getCompany(compID);

		$("#dropcompname").text(company.name);
		$("#droplob").text("Automotive");

		// Load records	
		biz.loadExplainers(compID);	
		biz.loadEmerging(compID);
		biz.loadRecords(compID);	
		biz.loadContext(compID);	
	}

	biz.loadExplainers = function(compID) {

	    console.log("load explainers");

//		$("#datatablehead").empty();
//		$("#datatablebody").empty();
		
	    var company = biz.getCompany(compID);
		
		$.ajax({
			url: "/discoveryStreaming/lob/company/explainers/shyre?company=" + company.name + "&lob=auto&timeid=0",
			type: 'GET',
			success: function(explainers, status) {

				// Append the column header
				var headers = "";
				for (i = 0; i < recordset.columns.length; i++) {
					headers += "<th>" + recordset.columns[i] + "</th>";
				}
				$("#datatablehead").append('<tr>' + headers + '</tr>');

			}
		});

		$.ajax({
			url: "/discoveryStreaming/lob/company/explainers/opa?company=" + company.name + "&lob=auto&timeid=0",
			type: 'GET',
			success: function(explainers, status) {

				var explainerHTML = '<div class="panel panel-default" style="margin-left: 5px; margin-right: 25px;">' +'
									'	<div class="panel-heading"><h4><b>" + company.name + "</b> is behaving like <b>Ford</b>:</h4></div>' +
									'	<div class="panel-body">' +	
									'		<h4>3 of 7 HSCODE items imported</h4>' +
									'			<table id="datatable" class="table">' +
									'		      <thead>' +
									'		        <tr>' +
									'		          <th>HSCODE</th>' +
									'		          <th>Product Description</th>' +
									'		        </tr>' +
									'		      </thead>' +
									'		      <tbody>';
													<tr><th scope="row">850520</th><td>Electro-magnetic couplings, clutches and brakes</td></tr>
													<tr><th scope="row">851110</th><td>Spark plugs</td></tr>
													<tr><th scope="row">851150</th><td>Generators and alternators</td></tr>
				explainerHTML += 	'			  </tbody>' +
									'			 </table>' +
									'		</div>' +
									'	</div>';
				
				$("#explainers-shyre").append(explainerHTML);
				
				// Append the column header
				var headers = "";
				for (i = 0; i < recordset.columns.length; i++) {
					headers += "<th>" + recordset.columns[i] + "</th>";
				}
				$("#datatablehead").append('<tr>' + headers + '</tr>');

			}
		});
	}
		
	biz.loadRecords = function(compID) {

	    console.log("tick");

		$("#datatablehead").empty();
		$("#datatablebody").empty();

		$.ajax({
			url: "/discoveryStreaming/lob/company/records",
			type: 'GET',
			success: function(recordset, status) {

				// Append the column header
				var headers = "";
				for (i = 0; i < recordset.columns.length; i++) {
					headers += "<th>" + recordset.columns[i] + "</th>";
				}
				$("#datatablehead").append('<tr>' + headers + '</tr>');

				// Append each record
				for (i = 0; i < recordset.records.length; i++) {
					var record = recordset.records[i];

					var recordRow = "";
					for (j = 0; j < record.values.length; j++) {
						recordRow += "<td>" + record.values[j] + "</td>";
					}

					var attention = "";
					if (Math.random() < .1) {
						attention = " class=\"danger\"";
					}
					$("#datatablebody").append("<tr" + attention + ">" + recordRow + "</tr>");
				}
			}
		});

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

					var recordRow = "";
					for (j = 0; j < record.values.length; j++) {

						var menu = "";
						if (j ==  record.values.length - 1) {
							menu = "<div class=\"btn-group pull-right\">" +
								   "<button class=\"btn btn-default btn-xs\" type=\"button\">Add to Model</button>"
							       //"<button class=\"btn btn-default btn-xs dropdown-toggle\" type=\"button\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">Add to Model <span class=\"caret\"></span></button>"
						           "<ul class=\"dropdown-menu\">" +
						           " <li><a href=\"#\">Add to Model</a></li>" +
							       " <li><a href=\"#\">Another Action</a></li>" +
							       "</ul>" +
							       "</div>";
						}

						recordRow += "<td>" + record.values[j] + menu + "</td>";
					}
					$("#ebody").append("<tr>" + recordRow + "</tr>");
				}
			}
		});
	}

	if (!window.biz) {
		window.biz = biz;
	}
})();
