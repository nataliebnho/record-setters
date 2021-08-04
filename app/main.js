Parse.Cloud.define("pushsample", (request) => {
    var extraTime = 300000; //30 sec for test
    var currentDate = Date.now();
    //since date object is just number we can add our extra time to our date.
    var pushDate = currentDate + extraTime; //push date is 30 sec later than now
    return Parse.Push.send({
        push_time: pushDate,
        channels: ["zoom"],
        data: {
            title: "The final results for zoom is in! Check to see who won...",
            alert: "Record Setters",
        }
    }, { useMasterKey: true });
});