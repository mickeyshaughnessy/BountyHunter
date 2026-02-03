import boto3
import json
import os
from datetime import datetime
from typing import Dict, List, Optional

class S3Storage:
    def __init__(self):
        self.s3_client = None
        self.bucket = os.getenv('S3_BUCKET_NAME', 'bounty-hunter-data')
        self.data_prefix = 'data/'
    
    def _get_client(self):
        if self.s3_client is None:
            self.s3_client = boto3.client(
                's3',
                aws_access_key_id=os.getenv('AWS_ACCESS_KEY_ID'),
                aws_secret_access_key=os.getenv('AWS_SECRET_ACCESS_KEY'),
                region_name=os.getenv('AWS_REGION', 'us-east-1')
            )
        return self.s3_client
    
    def _get_key(self, collection: str) -> str:
        return f"{self.data_prefix}{collection}.json"
    
    def _read_collection(self, collection: str) -> List[Dict]:
        try:
            key = self._get_key(collection)
            response = self._get_client().get_object(Bucket=self.bucket, Key=key)
            data = json.loads(response['Body'].read().decode('utf-8'))
            return data
        except Exception as e:
            if 'NoSuchKey' in str(e):
                return []
            print(f"Error reading {collection}: {e}")
            return []
    
    def _write_collection(self, collection: str, data: List[Dict]) -> bool:
        try:
            key = self._get_key(collection)
            self._get_client().put_object(
                Bucket=self.bucket,
                Key=key,
                Body=json.dumps(data, indent=2, default=str),
                ContentType='application/json'
            )
            return True
        except Exception as e:
            print(f"Error writing {collection}: {e}")
            return False
    
    def get_users(self) -> List[Dict]:
        return self._read_collection('users')
    
    def save_users(self, users: List[Dict]) -> bool:
        return self._write_collection('users', users)
    
    def get_workouts(self) -> List[Dict]:
        return self._read_collection('workouts')
    
    def save_workouts(self, workouts: List[Dict]) -> bool:
        return self._write_collection('workouts', workouts)
    
    def get_recurring_workouts(self) -> List[Dict]:
        return self._read_collection('recurring_workouts')
    
    def save_recurring_workouts(self, recurring: List[Dict]) -> bool:
        return self._write_collection('recurring_workouts', recurring)

storage = S3Storage()
