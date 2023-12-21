import os
import uvicorn
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

app = Flask(__name__)

# Import dataset
user_rating_data = pd.read_csv('https://raw.githubusercontent.com/WayGo12/WayGoApp/main/assets/dataset/user_rating_dataset.csv')
places_data = pd.read_csv('https://raw.githubusercontent.com/WayGo12/WayGoApp/main/assets/dataset/places_dataset.csv')
user_data = pd.read_csv('https://raw.githubusercontent.com/WayGo12/WayGoApp/main/assets/dataset/user_id_dataset.csv')
data_recommend = pd.merge(user_rating_data.groupby('Place_ID')['Place_Rating'].mean(), places_data, on='Place_ID')
data_copy1 = data_recommend.copy()

# Load collaborative filtering model 
model_path = 'model'
model = load_model(model_path)

# data preparation, indexing and dicoding
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

# predict by user_ID
def recommend_by_collaborative_filtering(model, places_data, user_rating_data, user_id, region):
    num_users, num_resto, user_to_user_encoded, user_encoded_to_user, resto_to_resto_encoded, resto_encoded_to_resto = prepare_data(user_rating_data)
    resto_df = places_data
    df = user_rating_data
    resto_df = resto_df[resto_df['Place_Region'] == region]
    # session = tf.compat.v1.Session()
    # user_id_value = session.run(user_id)
    user_id_value = int(user_id)
    resto_visited_by_user = df[df.ID_user == user_id_value]
    resto_not_visited = resto_df[~resto_df['Place_ID'].isin(resto_visited_by_user.Place_ID.values)]['Place_ID']
    resto_not_visited = list(
        set(resto_not_visited).intersection(resto_to_resto_encoded.keys())
    )
    resto_not_visited = list(map(resto_to_resto_encoded.get, resto_not_visited))

    user_encoder = user_to_user_encoded[user_id]
    user_encoder_reshaped = np.array([user_encoder]).reshape(1, -1)
    resto_not_visited_reshaped = np.array(resto_not_visited).reshape(-1, 1)
    user_resto_array = np.hstack((np.tile(user_encoder_reshaped, (len(resto_not_visited), 1)), resto_not_visited_reshaped))
    user_resto_array = tf.cast(user_resto_array, dtype=tf.int64)
    ratings = model.predict(user_resto_array).flatten()
    top_ratings_indices = ratings.argsort()[-300:][::-1]
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

def generate_rundown_for_user(user_data):
    places_data = {
        'Nama Tempat': user_data['Nama Tempat'],
        'Latitude': user_data['Latitude'],
        'Longitude': user_data['Longitude'],
        'Kategori': user_data['Kategori']
    }

    def add_rundown_entry(time_range, place_index, category_filter=None):
        place = find_closest_place(place_index, places_data, category_filter)
        if place is not None:
            distance = haversine_distance(
                (places_data['Latitude'][place_index], places_data['Longitude'][place_index]),
                (places_data['Latitude'][place], places_data['Longitude'][place])
            )
            rundown.append({
                'Jam Rundown': time_range,
                'Nama Tempat': places_data['Nama Tempat'][place],
                'Kategori': places_data['Kategori'][place],
                'Jarak Tempat': distance
            })
            delete_processed_place(places_data, place_index)
        else:
            # Add an entry with placeholder values if no valid place is found
            rundown.append({
                'Jam Rundown': time_range,
                'Nama Tempat': "Tidak ada rekomendasi.",
                'Kategori': "",
                'Jarak Tempat': ""
            })

    # 9 PM - 6 AM: Accommodation
    accommodation_place = user_data['Kategori'].index('Accomodation')
    rundown = [{'Jam Rundown': '9 PM - 6 AM',
                'Nama Tempat': places_data['Nama Tempat'][accommodation_place],
                'Kategori': places_data['Kategori'][accommodation_place],
                'Jarak Tempat': ""}]

    # 6 AM - 8 AM: Culinary (Breakfast)
    add_rundown_entry('6 AM - 8 AM', accommodation_place, ['Culinary'])
    # 8 AM - 10 AM: Activity
    add_rundown_entry('8 AM - 10 AM', accommodation_place, ['Nautical', 'Nature', 'History', 'Entertainment', 'Shopping', 'Art'])
    # 10 AM - 12 PM: Activity
    add_rundown_entry('10 AM - 12 PM', accommodation_place, ['Nautical', 'Nature', 'History', 'Entertainment', 'Shopping', 'Art'])
    # 12 PM - 1 PM: Culinary (Lunch)
    add_rundown_entry('12 PM - 1 PM', accommodation_place, ['Culinary'])
    # 1 PM - 3 PM: Activity
    add_rundown_entry('1 PM - 3 PM', accommodation_place, ['Nautical', 'Nature', 'History', 'Entertainment', 'Shopping', 'Art'])
    # 3 PM - 5 PM: Activity
    add_rundown_entry('3 PM - 5 PM', accommodation_place, ['Nautical', 'Nature', 'History', 'Entertainment', 'Shopping', 'Art'])
    # 5 PM - 7 PM: Culinary (Dinner)
    add_rundown_entry('5 PM - 7 PM', accommodation_place, ['Culinary'])
    # 7 PM - 9 PM: Shopping
    add_rundown_entry('7 PM - 9 PM', accommodation_place, ['Shopping'])
    return rundown

def predict(user_rating_data, model, places_data, user_id, region):
    global recommendations
    recommendations = recommend_by_collaborative_filtering(model, places_data, user_rating_data, user_id, region)
    return recommendations

def generate_rundown(recommendations):
    rundown = generate_rundown_for_user(recommendations)
    return rundown


@app.route('/', methods=['GET'])
def home():
    return "API is running!"

@app.route('/generate', methods=['GET', 'POST'])
def index():
    if request.method == 'POST':
        # Process POST request
        user_id = int(request.form['user_id'])
        region = request.form['region']

        recommendations = predict(user_rating_data, model, places_data, user_id, region)
        rundown = generate_rundown(recommendations)

        return jsonify({
            "status": "success",
            "recommendations": recommendations,
            "rundown": rundown
        })
    elif request.method == 'GET':
        # Process GET request
        # You can add logic here to handle GET requests, e.g., return a default response
        user_id = request.args.get('user_id')
        region = request.args.get('region')

        if user_id is not None and region is not None:
            recommendations = predict(user_rating_data, model, places_data, int(user_id), region)
            rundown = generate_rundown(recommendations)

            return jsonify({
                "status": "success",
                "recommendations": recommendations,
                "rundown": rundown
            })
        else:
            return jsonify({
                "status": "error",
                "message": "Invalid request. Provide 'user_id' and 'region' parameters."
            })

if __name__ == '__main__':
    app.run(debug=True)
    app.run(host='0.0.0.0',port=int(os.environ.get('PORT', 5000)))
    # port=int(os.environ.get('PORT', 5000))
    # uvicorn.run(app, host='0.0.0.0',port=port)