const express = require('express')
const bodyParser = require('body-parser')
const cors = require('cors')
const { pool } = require('./config')

const app = express()

app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))
app.use(cors())


//Get user information with user_id
const getUser = (request, response) => {
  pool.query('SELECT * FROM app_user WHERE user_id = $1', [request.params.user_id], (error, results) => {
    if (error) {
      throw error
    }
    response.status(200).json(results.rows)
  })
}

app.get('/get_users/:user_id',getUser)

//Create user data
const addUser= (request, response) => {
  const { id, name, email, password, created_at, updated_at, username, user_id } = request.body

  pool.query('INSERT INTO app_user(id, name, email, password, created_at, updated_at, username, user_id) VALUES ($1, $2, $3, $4, $5, $6, $7, $8)', [id, name, email, password, created_at, updated_at, username, user_id], error => {
    if (error) {
      throw error
    }
    response.status(201).json({ status: 'success', message: 'User added' })
  })
}
app.route('/create_users').post(addUser)

//Get number of users currently
const getUserCount = (request, response) => {
  pool.query('SELECT COUNT(id) FROM app_user', (error, results) => {
    if (error) {
      throw error
    }
    response.status(200).json(results.rows[0])
  })
}
app.route('/user_count').get(getUserCount)

//Get password 
const getPassword = (request, response) => {
  const { username } = request.body

  pool.query('SELECT COUNT(*) FROM app_user WHERE username = $1 AND password = $2', [request.params.un, request.params.pw], (error, results) => {
    if (error) {
      throw error
    }
    response.status(200).json(results.rows[0])
  })
}
app.get('/password/:un/:pw',getPassword)

//Check if username exists
//UPTODATE AND WORKING EXAMPLE FOR PARAMS
const getUsernameOccurrence = (request, response) => {
  pool.query('SELECT COUNT(username) FROM app_user WHERE username = $1', [request.params.un], (error, results) => {
    if (error) {
      throw error
    }
    response.status(200).json(results.rows[0])
  })
}
app.get('/username_occurrence/:un', getUsernameOccurrence)

//Check if email exists
const getEmailOccurrence = (request, response) => {
  pool.query('SELECT COUNT(email) FROM app_user WHERE email = $1', [request.params.email], (error, results) => {
    if (error) {
      throw error
    }
    response.status(200).json(results.rows[0])
  })
}
app.get('/email_occurrence/:email', getEmailOccurrence)

// Start server
app.listen(process.env.PORT || 3002, () => {
  console.log(`Server listening`)
})