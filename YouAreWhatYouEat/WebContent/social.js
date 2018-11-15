var s = "";
$(function() {

    $("#suggestion_msg").hide();
    $("#meals_msg").hide();
    $("#followers_msg").hide();

    // get suggestions
    $.ajax("UserManager", {
        type: 'POST',
        data: {
            type: "Suggestions",
            number: 2
        },
        async: false
    })
    .done(function(d) {
        try {
            var json = JSON.parse(d);
            s = json;
            if(json.length == 0) { $("#suggestion_msg").show(); }
            else { $("#suggesion_msg").hide(); }
            $.each(json, function(i, user){
          
                let str = '<tr id="r' + user.userId 
                    +'"><td><img src="' + user.picture
                    + '" class="prof_sugg" height="120" /></td>'
                    + '<td><b>' + user.name + '</b></td>'
                    + '<td>' + user.meals + '</td>'
                    + '<td><b>Likes</b><br>' + user.likes + '</td>'
                    + '<td><button class="btn btn-default follow" k="' 
                    + user.userId 
                    + '">Follow</button></td>';

                $(str).prependTo("#suggestionsTable > tbody");
            });
        }catch(d) {
            alert("error parsing");
        }

        $(".follow").click(function(){
            let userId = $(this).attr("k");
            $.ajax("UserManager", {
                type: 'POST', 
                data : {
                    type: "FollowRelation",
                    userId: userId
                },
                async: false
            })
            .done(function() {
                $("#r" + userId).remove();
            });
        });
    });

    // get meals
    $.ajax("UserManager", {
        type: 'POST',
        data: {
            type: "suggestedMeals",
            number: 2
        },
        async: false
    })
    .done(function(d) {
        try {
            var json = JSON.parse(d);
            s = json;
            if(json.length == 0) { $("#meals_msg").show(); }
            else { $("#meals_msg").hide(); }
            $.each(json, function(i, meal){
          
                let str = '<tr id="try' + meal.mealId 
                    +'">'
                    + '<td><b></b><br>'
                    + '<td><b>' + meal.mealName + '</b><br>'
                    + meal.foodItems + '</td>'
                    + '<td>By ' + meal.createdBy + '</td>'
                    + '<td><button class="btn btn-danger try" mealId="' 
                    + meal.mealId 
                    + '">Try</button></td>';

                $(str).prependTo("#mealsTable > tbody");
            });
        }catch(d) {
            alert("error parsing");
        }

        $(".try").click(function(){

            let mealId = $(this).attr("mealId");
            
            $.ajax("UserManager", {
                type: 'POST',
                data: {
                    type: "tryMeal",
                    mealId: mealId
                },
                async: false
            })
            .done(function() {
                $("#try" + mealId).remove();
            });
        });
        console.log(d);
    });

    // get followers
    $.ajax("UserManager", {
        type: 'post',
        data: {
            type: "followers",
            number: 2
        },
        async: false
    })
    .done(function(d) {
        var json = JSON.parse(d);
        if(json.length == 0) { $("#followers_msg").show(); }
        else { $("#followers_msg").hide(); }
        let str = '';

        $.each(json, function(i, user) {
            str += '<tr><td><img src="' + user.picture + '" height="120" class="prof_sugg" /></td>';
            str += '<td>' + user.name + '</td>';
            str += '<td><button class="btn btn-danger suggest" userId="' + 
                user.userId + '">Suggest a meal</button></td>';
            str += '</tr>';
        });

        $("#followersTable tr").remove;
        $(str).prependTo("#followersTable > tbody");

        $(".suggest").click(function() {

            let followerId = $(this).attr("userId");

            // display modal
            $.ajax("SearchEngine", {
                type: 'post',
                data: {
                    type: 'getMeals'
                },
                async: false
            })
            // display meals
            .done(function(e) {
                // display meals and modal
                let json = JSON.parse(e);
                let str = '';

                $.each(json, function(i, meal) {
                    str += '<tr><td><b>' + meal.name + '</td>'
                        + '<td>' + meal.foodItems + '</td>'
                        + '<td><button class="btn btn-default suggestMeal" '
                        + 'mealId="' + meal.mealId + '" '
                        + 'followerId="' + followerId + '">Suggest'
                        + '</button></td></tr>';
                });

                $("#mealSuggestionsTable tr").remove();
                $(str).prependTo("#mealSuggestionsTable > tbody");

                $(".suggestMeal").click(function(){
                    let followerId = $(this).attr("followerId");
                    let mealId = $(this).attr("mealId");

                    $.ajax("SearchEngine", {
                        type: 'post',
                        data: {
                            type: "suggestMealToFollower",
                            followerId: followerId,
                            mealId: mealId
                        },
                        async: false
                    })
                    .done(function(d) {
                        console.log("followerId: " + followerId);
                        console.log("mealId: " + mealId);
                        console.log(d);
                        $("#myModal").modal("hide");
                    });
                });

                // show modal
                $("#myModal").modal();
            });
        });
    });
});

