var apiKey = "AIzaSyBhAcbOBlU7lepVO-jyqtq7g1j9lRhT-_c";
var getChannelURL = "https://www.googleapis.com/youtube/v3/channels?part=id%2Csnippet%2Cstatistics%2CcontentDetails";
var subsURL = "https://www.googleapis.com/youtube/v3/subscriptions?part=snippet%2CcontentDetails&";

angular.module('searchApp', [])


    .controller('searchController', function($scope, $http) {

        //Set the default values
        $scope.query = "";
        $scope.searchResult = "";
        $scope.videoID = "rG_ry1hkFXg";
        $scope.title = "";

        $scope.search = function () {
            console.log("search clicked");
            let q = $scope.query; //get the search term from the input field
            if (q != null && q !== "") { //check if there is input in the field to search
                let handler = $http.get(getChannelURL +
                    "&forUsername=" + $scope.query +
                    "&key=" + apiKey);
                handler.success(function (response) { //if the call was successful
                    var channelTitle = response.items["0"].snippet.title;
                    var channelID = response.items["0"].id;
                    var thumbnail = response.items["0"].snippet.thumbnails.medium.url;
                    var description = response.items["0"].snippet.description;

                    let subsHandler = $http.get(subsURL +
                        "&channelId=" + channelID +
                        "&key=" + apiKey +
                        "&maxResults=" + 50);
                    subsHandler.success(function (response) { //if the call was successful
                        $scope.transit();
                        $scope.channelTitle = channelTitle;
                        $scope.thumbnail = thumbnail;
                        $scope.description = description;
                        $scope.bubbles(response);
                        console.log(response.kind);
                    });
                    subsHandler.error(function (response) {
                        $scope.channelTitle = "";
                        $scope.thumbnail = "";
                        $scope.description = "";
                        $scope.transitBack();
                        $scope.bubbles(response);
                        alert(response.error.message)
                    });
                    //$scope.searchResult = response.items; //store the items section of the returned JSON for ng-repeat
                });
                handler.error(function (response) {
                    $scope.transitBack();
                    $scope.bubbles(response);
                    alert(response.error.message)
                });
            }
        };

        $scope.transit = function(){
            $('.logo').css("position", "relative");
            $('.logo').css("top", "0");
            $('.logo').css("left", "0");
            $('.logo').css("transform", "translate(0%,0%)");
        }

        $scope.transitBack = function(){
            $('.logo').css("position", "absolute");
            $('.logo').css("top", "50%");
            $('.logo').css("left", "50%");
            $('.logo').css("transform", "translate(-50%,-50%)");
        }

        $scope.bubbles = function(json) {

            // Fake JSON data
            /*var json = {"countries_msg_vol": {
                   "CA": 170, "US": 393, "BB": 12, "CU": 9, "BR": 89, "MX": 192, "PY": 32, "UY": 9, "VE": 25, "BG": 42, "CZ": 12, "HU": 7, "RU": 184, "FI": 42, "GB": 162, "IT": 87, "ES": 65, "FR": 42, "DE": 102, "NL": 12, "CN": 92, "JP": 65, "KR": 87, "TW": 9, "IN": 98, "SG": 32, "ID": 4, "MY": 7, "VN": 8, "AU": 129, "NZ": 65, "GU": 11, "EG": 18, "LY": 4, "ZA": 76, "A1": 2, "Other": 254
               }};*/

            d3.selectAll("svg").remove();
            if(json.error) return;

            var diameter = 600;

            var svg = d3.select('#graph').append('svg')
                .attr('width', diameter)
                .attr('height', diameter);

            var bubble = d3.layout.pack()
                .size([diameter, diameter])
                .value(function (d) {
                    return d.size;
                })
                .sort(function (a, b) {
                    return -(a.value - b.value)
                })
                .padding(3);

            // generate data with calculated layout values
            var nodes = bubble.nodes(processData(json))
                .filter(function (d) {
                    return !d.children;
                }); // filter out the outer bubble

            var vis = svg.selectAll('circle')
                .data(nodes);

            var div = d3.select("body").append("div")
                .attr("class", "tooltip")
                .style("opacity", 0);

            vis.enter().append('circle')
                .attr('transform', function (d) {
                    return 'translate(' + d.x + ',' + d.y + ')';
                })
                .attr('r', function (d) {
                    return d.r;
                })
                .attr('class', function (d) {
                    return d.className;
                })
                .style("fill",function() {
                    return "hsl(" + Math.random() * 360 + ",100%,50%)";
                })
                .on("mouseover", function(d) {
                    div.transition()
                        .duration(200)
                        .style("opacity", .9);
                    div	.html(d.name + "<br/>"  + d.size)
                        .style("left", (d3.event.pageX) + "px")
                        .style("top", (d3.event.pageY - 28) + "px");
                })
                .on("mouseout", function(d) {
                    div.transition()
                        .duration(500)
                        .style("opacity", 0);
                });

            function processData(data) {
                var obj = data;

                var newDataSet = [];

                if(data.error) return {children: newDataSet.push({name: "", className: "", size: 0})};

                var titles = obj.items.map(function(d) {return d.snippet.title});
                var counts = obj.items.map(function(d) {return d.contentDetails.totalItemCount});

                for (var i=0; i< titles.length; i++) {
                    newDataSet.push({
                        name: titles[i],
                        className: titles[i].toLowerCase(),
                        effect: "jello",
                        size: counts[i]
                    });
                }
                return {children: newDataSet};
            }
        }

    });