var s = "";
$(function() {
    // get suggestions
    $.post("UserManager", {
        type: "Suggestions",
        number: 2
    })
    .done(function(d) {
        try {
            var json = JSON.parse(d);
            s = json;
            $.each(json, function(i, user){
          
                let str = '<tr id="r' + user.userId 
                    +'"><td><img src="' + user.picture
                    + '" /></td>'
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
            $.post("UserManager", {
                type: "FollowRelation",
                userId: userId
            })
            .done(function() {
                $("#r" + userId).remove();
            });
        });
    });

    // get meals
    $.post("UserManager", {
        type: "suggestedMeals",
        number: 2
    })
    .done(function(d) {
        try {
            var json = JSON.parse(d);
            s = json;
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
            
            $.post("UserManager", {
                type: "tryMeal",
                mealId: mealId
            })
            .done(function() {
                $("#try" + mealId).remove();
            });
        });
        alert(d);
    });

    /**
    // get friends
    $.post("UserManager", {
        type: "followers",
        number: 2
    })
    .done(function(d) {
        alert(d);
    });
    // toggle friends
    $.post("UserManager", {
        type: "followRelation",
        number: 2
    })
    .done(function(d) {
        alert(d);
    });
    **/
});

