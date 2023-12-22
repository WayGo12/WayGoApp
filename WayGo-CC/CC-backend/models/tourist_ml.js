'use strict';
const {
  Model
} = require('sequelize');
module.exports = (sequelize, DataTypes) => {
  class tourist_ML extends Model {
    /**
     * Helper method for defining associations.
     * This method is not a part of Sequelize lifecycle.
     * The `models/index` file will call this method automatically.
     */
    static associate(models) {
      // define association here
    }
  }
  tourist_ML.init({
    place_id: DataTypes.INTEGER,
    place_name: DataTypes.STRING(75),
    place_location: DataTypes.STRING(75),
    place_region: DataTypes.STRING(75),
    place_price: DataTypes.BIGINT,
    place_rating: DataTypes.FLOAT,
    place_opening_hours: DataTypes.STRING(75),
    latitude: DataTypes.FLOAT,
    longtitude: DataTypes.FLOAT(75),
    place_category: DataTypes.STRING(75),
    locate_url: DataTypes.STRING(75)
  }, {
    sequelize,
    modelName: 'tourist_ML',
  });
  return tourist_ML;
};