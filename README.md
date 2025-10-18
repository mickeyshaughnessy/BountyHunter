# 🦅 Bounty Hunter - Physical Fitness Quest Platform

A beautifully designed platform for creating and completing physical fitness challenges. Features Mapbox integration for location-based quests and a Native American-inspired design aesthetic.

## 🎨 Features

- **Interactive Map**: Mapbox-powered map showing all fitness quests with location markers
- **Physical Fitness Focus**: Trail running, hiking, yoga, swimming, cycling, and walking challenges
- **Location-Based**: Click to set quest locations on the map
- **Native American Theme**: Earthy colors, geometric patterns, and natural design elements
- **Simple Architecture**: Flask backend with JSON storage in S3 (no SQL database)

## 🏗️ Architecture

### Backend (Flask + S3)
- **Flask** web server (simple and lightweight)
- **handlers.py** - All business logic in one file
- **storage.py** - S3 integration for JSON data storage
- No SQL database - all data stored as JSON files in S3

### Frontend
- Vanilla HTML/CSS/JavaScript
- **Mapbox GL JS** for interactive maps
- Native American design theme (turquoise, coral, earth tones)
- Responsive design

### Data Storage (S3)
```
s3://your-bucket/
  data/
    users.json
    bounties.json
    claims.json
  proofs/
    {user_id}/{bounty_id}/{filename}
```

## 🚀 Quick Start

### Prerequisites
- Python 3.11+
- AWS account with S3 bucket
- Mapbox account (free tier is fine)

### 1. Set Up S3 Bucket
```bash
# Create S3 bucket
aws s3 mb s3://bounty-hunter-data

# Set CORS policy (create cors.json):
{
  "CORSRules": [{
    "AllowedOrigins": ["*"],
    "AllowedMethods": ["GET", "PUT", "POST"],
    "AllowedHeaders": ["*"]
  }]
}

aws s3api put-bucket-cors --bucket bounty-hunter-data --cors-configuration file://cors.json
```

### 2. Configure Environment
```bash
# Copy example env file
cp .env.example .env

# Edit .env with your credentials:
# - AWS_ACCESS_KEY_ID
# - AWS_SECRET_ACCESS_KEY
# - S3_BUCKET_NAME
# - MAPBOX_TOKEN (get from https://account.mapbox.com/)
```

### 3. Update Mapbox Token in HTML Files
Edit `web/index.html` and `web/submit.html`:
```javascript
mapboxgl.accessToken = 'YOUR_MAPBOX_TOKEN_HERE';
```
Replace with your actual Mapbox token.

### 4. Install Dependencies
```bash
cd server
pip install -r requirements.txt
```

### 5. Run the Server
```bash
python app.py
```

The API will run on `http://localhost:5000`

### 6. Serve the Frontend
In a new terminal:
```bash
cd web
python -m http.server 8080
```

Visit `http://localhost:8080` in your browser.

## 🎯 Usage

### Creating a Quest
1. Click "Create Quest" in navigation
2. Login or register
3. Click on the map to set the quest location
4. Fill in the quest details:
   - Title (e.g., "Morning Trail Run at Eagle Peak")
   - Description (what to do and how to prove it)
   - Reward amount
5. Submit to create the quest

### Accepting a Quest
1. Browse quests on the main map page
2. Click "Accept Quest" on any open quest
3. Complete the physical challenge
4. Submit proof (photo, GPS data, etc.)
5. Wait for the quest creator to approve

### Map Features
- 🏹 = Open quest (can be accepted)
- ✓ = Completed quest
- Click markers for quest details
- Pan and zoom to explore

## 📁 Project Structure

```
BountyHunter/
├── server/
│   ├── app.py              # Flask application
│   ├── handlers.py         # All API logic
│   ├── storage.py          # S3 JSON storage
│   └── requirements.txt    # Python dependencies
├── web/
│   ├── index.html         # Map view
│   ├── submit.html        # Create quest
│   ├── css/
│   │   └── styles.css     # Native American theme
│   └── js/
│       ├── api.js         # API client
│       └── app.js         # Frontend logic
├── .env.example           # Environment template
└── README.md
```

