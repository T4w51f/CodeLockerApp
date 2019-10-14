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
  const { user_id } = request.body

  pool.query('SELECT * FROM app_user WHERE user_id = $1', [user_id], (error, results) => {
    if (error) {
      throw error
    }
    response.status(200).json(results.rows)
  })
}

app.route('/get_users').get(getUser)

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
    response.status(202).json(results.rows)
  })
}
app.route('/user_count').get(getUserCount)

//Get password 
const getPassword = (request, response) => {
  const { username } = request.body

  pool.query('SELECT password FROM app_user WHERE username = $1', (error, results) => {
    if (error) {
      throw error
    }
    response.status(203).json(results.rows)
  })
}
app.route('/password').get(getPassword)

//Check if username exists
const getUsernameOccurence = (request, response) => {
  const { username } = request.body
  pool.query('SELECT COUNT(username) FROM app_user WHERE username = $1', [username], (error, results) => {
    if (error) {
      throw error
    }
    response.status(204).json(results.rows)
  })
}
app.route('/username_occurence').get(getUsernameOccurence)

//Check if email exists
const getEmailOccurence = (request, response) => {
  const { email } = request.body
  pool.query('SELECT COUNT(email) FROM app_user WHERE email = $1', [email], (error, results) => {
    if (error) {
      throw error
    }
    response.status(205).json(results.rows)
  })
}
app.route('/email_occurence').get(getEmailOccurence)

// app
//   .route('/users')
//   // GET endpoint
//   .get(getUser)
//   // POST endpoint
//   .post(addUser)

// Start server
app.listen(process.env.PORT || 3002, () => {
  console.log(`Server listening`)
})