const express = require('express');
const router = express.Router();
const userControllers = require('../controller/user_control'); // Correct the file path
const {authenticateToken, authenticateRefreshToken, checkBlacklist, isAdmin, isUserOwner,} = require('../middelware/auth');

// GET all users
router.get(
  '/',
  authenticateToken,
  authenticateRefreshToken,
  checkBlacklist,
  userControllers.getAllUsers,
);

// GET user detail
router.get(
  '/:id',
  authenticateToken,
  authenticateRefreshToken,
  isUserOwner,
  checkBlacklist,
  userControllers.getUserDetail,
);

// POST user sign in
router.post('/login', userControllers.login);

// POST user sign out
router.post(
  '/logout',
  authenticateToken,
  authenticateRefreshToken,
  checkBlacklist,
  userControllers.logout,
);

// POST user sign up
router.post('/register', userControllers.register);

// PUT update user
router.put(
  '/:id',
  authenticateToken,
  authenticateRefreshToken,
  isUserOwner,
  checkBlacklist,
  userControllers.updateUser,
);

// DELETE delete user
router.delete(
  '/:id',
  authenticateToken,
  authenticateRefreshToken,
  isAdmin,
  checkBlacklist,
  userControllers.deleteUser,
);

module.exports = router;
