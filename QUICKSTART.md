# 🦅 Quick Start Guide

Get the Bounty Hunter platform running in 5 minutes!

## 1. Get Your Mapbox Token
1. Go to https://account.mapbox.com/
2. Sign up (free)
3. Copy your default public token

## 2. Set Up AWS S3
```bash
# Create bucket
aws s3 mb s3://bounty-hunter-data

# Make it accessible (for development)
aws s3api put-bucket-cors --bucket bounty-hunter-data --cors-configuration '{
  "CORSRules": [{
    "AllowedOrigins": ["*"],
    "AllowedMethods": ["GET", "PUT", "POST"],
    "AllowedHeaders": ["*"]
  }]
}'
```

## 3. Configure
```bash
# Copy environment template
cp .env.example .env

# Edit .env with your values:
# - AWS credentials
# - S3 bucket name
```

**Edit web files:**
- Open `web/index.html` 
- Find `mapboxgl.accessToken = 'YOUR_MAPBOX_TOKEN_HERE';`
- Replace with your token
- Do the same in `web/submit.html`

## 4. Run the Server
```bash
cd server
pip install -r requirements.txt
python app.py
```

Server runs on: `http://localhost:5000`

## 5. Run the Web UI
New terminal:
```bash
cd web
python -m http.server 8080
```

Visit: `http://localhost:8080`

## 🎯 Try It Out!

1. **Register** - Click "Register" and create an account
2. **Create Quest** - Click "Create Quest", click map to set location
3. **Browse** - View quests on the map with markers
4. **Accept** - Click "Accept Quest" to start a challenge
5. **Complete** - Submit proof when done!

## 🎨 What You'll See

- Beautiful **Native American-inspired** design
- **Turquoise and coral** colors
- **Interactive Mapbox** map
- **Geometric patterns** and earth tones
- Quest markers: 🏹 (open) and ✓ (complete)

## 💡 Example Quest

**Title:** Morning Trail Run at Eagle Peak  
**Description:** Complete a 5-mile trail run starting at sunrise. Take a photo at the summit with your GPS tracking app visible.  
**Reward:** $50  
**Location:** Click the peak on the map

## 🐛 Troubleshooting

**Map not loading?**
- Check Mapbox token is correct in HTML files
- Check browser console for errors

**Can't connect to API?**
- Make sure Flask server is running on port 5000
- Check `.env` file exists with AWS credentials

**S3 errors?**
- Verify AWS credentials are correct
- Check bucket name matches `.env`
- Ensure bucket CORS is configured

## 📚 Next Steps

- Read full [README.md](README.md) for deployment
- Customize colors in `web/css/styles.css`
- Add more quest types in handlers.py
