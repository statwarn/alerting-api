# Routes

## Versioning

All routes are prefixed by the API version to use, for example `/v1` for API version 1.
Version(s) currently supported : `v1`

## Alerts

### Format
Alerts are described using the following JSON format:
```javascript
{
  "id": uuid
  "name": string,
  "timeseries_ids": uuid[],
  "activation_timeranges": [{
    "id": uuid,
    "timeUnit": "day"|"hour"|"minute"|"second",
    "start": integer
    "end": integer
  }],
  "actions": [{
    "id": uuid,
    "type": "webhook"|"email",
    "value": string,
    "ratelimits": [{
      "timeUnit": "day"|"hour"|"minute"|"second",
      "timeValue": integer,
      "maxTriggers": integer
    }]
  }],
  "triggers": [{
    // see triggers description
  }]
}
```

### Triggers

Triggers analyze the data (or the absence of data) recorded by your timeseries and determine whether or not to trigger an alert.

#### no_data
A `no_data` trigger is defined using the following JSON structure :
```json
{
  "operator": "no_data",
  "target": [string], // optional
  "rate": {
    "timeUnit": "day"|"hour"|"minute"|"second",
    "timeValue": integer
  }
}
```

#### any_data
#### anomalous
#### value
#### attribute

### GET /alerts
Returns alerts with the JSON format described above.


### POST /alerts

### PUT /alerts/:alert_id

### DELETE /alerts/:alert_id