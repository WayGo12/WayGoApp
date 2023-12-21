from flask import Flask, render_template, request, jsonify
import joblib
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import plotly.express as px
from keras.models import load_model
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.ensemble import RandomForestRegressor
from sklearn.preprocessing import OneHotEncoder
from zipfile import ZipFile
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers
from pathlib import Path
from geopy.distance import geodesic
from geopy.geocoders import Nominatim

app = Flask(__name__)

user_rating_data = pd.read_csv('https://raw.githubusercontent.com/WayGo12/WayGoApp/main/assets/dataset/user_rating_dataset.csv')
places_data = pd.read_csv('https://raw.githubusercontent.com/novitangrn/dataset/main/Capstone%20Dataset/places_dataset.csv')
user_data = pd.read_csv('https://raw.githubusercontent.com/WayGo12/WayGoApp/main/assets/dataset/user_id_dataset.csv')
geolocator = Nominatim(user_agent="GetLoc")
data_recommend = pd.merge(user_rating_data.groupby('Place_ID')['Place_Rating'].mean(), places_data, on='Place_ID')


model_path = 'model'
model = load_model(model_path)

def prepare_data(user_rating_data):
    data_collaborative_filtering = user_rating_data.copy()
    global num_users, num_resto, user_to_user_encoded, user_encoded_to_user, resto_to_resto_encoded, resto_encoded_to_resto
    user_ids = data_collaborative_filtering['ID_user'].unique().tolist()
    user_to_user_encoded = {x: i for i, x in enumerate(user_ids)}
    user_encoded_to_user = {i: x for i, x in enumerate(user_ids)}
    resto_ids = data_collaborative_filtering['Place_ID'].unique().tolist()
    resto_to_resto_encoded = {x: i for i, x in enumerate(resto_ids)}
    resto_encoded_to_resto = {i: x for i, x in enumerate(resto_ids)}
    data_collaborative_filtering['user'] = data_collaborative_filtering['ID_user'].map(user_to_user_encoded)
    data_collaborative_filtering['place'] = data_collaborative_filtering['Place_ID'].map(resto_to_resto_encoded)
    num_users = len(user_to_user_encoded)
    num_resto = len(resto_encoded_to_resto)
    data_collaborative_filtering['Place_Rating'] = data_collaborative_filtering['Place_Rating'].values.astype(np.float32)
    min_rating = min(data_collaborative_filtering['Place_Rating'])
    max_rating = max(data_collaborative_filtering['Place_Rating'])
    return  num_users, num_resto, user_to_user_encoded, user_encoded_to_user, resto_to_resto_encoded, resto_encoded_to_resto

def recommend_by_collaborative_filtering(model, places_data, user_rating_data, user_id):
    global resto_visited_by_user, resto_not_visited, resto_to_resto_encoded, user_to_user_encoded, user_encoder, user_resto_array, ratings, top_ratings_indices, recommended_resto_ids, recommended_resto_info
    resto_df = places_data
    df = user_rating_data
    resto_visited_by_user = df[df.ID_user == user_id]
    resto_not_visited = resto_df[~resto_df['Place_ID'].isin(resto_visited_by_user.Place_ID.values)]['Place_ID']
    resto_not_visited = list(
        set(resto_not_visited).intersection(resto_to_resto_encoded.keys())
    )
    resto_not_visited = list(map(resto_to_resto_encoded.get, resto_not_visited))

    user_encoder = user_to_user_encoded[user_id]
    user_resto_array = np.hstack(([[user_encoder]] * len(resto_not_visited), np.array(resto_not_visited)[:, None]))
    ratings = model.predict(user_resto_array).flatten()
    top_ratings_indices = ratings.argsort()[-150:][::-1]
    recommended_resto_ids = [resto_encoded_to_resto.get(resto_not_visited[index]) for index in top_ratings_indices]
    recommended_resto_info = resto_df[resto_df['Place_ID'].isin(recommended_resto_ids)][['Place_Name', 'Latitude', 'Longitude', 'Place_Category']].values.tolist()

    recommended_places = {
        'Nama Tempat': [],
        'Latitude': [],
        'Longitude': [],
        'Kategori' : []
    }

    for place_info in recommended_resto_info:
        recommended_places['Nama Tempat'].append(place_info[0])
        recommended_places['Latitude'].append(place_info[1])
        recommended_places['Longitude'].append(place_info[2])
        recommended_places['Kategori'].append(place_info[3])

    return recommended_places

