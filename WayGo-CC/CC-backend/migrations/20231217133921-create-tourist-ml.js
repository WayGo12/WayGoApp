'use strict';
/** @type {import('sequelize-cli').Migration} */
module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('tourist_ML', {
      place_id: {
        allowNull: false,
        autoIncrement: true,
        primaryKey: true,
        type: Sequelize.INTEGER
      },
      place_name: {
        type: Sequelize.STRING(75)
      },
      place_location: {
        type: Sequelize.STRING(75)
      },
      place_region: {
        type: Sequelize.STRING(75)
      },
      place_price: {
        type: Sequelize.BIGINT
      },
      place_rating: {
        type: Sequelize.FLOAT
      },
      place_opening_hours: {
        type: Sequelize.STRING(75)
      },
      latitude: {
        type: Sequelize.FLOAT
      },
      longtitude: {
        type: Sequelize.FLOAT
      },
      place_category: {
        type: Sequelize.STRING(75)
      },
      locate_url: {
        type: Sequelize.STRING(75)
      }
    });
  },
  async down(queryInterface, Sequelize) {
    await queryInterface.dropTable('tourist_ML');
  }
};