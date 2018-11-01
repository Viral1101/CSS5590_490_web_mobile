var Twit = require('twit'),
mongoose = require('mongoose');

var T = new Twit({
    consumer_key:         'QBaQkYQ4nDxKbp6cj42LoA5f4',
    consumer_secret:      '0nzqH1WLAzNn42wsVLBcF7Hq3c8ZVHVE8LUGaG1PF0u9mPAX8A',
    access_token:         '901833954298298368-tRg4AyA8P3pFrQ5NRl0tZkcQIobWFrL',
    access_token_secret:  '8vwVF7PaGif7hTCKEWToS68Psa3XXcvAvQZyICAO54IYZ',
    timeout_ms:           60*1000,  // optional HTTP request timeout to apply to all requests.
    strictSSL:            true,     // optional - requires SSL certificates to be valid.
})

mongoose.connect('mongodb://localhost/twitter');
var Schema = mongoose.Schema;
var userSchema = new Schema({}, {"strict": false});
var User = mongoose.model('User', userSchema, 'tweets');

module.exports = User;

var stream = T.stream('statuses/filter', { track: ['@sprintcare','@sprint']})

stream.on('tweet', function (tweet) {

    var TwitterData = new User(tweet);
    TwitterData.save(function (err, tweet) {
        if (err) return console.error(err);
        console.log(tweet);
    });

});