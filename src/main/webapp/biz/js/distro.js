(function() {


	var distro = {

	};

	distro.draw = function(element, inMean, inStdDev, outMean, outStdDev, current) {

		var dataYes = getData(inMean, inStdDev);
		var dataNo = getData(outMean, outStdDev);

		var ex = d3.extent(dataYes.concat(dataNo), function(d) {
		    return d.q;
		});

		// line chart based on http://bl.ocks.org/mbostock/3883245
		var margin = {
		        top: 10,
		        right: 10,
		        bottom: 20,
		        left: 25
		    },
		    width = 200 - margin.left - margin.right,
		    height = 70 - margin.top - margin.bottom;


		var x = d3.scale.linear()
		    .range([0, width])
		    .domain([-0.5, 1.5]);
//		    .domain(d3.extent(dataYes.concat(dataNo), function(d) {
//			    return d.q;
//			}));

		var y = d3.scale.linear()
		    .range([height, 0])
		    .domain(d3.extent(dataYes.concat(dataNo), function(d) {
			    return d.p;
			}));

		var xAxis = d3.svg.axis()
		    .scale(x)
		    .orient("bottom");

		var yAxis = d3.svg.axis()
		    .scale(y)
		    .orient("left");

		var lineYes = d3.svg.line()
		    .x(function(d) {
		        return x(d.q);
		    })
		    .y(function(d) {
		        return y(d.p);
		    });

		var lineNo = d3.svg.line()
		    .x(function(d) {
		        return x(d.q);
		    })
		    .y(function(d) {
		        return y(d.p);
		    });

		var svg = d3.select(element).append("svg")
		    .attr("width", width + margin.left + margin.right)
		    .attr("height", height + margin.top + margin.bottom)
		    .append("g")
		    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");


		svg.append("g")
		    .attr("class", "x axis")
		    .attr("transform", "translate(0," + height + ")")
		    .call(xAxis);

//		svg.append("g")
//		    .attr("class", "y axis")
//		    .call(yAxis);

		svg.append("path")
		    .datum(dataNo)
		    .attr("class", "line no")
		    .attr("d", lineNo);

		svg.append("path")
		    .datum(dataYes)
		    .attr("class", "line yes")
		    .attr("d", lineYes);

		var tri = d3.svg.symbol().type('triangle-down');
		svg.append("path")
			.attr('d', tri)
		    .attr("transform", "translate(" + x(current) + "," + (height-8) + ")")
		    .attr("class", "current");
	}

	function getData(mean, stddev) {

		var data = [];

		// loop to populate data array with
		// probabily - quantile pairs
		for (var i = 0; i < 5000; i++) {
		    q = normal() // calc random draw from normal dist
		    p = gaussian(q, mean, stddev) // calc prob of rand draw
		    el = {
		        "q": q,
		        "p": p
		    }
		    data.push(el)
		};

		// need to sort for plotting
		//https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/sort
		data.sort(function(x, y) {
		    return x.q - y.q;
		});

		return data;
	}

	// from http://bl.ocks.org/mbostock/4349187
	// Sample from a normal distribution with mean 0, stddev 1.
	function normal() {
	    var x = 0,
	        y = 0,
	        rds, c;
	    do {
	        x = Math.random() * 2 - 1;
	        y = Math.random() * 2 - 1;
	        rds = x * x + y * y;
	    } while (rds == 0 || rds > 1);
	    c = Math.sqrt(-2 * Math.log(rds) / rds); // Box-Muller transform
	    return x * c; // throw away extra sample y * c
	}

	//taken from Jason Davies science library
	// https://github.com/jasondavies/science.js/
	function gaussian(x, mean, sigma) {
		var gaussianConstant = 1 / Math.sqrt(2 * Math.PI),
	    x = (x - mean) / sigma;
	    return gaussianConstant * Math.exp(-.5 * x * x) / sigma;
	};


	if (!window.distro) {
		window.distro = distro;
	}
})();
	
