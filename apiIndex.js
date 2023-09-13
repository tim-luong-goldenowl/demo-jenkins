require('dotenv').config();
const express = require('express')
const app = express()
const port = 3000
const db = require('./queries')

app.get('/users', async (req, res) => {
  const users = await db.getUsers();
  res.json(users);
})

app.listen(port, () => {
  console.log(`Example app listening on port ${port}`)
})