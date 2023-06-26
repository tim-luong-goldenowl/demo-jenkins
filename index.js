const express = require('express')
const bodyParser = require('body-parser')
const app = express()
const port = 3000
const db = require('./queries')
app.use(bodyParser.json())
app.use(
  bodyParser.urlencoded({
    extended: true,
  })
)
app.set('view engine', 'ejs')


app.get('/', (req, res) => {
<<<<<<< Updated upstream
  res.send('Hello Worldddd master ok!')
=======
  res.render('index')
>>>>>>> Stashed changes
})

app.get('/home', (req, res) => {
  console.log('Someone is accessing the home page')
  res.render('home')
})

app.get('/about', (req, res) => {
  console.log('Someone is accessing the about page')
  res.render('about')
})

app.get('/users/new', (req, res) => {
  res.render('userNew')
})

app.get('/users', db.getUsers)

app.post('/users/new', db.createUser)


app.listen(port, () => {
  console.log(`Example app listening on port ${port}`)
})