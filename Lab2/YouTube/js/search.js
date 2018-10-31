var url = "https://www.googleapis.com/youtube/v3/subscriptions";
var apiKey = "AIzaSyBhAcbOBlU7lepVO-jyqtq7g1j9lRhT-_c";
let videoString = "https://www.youtube.com/embed/";


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
                let handler = $http.get(url + "search?" +  //build the API call
                    "part=" + "snippet" +
                    "&q=" + $scope.query +
                    "&type=" + "" +
                    "&key=" + apiKey +
                    "&maxResults=" + "20");
                handler.success(function (response) { //if the call was successful
                    $scope.transit(); //remove the hero shot elements to move the logo and search to the top of the page
                    console.log(response);
                    $scope.searchResult = response.items; //store the items section of the returned JSON for ng-repeat
                });
                handler.error(function (response) {
                    alert("There was some error processing your request.")
                });
            }
        };

        //Function to remove CSS elements that display a hero shot for the youtube logo and search bar
        // when first loading the page.
        $scope.transit = function(){
            $('.logo').css("position", "relative");
            $('.logo').css("top", "0");
            $('.logo').css("left", "0");
            $('.logo').css("transform", "translate(0%,0%)");
        }

        //Helper function to insert identifiers contained in each video element listed
        // into the modal display to contain the YouTube video itself and the title
        $scope.setVideo = function(id,title){
            var src = videoString + id; //build the src string
            $scope.title = title; //store the title in a controller accessible object
            $('#videoModal').modal('show'); //show the pop-out modal
            $('#YouTubeVideo').attr('src', src); //set the src attribute of the iframe to show a YouTube video

        };

        //Helper function to stop video playback when the modal is closed
        $scope.closeModal = function () {
            $('#videoModal iframe').prop('src', ''); //pause playback
        };
    });


    