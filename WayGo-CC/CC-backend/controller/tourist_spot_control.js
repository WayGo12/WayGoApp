const { Op } = require('sequelize');
const { TouristSpot } = require('../models');
const { authenticateToken, isAdmin } = require('../middelware/auth');

// Get all tourist spots endpoint
async function getAllTouristSpots(req, res) {
  try {
    // Check if the user is authenticated
    if (!req.user) {
      return res.status(401).json({ error: 'Unauthorized: User not authenticated' });
    }

    const allTouristSpots = await TouristSpot.findAll();
    res.status(200).json({ allTouristSpots });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
}

// Find tourist spot by name endpoint
async function findTouristSpotByName(req, res) {
  const spotName = req.params.name;

  try {
    // Check if the user is authenticated
    if (!req.user) {
      return res.status(401).json({ error: 'Unauthorized: User not authenticated' });
    }

    const touristSpot = await TouristSpot.findOne({
      where: {
        name: {
          [Op.like]: `%${spotName}%`, // Case-insensitive search
        },
      },
    });

    if (!touristSpot) {
      return res.status(404).json({ error: 'Tourist spot not found' });
    }

    res.status(200).json({ message: `Find Tourist Spot by Name Success`, touristSpot });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
}

// Find tourist spots by category endpoint
async function findTouristSpotsByCategory(req, res) {
  const spotCategory = req.params.category;

  try {
    // Check if the user is authenticated
    if (!req.user) {
      return res.status(401).json({ error: 'Unauthorized: User not authenticated' });
    }

    const touristSpots = await TouristSpot.findAll({
      where: {
        category: {
          [Op.like]: `%${spotCategory}%`, // Case-insensitive search
        },
      },
    });

    if (!touristSpots || touristSpots.length === 0) {
      return res.status(404).json({ error: 'Tourist spots not found for the category' });
    }

    res.status(200).json({ message: `Find Tourist Spots by Category Success`, touristSpots });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
}

async function getTouristSpotDetail(req, res) {
    const spotId = req.params.id;
  
    try {
      // Check if the user is authenticated
      if (!req.user) {
        return res.status(401).json({ error: 'Unauthorized: User not authenticated' });
      }
  
      const touristSpot = await TouristSpot.findByPk(spotId);
  
      if (!touristSpot) {
        return res.status(404).json({ error: 'Tourist spot not found' });
      }
  
      res.status(200).json({ message: `Get Detail for Tourist Spot ID ${spotId} Success`, touristSpot });
    } catch (error) {
      console.error(error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  }
  async function addTouristSpot(req, res) {
    const { id, name, image_url, location, coordinat, opening_hours, estimate_price, rating, region, category, locate_url } = req.body;
  
    try {
      // Check if the user is authenticated and is an admin
      if (!req.user || req.user.role !== 'admin') {
        return res.status(401).json({ error: 'Unauthorized: Admin access required' });
      }
  
      // Create a new tourist spot
      const newTouristSpot = await TouristSpot.create({
        id,
        name,
        image_url,
        location,
        coordinat,
        opening_hours,
        estimate_price,
        rating,
        region,
        category,
        locate_url
        // Add other fields as needed
      });
  
      res.status(201).json({
        message: `Create Tourist Spot ${name} Success`,
        touristSpot: newTouristSpot,
      });
    } catch (error) {
      console.error(error);
      res.status(500).json({ error: 'Internal Server Error' });
    }
  }

module.exports = {
  getAllTouristSpots,
  getTouristSpotDetail,
  findTouristSpotByName,
  findTouristSpotsByCategory,
  addTouristSpot
};
