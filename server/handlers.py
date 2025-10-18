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
        'created_at': datetime.utcnow().isoformat()
    }
    
    users.append(user)
    storage.save_users(users)
    
    return jsonify({
        'id': user['id'],
        'username': user['username'],
        'email': user['email'],
        'created_at': user['created_at']
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

def handle_create_bounty():
    user = get_current_user()
    if not user:
        return jsonify({'error': 'Unauthorized'}), 401
    
    data = request.json
    title = data.get('title')
    description = data.get('description')
    reward = data.get('reward')
    location = data.get('location')
    
    if not all([title, description, reward]):
        return jsonify({'error': 'Missing required fields'}), 400
    
    bounties = storage.get_bounties()
    
    bounty = {
        'id': get_next_id(bounties),
        'creator_id': user['id'],
        'creator_username': user['username'],
        'title': title,
        'description': description,
        'reward': reward,
        'location': location,
        'status': 'open',
        'created_at': datetime.utcnow().isoformat()
    }
    
    bounties.append(bounty)
    storage.save_bounties(bounties)
    
    return jsonify(bounty), 201

def handle_list_bounties():
    status_filter = request.args.get('status')
    bounties = storage.get_bounties()
    
    if status_filter:
        bounties = [b for b in bounties if b['status'] == status_filter]
    
    return jsonify(bounties), 200

def handle_get_bounty(bounty_id):
    bounties = storage.get_bounties()
    bounty = next((b for b in bounties if b['id'] == bounty_id), None)
    
    if not bounty:
        return jsonify({'error': 'Bounty not found'}), 404
    
    return jsonify(bounty), 200

def handle_claim_bounty(bounty_id):
    user = get_current_user()
    if not user:
        return jsonify({'error': 'Unauthorized'}), 401
    
    bounties = storage.get_bounties()
    bounty = next((b for b in bounties if b['id'] == bounty_id), None)
    
    if not bounty:
        return jsonify({'error': 'Bounty not found'}), 404
    
    if bounty['status'] != 'open':
        return jsonify({'error': 'Bounty not available'}), 400
    
    claims = storage.get_claims()
    
    claim = {
        'id': get_next_id(claims),
        'bounty_id': bounty_id,
        'hunter_id': user['id'],
        'hunter_username': user['username'],
        'status': 'active',
        'claimed_at': datetime.utcnow().isoformat()
    }
    
    claims.append(claim)
    storage.save_claims(claims)
    
    bounty['status'] = 'claimed'
    storage.save_bounties(bounties)
    
    return jsonify(claim), 201

def handle_submit_proof(claim_id):
    user = get_current_user()
    if not user:
        return jsonify({'error': 'Unauthorized'}), 401
    
    claims = storage.get_claims()
    claim = next((c for c in claims if c['id'] == claim_id), None)
    
    if not claim:
        return jsonify({'error': 'Claim not found'}), 404
    
    if claim['hunter_id'] != user['id']:
        return jsonify({'error': 'Not your claim'}), 403
    
    data = request.json
    proof_description = data.get('proof_description')
    proof_url = data.get('proof_url')
    
    claim['proof_description'] = proof_description
    claim['proof_url'] = proof_url
    claim['status'] = 'submitted'
    claim['submitted_at'] = datetime.utcnow().isoformat()
    
    storage.save_claims(claims)
    
    bounties = storage.get_bounties()
    bounty = next((b for b in bounties if b['id'] == claim['bounty_id']), None)
    if bounty:
        bounty['status'] = 'under_review'
        storage.save_bounties(bounties)
    
    return jsonify(claim), 200

def handle_review_claim(claim_id):
    user = get_current_user()
    if not user:
        return jsonify({'error': 'Unauthorized'}), 401
    
    claims = storage.get_claims()
    claim = next((c for c in claims if c['id'] == claim_id), None)
    
    if not claim:
        return jsonify({'error': 'Claim not found'}), 404
    
    bounties = storage.get_bounties()
    bounty = next((b for b in bounties if b['id'] == claim['bounty_id']), None)
    
    if not bounty or bounty['creator_id'] != user['id']:
        return jsonify({'error': 'Only bounty creator can review'}), 403
    
    data = request.json
    approved = data.get('approved', False)
    
    if approved:
        claim['status'] = 'approved'
        bounty['status'] = 'completed'
        claim['completed_at'] = datetime.utcnow().isoformat()
    else:
        claim['status'] = 'rejected'
        bounty['status'] = 'open'
    
    storage.save_claims(claims)
    storage.save_bounties(bounties)
    
    return jsonify({'message': 'Review submitted', 'approved': approved}), 200

def handle_my_claims():
    user = get_current_user()
    if not user:
        return jsonify({'error': 'Unauthorized'}), 401
    
    claims = storage.get_claims()
    my_claims = [c for c in claims if c['hunter_id'] == user['id']]
    
    return jsonify(my_claims), 200
