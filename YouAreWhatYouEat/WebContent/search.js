var s = "";
$(function() {
    
    //$("#cmpBtn").hide();
    listener();
    query();

    $("#searchBtn").click(function(){ query(); });

    function query() {
        $.post("SearchEngine", {
            type: "search",
            q: $("#search").val()
        })
        .done(function(d){
            console.log("trying to parse response " + d);
            try {
                // verify data conforms
                var json = JSON.parse(d);
                console.log("items passed: " + json.length);
                s = json;

                // populate list
                populateList(json);

                // click first
                //$(".foodItem").first().click();
            }
            catch(e) {
                console.log("error parsing");
            }
        });
    }
});

function populateList(data) {
    var str = "";

    // constants
    const prep = '<option class="foodItem" id="f';
    const end = '</option>';

    for(let i in data) {
        let item = data[i];
        str += prep + item.foodId + '">' + item.foodName + '</option>';
    }
    $("#list").html(str);
    listener();
}

// a click listener for food items
function listener() {

    // when an item is clicked
    $(".foodItem").click(function(){

        // show add button if available
        $("#addBtn").show();
        
        // show button
        $("#cmpBtn").show();

        // get item id
        var foodId = $(this).attr("id");
        foodId = foodId.substr(1);
        console.log("id is " + foodId);

        // request info from back end
        $.post("SearchEngine", {
            type: "getFood",
            foodId: foodId
        })
        .done(function(d){
            console.log("trying to parse response");
            try {
                // check it conforms
                var json = JSON.parse(d);
                s = json;
                console.log("parsing: " + json);
                console.log("foodId: " + json.foodId);
                console.log("foodName: " + json.foodName);
                console.log("calories: " + json.calories);

                // display info
                const br = '<br>';
                $("#info").html('<b>' + json.foodName + '</b>' + br 
                    + "calories: " + json.calories
                );
            }
            catch(e) {
                console.log("error parsing");
            }
        });

        // compare if necessary
        if(compare) {
            $("#cmpBtn")
                .html("Compare")
                .removeClass("btn-success")
                .addClass("btn-danger")
                .hide();
            compare = false;
            
            // back end
            $.post("SearchEngine", {
                type: "compare",
                food1: comp1,
                food2: foodId
            })
            .done(function(d){
                alert("response is " + d);

                try {
                    var json = JSON.parse(d);
                    $("#item1").text(json[0].foodName);
                    $("#cal1").text(json[0].calories);
                    $("#prot1").text(json[0].protein);
                    $("#vit1").text(json[0].vitamin);
                    $("#sugar1").text(json[0].sugar);

                    $("#item2").text(json[1].foodName);
                    $("#cal2").text(json[1].calories);
                    $("#prot2").text(json[1].protein);
                    $("#vit2").text(json[1].vitamin);
                    $("#sugar2").text(json[1].sugar);

                    s = json;
                } catch(e) {
                    alert("error parsing");
                }

                $("#myModal").modal();
            });
        }
        // if not comparing, save the food id
        else {
            comp1 = foodId;
        }
    });
}

// indicates if comparing
var compare = false;

// first element to compare
var comp1;

// compare button click
$("#cmpBtn").click( function(){

    // if no previous element, show
    if(!compare){
        $("#cmpBtn")
            .html("Select a second item")
            .removeClass("btn-danger")
            .addClass("btn-success");

        // update indicator
        compare = true;
    }
});


