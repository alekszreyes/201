DROP DATABASE IF EXISTS YouAreWhatYouEatUsers;
CREATE DATABASE YouAreWhatYouEatUsers;
USE YouAreWhatYouEatUsers;
CREATE TABLE Users(
	userID INT(255) PRIMARY KEY UNIQUE AUTO_INCREMENT,
	userName VARCHAR(30) UNIQUE,
    userpassword VARCHAR(100),
    profilePic VARCHAR(2083), 
    userEmail VARCHAR(30) UNIQUE
);

CREATE TABLE Diet(
	userID INT(255),
    foodID INT(255),
    dietID INT(255),
    FOREIGN KEY fk1 (userID) REFERENCES Users(userID)
    -- FOREIGN KEY fk2 (foodID) REFERENCES 
);

CREATE TABLE FollowRelation(
	from_ INT(255),
    target INT(255),
    FOREIGN KEY fk3 (from_) REFERENCES Users(userID),
    FOREIGN KEY fk4 (target) REFERENCES Users(userID)
);