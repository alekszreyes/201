DELETE FROM nutrition.DietUser WHERE dietID = 10 OR dietID = 11 OR dietID = 12;
DELETE FROM nutrition.DietFood WHERE dietID = 10 OR dietID = 11 OR dietID = 12;

INSERT INTO nutrition.DietUser (userID, dietID, creationTime, access) VALUES
    (1, 10, '2018-11-06', 1),
    (1, 11, '2018-11-12', 1),
    (1, 12, '2018-11-11', 1);
    

INSERT INTO nutrition.DietFood (dietID, dietName, foodID) VALUES
    (10, "Very Unhealthy", "21107"),
    (10, "Very Unhealthy", "25013"),
    (10, "Very Unhealthy", "21238"),
    (11, "Healthy Diet", "01123"),
    (11, "Healthy Diet", "06377"),
    (11, "Healthy Diet", "15011"),
    (12, "More Eggs", "01123"),
    (12, "More Eggs", "01123"),
    (12, "More Eggs", "06377"),
    (12, "More Eggs", "15011");
    