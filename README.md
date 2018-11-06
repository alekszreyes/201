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

if fails,

```javascript
{
    type: "exist",
    message: "Account already exists with this email."
}
```

if successful,

```javascript
{
    type: "success",
    message: "3"
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

If fails,

```javascript
{
    type: "notexist",
    message: "Account already exists."
}
```

if successful,

```javascript
{
    type: "success",
    message: "3"
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
    {"foodId":"2", "foodName":"apple"},
    {"foodId":"3", "foodName":"orange"}
]
```

#### Food Info

- type: "getFood"
- foodId: "3"

##### Response

```javascript
{
    foodId: 3, foodName: "Apple", 
        calories: "500", protein: "", 
        vitamin : "", sugar: ""
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
{
    [
        {
            foodId: 2,
            name: "Apple",
            calories: 500,
            protein: "", 
            vitamin : "", 
            sugar: ""
        },
        {
            foodId: 2,
            name: "Orange",
            calories: 300,
            protein: "", 
            vitamin : "", 
            sugar: ""
        }
    ]
}
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

## USDA Food Database

Instructions: In MySQLWorkbench, click on Server --> Data Import, and import the file nutrition_sr28.sql. After this process is completed, a database called "nutrition" will be created. See the script example_query_usda.sql for an example on how to query all the nutritional information for the search "smoothie". 

