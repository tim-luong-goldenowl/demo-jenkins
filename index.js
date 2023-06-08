const express = require('express')
const app = express()
const port = 3000

app.get('/', (req, res) => {
  res.send('Hello World from multiple-branch-pipeline')
})

app.listen(port, () => {
  console.log(`Example app listening on portttttt ${port}`)
})