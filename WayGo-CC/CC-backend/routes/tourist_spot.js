const express = require('express');
const router = express.Router();
const touristSpotControllers = require('../controller/tourist_spot_control');
const { authenticateToken, isAdmin } = require('../middelware/auth');

// GET all tourist spots
router.get(
  '/',
  authenticateToken,
  touristSpotControllers.getAllTouristSpots,
);

router.get(
    '/detail/:id',
    authenticateToken,
    touristSpotControllers.getTouristSpotDetail,
  );
// GET tourist spot by name
router.get(
  '/name/:name',
  authenticateToken,
  touristSpotControllers.findTouristSpotByName,
);

// GET tourist spots by category
router.get(
  '/category/:category',
  authenticateToken,
  touristSpotControllers.findTouristSpotsByCategory,
);

router.post(
    '/',
    authenticateToken,
    isAdmin,
    touristSpotControllers.addTouristSpot,
  );


module.exports = router;
