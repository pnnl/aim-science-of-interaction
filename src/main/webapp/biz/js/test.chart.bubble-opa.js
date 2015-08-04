// setup the chart
var s = 2.5,

    chart = d3.select("#bubble-holder")
		.append("svg").chart("Bubble")
        .key(function(d){return d.key;})
        .value(function(d){
            return d.score*s;
        })
        .threshold(5*s)
        .text(function(d){return d.key;}),
    _model,
    _data;

// set the attribute based on the type of feature (uppercase = port, lowercase = type)
chart.layer("bubbles")
    .on("enter", function(){
        this.attr("feature-type", function(d){
            return d.key === d.key.toUpperCase() ? "port" : "merchandise";
        });
    })
    .on("merge", function(){
        this.style("stroke-dasharray", function(d){
            return d.value.yes.mean > d.value.no.mean ? null : "5,5";
        });
    });

/*

// load the data
queue()
    .defer(d3.csv, model_url, function(d) {
        // create an actually useful structure
        return {
            keyword: d.keyword,
            yes: {mean: +d["yes.mean1"], stdev: +d["yes.stdev1"]},
            no: {mean: +d["no.mean1"], stdev: +d["no.stdev1"]},
            infoscore: +d.infscore1
        };
    })
    .defer(d3.csv, stream_url, function(d) {
        // convert everything excepth the LOB to Number, leave out LOB
        var d2 = {};
        for (k in d) {
            if (k !== "LOB") {
                d2[k] = +d[k];
            }
        }
        return d2;
    })
    .await(function(error, model, stream) {
        _model = model = d3.map(model, function(d){return d.keyword;})
        var entries = model.entries();

        function z_score(x, stats) {
            return (x - stats.mean)/(stats.stdev === 0 ? 1 : stats.stdev);
        }

        // simulate the data stream
        var i = 0;
        setInterval(function(){
            var data = _data = stream[i%stream.length];


            // compute the current score
            entries.forEach(function(d){
                var u = d.value.yes.mean,
                    s = d.value.yes.stdev,
                    zin = Math.abs(z_score(data[d.key], d.value.yes)),
                    zout = Math.abs(z_score(data[d.key], d.value.no)),
                    eps = .1;

                d.score = (zout + eps)/(zin + eps);
                // d.score = +(d.value.infoscore > .5)/(eps + zin); 
            });

            chart.draw(entries);

            console.log(entries);

            i++;
        }, 3000);
    });

*/