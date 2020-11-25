'use strict'

const mongoose = require('mongoose')
const Schema = mongoose.Schema

const APIModel = Schema({
  name: String,
  description: String,
  params: { type: Array, items: { type: 'object', properties: { tipo: String, nombre: String, descripcion: String }, default: [] } },
  url: { type: String, required: true },
  protocol: { type: String, required: true },
  values: { type: Array, items: { type: 'object', properties: { nombre: String, ruta: String, tipo: String }, required: true } },
  type: String,
  imageUrl: String,
  auth: { type: Array, items: { type: 'object', properties: { name: String, value: String }, default: [] } },
  acessKind: String,
  rate: String

})


module.exports = mongoose.model('API', APIModel)
