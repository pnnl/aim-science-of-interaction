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
                    return String(d.key);
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
                            chart.add_chart_property(properties, "align", "left");
                            chart.add_chart_property(properties, "labelX", function(d)
                            {
                                var align;

                                switch (chart.align())
                                {
                                    case "left":
                                        align = 0;
                                        break;
                                    case "right":
                                        align = 1;
                                        break;
                                    default:
                                        align = 0;
                                        break;
                                }
                                return chart.x()
                                    .range()[align];
                            });
                            chart.add_chart_property(properties, "labelY", function(d)
                            {
                                var align;

                                switch (chart.align())
                                {
                                    case "left":
                                        align = 0;
                                        break;
                                    case "right":
                                        align = chart.values()(d)
                                            .length - 1;
                                        break;
                                    default:
                                        align = 0;
                                        break;
                                }

                                return chart.y()(chart.py()(chart.values()(d)[align]));
                            });

                            chart.layer('ylabels', chart.new_group(),
                            {
                                dataBind: function(data)
                                {
                                    return this.selectAll('g')
                                        .data(data, function(d)
                                        {
                                            return d.hasOwnProperty("key") ? d.key : chart.key()(d);
                                        });
                                },
                                insert: function()
                                {
                                    var g = this.append('g');

                                    g.append('text')
                                        .classed('label background', true);

                                    g.append('text')
                                        .classed('label foreground', true);

                                    return g;
                                },
                                events:
                                {
                                    "merge:transition": function()
                                    {
                                        this
                                            .duration(1000)
                                            .attr("transform", function(d, i)
                                            {
                                                var x = chart.labelX()(d);
                                                x = isNaN(x) ? -1000 : x;
                                                return "translate(" + x + ", " + chart.labelY()(d, i) +
                                                    ")";
                                            })
                                            .style('display', function(d)
                                            {
                                                return isNaN(chart.labelX()(d)) ? 'none' : '';
                                            })
                                            .each(function(d)
                                            {
                                                var g = d3.select(this);

                                                g.selectAll('text')
                                                    .style('text-anchor', function(d)
                                                    {
                                                        var x = chart.labelX()(d);

                                                        if (x === chart.x()
                                                            .range()[1])
                                                        {
                                                            return "start";
                                                        }
                                                        else if (x === chart.x()
                                                            .range()[0])
                                                        {
                                                            return "end";
                                                        }
                                                        else
                                                        {
                                                            return "middle";
                                                        }
                                                    })
                                                    .attr('dy', '.5em')
                                                    .text(chart.key());
                                            });
                                    },
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
                },
                mouseover: function(d)
                {
                    chart.trigger("mouseover",
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
                    merged,
                    offset = chart.offset();

                this.data = data;

                var stack = d3.layout.stack()
                    .offset(offset),
                    layers = stack(data.map(function(layer)
                    {
                        return chart.values()(layer)
                            .map(function(d)
                            {
                                return {
                                    x: chart.px()(d),
                                    y: chart.py()(d) > threshold ? chart.py()(d) : chart.minimum()
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
                    .domain([d3.max(layers, function(layer)
                    {
                        return d3.max(layer, function(d)
                        {
                            return d.y0 + d.y;
                        });
                    }), 0]);

                return data;
            },

            initialize: function()
            {
                var x, y, area, properties = {},
                    label,
                    chart = this;

                chart.add_chart_property(properties, "offset", "wiggle");

                offset = chart.offset;

                chart.add_chart_property(properties, "minimum", 0.05);

                label = chart.label;

                chart.add_chart_property(properties, "label", false,
                {
                    get: function()
                    {
                        return !!properties.labelY;
                    },
                    set: function(v)
                    {
                        if (v)
                        {
                            label(v);
                            chart.labelX(function(d)
                            {
                                var x;

                                d.values.forEach(function(v, i)
                                {
                                    if ((chart.align() === "right" || x === undefined) && v.y >= chart.threshold())
                                    {
                                        x = v.x;
                                    }
                                });
                                return chart.x()(x);
                            });
                            chart.labelY(function(d)
                            {
                                var y;

                                d.values.forEach(function(v, i)
                                {
                                    if ((chart.align() === "right" || y === undefined) && v.y > chart.threshold())
                                    {
                                        y = d.layer[i].y0 + d.layer[i].y / 2;
                                    }
                                });
                                return chart.y()(y);
                            });
                        }
                    }
                });

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
                    })
                    .interpolate("monotone");

                //TOOD reattach mousemove, mouseout?

                chart.layer("data", chart.new_group(true),
                {
                    dataBind: function(data)
                    {
                        return this.selectAll("path")
                            .data(data, function(d)
                            {
                                return chart.key()(d);
                            });
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
                                return area(d.layer);
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
            getActualHeight: function()
            {
                var y = Math.abs(this.y()
                    .range()[0] - this.y()
                    .range()[1]);
                return Math.min(HEIGHT, y / this.data.length);
            },
            transform: function(data)
            {
                var chart = this,
                    py = chart.py(),
                    threshold = chart.threshold(),
                    merged;

                this.data = data;

                data = data.map(function(d)
                {
                    var values = chart.values()(d),
                        prev,
                        segments = [];

                    values.forEach(function(v)
                    {
                        if (py(v) >= threshold)
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
                        values: values,
                        segments: segments
                    };
                });

                merged = d3.merge(this.data.map(function(d)
                {
                    return chart.values()(d);
                }));

                if (this.autoscaleX())
                {
                    this.x()
                        .domain(d3.extent(merged, this.px()));
                }

                this.y()
                    .domain([-1, data.length - 1]);

                if (this.label())
                {
                    this.labelY(function(d, i)
                    {
                        return chart.y()(i) + chart.getActualHeight() / 2;
                    });
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
                            .data(data, function(d)
                            {
                                return d.key;
                            });
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
                                    return d.key;
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

                            this.attr("transform", function(d, i)
                            {
                                d.i = i;
                                return "translate(0," + chart.y()(i) + ")";
                            });

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
                                    return chart.x()(chart.px()(d[1])) - chart.x()(chart.px()(d[0]));
                                },
                                y: 0,
                                height: chart.getActualHeight()
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
                            this.each(function(d)
                                {
                                    console.log(this, d);
                                })
                                .remove();
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
                    var sorted = d.slice()
                        .sort(function(a, b)
                        {
                            return b.y - a.y || a.x - b.x || (String(a.key))
                                .localeCompare(String(b.key));
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
                        })
                        .map(function(d, i)
                        {
                            return i ? d + 1 :
                                d - 1;
                        }));

                if (this.label())
                {
                    this.labelY(function(d)
                    {
                        var align;

                        switch (chart.align())
                        {
                            case "left":
                                align = 0;
                                break;
                            case "right":
                                align = d.values.length - 1;
                                break;
                            default:
                                align = 0;
                                break;
                        }

                        return chart.y()(d.values[align].i);
                    });
                }

                return data;
            },

            initialize: function()
            {
                var flow, chart = this,
                    properties = {};


                flow = d3.svg.area()
                    .x(function(d)
                    {
                        return chart.x()(d.x);
                    })
                    .y(function(d)
                    {
                        return (d.y > chart.threshold() ? (chart.y()(d.i) - chart.size()[1] * 0.4 / chart.data.length / 2) : chart.y()(d.i));
                    })
                    .y1(function(d)
                    {
                        return (d.y > chart.threshold() ? (chart.y()(d.i) + chart.size()[1] * 0.4 / chart.data.length / 2) : chart.y()(d.i));
                    })
                    .interpolate("monotone");

                chart.layer("data", chart.new_group(true),
                {
                    dataBind: function(data)
                    {
                        return this.selectAll("path")
                            .data(data, function(d)
                            {
                                return d.key;
                            });
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
                    this.labelY(function(d)
                    {
                        var align;

                        switch (chart.align())
                        {
                            case "left":
                                align = 0;
                                break;
                            case "right":
                                align = chart.values()(d)
                                    .length - 1;
                                break;
                            default:
                                align = 0;
                                break;
                        }

                        return chart.y()(chart.py()(chart.values()(d)[align]));
                    });
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
                            .data(data, function(d)
                            {
                                return chart.key()(d);
                            });
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

                var xMax = Math.max(d3.max(dataArray, function(d)
                {
                    return d[0];
                }), d3.max(dataArray, function(d)
                {
                    return d[1];
                }));
                var xMin = Math.min(d3.min(dataArray, function(d)
                {
                    return d[0];
                }), d3.min(dataArray, function(d)
                {
                    return d[1];
                }));

                var xScale = chart.x()
                    .domain([xMin, xMax]);

                var yScale = chart.y()
                    .domain([0, dataArray.length - 1]);

                return dataArray;
            },

            initialize: function()
            {
                var chart = this;
                var barAngle = 15;

                chart.layer("features", chart.base.append("g")
                    .attr("class", "features"),
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
                                this.attr("class", function(_, i)
                                {
                                    return "feature feature" + i;
                                });
                            },
                            merge: function()
                            {
                                var x = chart.x(),
                                    y = chart.y();

                                this
                                    .attr('d', function(d, i)
                                    {
                                        var x1 = x(d[0]),
                                            y1 = (y(i));
                                        var x2 = x(d[1]),
                                            y2 = (y(i));

                                        var barBase = Math.tan(barAngle * Math.PI / 180) * (x1 > x2 ? x1 - x2 : x2 - x1);

                                        return 'M ' + x1 + ' ' + y1 + 'L' + x2 + ' ' + (y2 + barBase / 2) + 'L' + x2 +
                                            ' ' + (y2 - barBase / 2) + 'z';
                                    });
                            },
                            exit: function()
                            {
                                this.remove();
                            }
                        }
                    });

                chart.layer("text", chart.base.append("g")
                    .attr("class", "featureLabels"),
                    {
                        dataBind: function(data)
                        {
                            return this.selectAll("text")
                                .data(data, function(d, i)
                                {
                                    return "label" + (d.name || i);
                                });
                        },
                        insert: function(data)
                        {
                            return this.append("text");
                        },
                        events:
                        {
                            enter: function()
                            {
                                this.attr("class", function(_, i)
                                {
                                    return "featureLabel label" + i;
                                });
                            },
                            merge: function()
                            {
                                var x = chart.x(),
                                    y = chart.y();

                                this
                                    .attr("dominant-baseline", "middle")
                                    .attr("dx", function(d)
                                    {
                                        return (x(d[1]) + (x(d[0]) > x(d[1]) ? -10 : 10));
                                    })
                                    .attr("dy", function(d, i)
                                    {
                                        return y(i);
                                    })
                                    .style("visibility", function(d)
                                    {
                                        return (d[0] - d[1]) === 0 ? "hidden" : "visible";
                                    })
                                    .style("text-anchor", function(d)
                                    {
                                        return x(d[0]) > x(d[1]) ? "end" : "start";
                                    })
                                    .text(function(d, i)
                                    {
                                        return d.name || i;
                                    });
                            },
                            exit: function()
                            {
                                this.remove();
                            }
                        }
                    });

                // chart.layer("xAxisGroup", chart.new_group(),
                // {
                //     dataBind: function(data)
                //     {
                //         var xAxis = d3.svg.axis()
                //             .scale(chart.x())
                //             .orient("bottom");
                //
                //         var yAxis = d3.svg.axis()
                //             .scale(chart.y())
                //             .orient("left")
                //             .ticks(data.length)
                //             .tickFormat(function(d, i){
                //               return data[d].name;
                //             });;
                //
                //         return this.selectAll("g")
                //             .data([xAxis, yAxis]);
                //     },
                //     insert: function(data)
                //     {
                //         return this.append("g");
                //     },
                //     events:
                //     {
                //         // not sure
                //         enter : function()
                //         {
                //           this.attr("class", function(_, i)
                //           {
                //               return i;
                //           });
                //         },
                //         merge: function()
                //         {
                //             var size = chart.size(),
                //                 w = size[0],
                //                 h = size[1]; //-(chart.margin().top+chart.margin().bottom);
                //
                //             this.each(function(d, i)
                //             {
                //                 d3.select(this)
                //                     .attr("transform", "translate(0, " + (i === 0 ? h : 0) + ")")
                //                     .call(d);
                //             });
                //         },
                //
                //         exit: function()
                //         {
                //             this.remove();
                //         }
                //     }
                // });
            }
        });
}());
// adapted from: http://bl.ocks.org/mbostock/4063570
d3.chart("Useful")
    .extend("Dendrogram",
    {
        initialize: function()
        {
            var chart = this,
                properties = {};

            chart.add_chart_property(properties, "cluster", d3.layout.cluster());
            chart.add_chart_property(properties, "text", function(d)
            {
                return d.text;
            });
            chart.add_chart_property(properties, "key", function(d)
            {
                return d.key;
            });
            chart.add_chart_property(properties, "values", function(d)
            {
                return d.values;
            });

            var link_layer = chart.base.append("g")
                .classed("links", true),
                node_layer = chart.base.append("g")
                .classed("nodes", true);

            chart.layer("nodes", node_layer,
            {
                dataBind: function(data)
                {
                    var x = chart.x(),
                        y = chart.y(),
                        x_range = x.range(),
                        y_range = y.range(),
                        w = x_range[1] - x_range[0],
                        h = y_range[0] - y_range[1];

                    x.domain([0, w]);
                    y.domain([0, h]);

                    chart.nodes = chart.cluster()
                        .children(chart.values())
                        .size([h, w])
                        .nodes(data);

                    return this.selectAll("g")
                        .data(chart.nodes, chart.key());
                },
                insert: function()
                {
                    return this.append("g");
                },
                events:
                {
                    exit: function()
                    {
                        this.remove();
                    },
                    enter: function()
                    {
                        var values = chart.values();
                        this
                            .attr("class", function(d)
                            {
                                return d.parent ? (values(d) ? "internal" : "leaf") : "root";
                            });
                        this.append("circle")
                            .attr("r", 4.5);
                        this.append("text");
                    },
                    merge: function()
                    {
                        var x = chart.x(),
                            y = chart.y(),
                            values = chart.values();

                        this.attr("transform", function(d)
                            {
                                return "translate(" + x(d.y) + "," + y(d.x) + ")";
                            })
                            .attr("depth", function(d)
                            {
                                return d.depth;
                            });
                        this.select("circle");
                        this.select("text")
                            .attr("dx", function(d)
                            {
                                return values(d) ? -8 : 8;
                            })
                            .attr("dy", 5)
                            .text(chart.text());
                    }
                }
            });

            chart.layer("links", link_layer,
            {
                dataBind: function(data)
                {
                    return this.selectAll("path")
                        .data(chart.cluster()
                            .links(chart.nodes));
                },
                insert: function()
                {
                    return this.append("path");
                },
                events:
                {
                    exit: function()
                    {
                        this.remove();
                    },
                    merge: function()
                    {
                        var x = chart.x(),
                            y = chart.y(),
                            diagonal = d3.svg.diagonal()
                            .projection(function(d)
                            {
                                return [x(d.y), y(d.x)];
                            });
                        this.attr("d", diagonal)
                            .attr("depth", function(d)
                            {
                                return d.source.depth;
                            });
                    }
                }
            });
        }
    });
(function()
{
    // This chart has been adapted from the Collision Detection example on d3js.org
    // http://bl.ocks.org/mbostock/3231298

    d3.chart("Useful")
        .extend("Bubble",
        {
            initialize: function()
            {
                var chart = this,
                    properties = {};

                chart.add_chart_property(properties, "value", function(d)
                {
                    return d.value;
                });
                chart.add_chart_property(properties, "key", function(d)
                {
                    return d.key;
                });
                chart.add_chart_property(properties, "text", function(d)
                {
                    return d.text;
                });
                chart.add_chart_property(properties, "padding", 3);
                chart.add_chart_property(properties, "threshold", null);

                chart.force = d3.layout.force()
                    .gravity(0.05)
                    .charge(0)
                    .on("tick.collide", function(e)
                    {
                        chart.resolve_collisions();
                    });

                chart.layer("bubbles", chart.base.append("g")
                    .attr("class", "bubbles"),
                    {
                        dataBind: function(data)
                        {
                            return this.selectAll("g")
                                .data(data, chart.key());
                        },

                        insert: function()
                        {
                            return this.append("g");
                        },

                        events:
                        {
                            enter: function()
                            {
                                var x = chart.x(),
                                    y = chart.y(),
                                    x0 = d3.scale.linear()
                                    .domain([-1, 2])
                                    .range(x.domain());

                                this.each(function(d)
                                {
                                    d.x = x0(Math.random());
                                    d.y = y(y.domain()[0]) - Math.random();
                                });
                                this.append("circle");
                                this.append("text")
                                    .text(chart.text());
                            },

                            exit: function()
                            {
                                var x = chart.x(),
                                    y = chart.y(),
                                    xf = d3.scale.linear()
                                    .domain([-1, 2])
                                    .range(x.range());

                                this.transition()
                                    .duration(1000)
                                    .attr("transform", function(d)
                                    {
                                        return "translate(" + [xf(Math.random()), y.range()[0]] + ")";
                                    })
                                    .each("end", function()
                                    {
                                        d3.select(this)
                                            .remove();
                                    });
                            },

                            merge: function()
                            {

                                var x = chart.x(),
                                    y = chart.y(),
                                    dx = x.range(),
                                    dy = y.range(),
                                    w = dx[1] - dx[0],
                                    h = dy[0] - dy[1],
                                    selection = this,
                                    dr = Math.sqrt(w * w + h * h) / 2000; // amount of radius to change per tick is a small %-age of screen

                                x.domain([0, w]);
                                y.domain([0, h]);

                                chart.force
                                    .nodes(chart.data)
                                    .size([w, h])
                                    .start()
                                    .resume()
                                    .on("tick.xy", function()
                                    {
                                        selection
                                            .each(function(d)
                                            {
                                                if (Math.abs(d.r - d.r_target) <= dr)
                                                {
                                                    d.r = d.r_target;
                                                }
                                                else
                                                {
                                                    d.r += (d.r_target > d.r ? 1 : -1) * dr;
                                                }
                                            })
                                            .attr("transform", function(d)
                                            {
                                                return "translate(" + [x(d.x), y(d.y)] + ")";
                                            })
                                            .select("circle")
                                            .attr("r", function(d)
                                            {
                                                return d.r;
                                            });
                                    });
                            }
                        }
                    });

            },

            transform: function(data)
            {
                var chart = this,
                    value = chart.value(),
                    threshold = chart.threshold();

                chart.data = data.filter(function(d)
                {
                    d.r_target = value(d);
                    if (!("r" in d) || d.r < 0)
                    {
                        d.r = d.r_target;
                    }
                    return threshold === null ? true : d.r_target > threshold;
                });

                return chart.data;
            },

            resolve_collisions: function()
            {
                var chart = this,
                    nodes = chart.data,
                    value = chart.value(),
                    padding = chart.padding();

                var q = d3.geom.quadtree(nodes),
                    i = 0,
                    n = nodes.length;

                function collide(node)
                {

                    var r = node.r + padding + 16,
                        nx1 = node.x - r,
                        nx2 = node.x + r,
                        ny1 = node.y - r,
                        ny2 = node.y + r;
                    return function(quad, x1, y1, x2, y2)
                    {
                        if (quad.point && (quad.point !== node))
                        {
                            var x = node.x - quad.point.x,
                                y = node.y - quad.point.y,
                                l = Math.sqrt(x * x + y * y),
                                r = node.r + quad.point.r + 2.0 * padding;
                            if (l < r)
                            {
                                l = (l - r) / l * 0.25;
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
}());
