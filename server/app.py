from flask import Flask, jsonify
from flask_cors import CORS
from dotenv import load_dotenv
import handlers

load_dotenv()

app = Flask(__name__)
CORS(app)

@app.route('/')
def index():
    return jsonify({'message': 'Bounty Hunter API', 'version': '2.0'})

@app.route('/api/register', methods=['POST'])
def register():
    return handlers.handle_register()

@app.route('/api/login', methods=['POST'])
def login():
    return handlers.handle_login()

@app.route('/api/bounties', methods=['GET', 'POST'])
def bounties():
    if request.method == 'POST':
        return handlers.handle_create_bounty()
    return handlers.handle_list_bounties()

@app.route('/api/bounties/<int:bounty_id>', methods=['GET'])
def get_bounty(bounty_id):
    return handlers.handle_get_bounty(bounty_id)

@app.route('/api/bounties/<int:bounty_id>/claim', methods=['POST'])
def claim_bounty(bounty_id):
    return handlers.handle_claim_bounty(bounty_id)

@app.route('/api/claims/<int:claim_id>/submit', methods=['POST'])
def submit_proof(claim_id):
    return handlers.handle_submit_proof(claim_id)

@app.route('/api/claims/<int:claim_id>/review', methods=['POST'])
def review_claim(claim_id):
    return handlers.handle_review_claim(claim_id)

@app.route('/api/claims/my', methods=['GET'])
def my_claims():
    return handlers.handle_my_claims()

if __name__ == '__main__':
    from flask import request
    app.run(host='0.0.0.0', port=5000, debug=True)
