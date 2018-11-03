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



## SearchEngine

#### Search

- type: "search"
- q: ""

##### Response (JSON array)

- foodID:
- foodName:

e.g. 

```javascript
{
    [foodId: 1, foodName: "apple"], 
    [foodId: 2, foodName: "appricot"]
}
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
    foodIds: [2, 4]
}
```

##### Response

```javascript
{
    [
        foodId: 2,
        name: "Apple",
        calories: 500,
		protein: "", 
        vitamin : "", 
        sugar: ""
    ],[
		foodId: 2,
        name: "Apple",
        calories: 500,
		protein: "", 
        vitamin : "", 
        sugar: ""
    ]
}
```



## SummaryEngine

### Requests

## USDA Food Database

Instructions: In MySQLWorkbench, click on Server --> Data Import, and import the file nutrition_sr28.sql. After this process is completed, a database called "nutrition" will be created. See the script example_query_usda.sql for an example on how to query all the nutritional information for the search "smoothie". 

