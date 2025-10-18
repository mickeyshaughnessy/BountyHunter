import sys
import os
os.environ['AWS_ACCESS_KEY_ID'] = 'test'
os.environ['AWS_SECRET_ACCESS_KEY'] = 'test'
os.environ['S3_BUCKET_NAME'] = 'test-bucket'

# Mock boto3 for testing
class MockS3:
    def __init__(self, *args, **kwargs):
        pass
    def get_object(self, **kwargs):
        raise Exception("NoSuchKey")
    def put_object(self, **kwargs):
        return True

import unittest.mock as mock
sys.modules['boto3'] = mock.MagicMock()
sys.modules['boto3'].client = MockS3

from app import app

with app.test_client() as client:
    response = client.get('/')
    print(f"✓ Server responds: {response.json}")
    assert response.status_code == 200
    print("✓ All basic tests passed!")
