from flask import jsonify, request
from datetime import datetime
from typing import List, Dict, Optional
import hashlib
import secrets
from storage import storage

active_tokens = {}

def hash_password(password: str) -> str:
    return hashlib.sha256(password.encode()).hexdigest()

def generate_token() -> str:
    return secrets.token_urlsafe(32)

def get_current_user():
    auth_header = request.headers.get('Authorization')
    if not auth_header or not auth_header.startswith('Bearer '):
        return None
    token = auth_header.split(' ')[1]
    return active_tokens.get(token)

def get_next_id(collection: List[dict]) -> int:
    if not collection:
        return 1
    return max(item['id'] for item in collection) + 1

def handle_register():
    data = request.json
    username = data.get('username')
    email = data.get('email')
    password = data.get('password')
    
    # New fields
    full_name = data.get('full_name')
    location = data.get('location')
    age = data.get('age')
    weight = data.get('weight')
    activity_types = data.get('activity_types', []) # e.g., ["group", "gym"]
    payment_info = data.get('payment_info')
    
    if not all([username, email, password]):
        return jsonify({'error': 'Missing required fields'}), 400
    
    users = storage.get_users()
    
    if any(u['username'] == username or u['email'] == email for u in users):
        return jsonify({'error': 'Username or email already exists'}), 400
    
    user = {
        'id': get_next_id(users),
        'username': username,
        'email': email,
        'password_hash': hash_password(password),
        'full_name': full_name,
        'location': location,
        'age': age,
        'weight': weight,
        'activity_types': activity_types,
        'payment_info': payment_info,
        'created_at': datetime.utcnow().isoformat()
    }
    
    users.append(user)
    storage.save_users(users)
    
    return jsonify({
        'id': user['id'],
        'username': user['username'],
        'email': user['email']
    }), 201

def handle_login():
    data = request.json
    username = data.get('username')
    password = data.get('password')
    
    if not all([username, password]):
        return jsonify({'error': 'Missing credentials'}), 400
    
    users = storage.get_users()
    user = next((u for u in users if u['username'] == username), None)
    
    if not user or user['password_hash'] != hash_password(password):
        return jsonify({'error': 'Invalid credentials'}), 401
    
    token = generate_token()
    active_tokens[token] = user
    
    return jsonify({'token': token, 'user': {
        'id': user['id'],
        'username': user['username'],
        'email': user['email']
    }}), 200

def handle_get_suggestions():
    user = get_current_user()
    if not user:
        return jsonify({'error': 'Unauthorized'}), 401
    
    # Mock suggestions based on activity types
    suggestions = [
        {'id': 1, 'title': 'Morning Run', 'type': 'individual', 'duration': '30 mins'},
        {'id': 2, 'title': 'Gym Session', 'type': 'gym', 'duration': '60 mins'},
        {'id': 3, 'title': 'Yoga Class', 'type': 'group', 'duration': '45 mins'},
    ]
    
    user_types = user.get('activity_types', [])
    if user_types:
        suggestions = [s for s in suggestions if s['type'] in user_types]
        
    return jsonify(suggestions), 200

def handle_log_workout():
    user = get_current_user()
    if not user:
        return jsonify({'error': 'Unauthorized'}), 401
    
    data = request.json
    workouts = storage.get_workouts()
    
    workout = {
        'id': get_next_id(workouts),
        'user_id': user['id'],
        'title': data.get('title'),
        'type': data.get('type'),
        'duration': data.get('duration'),
        'data': data.get('data'), # Arbitrary workout data
        'timestamp': datetime.utcnow().isoformat()
    }
    
    workouts.append(workout)
    storage.save_workouts(workouts)
    
    return jsonify(workout), 201

def handle_get_history():
    user = get_current_user()
    if not user:
        return jsonify({'error': 'Unauthorized'}), 401
    
    workouts = storage.get_workouts()
    user_workouts = [w for w in workouts if w['user_id'] == user['id']]
    
    return jsonify(user_workouts), 200

def handle_create_recurring():
    user = get_current_user()
    if not user:
        return jsonify({'error': 'Unauthorized'}), 401
    
    data = request.json
    recurring = storage.get_recurring_workouts()
    
    item = {
        'id': get_next_id(recurring),
        'user_id': user['id'],
        'title': data.get('title'),
        'schedule': data.get('schedule'), # e.g., "daily", "weekly"
        'created_at': datetime.utcnow().isoformat()
    }
    
    recurring.append(item)
    storage.save_recurring_workouts(recurring)
    
    return jsonify(item), 201

def handle_get_recurring():
    user = get_current_user()
    if not user:
        return jsonify({'error': 'Unauthorized'}), 401
    
    recurring = storage.get_recurring_workouts()
    user_recurring = [r for r in recurring if r['user_id'] == user['id']]
    
    return jsonify(user_recurring), 200
