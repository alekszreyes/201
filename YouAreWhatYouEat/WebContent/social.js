$(function() {
    // get suggestions
    $.post("UserManager", {
        type: "Suggestions",
        number: 2
    })
    .done(function(d) {
        alert(d);
    });

    // get meals
    $.post("UserManager", {
        type: "suggestedMeals",
        number: 2
    })
    .done(function(d) {
        alert(d);
    });

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
});
~
