var apiKey = "AIzaSyBhAcbOBlU7lepVO-jyqtq7g1j9lRhT-_c";
var url = "https://www.googleapis.com/youtube/v3/";

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
                let handler = $http.get(url + "subscriptions?" +  //build the API call
                    "part=" + "snippet%2CcontentDetails" +
                    "&channelId=" + $scope.query +
                    "&key=" + apiKey +
                    "&maxResults=" + "50");
                handler.success(function (response) { //if the call was successful
                    //$scope.transit(); //remove the hero shot elements to move the logo and search to the top of the page
                    $scope.bubbles(response);
                    console.log(response.kind);
                    //$scope.searchResult = response.items; //store the items section of the returned JSON for ng-repeat
                });
                handler.error(function (response) {
                    alert("There was some error processing your request.")
                });
            }
        };



        $scope.bubbles = function(json) {

            // Fake JSON data
            /*var json = {"countries_msg_vol": {
                   "CA": 170, "US": 393, "BB": 12, "CU": 9, "BR": 89, "MX": 192, "PY": 32, "UY": 9, "VE": 25, "BG": 42, "CZ": 12, "HU": 7, "RU": 184, "FI": 42, "GB": 162, "IT": 87, "ES": 65, "FR": 42, "DE": 102, "NL": 12, "CN": 92, "JP": 65, "KR": 87, "TW": 9, "IN": 98, "SG": 32, "ID": 4, "MY": 7, "VN": 8, "AU": 129, "NZ": 65, "GU": 11, "EG": 18, "LY": 4, "ZA": 76, "A1": 2, "Other": 254
               }};*/

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

            vis.enter().append('circle')
                .attr('transform', function (d) {
                    return 'translate(' + d.x + ',' + d.y + ')';
                })
                .attr('r', function (d) {
                    return d.r;
                })
                .attr('class', function (d) {
                    return d.className;
                });

            function processData(data) {
                var obj = data;

                var newDataSet = [];

                var titles = obj.items.map(function(d) {return d.snippet.title});
                var counts = obj.items.map(function(d) {return d.contentDetails.totalItemCount});

                for (var i=0; i< titles.length; i++) {
                    newDataSet.push({
                        name: titles[i],
                        className: titles[i].toLowerCase(),
                        size: counts[i]
                    });
                }
                return {children: newDataSet};
            }
        }

    });