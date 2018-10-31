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

                //API call to get the channelID from the user name
                let handler = $http.get(getChannelURL +
                    "&forUsername=" + $scope.query +
                    "&key=" + apiKey);
                handler.success(function (response) { //if the call was successful

                    //get the channel details from the JSON to set later so that the page
                    // isn't distorted by updating it too soon
                    var channelTitle = response.items["0"].snippet.title;
                    var channelID = response.items["0"].id;
                    var thumbnail = response.items["0"].snippet.thumbnails.medium.url;
                    var description = response.items["0"].snippet.description;

                    //Define a sub api call to get the subscription list of the channel by using the id
                    // obtained in the previous API call
                    let subsHandler = $http.get(subsURL +
                        "&channelId=" + channelID +
                        "&key=" + apiKey +
                        "&maxResults=" + 50);
                    subsHandler.success(function (response) { //if the call was successful
                        $scope.transit(); //move the logo and search bar out of the way
                        $scope.channelTitle = channelTitle; //
                        $scope.thumbnail = thumbnail;       //set the channel variables to display in the media object
                        $scope.description = description;   //
                        $scope.bubbles(response);           //**call the d3 function to create the bubble svg
                        console.log(response.kind);
                    });
                    //Some channels do not permit seeing their subscription list, so the page needs to be reset
                    subsHandler.error(function (response) {
                        $scope.channelTitle = "";   //
                        $scope.thumbnail = "";      //clear channel variables
                        $scope.description = "";    //
                        $scope.transitBack();       //put the logo back in the middle
                        $scope.bubbles(response);   //remove the bubble svg
                        alert(response.error.message)
                    });
                    //$scope.searchResult = response.items; //store the items section of the returned JSON for ng-repeat
                });
                //handler if the user is not found
                handler.error(function (response) {
                    $scope.channelTitle = "";   //
                    $scope.thumbnail = "";      //clear channel variables
                    $scope.description = "";    //
                    $scope.transitBack();       //put the logo back in the middle
                    $scope.bubbles(response);   //remove the bubble svg
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

            d3.selectAll("svg").remove();   //clear out any previous graph
            d3.selectAll("div.tooltip").remove();   //remove previous tooltip div
            if(json.error) return;          //end the function if in an error condition

            var diameter = 600;

            //create the svg object and add the <svg> tag to the page
            var svg = d3.select('#graph').append('svg')
                .attr('width', diameter)
                .attr('height', diameter);

            //load the information
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

            //create the div object to relocate when the bubbles are moused over/out
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
                    return "hsl(" + Math.random() * 360 + ",100%,50%)"; //random color generator
                })
                .on("mouseover", function(d) {          //move the tooltip div and set the values when mousing over a bubble
                    div.transition()
                        .duration(200)
                        .style("opacity", .9);
                    div	.html(d.name + "<br/>"  + d.size)
                        .style("left", (d3.event.pageX) + "px")
                        .style("top", (d3.event.pageY - 28) + "px");
                })
                .on("mouseout", function(d) {           //hide the tooltip on mouseout
                    div.transition()
                        .duration(500)
                        .style("opacity", 0);
                });

            function processData(data) {
                var obj = data;

                var newDataSet = [];

                //create an empty object if an error state gets through
                if(data.error) return {children: newDataSet.push({name: "", className: "", size: 0})};

                //Gather the channel titles and the number of videos they have
                var titles = obj.items.map(function(d) {return d.snippet.title});
                var counts = obj.items.map(function(d) {return d.contentDetails.totalItemCount});

                //build the object to store and access later
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