def haversine_distance(coord1, coord2):
    # Calculate the distance using the Haversine formula
    return geodesic(coord1, coord2).kilometers

def find_closest_place(current_place, places_data, valid_categories):
    if current_place is None or current_place >= len(places_data['Latitude']):
        return None
    current_coord = (places_data['Latitude'][current_place], places_data['Longitude'][current_place])
    valid_places = [(i, (places_data['Latitude'][i], places_data['Longitude'][i])) for i in range(len(places_data['Nama Tempat']))
                    if places_data['Kategori'][i] in valid_categories and i != current_place]
    if not valid_places:
        return None
    closest_place, closest_distance = min(valid_places, key=lambda x: haversine_distance(current_coord, x[1]))
    return closest_place


def delete_processed_place(places_data, index):
    del places_data['Nama Tempat'][index]
    del places_data['Latitude'][index]
    del places_data['Longitude'][index]
    del places_data['Kategori'][index]

def get_lat_lng(location):
    try:
        location_info = geolocator.geocode(location)
        if location_info is not None:
            return location_info.latitude, location_info.longitude
        else:
            return None, None
    except Exception as e:
        print(f"Error getting coordinates: {e}")
        return None, None

def get_start_point(region, places_data, coordinates):
    # Check if region is in the dataset places_data
    dataset_coordinates = get_coordinates_from_dataset(region, places_data)
    if dataset_coordinates:
        return dataset_coordinates

    # Check if region is in the dictionary coordinates
    coordinates_from_region = get_coordinates_from_region(region)
    if coordinates_from_region:
        return coordinates_from_region

    # If not found in dataset or dictionary, assume it's a user-provided location
    return get_coordinates_from_input(region)

def get_coordinates_from_dataset(region, places_data):
    # Check if coordinates are available in the dataset for the provided region
    row = places_data.loc[places_data['Place_Name'] == region]
    if not row.empty:
        return {'Latitude': row['Latitude'].values[0], 'Longitude': row['Longitude'].values[0], 'Category': row['Place_Category'].values[0]}
    return None

def get_coordinates_from_region(region):
    coordinates = {
        'East Bali': {'Latitude': -8.44137911909141, 'Longitude': 115.59621367293977},
        'West Bali': {'Latitude': -8.154648635396274, 'Longitude': 114.42891625362873},
        'North Bali': {'Latitude': -8.104347479264572, 'Longitude': 115.08260280573943},
        'South Bali': {'Latitude': -8.699390567576343, 'Longitude': 115.17735989086462},
    }
    return coordinates.get(region, None)

def get_coordinates_from_input(region):
    latitude, longitude = get_lat_lng(region)
    if latitude is None or longitude is None:
        raise ValueError(f"Failed to retrieve latitude and longitude for the input place: {region}")
    return {'Latitude': latitude, 'Longitude': longitude, 'Category': 'User Input'}

def get_coordinates_for_accommodation(recommendations, start_point):
    accommodation_indices = [index for index, category in enumerate(recommendations['Kategori']) if category == 'Accommodation']
    if not accommodation_indices:
        return None
    distances = [haversine_distance((start_point['Latitude'], start_point['Longitude']),
                                    (recommendations['Latitude'][index], recommendations['Longitude'][index]))
                 for index in accommodation_indices]
    closest_accommodation_index = accommodation_indices[distances.index(min(distances))]
    closest_accommodation_details = {
        'Nama Tempat': recommendations['Nama Tempat'][closest_accommodation_index],
        'Latitude': recommendations['Latitude'][closest_accommodation_index],
        'Longitude': recommendations['Longitude'][closest_accommodation_index],
        'Kategori': recommendations['Kategori'][closest_accommodation_index],
        'Jarak Tempat': distances[0]
    }
    return closest_accommodation_details

