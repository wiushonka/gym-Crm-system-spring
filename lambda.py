import boto3
import csv
import io
import json
import logging
from datetime import datetime
import os

logger = logging.getLogger()
logger.setLevel(logging.INFO)

dynamodb = boto3.resource('dynamodb')
s3 = boto3.client('s3')

TABLE_NAME = os.environ['DYNAMODB_TABLE']
BUCKET_NAME = os.environ['S3_BUCKET']

def lambda_handler(event, context):
    table = dynamodb.Table(TABLE_NAME)

    now = datetime.utcnow()
    year = str(now.year)
    month = f"{now.month:02d}"

    response = table.scan()
    items = response.get('Items', [])

    logger.info("DynamoDB scan result (pretty printed):")
    logger.info(json.dumps(items, indent=2, default=str))

    output = io.StringIO()
    writer = csv.writer(output)

    writer.writerow([
        "Trainer First Name",
        "Trainer Last Name",
        "Current Month Trainings Duration"
    ])

    for item in items:
        first_name = item.get('firstName')
        last_name = item.get('lastName')
        active = item.get('active', True)
        trainings = item.get('trainings', {})

        month_duration = 0
        if year in trainings:
            month_duration = int(trainings[year].get(month, 0))

        if not active and month_duration == 0:
            continue

        writer.writerow([
            first_name,
            last_name,
            month_duration
        ])

    report_name = f"Trainers_Trainings_summary_{year}_{month}.csv"

    s3.put_object(
        Bucket=BUCKET_NAME,
        Key=report_name,
        Body=output.getvalue(),
        ContentType='text/csv'
    )

    return {
        "statusCode": 200,
        "file": report_name
    }
