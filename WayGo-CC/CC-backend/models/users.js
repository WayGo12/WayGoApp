'use strict';
const {
  Model
} = require('sequelize');
module.exports = (sequelize, DataTypes) => {
  class users extends Model {
    /**
     * Helper method for defining associations.
     * This method is not a part of Sequelize lifecycle.
     * The `models/index` file will call this method automatically.
     */
    static associate(models) {
      // define association here
    }
  }
  users.init({
    name: DataTypes.STRING(75),
    email: DataTypes.STRING(75),
    password: DataTypes.STRING(255),
    role: DataTypes.STRING(75)
  }, {
    sequelize,
    modelName: 'users',
  });
  return users;
};