def generate_rundown_for_user(places_dataset, recommendations, region, num_days, start_point):
    places_data = {
        'Nama Tempat': recommendations['Nama Tempat'],
        'Latitude': recommendations['Latitude'],
        'Longitude': recommendations['Longitude'],
        'Kategori': recommendations['Kategori']
    }

    def add_rundown_entry(time_range, place_index, category_filter=None):
        place = find_closest_place(place_index, places_data, category_filter)
        if place is not None:
            distance = haversine_distance(
                (places_data['Latitude'][place_index], places_data['Longitude'][place_index]),
                (places_data['Latitude'][place], places_data['Longitude'][place])
            )
            rundown.append({
                'Day': '',
                'Jam Rundown': time_range,
                'Nama Tempat': places_data['Nama Tempat'][place],
                'Kategori': places_data['Kategori'][place],
                'Jarak Tempat': distance
            })
            delete_processed_place(places_data, place_index)
            return place  # Return the index of the processed place
        else:
            # Add an entry with placeholder values if no valid place is found
            rundown.append({
                'Day': '',
                'Jam Rundown': time_range,
                'Nama Tempat': "Tidak ada rekomendasi.",
                'Kategori': "",
                'Jarak Tempat': ""
            })
            return None

    closest_accommodation_details = get_coordinates_for_accommodation(recommendations, start_point)

    if closest_accommodation_details is not None:
        first_place_distance = closest_accommodation_details.get('Jarak Tempat')
        # Handle the case where there is no 'Accommodation' found
    else:
      first_place_distance = None

    # 9 PM - 6 AM: Accommodation
    rundown = [{
        'Day': '',
        'Jam Rundown': '9 PM - 6 AM',
        'Nama Tempat': closest_accommodation_details['Nama Tempat'],
        'Kategori': closest_accommodation_details['Kategori'],
        'Jarak Tempat': first_place_distance
    }]

    # Define time ranges for activities
    global time_ranges
    time_ranges = [
        ('6 AM - 8 AM', ['Culinary']),
        ('8 AM - 10 AM', ['Nautical', 'Nature', 'History', 'Entertainment', 'Shopping', 'Art']),
        ('10 AM - 12 PM', ['Nautical', 'Nature', 'History', 'Entertainment', 'Shopping', 'Art']),
        ('12 PM - 1 PM', ['Culinary']),
        ('1 PM - 3 PM', ['Nautical', 'Nature', 'History', 'Entertainment', 'Shopping', 'Art']),
        ('3 PM - 5 PM', ['Nautical', 'Nature', 'History', 'Entertainment', 'Shopping', 'Art']),
        ('5 PM - 7 PM', ['Culinary']),
        ('7 PM - 9 PM', ['Shopping']),
    ]

    # Initialize accommodation_place to the index of the closest accommodation place based on the start point
    accommodation_name = closest_accommodation_details['Nama Tempat']
    accommodation_place = places_data['Nama Tempat'].index(accommodation_name)
    current_place_index = accommodation_place

    # Loop for num_days
    last_index = 0
    for day in range(num_days):
        # Generate rundown for each day and append it to the main rundown list
        for time_range, categories in time_ranges:
            current_place = add_rundown_entry(time_range, current_place_index, categories)
            # Update current place index
            current_place_index = find_closest_place(current_place, places_data, categories)

        # Add an entry for 9 PM - 6 AM at the end of each day
        processed_place_index = add_rundown_entry('9 PM - 6 AM', current_place_index, ['Accommodation'])
        # Update accommodation place for the next day (exclude the current accommodation place)
        current_place_index = processed_place_index

    return rundown

def predict():
    num_users, num_resto, user_to_user_encoded, user_encoded_to_user, resto_to_resto_encoded, resto_encoded_to_resto = prepare_data(user_rating_data)
    global recommendations
    recommendations = recommend_by_collaborative_filtering(model, places_data, user_rating_data, user_id)
    return recommendations

def main(recommendations):
    start_point = get_start_point(region, places_data, recommendations)
    if not start_point:
        # If no predefined region or user-provided location, get coordinates for 'Accommodation'
        start_point = recommendations['Kategori'].index('Accomodation')
    rundown = generate_rundown_for_user(places_data, recommendations, region, num_days, start_point)
    last_index = 0
    for day in range(num_days):
        len_time_range = len(time_ranges)
        increment_index = last_index + len_time_range+1
        for i in range(last_index, increment_index):
            rundown[i]['Day'] = f'Day {day+1}'
        last_index = increment_index
    return rundown

def pred(user_rating_data, model, places_data, user_id, region, num_days):
    global recommendations
    recommendations = predict()
    return recommendations

def generate_rundown(recommendations):
    rundown = generate_rundown_for_user(recommendations)
    return rundown

@app.route('/', methods=['GET', 'POST'])
def index():
    if request.method == 'POST':
        user_id = int(request.form['user_id'])
        region = request.form['region']
        num_days = int(request.form['num_days'])
        recommendations = pred(user_rating_data, model, places_data, user_id, region, num_days)
        rundown = main(recommendations)
        return render_template('result.html', recommendations=recommendations, rundown=rundown)
    return render_template('index.html')

if __name__ == '__main__':
    app.run(debug=True)