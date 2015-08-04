(function()
{
    function add_chart_property(chart, parent, name, value, optional)
    {
        if (typeof optional === 'undefined')
        {
            optional = {};
        }

        if (!("get" in optional))
        {
            optional.get = function(d)
            {
                return d;
            };
        }
        if (!("set" in optional))
        {
            optional.set = function(d)
            {
                return d;
            };
        }

        parent[name] = value;
        chart[name] = function(d)
        {
            if (arguments.length === 0)
            {
                return optional.get(parent[name]);
            }
            parent[name] = optional.set(d);
            return chart;
        };
    }

    d3.layout.SimpleStackLayout = function()
    {

        function self(data)
        {
            var values = self.values(),
                x = self.x(),
                y = self.y(),
                y_tot = 0.0;

            if (properties.order !== null)
            {
                data = data.sort(properties.order);
            }

            // for each row
            data.forEach(function(d, i)
            {
                var values_i = values(d),
                    yi_max = d3.max(values_i, y);

                // for each column
                values_i.forEach(function(e, j)
                {
                    e.x = x(e);

                    if (properties.spacing === "unit")
                    {
                        if (properties.threshold === null)
                        {
                            e.y = y(e) / yi_max;
                        }
                        else
                        {
                            e.y = y(e) > properties.threshold ? 1.0 : 0.0;
                        }
                        e.y0 = i + i * properties.padding;
                    }
                    else if (properties.spacing === "scaled")
                    {
                        e.y = y(e);
                        e.y0 = y_tot + i * properties.padding;
                    }
                });

                y_tot += yi_max;
            });

            return data;
        }

        var properties = {};

        add_chart_property(self, properties, "values", function(d)
        {
            return d;
        });
        add_chart_property(self, properties, "order", function(d)
        {
            return d;
        });
        // chart.add_chart_property(properties, "out", function(d){return d;});
        add_chart_property(self, properties, "x", function(d)
        {
            return d.x;
        });
        add_chart_property(self, properties, "y", function(d)
        {
            return d.y;
        });

        add_chart_property(self, properties, "spacing", "scaled");
        add_chart_property(self, properties, "threshold", null);
        add_chart_property(self, properties, "padding", 0.0);


        return self;
    };

    d3.chart("Useful",
    {
        initialize: function()
        {
            var chart = this,
                size = null,
                properties = {};

            chart.add_chart_property = function(parent, name, value, optional)
            {
                add_chart_property(chart, parent, name, value, optional);
            };

            chart.add_chart_property(properties, "x", d3.scale.linear());
            chart.add_chart_property(properties, "y", d3.scale.linear());
            chart.add_chart_property(properties, "background", chart.base.append("rect")
                .classed("background", true));
            chart.add_chart_property(properties, "margin",
            {
                top: 5,
                bottom: 10,
                left: 15,
                right: 20
            });
            chart.add_chart_property(properties, "interactive", false);
            chart.add_chart_property(properties, "size", null,
            {
                get: function(d)
                {
                    var rect, node;

                    if (size)
                    {
                        return size;
                    }
                    else
                    {
                        node = chart.base.node();
                        rect = node.getBoundingClientRect();

                        while (rect.width * rect.height === 0)
                        {
                            node = node.parentNode;
                            rect = node.getBoundingClientRect();
                        }

                        return [rect.width, rect.height];
                    }
                }
            });

            chart.base.append('defs')
                .append("clipPath")
                .attr("id", "clip")
                .append("rect");
        },

        transform: function(data)
        {
            var chart = this;

            // auto scale to fit parent node's bounding rectangle
            var size = chart.size();

            var margin = chart.margin(),
                w = size[0] - margin.left - margin.right,
                h = size[1] - margin.top - margin.bottom;

            chart.x()
                .range([0, w]);
            chart.y()
                .range([h, 0]);

            chart.background()
                .attr("x", margin.left)
                .attr("y", margin.top)
                .attr("width", w)
                .attr("height", h);
            chart.base.select("defs #clip rect")
                .attr(
                {
                    width: w,
                    height: h
                });

            // update all layers with the margins
            Object.keys(chart._layers)
                .forEach(function(layer_name)
                {
                    chart.layer(layer_name)
                        .attr("transform", "translate(" + [margin.left, margin.top] + ")");
                });

            return data;
        },

        new_group: function(clip)
        {
            var g = this.base.append("g");

            if (clip)
            {
                g.attr("clip-path", "url(#clip)");
            }

            return g;
        },

    });

    d3.chart("Useful")
        .extend("Stream",
        {
            initialize: function()
            {
                var chart = this,
                    properties = {};

                chart.add_chart_property(properties, "key", function(d)
                {
                    return d.key;
                });
                chart.add_chart_property(properties, "stack", d3.layout.stack());
                chart.add_chart_property(properties, "autoscale", "both");

                // chart variables
                chart.area = d3.svg.area()
                    .x(function(d)
                    {
                        return chart.x()(d.x);
                    })
                    .y0(function(d)
                    {
                        return chart.y()(d.y0);
                    })
                    .y1(function(d)
                    {
                        return chart.y()(d.y0 + d.y);
                    });

                // layer definitions
                chart.layer("area", chart.new_group()
                    .attr("class", "area"),
                    {
                        dataBind: function(data)
                        {
                            return this.selectAll("path")
                                .data(data, chart.key());
                        },

                        insert: function()
                        {
                            return this.append("path");
                        },

                        events:
                        {
                            enter: function()
                            {
                                this.append("title")
                                    .text(chart.key());
                            },

                            exit: function()
                            {
                                return this.remove();
                            },

                            "merge:transition": function()
                            {
                                var values = chart.stack()
                                    .values();
                                return this.duration(500)
                                    .attr("d", function(d)
                                    {
                                        return chart.area(values(d));
                                    });
                            }
                        }
                    });
            },

            transform: function(data)
            {
                var chart = this;
                chart.data = data;

                // finally, setup scales
                var stack = chart.stack(),
                    values = stack.values(),
                    stacked_data = stack(data),
                    // size = chart.size(),
                    x_scale = chart.x(),
                    y_scale = chart.y(),
                    autoscale = chart.autoscale(),
                    x_attr = stack.x(),
                    y_attr = stack.y(),
                    all_values;

                if (autoscale === "x" ||
                    autoscale === "y" ||
                    autoscale === "both")
                {
                    all_values = [].concat.apply([], data.map(values));
                }

                if (autoscale === "x" || autoscale === "both")
                {
                    x_scale.domain(d3.extent(all_values, x_attr));
                }
                if (autoscale === "y" || autoscale === "both")
                {
                    y_scale.domain([0, d3.max(all_values, function(d)
                    {
                        return d.y0 + d.y;
                    })]);
                }

                return stacked_data;
            }
        });

    d3.chart("Useful")
        .extend("Words",
        {
            initialize: function()
            {
                var chart = this,
                    properties = {};

                chart.add_chart_property(properties, "text", function(d)
                {
                    return d.text;
                });
                chart.add_chart_property(properties, "fontSize", function(d)
                {
                    return d.size + "px";
                });
                chart.add_chart_property(properties, "fontFamily", function(d)
                {
                    return "Impact";
                });

                // layer definitions
                chart.layer("words", chart.new_group()
                    .attr("class", "words"),
                    {
                        dataBind: function(data)
                        {
                            return this.selectAll("text")
                                .data(data, chart.text());
                        },

                        insert: function()
                        {
                            return this.append("text");
                        },

                        events:
                        {
                            exit: function()
                            {
                                return this.remove();
                            },

                            "merge:transition": function()
                            {
                                var size = chart.size(),
                                    w = size[0],
                                    h = size[1],
                                    x = chart.x(),
                                    y = chart.y();
                                this.duration(500)
                                    .text(chart.text())
                                    .style("font-size", chart.fontSize())
                                    .style("font-family", chart.fontFamily())
                                    .attr("text-anchor", "middle")
                                    .attr("transform", function(d)
                                    {
                                        return "translate(" + [x(d.x), y(d.y)] + ")rotate(" + d.rotate + ")";
                                    });
                            }
                        }
                    });
            }
        });

}());
(function()
{
    d3.chart("Useful")
        .extend("UCHD",
        {
            initialize: function()
            {
                var properties = {},
                    chart = this,
                    xAxis;

                this.margin(
                {
                    top: 0,
                    left: 50,
                    bottom: 50,
                    right: 0
                });

                this.add_chart_property(properties, "key", function(d)
                {
                    return d.key;
                });
                this.add_chart_property(properties, "values", function(d)
                {
                    return d.values;
                });

                this.add_chart_property(properties, "px", function(d)
                {
                    return d.x;
                });
                this.add_chart_property(properties, "py", function(d)
                {
                    return d.y;
                });
                this.add_chart_property(properties, "threshold", 0);
                this.add_chart_property(properties, "autoscaleX", true);
                this.add_chart_property(properties, "autoscaleY", true);

                this.add_chart_property(properties, "label", false,
                {
                    get: function()
                    {
                        return !!properties.labelY;
                    },
                    set: function(v)
                    {
                        if (v)
                        {
                            chart.add_chart_property(properties, "labelY", d3.scale.ordinal());

                            chart.layer('ylabels', chart.new_group(),
                            {
                                dataBind: function(data)
                                {
                                    return this.selectAll('text')
                                        .data(data, function(d)
                                        {
                                            return d.key;
                                        });
                                },
                                insert: function()
                                {
                                    return this.append('text')
                                        .classed('label', true)
                                        .style('text-anchor', 'end')
                                        .attr('x', function()
                                        {
                                            return chart.x()
                                                .range()[0];
                                        })
                                        .attr('y', function(d)
                                        {
                                            return chart.y()(chart.labelY()(d.key));
                                        })
                                        .attr('dy', '1em')
                                        .text(function(d)
                                        {
                                            return d.key;
                                        });
                                },
                                events:
                                {
                                    exit: function()
                                    {
                                        this.remove();
                                    }
                                }
                            });
                        }
                    }
                });

                this.add_chart_property(properties, "axis", false,
                {
                    get: function()
                    {
                        return xAxis;
                    },
                    set: function(axis)
                    {

                        if (axis)
                        {
                            if (typeof(axis) === "function")
                            {
                                xAxis = axis;
                            }
                            else
                            {
                                xAxis = d3.svg.axis()
                                    .orient("bottom");
                            }

                            chart.new_group()
                                .classed("x axis", true);
                        }
                        else
                        {
                            axis = false;
                        }
                    }
                });
            },
            transform: function(data)
            {
                if (this.axis())
                {
                    this.axis()
                        .scale(this.x());

                    this.base.select(".x.axis")
                        .attr('transform',
                            'translate(' + (this.margin()
                                .left - 1) + ',' + (this.size()[1] - this.margin()
                                .bottom) + ')');
                }

                return data;
            }
        });
}());
(function()
{
    var handlerFactory = function(chart)
        {
            return {
                click: function(d)
                {
                    chart.trigger("click",
                    {
                        d: d,
                        x: chart.x()
                            .invert(d3.event.layerX - chart.margin()
                                .left),
                        y: chart.y()
                            .invert(d3.event.layerY - chart.margin()
                                .top)
                    });
                },
                mouseenter: function(d)
                {
                    chart.trigger("mouseenter",
                    {
                        d: d,
                        x: chart.x()
                            .invert(d3.event.layerX - chart.margin()
                                .left),
                        y: chart.y()
                            .invert(d3.event.layerY - chart.margin()
                                .top)
                    });
                },
                mouseleave: function(d)
                {
                    chart.trigger("mouseleave",
                    {
                        d: d,
                        x: chart.x()
                            .invert(d3.event.layerX - chart.margin()
                                .left),
                        y: chart.y()
                            .invert(d3.event.layerY - chart.margin()
                                .top)
                    });
                }
            };
        },
        generator = {
            transform: function(data)
            {
                var chart = this,
                    threshold = chart.threshold(),
                    merged;

                this.data = data;

                var stack = d3.layout.stack()
                    .offset("wiggle"),
                    layers = stack(data.map(function(layer)
                    {
                        return chart.values()(layer)
                            .map(function(d)
                            {
                                return {
                                    x: chart.px()(d),
                                    y: Math.max(chart.py()(d), threshold)
                                };
                            });
                    }));

                data.forEach(function(d, i)
                {
                    d.layer = layers[i];
                });

                if (this.autoscaleX())
                {
                    merged = d3.merge(data.map(chart.values()));
                    this.x()
                        .domain(d3.extent(merged, this.px()));
                }

                this.y()
                    .domain([0, d3.max(layers, function(layer)
                    {
                        return d3.max(layer, function(d)
                        {
                            return d.y0 + d.y;
                        });
                    })]);

                if (this.label())
                {
                    this.labelY()
                        .domain(this.data.map(function(d)
                        {
                            return d.key;
                        }))
                        .range(this.data.map(function(d)
                        {
                            return d.layer[0].y / 2 + d.layer[0].y0;
                        }));
                }

                return data;
            },

            initialize: function()
            {
                var x, y, area, chart = this;

                area = d3.svg.area()
                    .x(function(d)
                    {
                        return chart.x()(d.x);
                    })
                    .y0(function(d)
                    {
                        return chart.y()(d.y0);
                    })
                    .y1(function(d)
                    {
                        return chart.y()(d.y0 + d.y);
                    });


                //TOOD reattach mousemove, mouseout?

                chart.layer("data", chart.new_group(true),
                {
                    dataBind: function(data)
                    {
                        return this.selectAll("path")
                            .data(data.map(function(d)
                            {
                                return d.layer;
                            }));
                    },
                    insert: function(data)
                    {
                        return this.append("path");
                    },
                    events:
                    {
                        enter: function()
                        {
                            var sel = this,
                                handlers = handlerFactory(chart);

                            sel.attr("class", function(_, i)
                            {
                                return i;
                            });

                            if (chart.interactive())
                            {
                                Object.keys(handlers)
                                    .forEach(function(k)
                                    {
                                        sel.on(k, handlers[k]);
                                    });
                            }
                        },
                        merge: function()
                        {
                            this.attr("d", area);

                            if (chart.axis())
                            {
                                chart.base.select(".x.axis")
                                    .call(chart.axis());
                            }
                        },
                        //"merge:transition" : function() {},
                        exit: function()
                        {
                            this.remove();
                        }
                    }
                });

                //			chart.layer("text", chart.base.append("g"), {
                //					dataBind : function(data) {},
                //					insert : function(data) {},
                //					events : function(){}
                //				});
            }
        };

    d3.chart("UCHD")
        .extend("Area", generator);
}());
(function()
{
    var HEIGHT = 20,
        handlerFactory = function(chart)
        {
            return {
                click: function(d)
                {
                    chart.trigger("click",
                    {
                        d: d,
                        x: chart.x()
                            .invert(d3.event.layerX - chart.margin()
                                .left),
                        y: chart.y()
                            .invert(d3.event.layerY - chart.margin()
                                .top)
                    });
                },
                mouseenter: function(d)
                {
                    chart.trigger("mouseenter",
                    {
                        d: d,
                        x: chart.x()
                            .invert(d3.event.layerX - chart.margin()
                                .left),
                        y: chart.y()
                            .invert(d3.event.layerY - chart.margin()
                                .top)
                    });
                },
                mouseleave: function(d)
                {
                    chart.trigger("mouseleave",
                    {
                        d: d,
                        x: chart.x()
                            .invert(d3.event.layerX - chart.margin()
                                .left),
                        y: chart.y()
                            .invert(d3.event.layerY - chart.margin()
                                .top)
                    });
                }
            };
        },
        generator = {
            transform: function(data)
            {
                var chart = this,
                    py = chart.py(),
                    threshold = chart.threshold();

                data = data.map(function(d)
                    {
                        var values = chart.values()(d),
                            v,
                            prev,
                            segments = [];

                        values.forEach(function(v)
                        {
                            if (py(v) > threshold)
                            {
                                if (!prev)
                                {
                                    segments.push([v]);
                                }

                                prev = v;
                            }
                            else if (prev && py(v) < threshold)
                            {
                                segments[segments.length - 1].push(prev);
                                prev = null;
                            }
                        });

                        if (prev)
                        {
                            segments[segments.length - 1].push(prev);
                        }

                        return {
                            key: chart.key()(d),
                            values: chart.values()(d),
                            segments: segments
                        };
                    })
                    .filter(function(d)
                    {
                        return d.segments.length;
                    });

                this.data = data;

                merged = d3.merge(d3.merge(data.map(function(d)
                {
                    return d.segments;
                })));

                if (this.autoscaleX())
                {
                    this.x()
                        .domain(d3.extent(merged, this.px()));
                }

                this.y()
                    .domain([0, data.length - 1])
                    .range([0, (data.length - 1) * HEIGHT]);

                if (this.label())
                {
                    this.labelY()
                        .domain(this.data.map(function(d)
                        {
                            return d.key;
                        }))
                        .range(this.data.map(function(_, i)
                        {
                            return i;
                        }));
                }

                return data;
            },

            initialize: function()
            {
                var chart = this;

                //TOOD reattach mousemove, mouseout?

                chart.layer("data", chart.new_group(true),
                {
                    dataBind: function(data)
                    {
                        return this.selectAll("g")
                            .data(data);
                    },
                    insert: function()
                    {
                        return this.append("g");
                    },
                    events:
                    {
                        enter: function()
                        {
                            this.attr(
                            {
                                "class": function(d)
                                {
                                    return chart.key()(d);
                                },
                                transform: function(_, i)
                                {
                                    return "translate(0," + (i * HEIGHT) + ")";
                                }
                            });
                        },
                        merge: function()
                        {
                            var enter,
                                rect = this.selectAll("rect")
                                .data(function(d)
                                {
                                    return d.segments;
                                }),
                                handlers = handlerFactory(chart);


                            enter = rect.enter()
                                .append("rect");

                            rect.attr(
                            {
                                x: function(d)
                                {
                                    return chart.x()(chart.px()(d[0]));
                                },
                                width: function(d)
                                {
                                    console.debug(d);
                                    return chart.x()(chart.px()(d[1])) - chart.x()(chart.px()(d[0]));
                                },
                                y: 0,
                                height: HEIGHT
                            });

                            if (chart.interactive())
                            {
                                Object.keys(handlers)
                                    .forEach(function(k)
                                    {
                                        enter.on(k, handlers[k]);
                                    });
                            }

                            rect.exit()
                                .remove();

                            if (chart.axis())
                            {
                                chart.base.select(".x.axis")
                                    .call(chart.axis());
                            }
                        },
                        //"merge:transition" : function() {},
                        exit: function()
                        {
                            this.remove();
                        }
                    }
                });

                //			chart.layer("text", chart.base.append("g"), {
                //					dataBind : function(data) {},
                //					insert : function(data) {},
                //					events : function(){}
                //				});
            }
        };

    d3.chart("UCHD")
        .extend("Bar", generator);
}());
(function()
{
    var horz = 5,
        handlerFactory = function(chart)
        {
            return {
                click: function(d)
                {
                    chart.trigger("click",
                    {
                        d: d,
                        x: chart.x()
                            .invert(d3.event.layerX - chart.margin()
                                .left),
                        y: chart.y()
                            .invert(d3.event.layerY - chart.margin()
                                .top)
                    });
                },
                mouseenter: function(d)
                {
                    chart.trigger("mouseenter",
                    {
                        d: d,
                        x: chart.x()
                            .invert(d3.event.layerX - chart.margin()
                                .left),
                        y: chart.y()
                            .invert(d3.event.layerY - chart.margin()
                                .top)
                    });
                },
                mouseleave: function(d)
                {
                    chart.trigger("mouseleave",
                    {
                        d: d,
                        x: chart.x()
                            .invert(d3.event.layerX - chart.margin()
                                .left),
                        y: chart.y()
                            .invert(d3.event.layerY - chart.margin()
                                .top)
                    });
                }
            };
        },
        generator = {
            transform: function(data)
            {
                var chart = this,
                    max = 0,
                    px = chart.px(),
                    py = chart.py(),
                    threshold = chart.threshold(),
                    transpose,
                    merged;

                this.data = data;

                transpose = d3.transpose(data.map(function(d)
                {
                    return chart.values()(d)
                        .map(function(v)
                        {
                            return {
                                key: chart.key()(d),
                                x: px(v),
                                y: py(v)
                            };
                        });
                }));

                transpose.forEach(function(d)
                {
                    var sorted = d.filter(function(v)
                        {
                            return v.y > threshold;
                        })
                        .sort(function(a, b)
                        {
                            return b.y - a.y;
                        });
                    max = Math.max(sorted.length - 1, max);

                    d.forEach(function(v)
                    {
                        v.i = sorted.indexOf(v);
                    });
                });

                data = d3.transpose(transpose)
                    .map(function(d)
                    {
                        var key;

                        d.forEach(function(v)
                        {
                            if (v.i >= 0)
                            {
                                v.i = max - v.i;
                            }
                        });

                        return {
                            key: d.length ? d[0].key : "",
                            values: d
                        };
                    });

                merged = d3.merge(data.map(function(d)
                {
                    return d.values;
                }));
                if (this.autoscaleX())
                {
                    this.x()
                        .domain(d3.extent(merged, function(d)
                        {
                            return d.x;
                        }));
                }

                this.y()
                    .domain(d3.extent(merged, function(d)
                    {
                        return d.i;
                    }));

                if (this.label())
                {
                    this.labelY()
                        .domain(data.map(function(d)
                        {
                            return d.key;
                        }))
                        .range(data.map(function(d)
                        {
                            return d.values[0].i;
                        }));
                }

                return data;
            },

            initialize: function()
            {
                var flow, chart = this,
                    properties = {};

                flow = d3.svg.line()
                    .x(function(d)
                    {
                        return chart.x()(d.x);
                    })
                    .y(function(d)
                    {
                        return d.i < 0 ? -chart.y()(chart.y()
                            .domain()[0]) : chart.y()(d.i);
                    })
                    .interpolate(function(points)
                    {
                        var p0, move;

                        return points.map(function(p1)
                            {
                                var str, midX;

                                if (!p0)
                                {
                                    str = ["0,", p1[1], " h", 3 * horz / 2, " "].join("");
                                }
                                else if (p1[1] < 0)
                                {
                                    if (!move)
                                    {
                                        midX = (p0[0] + p1[0]) / 2;
                                        str = ["C", midX, ",", p0[1], " ", midX, ",", (p0[1] + 10), " ", midX, ",", (p0[1] + 10), " "]
                                            .join("");
                                        move = true;
                                    }
                                }
                                else if (move)
                                {
                                    midX = (p0[0] + p1[0]) / 2;
                                    str = ["M", midX, ",", p1[1] + 10, "C", midX, ",", p1[1] + 10, " ", midX, ",", p1[1], " ", p1[0],
                                            ",", p1[1], " "
                                        ]
                                        .join("");
                                    move = false;
                                }
                                else
                                {
                                    midX = (p0[0] + p1[0]) / 2;
                                    str = ["C", midX, ",", p0[1], " ", midX, ",", p1[1], " ", p1[0] - horz / 2, ",", p1[1], " h", horz, " "]
                                        .join("");
                                }

                                p0 = p1;
                                return str;
                            })
                            .join(" ");
                    });


                //TOOD reattach mousemove, mouseout?

                chart.layer("data", chart.new_group(true),
                {
                    dataBind: function(data)
                    {
                        return this.selectAll("path")
                            .data(data);
                    },
                    insert: function(data)
                    {
                        return this.append("path");
                    },
                    events:
                    {
                        enter: function()
                        {
                            var sel = this,
                                handlers = handlerFactory(chart);

                            sel.attr("class", function(d)
                            {
                                return chart.key()(d);
                            });

                            if (chart.interactive())
                            {
                                Object.keys(handlers)
                                    .forEach(function(k)
                                    {
                                        sel.on(k, handlers[k]);
                                    });
                            }
                        },
                        merge: function()
                        {
                            this.attr("d", function(d)
                            {
                                return flow(d.values);
                            });

                            if (chart.axis())
                            {
                                chart.base.select(".x.axis")
                                    .call(chart.axis());
                            }
                        },
                        //"merge:transition" : function() {},
                        exit: function()
                        {
                            this.remove();
                        }
                    }
                });

                //			chart.layer("text", chart.base.append("g"), {
                //					dataBind : function(data) {},
                //					insert : function(data) {},
                //					events : function(){}
                //				});
            }
        };

    d3.chart("UCHD")
        .extend("Flow", generator);
}());
(function()
{
    var handlerFactory = function(chart)
        {
            return {
                click: function(d)
                {
                    chart.trigger("click",
                    {
                        d: d,
                        x: chart.x()
                            .invert(d3.event.layerX - chart.margin()
                                .left),
                        y: chart.y()
                            .invert(d3.event.layerY - chart.margin()
                                .top)
                    });
                },
                mouseenter: function(d)
                {
                    chart.trigger("mouseenter",
                    {
                        d: d,
                        x: chart.x()
                            .invert(d3.event.layerX - chart.margin()
                                .left),
                        y: chart.y()
                            .invert(d3.event.layerY - chart.margin()
                                .top)
                    });
                },
                mouseleave: function(d)
                {
                    chart.trigger("mouseleave",
                    {
                        d: d,
                        x: chart.x()
                            .invert(d3.event.layerX - chart.margin()
                                .left),
                        y: chart.y()
                            .invert(d3.event.layerY - chart.margin()
                                .top)
                    });
                }
            };
        },
        generator = {
            transform: function(data)
            {
                var merged, chart = this;

                this.data = data;

                if (this.autoscaleX())
                {
                    merged = d3.merge(data.map(this.values()));
                    this.x()
                        .domain(d3.extent(merged, this.px()));
                }

                if (this.autoscaleY())
                {
                    if (!merged)
                    {
                        merged = d3.merge(data.map(this.values()));
                    }

                    this.y()
                        .domain(d3.extent(merged, this.py()));
                }

                if (this.label())
                {
                    this.labelY()
                        .domain(this.data.map(function(d)
                        {
                            return d.key;
                        }))
                        .range(this.data.map(function(d)
                        {
                            return chart.py()(chart.values()(d)[0]);
                        }));
                }

                return data;
            },

            initialize: function()
            {
                var line, chart = this,
                    properties = {};

                this.add_chart_property(properties, "interpolate", "linear");

                line = d3.svg.line()
                    .x(function(d)
                    {
                        return chart.x()(chart.px()(d));
                    })
                    .y(function(d)
                    {
                        return chart.y()(chart.py()(d));
                    });

                //TOOD reattach mousemove, mouseout?

                chart.layer("data", chart.new_group(true),
                {
                    dataBind: function(data)
                    {
                        return this.selectAll("path")
                            .data(data);
                    },
                    insert: function(data)
                    {
                        return this.append("path");
                    },
                    events:
                    {
                        enter: function()
                        {
                            var selection = this,
                                handlers = handlerFactory(chart);

                            selection.attr("class", function(_, i)
                            {
                                return i;
                            });

                            if (chart.interactive())
                            {
                                Object.keys(handlers)
                                    .forEach(function(k)
                                    {
                                        selection.on(k, handlers[k]);
                                    });
                            }
                        },
                        merge: function()
                        {
                            line.interpolate(chart.interpolate());

                            this.attr("d", function(d)
                            {
                                return line(chart.values()(d));
                            });

                            if (chart.axis())
                            {
                                chart.base.select(".x.axis")
                                    .call(chart.axis());
                            }
                        },
                        //"merge:transition" : function() {},
                        exit: function()
                        {
                            this.remove();
                        }
                    }
                });

                //			chart.layer("text", chart.base.append("g"), {
                //					dataBind : function(data) {},
                //					insert : function(data) {},
                //					events : function(){}
                //				});
            }
        };

    d3.chart("UCHD")
        .extend("Line", generator);
}());
(function()
{

    d3.chart("Useful")
        .extend("FeatureImportanceChart",
        {

            transform: function(dataArray)
            {

                var chart = this;
                chart.data = dataArray;


                var yMax = Math.max(d3.max(dataArray, function(d)
                {
                    return d[0];
                }), d3.max(dataArray, function(d)
                {
                    return d[1];
                }));
                var yMin = Math.min(d3.min(dataArray, function(d)
                {
                    return d[0];
                }), d3.min(dataArray, function(d)
                {
                    return d[1];
                }));




                var xScale = chart.x()
                    .domain([0, dataArray.length]);


                var yScale = chart.y()
                    .domain([yMin, yMax]);





                return dataArray;
            },


            initialize: function()
            {
                var chart = this;


                var barAngle = 5;

                chart.layer("features", chart.new_group(),
                {
                    dataBind: function(data)
                    {

                        return this.selectAll("path")
                            .data(data);
                    },
                    insert: function(data)
                    {

                        return this.append("path");
                    },
                    events:
                    {
                        // not sure
                        // enter : function() {
                        //   this.attr("class", function(_,i) { return i; });
                        // },
                        merge: function()
                        {


                            var size = chart.size(),
                                w = size[0],
                                h = size[1],
                                x = chart.x(),
                                y = chart.y();

                            this.attr('d', function(d, i)
                            {

                                var x1 = x(i),
                                    y1 = (y(d[0]));
                                var x2 = x(i),
                                    y2 = (y(d[1]));

                                var barBase = Math.tan(barAngle * Math.PI / 180) * (y1 > y2 ? y1 - y2 : y2 - y1);


                                return 'M ' + x1 + ' ' + y1 + 'L' + (x2 + barBase / 2) + ' ' + y2 + 'L' + (x2 - barBase / 2) +
                                    ' ' + y2 + 'z';
                            });
                        },

                        exit: function()
                        {
                            this.remove();
                        }
                    }
                });

                chart.layer("xAxisGroup", chart.new_group(),
                {
                    dataBind: function(data)
                    {


                        var xAxis = d3.svg.axis()
                            .scale(chart.x())
                            .orient("bottom")
                            .ticks(data.length);



                        var yAxis = d3.svg.axis()
                            .scale(chart.y())
                            .orient("left")
                            .ticks(data.length);



                        return this.selectAll("g")
                            .data([xAxis, yAxis]);
                    },
                    insert: function(data)
                    {

                        return this.append("g");
                    },
                    events:
                    {
                        // not sure
                        // enter : function() {
                        //   this.attr("class", function(_,i) { return i; });
                        // },
                        merge: function()
                        {
                            var size = chart.size(),
                                w = size[0],
                                h = size[1];

                            this.each(function(d, i)
                            {
                                d3.select(this)
                                    .attr("transform", "translate(0, " + (i === 0 ? h : 0) + ")")
                                    .call(d);
                            });

                        },

                        exit: function()
                        {
                            this.remove();
                        }
                    }
                });


            }
        });
}());
