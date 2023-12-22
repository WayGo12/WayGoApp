'use strict';
const {
  Model
} = require('sequelize');
module.exports = (sequelize, DataTypes) => {
  class tourist_spot extends Model {
    /**
     * Helper method for defining associations.
     * This method is not a part of Sequelize lifecycle.
     * The `models/index` file will call this method automatically.
     */
    static associate(models) {
      // define association here
    }
  }
  tourist_spot.init({
    id: {
      type: DataTypes.STRING(10), 
      primaryKey:true
    },
    name: DataTypes.STRING(75),
    image_url: DataTypes.STRING(255),
    location: DataTypes.STRING(75),
    coordinat: DataTypes.STRING(75),
    opening_hours: DataTypes.STRING(255),
    estimate_price: DataTypes.STRING(255),
    rating: DataTypes.FLOAT,
    region: DataTypes.STRING(75),
    category: DataTypes.STRING(75),
    locate_url: DataTypes.STRING(255)
  }, {
    sequelize,
    modelName: 'TouristSpot',
    tableName: 'tourist_spots'
  });
  return tourist_spot;
};