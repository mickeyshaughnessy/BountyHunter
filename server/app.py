from flask import Flask, jsonify, request
from flask_cors import CORS
from dotenv import load_dotenv
import handlers

load_dotenv()

app = Flask(__name__)
CORS(app)

@app.route('/')
def index():
    return jsonify({'message': 'Bounty Hunter API', 'version': '3.0'})

@app.route('/api/register', methods=['POST'])
def register():
    return handlers.handle_register()

@app.route('/api/login', methods=['POST'])
def login():
    return handlers.handle_login()

@app.route('/api/suggestions', methods=['GET'])
def suggestions():
    return handlers.handle_get_suggestions()

@app.route('/api/workouts', methods=['GET', 'POST'])
def workouts():
    if request.method == 'POST':
        return handlers.handle_log_workout()
    return handlers.handle_get_history()

@app.route('/api/recurring', methods=['GET', 'POST'])
def recurring():
    if request.method == 'POST':
        return handlers.handle_create_recurring()
    return handlers.handle_get_recurring()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
