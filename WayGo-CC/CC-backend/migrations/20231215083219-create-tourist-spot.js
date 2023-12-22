'use strict';
/** @type {import('sequelize-cli').Migration} */
module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('tourist_spots', {
      id: {
        allowNull: true,
        primaryKey: true,
        type: Sequelize.STRING(75)
      },
      name: {
        type: Sequelize.STRING(75),
        allowNull: false
      },
      image_url: {
        type: Sequelize.STRING(255),
        allowNull: false
      },
      location: {
        type: Sequelize.STRING(75),
        allowNull: false
      },
      coordinat: {
        type: Sequelize.STRING(75),
        allowNull: false
      },
      opening_hours: {
        type: Sequelize.STRING(255),
        allowNull: false
      },
      estimate_price: {
        type: Sequelize.STRING(255),
        allowNull: false
      },
      rating: {
        type: Sequelize.FLOAT,
        allowNull: false
      },
      region: {
        type: Sequelize.STRING(75),
        allowNull: false
      },
      category: {
        type: Sequelize.STRING(75),
        allowNull: false
      },
      locate_url: {
        type: Sequelize.STRING(255),
        allowNull: false
      },
      createdAt: {
        allowNull: false,
        type: Sequelize.DATE
      },
      updatedAt: {
        allowNull: false,
        type: Sequelize.DATE
      }
    });
  },
  async down(queryInterface, Sequelize) {
    await queryInterface.dropTable('tourist_spots');
  }
};