## 🎨 Design Theme

The platform uses a **Native American-inspired** design:

### Color Palette
- **Earth Brown** (#8B4513) - Primary borders and text
- **Turquoise** (#40E0D0) - Accents and highlights
- **Coral** (#FF6B6B) - Rewards and CTAs
- **Sand/Cream** (#D2B48C, #FFF8E7) - Backgrounds
- **Sunset Orange** (#FF8C42) - Secondary accents

### Design Elements
- Geometric border patterns
- Earthy, natural textures
- Sacred symbols (🦅 eagle, 🏹 arrow, 💎 gem)
- Georgia serif font for readability
- Warm, inviting color scheme

## 🔌 API Endpoints

### Authentication
- `POST /api/register` - Register new user
- `POST /api/login` - Login and get token

### Bounties
- `GET /api/bounties` - List all quests
- `POST /api/bounties` - Create new quest
- `GET /api/bounties/{id}` - Get quest details
- `POST /api/bounties/{id}/claim` - Accept quest

### Claims
- `GET /api/claims/my` - Get my accepted quests
- `POST /api/claims/{id}/submit` - Submit proof
- `POST /api/claims/{id}/review` - Review submission (creator only)

## 🏃 Quest Examples

1. **Trail Running**
   - "Complete 5-mile morning trail run at Sunrise Peak"
   - Reward: $50
   - Proof: GPS tracking screenshot

2. **Outdoor Yoga**
   - "Attend sunrise yoga session at River Valley Park"
   - Reward: $30
   - Proof: Photo from the class

3. **Hiking Challenge**
   - "Summit Eagle Mountain before noon"
   - Reward: $100
   - Proof: Summit photo with timestamp

4. **Swimming**
   - "Swim 1 mile in open water at Lake Blue"
   - Reward: $75
   - Proof: Swimming app screenshot

5. **Cycling**
   - "Complete 20-mile bike ride on Canyon Trail"
   - Reward: $60
   - Proof: Cycling app data

## 🚀 Deploying to AWS VM

### 1. Set Up EC2 Instance
```bash
# SSH into your AWS VM
ssh -i your-key.pem ubuntu@your-vm-ip

# Install Python and dependencies
sudo apt update
sudo apt install python3 python3-pip -y

# Clone your repo
git clone your-repo-url
cd BountyHunter
```

### 2. Configure Environment
```bash
cd server
pip3 install -r requirements.txt

# Create .env with your AWS credentials
nano .env
```

### 3. Run with Gunicorn (Production)
```bash
pip3 install gunicorn

# Run the server
gunicorn -w 4 -b 0.0.0.0:5000 app:app
```

### 4. Set Up Nginx (Reverse Proxy)
```bash
sudo apt install nginx

# Configure nginx to proxy to Flask
sudo nano /etc/nginx/sites-available/bounty-hunter

# Add:
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /path/to/BountyHunter/web;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:5000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

# Enable and restart
sudo ln -s /etc/nginx/sites-available/bounty-hunter /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### 5. Run as Service (systemd)
```bash
sudo nano /etc/systemd/system/bounty-hunter.service

# Add:
[Unit]
Description=Bounty Hunter API
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu/BountyHunter/server
Environment="PATH=/home/ubuntu/.local/bin"
ExecStart=/usr/local/bin/gunicorn -w 4 -b 0.0.0.0:5000 app:app

[Install]
WantedBy=multi-user.target

# Enable and start
sudo systemctl enable bounty-hunter
sudo systemctl start bounty-hunter
sudo systemctl status bounty-hunter
```

## 🔒 Security Notes

- Store `.env` file securely (never commit to git)
- Use IAM roles with minimal S3 permissions
- In production, use HTTPS (Let's Encrypt)
- Implement rate limiting for API endpoints
- Add input validation and sanitization
- Use strong password requirements

## 📝 License

MIT License - Use freely for any purpose.

## 🤝 Contributing

This is a personal project, but feel free to fork and customize!

## 🎊 Acknowledgments

- Native American design inspiration from traditional geometric patterns
- Mapbox for excellent mapping platform
- Flask community for the simple web framework
