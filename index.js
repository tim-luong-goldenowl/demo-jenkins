const express = require('express')
const app = express()
const port = 3000

app.get('/', (req, res) => {
  res.send('Hello Worldddd master ok! multiple-branch-pipeline-2')
})

app.listen(port, () => {
  console.log(`Example app listening on portttttt ${port}`)
})