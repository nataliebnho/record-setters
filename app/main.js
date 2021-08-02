Parse.Cloud.define("pushsample", (request) => {

    return Parse.Push.send({
        channels: ["News"],
        data: {
            title: "Congratulations, you have won a category!",
            alert: "Record Setters",
        }
    }, { useMasterKey: true });
});