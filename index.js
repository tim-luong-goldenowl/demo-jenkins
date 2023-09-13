require('dotenv').config();
const express = require('express')
const bodyParser = require('body-parser')
const app = express()
const port = 3000

app.use(bodyParser.json())
app.use(
  bodyParser.urlencoded({
    extended: true,
  })
)
app.set('view engine', 'ejs')

app.get('/', (req, res) => {
  console.log('Someone is accessing the home pageeee')
  res.render('home')
})

app.get('/about', (req, res) => {
  console.log('Someone is accessing the about page')
  res.render('about')
})

app.get('/users/new', (req, res) => {
  res.render('userNew')
})


app.listen(port, () => {
  console.log(`Example appppp listening on port ${port}`)
})