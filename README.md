# Front-End to Back-End

Every request is a POST. You can see the data by going [here](food.hiddetek.com). As I complete each call, I will include a version in that site. Instead of submitting it, it will print the data submitted in an alert box.

## UserManager

#### Register 

**Status**: Front end completed and tested.

This is the first integration we should make. The challenge here, besides connecting the first bridge, will be to receive and store the image. [Here is something that might help](https://stackoverflow.com/questions/4006520/using-html5-file-uploads-with-ajax-and-jquery), at least pseudocode. 

I am including the name of the original file in case you need to extract the extension (png vs jpg, etc).

##### Args

- type: "register"
- firstName: "some text"
- lastName: "some text"
- email: "some text"
- picture: [bynary] OR "undefined"
- pictureName: "something.jpg" OR "something.png" 
- password: "some text"

```javascript
{
    type: "register",
    firstName: "Alex",
	lastName: "",
	email: "",
	picture: "",
    pictureName: "something.jpg"
	password: ""
}
```

##### Response (JSON object)

I built some tests that will let you know what answer are you sending to the front end and whether it conforms to this protocol. If successful, it will advance to the page <code>loggedIn.html</code>.

- type: 
  - "success": all is well
  - "exists": an account exists with this email
  - "invalid": parameters missing or bad request
- message: "e.g. A message to display to the user if <exists> or <invalid>"

##### Example

if the user has already exists,

```javascript
{
    type: "exist",
    message: "Account already exists with this email!"
}
```

if successfully created an account, 

```javascript
{
    type: "success",
    message: "3Successfully registered!"
}
```

if the user does not fill in all of the information

```javascript
{
    type: "invalid",
    message: "Error! Some information are missing!"
}
```



#### Login 

- type: "login"
- email: ""
- password: ""

##### Response (JSON object)

- type: 
  - "success": all is well
  - "notexists": doesn't exist
  - "invalid": parameters missing or bad request
- message: 
  - if it fails, "e.g. A message to display to the user <nonexists> or <invalid>"
  - if successful, userID

##### Example

If the user trying to log in does not exist in the database,

```javascript
{
    type: "notexist",
    message: "The specified the user does not exist! Please check your email and password again!"
}
```

if the user successfully logged in,

```javascript
{
    type: "success",
    message: "Successfully logged in!"
}
```

if the user does not fill all of the information

```javascript
{
    type: "invalid",
    message: "Error! Some information are missing!"
}
```


#### Logout

Front end sends

```javascript
{
    type: "logout"
}
```

Back end response

```javascript
{
    type: "success"
}
```

The response exists so that FE knows that BE acknowledged request as opposed as the connection broke or the like.

#### Suggestions (People)

This call should get people that the current user is not currently following. 

The field *number* in the request determines how many users return. 

The response field *likes* returns a few item foods that the suggested person has included in her/his diet.

##### Request

```javascript
{
    type: "Suggestions",
    number: 1
}
```

##### Response

```javascript
{
    userId: 3,
    name: "Veronica",
    meals: "Vegan Meal, Meat diet",
    likes: "Smoothies, apples"
}
```

#### Suggested Meals

Pull friends' meals that are public or have been suggested to this user. 

*createdBy* returns the name of the creator of  the meal.

##### Request

```javascript
{
    type: "suggestedMeals"
}
```

##### Response

```javascript
{
    mealId: 3,
    foodItems: "Apple, Orange, Lettuce",
    createdBy: "Taylor Swift"
}
```

#### Followers

Return an array of the followers of this user

##### Request

```javascript
{
    type: "followers"
}
```

##### Response

```javascript
[{
	userId: 3,
    name: "Taylor Swift",
    picture "taylor.jpg"
},{
	userId: 4,
    name: "Mickey Mouse",
    picture "mouse.jpg"
}]
```

#### Tangle (add or remove) Follower Relation

Add or remove a follow relation between two users

Given two users user1 and user2, if user1 is following user2, delete the follow relation. 
Otherwise let user1 follow user2.

##### Request

```javascript
{
    type: "FollowRelation",
    userId1: 1,
    userId2: 2
}
```

##### Response

If the backend added their relation

```javascript
{
    message: "relation added"
}
```

if the backend deleted their relation

```javascript
{
    message: "relation deleted"
}
```


## SearchEngine

#### Search

- type: "search"
- q: ""

```javascript
{
    type: "search",
    q: "a"
}
```

##### Response (JSON array)

The backend should return all the food items that begin with the string passed in q. Spaces are considered OR requests. Empty q should return all food items (maybe just the first one hundred). Notice this is a  **JSON array** so the syntax is a bit different (but not too much).

- foodID:
- foodName:

e.g. 

```javascript
[
    {
        "foodId":"2", 
        "foodName":"apple"
    },
    {
        "foodId":"3", 
        "foodName":"orange"
    }
]
```

#### Food Info

- type: "getFood"
- foodId: "3"

##### Response

```javascript
{
    foodId: 3,
    foodName: "Apple",
    calories: "500",
    protein: "",
    vitamin : "",
    sugar: ""
}
```

#### Compare

##### Request

```javascript
{
    type: "compare",
    food1: "food1Id",
    food2: "food2Id"
}
```

##### Response

```javascript

[
    {
        "foodId": 1005,
        "foodName": "Cheese, brick",
        "calories": "371.000 kcal",
        "protein": "23.240 g",
        "sugar": "0.510 g"
    },
    {
        "foodId": 1006,
        "foodName": "Cheese, brie",
        "calories": "334.000 kcal",
        "protein": "20.750 g",
        "sugar": "0.450 g"
    }
]

```

### Create a meal

#### Request

This happens when a user has combined food items and wants to save this into a meal that is presumably consuming.

```javascript
{
    type: "saveMeal"
    name: "Veggie Meal",
    content: [3,2,10,19]
}
```

#### Response

This should only be done by a logged in user. If the request is sent from an account that is not logged, BE notifies so FE can redirect user to the correct page.

if comes from a logged user, make sure you are tracking the calories that the user is consuming in this meal. Also return the following.

Include the mealId so that FE knows how to id it for future requests.

```javascript
{ 
    type: "success",
    mealId: "4"
}
```

### Delete Meal

When deleting a meal, ensure that no other user has a relationship with the same mealId. 

If no other user has a relationship to the same mealId, you can delete the relationship between the mealId and the foodId rows along with the relationship between the mealId and userId. Otherwise, delete only the relationship between the mealId and the userId.

#### Request 

```javascript
{
    type: "deleteMeal",
    mealId: "mealId"
}
```

#### Response

```javascript
{
    type: "success"
}
```

### Make a meal public

Meals are private by default. Upon a call of this call, a meal should be declared public.

#### Request 

```javascript
{
    type: "shareMeal",
    mealId: "mealId"
}
```

#### Response

```javascript
{
    type: "success"
}
```


## SummaryEngine

### Get the consumption of the calories in the past two weeks

Frontend requests for summary of the amount of calories in the past two weeks.

### Request
```javascript
{
    type: "getData"
}
```

### Response
Passing amount of calories consumed in the last 14 days to the frontend
Note: all arrays in the response object have length 7
```javascript
{
    axis: ["Sunday", ...],
    week1: [45, ...],
    week2: [23, ...]
}
```
## Web Socket and Multi-Threading   

Use the basic way to initialize the web socket in any of the front-end pages, for ex.
```javascript
function ConnectToServer() {
	socket = new WebSocket("ws://localhost:8080/YouAreWhatYouEat/ss");
	// Set up the four basic web-socket functions here
}
```
**Note:** Currently it is not clear whether the session storage is shared between a regular servlet and a web socket servlet. If it is possible, there is no need to pass in an id parameter from the front end. Otherwise we'll have to pass an id parameter to make the socket aware of which user the current session belongs to. The web-socket utilize a path parameter to capture the id, so bascally it's calling a sub-routine in addition to the websocket server.
*ex.*
```javascript
	socket = new WebSocket("ws://localhost:8080/YouAreWhatYouEat/ss/" + id);
```


## USDA Food Database

Instructions: In MySQLWorkbench, click on Server --> Data Import, and import the file nutrition_sr28.sql. After this process is completed, a database called "nutrition" will be created. See the script example_query_usda.sql for an example on how to query all the nutritional information for the search "smoothie". 

