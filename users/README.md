## How to run

Run sbt in the user directory
Use run in sbt to compile and start the server
Use included Postman collection to interact with server

## Postman collection
NOTE: Please create and select an environment with a userUuid variable, to enable automatic User.Id population
Includes basic HTTP calls for all endpoints
Whenever a new user is created, their User.Id is automatically used in all other calls.

## Endpoints
POST /user
Create a user
Body:
	{
		"username": string,
		"emailAddress": string,
		"password": optional, string
	}
	
POST /login
Login using username and password
Body:
	{
		"username": string,
		"password": string
	}
	
GET /user/:userUuid
Return a single user by userUuid

GET /user
Return all users

PATCH /user/:userUuid/email
Change a user's email address
Body:
	{
		"username": string,
		"emailAddress": string,
		"password": optional, string
	}

PATCH /user/:userUuid/password
Change a user's password
Body:
	{
		"username": string,
		"emailAddress": string,
		"password": optional, string
	}

PATCH /user/:userUuid/resetPassword
Remove a user's password

PATCH /user/:userUuid/block
Set a user to "blocked" status

PATCH /user/:userUuid/unblock
Set a user to "active" status

DELETE /user/:userUuid
Delete a user by userUuid
