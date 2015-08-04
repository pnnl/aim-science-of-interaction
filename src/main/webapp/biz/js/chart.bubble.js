// This chart has been adapted from the Collision Detection example on d3js.org
// http://bl.ocks.org/mbostock/3231298

d3.chart("Useful").extend("Bubble", {
    initialize: function() {
        var chart = this,
            properties = {};

        chart.add_chart_property(properties, "value", function(d){return d.value;});
        chart.add_chart_property(properties, "key", function(d){return d.key;});
        chart.add_chart_property(properties, "text", function(d){return d.text;});
        chart.add_chart_property(properties, "padding", 3);
        chart.add_chart_property(properties, "threshold", null);

        chart.force = d3.layout.force()
            .gravity(0.05)
            .charge(0)
            .on("tick.collide", function(e) {
                chart.resolve_collisions();
            });

        chart.layer("bubbles", chart.base.append("g").attr("class", "bubbles"), {
            dataBind: function(data) {
                return this.selectAll("g")
                    .data(data, chart.key());
            },

            insert: function() {
                return this.append("g");
            },

            events: {
                enter: function() {
                    var x = chart.x(),
                        y = chart.y(),
                        x0 = d3.scale.linear()
                            .domain([-1,2])
                            .range(x.domain());

                    this.each(function(d) {
                        d.x = x0(Math.random());
                        d.y = y(y.domain()[0]) - Math.random();
                    });
                    this.append("circle");
                    this.append("text")
                        .text(chart.text());
                },

                exit: function() {
                    var x = chart.x(),
                        y = chart.y(),
                        xf = d3.scale.linear()
                            .domain([-1,2])
                            .range(x.range());

                    this.transition().duration(1000)
                        .attr("transform", function(d){
                            return "translate(" + [xf(Math.random()), y.range()[0]] + ")";
                        })
                        .each("end", function(){
                            d3.select(this).remove();
                        });
                },

                merge: function() {

                    var x = chart.x(),
                        y = chart.y(),
                        dx = x.range(),
                        dy = y.range(),
                        w = dx[1] - dx[0],
                        h = dy[0] - dy[1],
                        selection = this,
                        dr = Math.sqrt(w*w + h*h)/2000; // amount of radius to change per tick is a small %-age of screen

                    x.domain([0, w]);
                    y.domain([0, h]);

                    chart.force
                        .nodes(chart.data)
                        .size([w,h])
                        .start()
                        .resume()
                        .on("tick.xy", function(){
                            selection
                                .each(function(d){
                                    if (Math.abs(d.r - d.r_target) <= dr) {
                                        d.r = d.r_target;
                                    }
                                    else {
                                        d.r += (d.r_target > d.r ? 1 : -1)*dr;
                                    }
                                })
                                .attr("transform", function(d){
                                    return "translate("+ [x(d.x),y(d.y)] +")"
                                })
                                .select("circle")
                                    .attr("r", function(d){return d.r;});
                        });
                }
            }
        });

    },

    transform: function(data) {
        var chart = this,
            value = chart.value(),
            threshold = chart.threshold();

        return chart.data = data.filter(function(d){
            d.r_target = value(d)
            if (!("r" in d) || d.r < 0) {
                d.r = d.r_target;
            }
            return threshold === null ? true : d.r_target > threshold;
        });
    },

    resolve_collisions: function() {
        var chart = this,
            nodes = chart.data,
            value = chart.value(),
            padding = chart.padding();       

        var q = d3.geom.quadtree(nodes),
        i = 0,
        n = nodes.length;

        function collide(node) {

          var r = node.r + padding + 16,
              nx1 = node.x - r,
              nx2 = node.x + r,
              ny1 = node.y - r,
              ny2 = node.y + r;
          return function(quad, x1, y1, x2, y2) {
            if (quad.point && (quad.point !== node)) {
              var x = node.x - quad.point.x,
                  y = node.y - quad.point.y,
                  l = Math.sqrt(x * x + y * y),
                  r = node.r + quad.point.r + 2.0*padding;
              if (l < r) {
                l = (l - r) / l * .25;
                node.x -= x *= l;
                node.y -= y *= l;
                quad.point.x += x;
                quad.point.y += y;
              }
            }
            return x1 > nx2 || x2 < nx1 || y1 > ny2 || y2 < ny1;
          };
        }

        while (++i < n) q.visit(collide(nodes[i]));
    }
});